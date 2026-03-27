package com.gindevp.meeting.service.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingDocument;
import com.gindevp.meeting.domain.MeetingParticipant;
import com.gindevp.meeting.domain.MeetingTask;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class MeetingAiAssistantService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingAiAssistantService.class);
    private static final String ENTITY_NAME = "meetingAi";

    private final OpenAiProperties props;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public MeetingAiAssistantService(OpenAiProperties props, ObjectMapper objectMapper, RestClient.Builder restClientBuilder) {
        this.props = props;
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
            .baseUrl(props.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public String answerQuestion(
        Meeting meeting,
        List<String> agendaTopics,
        List<MeetingParticipant> participants,
        List<MeetingTask> tasks,
        List<MeetingDocument> documents,
        List<AiChatMessageDTO> history
    ) {
        ensureKey();
        String context = buildContext(meeting, agendaTopics, participants, tasks, documents);

        String system =
            """
            Bạn là trợ lý hỏi đáp cho cuộc họp.
            - Ưu tiên dùng CONTEXT (biên bản/agenda/tài liệu) để trả lời câu hỏi liên quan cuộc họp.
            - Nếu câu hỏi nằm ngoài CONTEXT, bạn VẪN có thể trả lời theo kiến thức chung.
            - Hãy ghi rõ phần nào là suy luận/kiến thức chung (ngoài cuộc họp) nếu không có trong CONTEXT.
            - Nếu không chắc chắn, nói rõ mức độ không chắc và gợi ý thông tin cần bổ sung.
            Trả lời ngắn gọn, có thể gạch đầu dòng.
            """;

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", system + "\n\nCONTEXT:\n" + context));
        if (history != null) {
            for (AiChatMessageDTO m : history) {
                if (m == null || m.content() == null) continue;
                String role = m.role() != null ? m.role().trim().toLowerCase() : "user";
                if (!role.equals("user") && !role.equals("assistant")) role = "user";
                messages.add(new Message(role, m.content()));
                if (messages.size() >= 30) break; // safety
            }
        }

        OpenAiChatCompletionsRequest req = new OpenAiChatCompletionsRequest(props.getModel(), messages, null, 0.2);
        OpenAiChatCompletionsResponse resp = postChat(req);
        return extractContent(resp);
    }

    private void ensureKey() {
        if (props == null || !props.hasApiKey()) {
            throw new BadRequestAlertException("OPENAI_API_KEY is missing", ENTITY_NAME, "openai_key_missing");
        }
    }

    private OpenAiChatCompletionsResponse postChat(OpenAiChatCompletionsRequest req) {
        try {
            return restClient
                .post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey().trim())
                .body(req)
                .retrieve()
                .body(OpenAiChatCompletionsResponse.class);
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new BadRequestAlertException("OpenAI quota/rate limit exceeded. Check billing.", ENTITY_NAME, "openai_quota");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BadRequestAlertException("OpenAI API key invalid.", ENTITY_NAME, "openai_unauthorized");
        }
    }

    private String extractContent(OpenAiChatCompletionsResponse resp) {
        if (resp == null || resp.choices == null || resp.choices.isEmpty()) return "";
        Choice c0 = resp.choices.get(0);
        if (c0 == null || c0.message == null || c0.message.content == null) return "";
        // Some environments may display mojibake if the console is not UTF-8; ensure the server-side string is normalized.
        return c0.message.content.trim();
    }

    private String buildContext(
        Meeting meeting,
        List<String> agendaTopics,
        List<MeetingParticipant> participants,
        List<MeetingTask> tasks,
        List<MeetingDocument> documents
    ) {
        StringBuilder sb = new StringBuilder();

        Instant now = Instant.now();
        sb.append("Now: ").append(fmtInstant(now)).append("\n");

        if (meeting != null) {
            sb.append("Meeting ID: ").append(meeting.getId() != null ? meeting.getId() : "").append("\n");
        }
        sb.append("Title: ").append(meeting != null && meeting.getTitle() != null ? meeting.getTitle() : "").append("\n");
        if (meeting != null) {
            sb
                .append("Time: ")
                .append(fmtInstant(meeting.getStartTime()))
                .append(" - ")
                .append(fmtInstant(meeting.getEndTime()))
                .append("\n");
            if (meeting.getEndTime() != null) {
                boolean ended = meeting.getEndTime().isBefore(now);
                sb.append("Meeting ended: ").append(ended).append("\n");
            }
            sb.append("Mode: ").append(meeting.getMode() != null ? meeting.getMode() : "").append("\n");
            if (meeting.getOnlineLink() != null && !meeting.getOnlineLink().trim().isBlank()) {
                sb.append("Online link: ").append(meeting.getOnlineLink().trim()).append("\n");
            }
            if (meeting.getType() != null) sb
                .append("Type: ")
                .append(meeting.getType().getName() != null ? meeting.getType().getName() : "")
                .append("\n");
            if (meeting.getLevel() != null) sb
                .append("Level: ")
                .append(meeting.getLevel().getName() != null ? meeting.getLevel().getName() : "")
                .append("\n");
            if (meeting.getOrganizerDepartment() != null) {
                sb
                    .append("Organizer department: ")
                    .append(meeting.getOrganizerDepartment().getName() != null ? meeting.getOrganizerDepartment().getName() : "")
                    .append("\n");
            }
            if (meeting.getStatus() != null) sb.append("Status: ").append(meeting.getStatus()).append("\n");
            if (meeting.getHost() != null) sb.append("Host: ").append(displayUser(meeting.getHost())).append("\n");
            if (meeting.getSecretary() != null) sb.append("Secretary: ").append(displayUser(meeting.getSecretary())).append("\n");
            if (meeting.getRequester() != null) sb.append("Requester: ").append(displayUser(meeting.getRequester())).append("\n");
        }
        if (agendaTopics != null && !agendaTopics.isEmpty()) sb.append("Agenda: ").append(String.join("; ", agendaTopics)).append("\n");
        String minutesText = null;
        if (meeting != null) {
            String note = meeting.getNote();
            String objectives = meeting.getObjectives();
            minutesText = (note != null && !note.trim().isBlank()) ? note : objectives;
        }
        sb.append("Minutes:\n").append(minutesText != null ? minutesText : "").append("\n");

        if (participants != null && !participants.isEmpty()) {
            sb.append("Participants:\n");
            int n = 0;
            for (MeetingParticipant p : participants) {
                if (p == null) continue;
                String who = p.getUser() != null
                    ? displayUser(p.getUser())
                    : (p.getDepartment() != null ? displayDepartment(p.getDepartment()) : "(unknown)");
                sb.append("- ").append(who);
                if (p.getRole() != null) sb.append(" | role=").append(p.getRole());
                if (p.getIsRequired() != null) sb.append(" | required=").append(p.getIsRequired());
                if (p.getAttendance() != null) sb.append(" | attendance=").append(p.getAttendance());
                if (p.getConfirmationStatus() != null) sb.append(" | confirm=").append(p.getConfirmationStatus());
                if (p.getAbsentReason() != null && !p.getAbsentReason().trim().isBlank()) sb
                    .append(" | absentReason=")
                    .append(p.getAbsentReason().trim());
                sb.append("\n");
                n++;
                if (n >= 40) break;
            }
        }

        if (tasks != null && !tasks.isEmpty()) {
            sb.append("Existing tasks:\n");
            int n = 0;
            for (MeetingTask t : tasks) {
                if (t == null) continue;
                if (t.getTitle() == null || t.getTitle().trim().isBlank()) continue;
                sb.append("- ").append(t.getTitle().trim());
                if (t.getStatus() != null) sb.append(" | status=").append(t.getStatus());
                if (t.getDueAt() != null) sb.append(" | dueAt=").append(fmtInstant(t.getDueAt()));
                if (t.getAssignee() != null) sb.append(" | assignee=").append(displayUser(t.getAssignee()));
                if (t.getDepartment() != null) sb.append(" | dept=").append(displayDepartment(t.getDepartment()));
                sb.append("\n");
                n++;
                if (n >= 40) break;
            }
        }

        if (documents != null && !documents.isEmpty()) {
            sb.append("Documents:\n");
            int count = 0;
            for (MeetingDocument d : documents) {
                if (d == null) continue;
                String name = d.getFileName() != null ? d.getFileName() : "(unnamed)";
                String ct = d.getFileContentType() != null ? d.getFileContentType() : "";
                sb.append("- ").append(name).append(" (").append(ct).append(")\n");
                String extracted = extractTextIfPlain(d);
                if (extracted != null && !extracted.isBlank()) {
                    sb.append("  Content:\n").append(trimTo(extracted, 4000)).append("\n");
                }
                count++;
                if (count >= 5) break;
            }
        }
        return sb.toString().trim();
    }

    private String fmtInstant(Instant t) {
        return t != null ? t.toString() : "";
    }

    private String displayUser(com.gindevp.meeting.domain.User u) {
        if (u == null) return "";
        String first = u.getFirstName() != null ? u.getFirstName().trim() : "";
        String last = u.getLastName() != null ? u.getLastName().trim() : "";
        String full = (first + " " + last).trim();
        String login = u.getLogin() != null ? u.getLogin().trim() : "";
        if (!full.isBlank() && !login.isBlank()) return full + " (" + login + ")";
        return !full.isBlank() ? full : login;
    }

    private String displayDepartment(com.gindevp.meeting.domain.Department d) {
        if (d == null) return "";
        String name = d.getName() != null ? d.getName().trim() : "";
        String code = d.getCode() != null ? d.getCode().trim() : "";
        if (!name.isBlank() && !code.isBlank()) return name + " (" + code + ")";
        return !name.isBlank() ? name : code;
    }

    private String extractTextIfPlain(MeetingDocument d) {
        try {
            String ct = d.getFileContentType() != null ? d.getFileContentType().toLowerCase() : "";
            if (!(ct.startsWith("text/") || ct.contains("json") || ct.contains("xml") || ct.contains("csv"))) return null;
            byte[] bytes = d.getFile();
            if (bytes == null || bytes.length == 0) return null;
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private String trimTo(String s, int max) {
        if (s == null) return "";
        String t = s.trim();
        if (t.length() <= max) return t;
        return t.substring(0, Math.max(0, max - 1)) + "…";
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
}
