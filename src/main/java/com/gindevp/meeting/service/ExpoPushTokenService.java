package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.ExpoPushToken;
import com.gindevp.meeting.repository.ExpoPushTokenRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpoPushTokenService {

    private final ExpoPushTokenRepository expoPushTokenRepository;

    public ExpoPushTokenService(ExpoPushTokenRepository expoPushTokenRepository) {
        this.expoPushTokenRepository = expoPushTokenRepository;
    }

    /**
     * Đăng ký hoặc cập nhật token cho user. Nếu token đã tồn tại (của user khác hoặc cùng user) thì update user_id.
     */
    @Transactional
    public void registerToken(Long userId, String token) {
        if (userId == null || token == null || token.isBlank()) return;
        token = token.trim();
        if (!token.startsWith("ExponentPushToken[") && !token.startsWith("ExpoPushToken[")) {
            return;
        }
        List<ExpoPushToken> existing = expoPushTokenRepository.findByToken(token);
        if (!existing.isEmpty()) {
            ExpoPushToken t = existing.get(0);
            if (t.getUserId().equals(userId)) return;
            t.setUserId(userId);
            expoPushTokenRepository.save(t);
            return;
        }
        ExpoPushToken e = new ExpoPushToken();
        e.setUserId(userId);
        e.setToken(token);
        expoPushTokenRepository.save(e);
    }
}
