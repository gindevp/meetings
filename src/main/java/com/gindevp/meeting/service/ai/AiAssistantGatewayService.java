package com.gindevp.meeting.service.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingParticipant;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import com.gindevp.meeting.repository.IncidentRepository;
import com.gindevp.meeting.repository.MeetingDocumentRepository;
import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.MeetingTaskRepository;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class AiAssistantGatewayService {

    private static final String ENTITY_NAME = "aiAssistant";

    private final OpenAiProperties props;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MeetingTaskRepository meetingTaskRepository;
    private final MeetingDocumentRepository meetingDocumentRepository;
    private final IncidentRepository incidentRepository;

    public AiAssistantGatewayService(
        OpenAiProperties props,
        ObjectMapper objectMapper,
        RestClient.Builder restClientBuilder,
        MeetingRepository meetingRepository,
        MeetingParticipantRepository meetingParticipantRepository,
        MeetingTaskRepository meetingTaskRepository,
        MeetingDocumentRepository meetingDocumentRepository,
        IncidentRepository incidentRepository
    ) {
        this.props = props;
        this.objectMapper = objectMapper;
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.meetingTaskRepository = meetingTaskRepository;
        this.meetingDocumentRepository = meetingDocumentRepository;
        this.incidentRepository = incidentRepository;
        this.restClient = restClientBuilder
            .baseUrl(props.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public AiAssistantResponse chat(AiAssistantRequest request, User currentUser) {
        ensureKey();
        String conversationId = request.conversationId() != null && !request.conversationId().isBlank()
            ? request.conversationId()
            : String.valueOf(System.currentTimeMillis());
        JsonNode contextNode = request.context() != null ? request.context() : objectMapper.createObjectNode();
        String runtimeFacts = buildRuntimeFacts(contextNode, currentUser);

        String systemPrompt = buildSystemPrompt(currentUser, contextNode, runtimeFacts);
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));

        if (request.recentMessages() != null) {
            for (AiAssistantChatMessageDTO m : request.recentMessages()) {
                if (m == null || m.content() == null || m.content().isBlank()) continue;
                String role = "assistant".equalsIgnoreCase(m.role()) ? "assistant" : "user";
                messages.add(new Message(role, m.content()));
                if (messages.size() >= 24) break;
            }
        }
        messages.add(new Message("user", request.message() == null ? "" : request.message()));

        OpenAiChatCompletionsRequest req = new OpenAiChatCompletionsRequest(props.getModel(), messages, null, 0.5);
        OpenAiChatCompletionsResponse resp = postChat(req);
        ParsedAssistantOutput out = parseAssistantOutput(extractContent(resp));
        out = autoPlanIfNeeded(request.message(), out);

        List<AiAssistantActionResultDTO> results = executeActions(out.actions(), currentUser, contextNode);
        String finalAnswer = mergeAnswerWithExecution(out.answer(), results);
        return new AiAssistantResponse(conversationId, finalAnswer, results);
    }

    private String buildSystemPrompt(User currentUser, JsonNode contextNode, String runtimeFacts) {
        Set<String> roles = new HashSet<>();
        if (currentUser.getAuthorities() != null) {
            currentUser.getAuthorities().forEach(a -> roles.add(a.getName()));
        }
        return """
        Bạn là AI Assistant cho hệ thống quản lý cuộc họp.
        Nhiệm vụ:
        - Trả lời tự nhiên, linh hoạt theo ngữ cảnh; ưu tiên ngắn gọn nhưng đủ ý.
        - Hiểu context UI hiện tại (route, meetingId, modal, selectedItems, user).
        - Chỉ đề xuất action nằm trong danh sách cho phép.
        - KHÔNG tự bịa dữ liệu hoặc tự xác nhận hành động rủi ro.
        - Các thao tác thay đổi dữ liệu (create/update/delete/add/remove participant) phải yêu cầu confirm rõ ràng.
        - Nếu user hỏi dạng "cuộc họp của tôi hôm nay/sắp tới", hãy chủ động dùng action getUserMeetings.

        Danh sách action cho phép:
        - createMeeting
        - updateMeeting
        - deleteMeeting
        - addParticipant
        - removeParticipant
        - getUserMeetings
        - openModal
        - navigateToPage

        User hiện tại:
        - id: %s
        - login: %s
        - roles: %s
        - now: %s

        Context UI hiện tại (JSON):
        %s

        Runtime facts từ backend:
        %s

        Ưu tiên trả JSON hợp lệ (không markdown) theo schema:
        {
          "answer": "string",
          "actions": [
            {
              "name": "createMeeting|updateMeeting|deleteMeeting|addParticipant|removeParticipant|getUserMeetings|openModal|navigateToPage",
              "parameters": { "any": "json" },
              "reason": "string"
            }
          ]
        }
        Nếu không thể trả JSON thì trả text tự nhiên bình thường.
        """.formatted(
                currentUser.getId(),
                currentUser.getLogin(),
                String.join(",", roles),
                Instant.now(),
                contextNode.toPrettyString(),
                runtimeFacts
            );
    }

    private String buildRuntimeFacts(JsonNode contextNode, User currentUser) {
        Map<String, Object> facts = new LinkedHashMap<>();
        facts.put("currentUser", buildCurrentUserFacts(currentUser));

        List<Map<String, Object>> userMeetings = buildUserMeetingsGraph();
        facts.put("userMeetings", userMeetings);
        facts.put("userMeetingsCount", userMeetings.size());

        Long meetingId = extractMeetingId(contextNode);
        if (meetingId != null) {
            meetingRepository
                .findOneWithToOneRelationships(meetingId)
                .ifPresent(m -> {
                    Map<String, Object> meeting = new LinkedHashMap<>();
                    meeting.put("id", m.getId());
                    meeting.put("title", m.getTitle());
                    meeting.put("startTime", m.getStartTime());
                    meeting.put("endTime", m.getEndTime());
                    meeting.put("status", m.getStatus() != null ? m.getStatus().name() : "");
                    meeting.put("host", m.getHost() != null ? m.getHost().getLogin() : "");
                    meeting.put("secretary", m.getSecretary() != null ? m.getSecretary().getLogin() : "");
                    meeting.put("department", m.getOrganizerDepartment() != null ? m.getOrganizerDepartment().getName() : "");
                    meeting.put("objectives", m.getObjectives());
                    meeting.put("note", m.getNote());
                    meeting.put("participantsCount", meetingParticipantRepository.countByMeetingId(m.getId()));
                    meeting.put("tasksCount", meetingTaskRepository.countByMeetingId(m.getId()));
                    meeting.put("documentsCount", meetingDocumentRepository.countByMeetingId(m.getId()));
                    meeting.put("incidentsCount", incidentRepository.countByMeetingId(m.getId()));
                    facts.put("currentMeeting", meeting);
                });
        }
        facts.put("serverNow", Instant.now().toString());
        try {
            return objectMapper.writeValueAsString(facts);
        } catch (Exception e) {
            return "{}";
        }
    }

    private Map<String, Object> buildCurrentUserFacts(User currentUser) {
        Map<String, Object> u = new LinkedHashMap<>();
        u.put("id", currentUser.getId());
        u.put("login", currentUser.getLogin());
        u.put("firstName", currentUser.getFirstName());
        u.put("lastName", currentUser.getLastName());
        u.put("email", currentUser.getEmail());
        if (currentUser.getDepartment() != null) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("id", currentUser.getDepartment().getId());
            d.put("code", currentUser.getDepartment().getCode());
            d.put("name", currentUser.getDepartment().getName());
            u.put("department", d);
        }
        List<String> roles = new ArrayList<>();
        if (currentUser.getAuthorities() != null) {
            currentUser
                .getAuthorities()
                .forEach(a -> {
                    if (a != null && a.getName() != null) roles.add(a.getName());
                });
        }
        u.put("roles", roles);
        return u;
    }

    private List<Map<String, Object>> buildUserMeetingsGraph() {
        Set<Long> ids = new LinkedHashSet<>();
        List<Meeting> all = new ArrayList<>();

        List<Meeting> requester = meetingRepository.findByRequesterIsCurrentUser();
        requester.forEach(m -> {
            if (m != null && m.getId() != null && ids.add(m.getId())) all.add(m);
        });
        List<Meeting> host = meetingRepository.findByHostIsCurrentUser();
        host.forEach(m -> {
            if (m != null && m.getId() != null && ids.add(m.getId())) all.add(m);
        });
        List<Meeting> secretary = meetingRepository.findBySecretaryIsCurrentUser();
        secretary.forEach(m -> {
            if (m != null && m.getId() != null && ids.add(m.getId())) all.add(m);
        });

        List<MeetingParticipant> asParticipant = meetingParticipantRepository.findByCurrentUserWithMeeting();
        asParticipant.forEach(p -> {
            Meeting m = p != null ? p.getMeeting() : null;
            if (m != null && m.getId() != null && ids.add(m.getId())) all.add(m);
        });

        all.sort(
            Comparator.comparing((Meeting m) -> m.getStartTime() != null ? m.getStartTime() : Instant.EPOCH, Comparator.reverseOrder())
        );
        List<Map<String, Object>> graph = new ArrayList<>();
        for (Meeting m : all) {
            if (m == null || m.getId() == null) continue;
            Meeting full = meetingRepository.findOneWithToOneRelationships(m.getId()).orElse(m);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", full.getId());
            row.put("title", full.getTitle());
            row.put("startTime", full.getStartTime());
            row.put("endTime", full.getEndTime());
            row.put("status", full.getStatus() != null ? full.getStatus().name() : "");
            row.put("department", full.getOrganizerDepartment() != null ? full.getOrganizerDepartment().getName() : "");
            row.put("host", full.getHost() != null ? full.getHost().getLogin() : "");
            row.put("requester", full.getRequester() != null ? full.getRequester().getLogin() : "");
            row.put("secretary", full.getSecretary() != null ? full.getSecretary().getLogin() : "");
            row.put("participantsCount", meetingParticipantRepository.countByMeetingId(full.getId()));
            row.put("tasksCount", meetingTaskRepository.countByMeetingId(full.getId()));
            row.put("documentsCount", meetingDocumentRepository.countByMeetingId(full.getId()));
            row.put("incidentsCount", incidentRepository.countByMeetingId(full.getId()));
            graph.add(row);
            if (graph.size() >= 40) break;
        }
        return graph;
    }

    private Long extractMeetingId(JsonNode contextNode) {
        if (contextNode == null || contextNode.isMissingNode()) return null;
        if (contextNode.hasNonNull("meetingId")) {
            long id = contextNode.path("meetingId").asLong(-1);
            if (id > 0) return id;
        }
        String route = contextNode.path("route").asText("");
        if (route != null && !route.isBlank()) {
            String digits = route.replaceAll(".*(?:meeting|meetings)/(?:edit/)?(\\d+).*", "$1");
            if (digits.matches("\\d+")) return Long.valueOf(digits);
            String byQuery = route.replaceAll(".*[?&]meetingId=(\\d+).*", "$1");
            if (byQuery.matches("\\d+")) return Long.valueOf(byQuery);
        }
        return null;
    }

    private ParsedAssistantOutput parseAssistantOutput(String content) {
        try {
            JsonNode root = objectMapper.readTree(content == null ? "{}" : content);
            String answer = root.path("answer").asText("");
            List<AiAssistantPlannedActionDTO> actions = new ArrayList<>();
            if (root.path("actions").isArray()) {
                for (JsonNode n : root.path("actions")) {
                    String name = n.path("name").asText("");
                    if (name.isBlank()) continue;
                    JsonNode parameters = n.path("parameters");
                    String reason = n.path("reason").asText("");
                    actions.add(
                        new AiAssistantPlannedActionDTO(
                            name,
                            parameters.isMissingNode() ? objectMapper.createObjectNode() : parameters,
                            reason
                        )
                    );
                    if (actions.size() >= 6) break;
                }
            }
            return new ParsedAssistantOutput(answer, actions);
        } catch (Exception e) {
            return new ParsedAssistantOutput(content == null ? "" : content, List.of());
        }
    }

    private ParsedAssistantOutput autoPlanIfNeeded(String userMessage, ParsedAssistantOutput out) {
        if (out == null) return new ParsedAssistantOutput("", List.of());
        if (out.actions() != null && !out.actions().isEmpty()) return out;
        String q = userMessage != null ? userMessage.toLowerCase() : "";
        if (
            q.contains("tạo cuộc họp") ||
            q.contains("tao cuoc hop") ||
            q.contains("đặt lịch họp") ||
            q.contains("dat lich hop") ||
            q.contains("create meeting")
        ) {
            List<AiAssistantPlannedActionDTO> plans = new ArrayList<>();
            plans.add(new AiAssistantPlannedActionDTO("createMeeting", objectMapper.createObjectNode(), "Start meeting creation flow."));
            return new ParsedAssistantOutput(out.answer(), plans);
        }
        if (q.contains("cuộc họp đầu tiên") || q.contains("hop dau tien") || q.contains("dau tien") || q.contains("first meeting")) {
            // Ask client to open the first meeting shown in current UI list snapshot.
            List<AiAssistantPlannedActionDTO> plans = new ArrayList<>();
            // placeholder, actual id/path will be filled by executeActions using context snapshot
            plans.add(
                new AiAssistantPlannedActionDTO(
                    "openFirstMeetingFromContext",
                    objectMapper.createObjectNode(),
                    "Open first meeting from current list context."
                )
            );
            return new ParsedAssistantOutput(out.answer(), plans);
        }
        if (
            q.contains("cuộc họp của tôi") ||
            q.contains("hop cua toi") ||
            q.contains("hôm nay") ||
            q.contains("hom nay") ||
            q.contains("sắp tới") ||
            q.contains("sap toi")
        ) {
            return new ParsedAssistantOutput(
                out.answer(),
                List.of(
                    new AiAssistantPlannedActionDTO(
                        "getUserMeetings",
                        objectMapper.valueToTree(Map.of("scope", q.contains("hôm nay") || q.contains("hom nay") ? "today" : "upcoming")),
                        "Auto enrich meetings list."
                    )
                )
            );
        }
        return out;
    }

    private List<AiAssistantActionResultDTO> executeActions(
        List<AiAssistantPlannedActionDTO> actions,
        User currentUser,
        JsonNode contextNode
    ) {
        List<AiAssistantActionResultDTO> out = new ArrayList<>();
        for (AiAssistantPlannedActionDTO a : actions) {
            String name = a.name() == null ? "" : a.name().trim();
            JsonNode params = a.parameters() == null ? objectMapper.createObjectNode() : a.parameters();
            switch (name) {
                case "createMeeting" -> out.add(handleCreateMeetingAction(params, currentUser));
                case "openFirstMeetingFromContext" -> out.add(handleOpenFirstMeetingFromContext(contextNode));
                case "getUserMeetings" -> out.add(handleGetUserMeetings(params));
                case "navigateToPage", "openModal" -> out.add(
                    new AiAssistantActionResultDTO(name, "client_action", false, "Client should execute this UI action.", params)
                );
                case "updateMeeting", "deleteMeeting", "addParticipant", "removeParticipant" -> out.add(
                    new AiAssistantActionResultDTO(name, "needs_confirmation", true, "Action requires explicit user confirmation.", params)
                );
                default -> out.add(new AiAssistantActionResultDTO(name, "rejected", false, "Unsupported action.", params));
            }
        }
        return out;
    }

    private AiAssistantActionResultDTO handleCreateMeetingAction(JsonNode params, User currentUser) {
        Map<String, Object> draft = new LinkedHashMap<>();
        draft.put("title", params.path("title").asText(""));
        String mappedType = mapMeetingType(params.path("meetingType").asText(""));
        String mappedLevel = mapMeetingLevel(params.path("meetingLevel").asText(""));
        draft.put("meetingType", isBlank(mappedType) ? "offline" : mappedType);
        draft.put("meetingLevel", isBlank(mappedLevel) ? "department" : mappedLevel);
        draft.put("startTime", params.path("startTime").asText(""));
        draft.put("endTime", params.path("endTime").asText(""));
        draft.put("meetingLink", params.path("meetingLink").asText(""));
        draft.put("selectedRoomId", safeLong(params.path("selectedRoomId"), null));
        draft.put("requesterId", safeLong(params.path("requesterId"), currentUser.getId()));
        draft.put("hostId", safeLong(params.path("hostId"), currentUser.getId()));
        draft.put("secretaryId", safeLong(params.path("secretaryId"), null));
        draft.put(
            "organizerDepartmentId",
            safeLong(params.path("organizerDepartmentId"), currentUser.getDepartment() != null ? currentUser.getDepartment().getId() : null)
        );
        draft.put("description", params.path("description").asText(""));
        boolean confirmed = params.path("confirmed").asBoolean(false);

        List<String> missing = collectMissingFields(draft);
        Map<String, Object> options = createMeetingOptions();
        if (!missing.isEmpty() || !confirmed) {
            String message = !missing.isEmpty()
                ? "Thiếu thông tin tạo cuộc họp: " + String.join(", ", missing) + ". Vui lòng chọn/bổ sung rồi xác nhận."
                : "Bạn vui lòng xác nhận tạo cuộc họp với thông tin đã cung cấp.";
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("draft", draft);
            payload.put("missingFields", missing);
            payload.put("options", options);
            return new AiAssistantActionResultDTO("createMeeting", "needs_confirmation", true, message, objectMapper.valueToTree(payload));
        }

        List<String> conflicts = precheckMeetingConflicts(draft);
        if (!conflicts.isEmpty()) {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("draft", draft);
            payload.put("conflicts", conflicts);
            payload.put("options", options);
            return new AiAssistantActionResultDTO(
                "createMeeting",
                "rejected",
                false,
                "Không thể tạo họp do xung đột: " + String.join("; ", conflicts),
                objectMapper.valueToTree(payload)
            );
        }

        // Let frontend create via existing robust form API.
        return new AiAssistantActionResultDTO(
            "createMeetingFromAi",
            "client_action",
            false,
            "Đã đủ thông tin và qua precheck. Tiến hành tạo cuộc họp.",
            objectMapper.valueToTree(draft)
        );
    }

    private List<String> collectMissingFields(Map<String, Object> draft) {
        List<String> missing = new ArrayList<>();
        if (isBlank((String) draft.get("title"))) missing.add("title");
        if (isBlank((String) draft.get("startTime"))) missing.add("startTime");
        if (isBlank((String) draft.get("endTime"))) missing.add("endTime");
        // For AI quick-create, we allow draft creation with minimal required fields.
        // Other fields can be edited later before submit.
        return missing;
    }

    private List<String> precheckMeetingConflicts(Map<String, Object> draft) {
        List<String> conflicts = new ArrayList<>();
        Instant start = parseInstantFlexible((String) draft.get("startTime"));
        Instant end = parseInstantFlexible((String) draft.get("endTime"));
        if (start == null || end == null || !start.isBefore(end)) {
            conflicts.add("Thời gian bắt đầu/kết thúc không hợp lệ");
            return conflicts;
        }
        Long roomId = (Long) draft.get("selectedRoomId");
        Long hostId = (Long) draft.get("hostId");
        List<MeetingStatus> active = List.of(MeetingStatus.PENDING_APPROVAL, MeetingStatus.APPROVED);
        if (roomId != null) {
            long c = meetingRepository.countRoomConflicts(roomId, start, end, active, null);
            if (c > 0) conflicts.add("Phòng họp bị trùng lịch");
        }
        if (hostId != null) {
            long c = meetingRepository.countHostConflicts(hostId, start, end, active, null);
            if (c > 0) conflicts.add("Chủ trì bị trùng lịch");
        }
        return conflicts;
    }

    private Map<String, Object> createMeetingOptions() {
        Map<String, Object> options = new LinkedHashMap<>();
        options.put("meetingTypeOptions", List.of("offline", "online", "hybrid"));
        options.put("meetingLevelOptions", List.of("company", "department"));
        options.put("confirmHint", "Để tạo, gửi confirmed=true cùng thông tin đã đủ.");
        return options;
    }

    private String mapMeetingType(String raw) {
        String v = raw == null ? "" : raw.trim().toLowerCase();
        if (v.contains("online")) return "online";
        if (v.contains("hybrid")) return "hybrid";
        if (v.contains("offline") || v.contains("in_person") || v.contains("inperson")) return "offline";
        return v;
    }

    private String mapMeetingLevel(String raw) {
        String v = raw == null ? "" : raw.trim().toLowerCase();
        if (v.contains("company") || v.contains("corporate")) return "company";
        if (v.contains("department") || v.contains("team")) return "department";
        return v;
    }

    private Long safeLong(JsonNode n, Long defaultValue) {
        if (n == null || n.isMissingNode() || n.isNull()) return defaultValue;
        if (n.isNumber()) return n.asLong();
        String s = n.asText("").trim();
        if (s.isEmpty()) return defaultValue;
        try {
            return Long.valueOf(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private Instant parseInstantFlexible(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Instant.parse(s.trim());
        } catch (Exception e) {
            try {
                // Accept local datetime format (e.g. 2026-03-27T09:00)
                LocalDateTime dt = LocalDateTime.parse(s.trim());
                return dt.atZone(ZoneId.systemDefault()).toInstant();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private AiAssistantActionResultDTO handleOpenFirstMeetingFromContext(JsonNode contextNode) {
        JsonNode selectedItems = contextNode.path("selectedItems");
        JsonNode list = selectedItems.path("meetingListSnapshot");
        if (!list.isArray() || list.isEmpty()) {
            return new AiAssistantActionResultDTO(
                "openFirstMeetingFromContext",
                "rejected",
                false,
                "No meeting list snapshot found in current UI context.",
                objectMapper.createObjectNode()
            );
        }
        JsonNode first = list.get(0);
        long meetingId = first.path("id").asLong(-1);
        if (meetingId <= 0) {
            return new AiAssistantActionResultDTO(
                "openFirstMeetingFromContext",
                "rejected",
                false,
                "First meeting id is invalid in context.",
                objectMapper.createObjectNode()
            );
        }
        String activeTab = selectedItems.path("activeTab").asText("approved");
        String path = "/plans?tab=" + activeTab + "&meetingId=" + meetingId;
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("path", path);
        payload.put("meetingId", meetingId);
        payload.put("meetingTitle", first.path("title").asText(""));
        return new AiAssistantActionResultDTO(
            "navigateToPage",
            "client_action",
            false,
            "Navigate to the first meeting on current list.",
            objectMapper.valueToTree(payload)
        );
    }

    private AiAssistantActionResultDTO handleGetUserMeetings(JsonNode params) {
        String scope = params.path("scope").asText("upcoming").toLowerCase();
        List<Meeting> mine = new ArrayList<>();
        mine.addAll(meetingRepository.findByRequesterIsCurrentUser());
        mine.addAll(meetingRepository.findByHostIsCurrentUser());

        LocalDate today = LocalDate.now();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Meeting m : mine) {
            if (m == null || m.getId() == null || m.getStartTime() == null) continue;
            LocalDate d = m.getStartTime().atZone(ZoneId.systemDefault()).toLocalDate();
            if ("today".equals(scope) && !d.equals(today)) continue;
            if ("upcoming".equals(scope) && m.getStartTime().isBefore(Instant.now())) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", m.getId());
            row.put("title", m.getTitle());
            row.put("startTime", m.getStartTime());
            row.put("endTime", m.getEndTime());
            row.put("status", m.getStatus() != null ? m.getStatus().name() : "");
            rows.add(row);
            if (rows.size() >= 20) break;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("scope", scope);
        payload.put("meetings", rows);
        return new AiAssistantActionResultDTO(
            "getUserMeetings",
            "executed",
            false,
            "Fetched meetings for current user.",
            objectMapper.valueToTree(payload)
        );
    }

    private String mergeAnswerWithExecution(String answer, List<AiAssistantActionResultDTO> results) {
        if (results == null || results.isEmpty()) return answer != null ? answer : "";
        StringBuilder sb = new StringBuilder(answer != null ? answer.trim() : "");
        for (AiAssistantActionResultDTO r : results) {
            if (!"executed".equals(r.status())) continue;
            if ("getUserMeetings".equals(r.name())) {
                JsonNode meetings = r.payload() != null ? r.payload().path("meetings") : null;
                if (meetings != null && meetings.isArray()) {
                    sb.append("\n\nCuộc họp liên quan:\n");
                    for (JsonNode m : meetings) {
                        sb
                            .append("- #")
                            .append(m.path("id").asText(""))
                            .append(" ")
                            .append(m.path("title").asText(""))
                            .append(" (")
                            .append(m.path("startTime").asText(""))
                            .append(")\n");
                    }
                }
            }
        }
        return sb.toString().trim();
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
        return c0.message.content.trim();
    }

    private record ParsedAssistantOutput(String answer, List<AiAssistantPlannedActionDTO> actions) {}

    public record AiAssistantRequest(
        String message,
        JsonNode context,
        Long userId,
        String conversationId,
        List<AiAssistantChatMessageDTO> recentMessages
    ) {}

    public record AiAssistantChatMessageDTO(String role, String content) {}

    public record AiAssistantPlannedActionDTO(String name, JsonNode parameters, String reason) {}

    public record AiAssistantActionResultDTO(String name, String status, boolean requiresConfirmation, String message, JsonNode payload) {}

    public record AiAssistantResponse(String conversationId, String answer, List<AiAssistantActionResultDTO> actions) {}

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
