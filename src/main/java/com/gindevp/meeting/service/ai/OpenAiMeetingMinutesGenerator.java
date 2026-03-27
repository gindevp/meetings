package com.gindevp.meeting.service.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class OpenAiMeetingMinutesGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAiMeetingMinutesGenerator.class);

    private final OpenAiProperties props;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public OpenAiMeetingMinutesGenerator(OpenAiProperties props, ObjectMapper objectMapper, RestClient.Builder restClientBuilder) {
        this.props = props;
        this.objectMapper = objectMapper;

        this.restClient = restClientBuilder
            .baseUrl(props.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public String name() {
        String model = props.getModel();
        if (model == null || model.trim().isBlank()) return "openai";
        return "openai/" + model.trim();
    }

    public MinutesSuggestionResult generate(String meetingTitle, String minutesText, List<String> agendaTopics) {
        String safeTitle = meetingTitle != null ? meetingTitle.trim() : "";
        String safeNote = minutesText != null ? minutesText.trim() : "";
        String agendaText = agendaTopics != null && !agendaTopics.isEmpty() ? String.join(" | ", agendaTopics) : "";
        String input = (safeTitle + "\n" + agendaText + "\n" + safeNote).trim();

        if (!props.hasApiKey()) {
            throw new IllegalStateException("OPENAI_API_KEY is missing");
        }

        String system =
            """
            Bạn là trợ lý phân tích biên bản họp. Hãy trích xuất thông tin từ ghi chú cuộc họp.
            Trả về JSON hợp lệ (không markdown, không code fence), theo schema:
            {
              "summary": "string",
              "decisions": ["string", ...],
              "tasks": [{"title":"string","description":"string"}]
            }
            Quy tắc:
            - summary: 3-7 gạch đầu dòng ngắn gọn.
            - decisions: chỉ các quyết định/kết luận đã thống nhất.
            - tasks: chỉ việc cần làm sau họp; title ngắn, description optional.
            - Tối đa 10 tasks.
            """;

        String user = buildUserPrompt(safeTitle, agendaTopics, safeNote);

        OpenAiChatCompletionsRequest req = new OpenAiChatCompletionsRequest(
            props.getModel(),
            List.of(new Message("system", system), new Message("user", user)),
            Map.of("type", "json_object"),
            0.2
        );

        OpenAiChatCompletionsResponse resp;
        try {
            resp = restClient
                .post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey().trim())
                .body(req)
                .retrieve()
                .body(OpenAiChatCompletionsResponse.class);
        } catch (HttpClientErrorException.TooManyRequests e) {
            // OpenAI uses 429 for quota / rate limits. Provide a user-friendly error for the UI.
            throw new BadRequestAlertException(
                "OpenAI quota/rate limit exceeded. Please check your OpenAI plan/billing and try again.",
                "aiMeetingSuggestion",
                "openai_quota"
            );
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BadRequestAlertException(
                "OpenAI API key is invalid. Please check OPENAI_API_KEY and try again.",
                "aiMeetingSuggestion",
                "openai_unauthorized"
            );
        }

        String content = extractContent(resp);
        ModelOutput out = parseModelOutput(content);

        List<SuggestedTaskDTO> tasks = new ArrayList<>();
        if (out.tasks != null) {
            for (Task t : out.tasks) {
                if (t == null || t.title == null) continue;
                String title = t.title.trim();
                if (title.isBlank()) continue;
                String desc = t.description != null ? t.description.trim() : "";
                tasks.add(new SuggestedTaskDTO(shorten(title, 120), shorten(desc, 500), (Instant) null, null, null));
                if (tasks.size() >= 10) break;
            }
        }

        String summary = normalizeSummary(out.summary);
        String decisions = formatDecisions(out.decisions);
        String inputHash = sha256Hex(input);

        return new MinutesSuggestionResult(inputHash, summary, decisions, tasks);
    }

    private String buildUserPrompt(String title, List<String> agendaTopics, String minutesText) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tiêu đề: ").append(title == null ? "" : title).append("\n");
        if (agendaTopics != null && !agendaTopics.isEmpty()) {
            sb.append("Agenda: ").append(String.join("; ", agendaTopics)).append("\n");
        }
        sb.append("Ghi chú/biên bản:\n").append(minutesText == null ? "" : minutesText);
        return sb.toString();
    }

    private String extractContent(OpenAiChatCompletionsResponse resp) {
        if (resp == null || resp.choices == null || resp.choices.isEmpty()) {
            throw new IllegalStateException("OpenAI response has no choices");
        }
        Choice c0 = resp.choices.get(0);
        if (c0 == null || c0.message == null || c0.message.content == null) {
            throw new IllegalStateException("OpenAI response has no message content");
        }
        return c0.message.content;
    }

    private ModelOutput parseModelOutput(String content) {
        try {
            JsonNode node = objectMapper.readTree(content);
            String summary = node.path("summary").asText("");
            List<String> decisions = objectMapper.convertValue(node.path("decisions"), new TypeReference<List<String>>() {});
            List<Task> tasks = objectMapper.convertValue(node.path("tasks"), new TypeReference<List<Task>>() {});
            return new ModelOutput(summary, decisions, tasks);
        } catch (Exception e) {
            LOG.warn("Failed to parse OpenAI JSON output; falling back to empty output. content={}", shorten(content, 500));
            return new ModelOutput("", List.of(), List.of());
        }
    }

    private String normalizeSummary(String summary) {
        String s = summary != null ? summary.trim() : "";
        if (s.isBlank()) return "";
        // If the model returns bullets already, keep them. Otherwise, prefix.
        String lower = s.toLowerCase(Locale.ROOT);
        if (lower.contains("\n- ") || lower.startsWith("- ")) return s;
        return "- " + s;
    }

    private String formatDecisions(List<String> decisions) {
        if (decisions == null || decisions.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("Quyết định:\n");
        int count = 0;
        for (String d : decisions) {
            if (d == null) continue;
            String t = d.trim();
            if (t.isBlank()) continue;
            sb.append("- ").append(shorten(t, 200)).append("\n");
            count++;
            if (count >= 10) break;
        }
        return sb.toString().trim();
    }

    private static String shorten(String s, int max) {
        if (s == null) return "";
        String t = s.trim();
        if (t.length() <= max) return t;
        return t.substring(0, Math.max(0, max - 1)).trim() + "…";
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest((input != null ? input : "").getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(dig);
        } catch (Exception e) {
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OpenAiChatCompletionsResponse(List<Choice> choices) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(Message message) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(String role, String content) {}

    public record OpenAiChatCompletionsRequest(
        String model,
        List<Message> messages,
        @JsonProperty("response_format") Map<String, Object> responseFormat,
        double temperature
    ) {}

    private record ModelOutput(String summary, List<String> decisions, List<Task> tasks) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Task(String title, String description) {}
}
