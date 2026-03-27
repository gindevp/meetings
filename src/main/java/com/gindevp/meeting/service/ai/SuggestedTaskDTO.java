package com.gindevp.meeting.service.ai;

import java.time.Instant;

public record SuggestedTaskDTO(String title, String description, Instant dueAt, Long assigneeId, Long departmentId) {}
