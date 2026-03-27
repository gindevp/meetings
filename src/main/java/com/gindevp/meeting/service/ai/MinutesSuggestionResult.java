package com.gindevp.meeting.service.ai;

import java.util.List;

public record MinutesSuggestionResult(String inputHash, String summary, String decisions, List<SuggestedTaskDTO> tasks) {}
