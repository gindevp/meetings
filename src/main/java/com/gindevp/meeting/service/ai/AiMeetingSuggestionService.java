package com.gindevp.meeting.service.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.meeting.domain.AiMeetingSuggestion;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.domain.enumeration.AiSuggestionStatus;
import com.gindevp.meeting.domain.enumeration.TaskStatus;
import com.gindevp.meeting.domain.enumeration.TaskType;
import com.gindevp.meeting.repository.AgendaItemRepository;
import com.gindevp.meeting.repository.AiMeetingSuggestionRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.service.MeetingTaskService;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.MeetingTaskDTO;
import com.gindevp.meeting.service.dto.UserDTO;
import com.gindevp.meeting.service.mapper.MeetingMapper;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AiMeetingSuggestionService {

    private static final Logger LOG = LoggerFactory.getLogger(AiMeetingSuggestionService.class);
    private static final String ENTITY_NAME = "aiMeetingSuggestion";
    private static final String PROMPT_VERSION_OPENAI = "phase2-openai-json-v1";

    private final AiMeetingSuggestionRepository aiMeetingSuggestionRepository;
    private final MeetingRepository meetingRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final MeetingMapper meetingMapper;
    private final MeetingTaskService meetingTaskService;
    private final ObjectMapper objectMapper;
    private final OpenAiProperties openAiProperties;
    private final OpenAiMeetingMinutesGenerator openAiGenerator;

    public AiMeetingSuggestionService(
        AiMeetingSuggestionRepository aiMeetingSuggestionRepository,
        MeetingRepository meetingRepository,
        AgendaItemRepository agendaItemRepository,
        MeetingMapper meetingMapper,
        MeetingTaskService meetingTaskService,
        ObjectMapper objectMapper,
        OpenAiProperties openAiProperties,
        OpenAiMeetingMinutesGenerator openAiGenerator
    ) {
        this.aiMeetingSuggestionRepository = aiMeetingSuggestionRepository;
        this.meetingRepository = meetingRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.meetingMapper = meetingMapper;
        this.meetingTaskService = meetingTaskService;
        this.objectMapper = objectMapper;
        this.openAiProperties = openAiProperties;
        this.openAiGenerator = openAiGenerator;
    }

    @Transactional(readOnly = true)
    public Optional<AiMeetingSuggestionDTO> getLatestDraft(Long meetingId) {
        return aiMeetingSuggestionRepository.findLatestDraftByMeetingId(meetingId).map(this::toDto);
    }

    public AiMeetingSuggestionDTO generateSuggestion(Long meetingId, User requestedBy, boolean forceRegenerate) {
        Optional<AiMeetingSuggestion> latestDraftOpt = aiMeetingSuggestionRepository.findLatestDraftByMeetingId(meetingId);
        if (!forceRegenerate && latestDraftOpt.isPresent()) {
            AiMeetingSuggestion s = latestDraftOpt.get();
            boolean draftIsOpenAi = s.getModel() != null && s.getModel().toLowerCase().startsWith("openai");
            if (draftIsOpenAi) {
                LOG.info(
                    "Returning cached AI suggestion {} for meeting {} (model={}, promptVersion={})",
                    s.getId(),
                    meetingId,
                    s.getModel(),
                    s.getPromptVersion()
                );
                return toDto(s);
            }
            LOG.info(
                "Ignoring cached AI suggestion {} for meeting {} because cachedModel={} (regenerating with OpenAI)",
                s.getId(),
                meetingId,
                s.getModel()
            );
        }

        Meeting meeting = meetingRepository
            .findOneWithToOneRelationships(meetingId)
            .orElseThrow(() -> new BadRequestAlertException("Meeting not found", ENTITY_NAME, "meetingnotfound"));

        String note = meeting.getNote();
        String objectives = meeting.getObjectives();
        String minutesText = (note != null && !note.trim().isBlank()) ? note : objectives;
        if (minutesText == null || minutesText.trim().isBlank()) {
            throw new BadRequestAlertException(
                "Meeting minutes are empty. Please add note/objectives before using AI suggestions.",
                ENTITY_NAME,
                "notenull"
            );
        }

        List<String> agendaTopics = agendaItemRepository
            .findByMeetingId(meetingId)
            .stream()
            .map(ai -> ai.getTopic() != null ? ai.getTopic().trim() : "")
            .filter(s -> !s.isBlank())
            .toList();

        if (openAiProperties == null || !openAiProperties.hasApiKey()) {
            throw new BadRequestAlertException(
                "OpenAI is required but OPENAI_API_KEY is missing. Please configure application.ai.openai.api-key or set OPENAI_API_KEY environment variable.",
                ENTITY_NAME,
                "openai_key_missing"
            );
        }
        LOG.info("Generating AI suggestion with OpenAI (hasOpenAiKey=true)");
        MinutesSuggestionResult result = openAiGenerator.generate(meeting.getTitle(), minutesText, agendaTopics);

        AiMeetingSuggestion entity = new AiMeetingSuggestion();
        entity.setMeeting(meeting);
        entity.setStatus(AiSuggestionStatus.DRAFT);
        entity.setModel(openAiGenerator.name());
        entity.setPromptVersion(PROMPT_VERSION_OPENAI);
        entity.setInputHash(result.inputHash());
        entity.setSummary(result.summary());
        entity.setDecisions(result.decisions());
        try {
            entity.setSuggestedTasksJson(objectMapper.writeValueAsString(result.tasks()));
        } catch (Exception e) {
            entity.setSuggestedTasksJson("[]");
        }

        entity = aiMeetingSuggestionRepository.save(entity);
        LOG.info("Generated AI suggestion {} for meeting {}", entity.getId(), meetingId);
        return toDto(entity);
    }

    public List<MeetingTaskDTO> applySuggestion(Long meetingId, Long suggestionId, User appliedBy) {
        AiMeetingSuggestion suggestion = aiMeetingSuggestionRepository
            .findById(suggestionId)
            .orElseThrow(() -> new BadRequestAlertException("Suggestion not found", ENTITY_NAME, "suggestionnotfound"));

        if (
            suggestion.getMeeting() == null || suggestion.getMeeting().getId() == null || !suggestion.getMeeting().getId().equals(meetingId)
        ) {
            throw new BadRequestAlertException("Suggestion does not belong to meeting", ENTITY_NAME, "meetingmismatch");
        }
        if (suggestion.getStatus() == AiSuggestionStatus.APPLIED) {
            throw new BadRequestAlertException("Suggestion already applied", ENTITY_NAME, "alreadyapplied");
        }

        List<SuggestedTaskDTO> tasks = parseTasks(suggestion.getSuggestedTasksJson());
        if (tasks.isEmpty()) {
            throw new BadRequestAlertException("No suggested tasks to apply", ENTITY_NAME, "notasks");
        }

        Meeting meeting = meetingRepository
            .findOneWithToOneRelationships(meetingId)
            .orElseThrow(() -> new BadRequestAlertException("Meeting not found", ENTITY_NAME, "meetingnotfound"));
        MeetingDTO meetingDTO = meetingMapper.toDto(meeting);

        List<MeetingTaskDTO> created = new ArrayList<>();
        for (SuggestedTaskDTO t : tasks) {
            if (t.title() == null || t.title().trim().isBlank()) continue;
            MeetingTaskDTO dto = new MeetingTaskDTO();
            dto.setMeeting(meetingDTO);
            dto.setType(TaskType.POST_MEETING);
            dto.setTitle(t.title().trim());
            dto.setDescription(t.description() != null ? t.description() : "");
            dto.setStatus(TaskStatus.TODO);
            dto.setDueAt(t.dueAt());

            if (appliedBy != null && appliedBy.getId() != null) {
                UserDTO assignedBy = new UserDTO();
                assignedBy.setId(appliedBy.getId());
                dto.setAssignedBy(assignedBy);
            }

            // Phase 1: keep assignee/department empty to avoid wrong auto-assignments.
            created.add(meetingTaskService.save(dto));
        }

        suggestion.setStatus(AiSuggestionStatus.APPLIED);
        suggestion.setLastModifiedDate(Instant.now());
        aiMeetingSuggestionRepository.save(suggestion);
        return created;
    }

    private List<SuggestedTaskDTO> parseTasks(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return objectMapper.readValue(json, new TypeReference<List<SuggestedTaskDTO>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private AiMeetingSuggestionDTO toDto(AiMeetingSuggestion s) {
        List<SuggestedTaskDTO> tasks = parseTasks(s.getSuggestedTasksJson());
        return new AiMeetingSuggestionDTO(
            s.getId(),
            s.getMeeting() != null ? s.getMeeting().getId() : null,
            s.getStatus(),
            s.getModel(),
            s.getPromptVersion(),
            s.getInputHash(),
            s.getSummary(),
            s.getDecisions(),
            tasks,
            s.getCreatedBy(),
            s.getCreatedDate()
        );
    }
}
