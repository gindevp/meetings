package com.gindevp.meeting.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.meeting.service.dto.SettingDTO;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service to read user notification preferences from settings.
 */
@Service
public class UserNotificationSettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserNotificationSettingsService.class);
    private static final String KEY_NOTIFICATIONS = "settings.notifications";

    private final SettingService settingService;
    private final ObjectMapper objectMapper;

    public UserNotificationSettingsService(SettingService settingService, ObjectMapper objectMapper) {
        this.settingService = settingService;
        this.objectMapper = objectMapper;
    }

    /**
     * Check if user has email meetings notification enabled (default: true).
     */
    public boolean isEmailMeetingsEnabled(Long userId) {
        return getBool(userId, "emailMeetings", true);
    }

    /**
     * Check if user has meeting reminder enabled (default: true).
     */
    public boolean isReminderMeetingsEnabled(Long userId) {
        return getBool(userId, "reminderMeetings", true);
    }

    /**
     * Check if user has approval notification enabled (default: true).
     */
    public boolean isApprovalNotifEnabled(Long userId) {
        return getBool(userId, "approvalNotif", true);
    }

    /**
     * Check if user has task deadline reminder enabled (default: false).
     */
    public boolean isTaskDeadlineReminderEnabled(Long userId) {
        return getBool(userId, "taskDeadlineReminder", false);
    }

    /**
     * Check if user has weekly report enabled (default: false, admin only).
     */
    public boolean isWeeklyReportEnabled(Long userId) {
        return getBool(userId, "weeklyReport", false);
    }

    private boolean getBool(Long userId, String field, boolean defaultValue) {
        if (userId == null) return defaultValue;
        try {
            Optional<SettingDTO> opt = settingService.findByUserIdAndKey(userId, KEY_NOTIFICATIONS);
            return opt
                .map(SettingDTO::getValue)
                .filter(v -> v != null)
                .map(v -> {
                    try {
                        JsonNode node = objectMapper.readTree(v);
                        if (node == null || !node.has(field)) return defaultValue;
                        JsonNode val = node.get(field);
                        return val != null && val.asBoolean(defaultValue);
                    } catch (Exception ex) {
                        LOG.debug("Could not parse notification settings for user {}: {}", userId, ex.getMessage());
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
        } catch (Exception e) {
            LOG.debug("Could not parse notification settings for user {}: {}", userId, e.getMessage());
            return defaultValue;
        }
    }
}
