package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.ExpoPushToken;
import com.gindevp.meeting.repository.ExpoPushTokenRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Gửi push notification tới thiết bị qua Expo Push API.
 * @see <a href="https://docs.expo.dev/push-notifications/sending-notifications/">Expo Push API</a>
 */
@Service
public class ExpoPushSenderService {

    private static final Logger LOG = LoggerFactory.getLogger(ExpoPushSenderService.class);
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final ExpoPushTokenRepository expoPushTokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public ExpoPushSenderService(ExpoPushTokenRepository expoPushTokenRepository) {
        this.expoPushTokenRepository = expoPushTokenRepository;
    }

    /**
     * Gửi push tới tất cả token của user.
     * @param userId user id
     * @param title tiêu đề thông báo
     * @param body nội dung (có thể null, dùng title)
     * @param data data bổ sung (linkUrl, type, notificationId...)
     */
    public void sendToUser(Long userId, String title, String body, Map<String, Object> data) {
        List<ExpoPushToken> tokens = expoPushTokenRepository.findByUserId(userId);
        if (tokens.isEmpty()) return;
        String bodyText = body != null && !body.isBlank() ? body : title;
        for (ExpoPushToken t : tokens) {
            sendOne(t.getToken(), title, bodyText, data);
        }
    }

    @SuppressWarnings("unchecked")
    private void sendOne(String to, String title, String body, Map<String, Object> data) {
        try {
            Map<String, Object> message = new java.util.HashMap<>();
            message.put("to", to);
            message.put("title", title != null ? title : "");
            message.put("body", body != null ? body : "");
            message.put("sound", "default");
            if (data != null && !data.isEmpty()) {
                Map<String, String> dataStr = new java.util.HashMap<>();
                for (Map.Entry<String, Object> e : data.entrySet()) {
                    if (e.getValue() != null) dataStr.put(e.getKey(), String.valueOf(e.getValue()));
                }
                if (!dataStr.isEmpty()) message.put("data", dataStr);
            }

            List<Map<String, Object>> messages = List.of(message);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("messages", messages), headers);

            ResponseEntity<String> res = restTemplate.exchange(EXPO_PUSH_URL, HttpMethod.POST, entity, String.class);
            if (!res.getStatusCode().is2xxSuccessful()) {
                LOG.warn("Expo push failed for token {}: {}", to.substring(0, Math.min(30, to.length())) + "...", res.getStatusCode());
            }
        } catch (Exception e) {
            LOG.warn("Expo push error for user token: {}", e.getMessage());
        }
    }
}
