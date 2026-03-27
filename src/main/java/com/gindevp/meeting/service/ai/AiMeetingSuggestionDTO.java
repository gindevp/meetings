package com.gindevp.meeting.service.ai;

import com.gindevp.meeting.domain.enumeration.AiSuggestionStatus;
import java.time.Instant;
import java.util.List;

public record AiMeetingSuggestionDTO(
    Long id,
    Long meetingId,
    AiSuggestionStatus status,
    String model,
    String promptVersion,
    String inputHash,
    String summary,
    String decisions,
    List<SuggestedTaskDTO> suggestedTasks,
    String createdBy,
    Instant createdDate
) {}
