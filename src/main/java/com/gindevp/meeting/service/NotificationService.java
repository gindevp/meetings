package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Notification;
import com.gindevp.meeting.repository.NotificationRepository;
import com.gindevp.meeting.service.dto.NotificationDTO;
import com.gindevp.meeting.service.mapper.NotificationMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ExpoPushSenderService expoPushSenderService;

    public NotificationService(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        ExpoPushSenderService expoPushSenderService
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.expoPushSenderService = expoPushSenderService;
    }

    public NotificationDTO create(Long userId, String title, String message, String type, String linkUrl) {
        LOG.debug("Create notification for user {}: {}", userId, title);
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setLinkUrl(linkUrl);
        n.setCreatedDate(Instant.now());
        n = notificationRepository.save(n);
        NotificationDTO dto = notificationMapper.toDto(n);
        // Gửi push tới app mobile (Expo) nếu user đã đăng ký token
        try {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("notificationId", n.getId());
            if (type != null) data.put("type", type);
            if (linkUrl != null) data.put("linkUrl", linkUrl);
            expoPushSenderService.sendToUser(userId, title, message, data);
        } catch (Exception e) {
            LOG.debug("Expo push failed for user {}: {}", userId, e.getMessage());
        }
        return dto;
    }

    public NotificationDTO save(NotificationDTO dto) {
        Notification entity = notificationMapper.toEntity(dto);
        entity = notificationRepository.save(entity);
        return notificationMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<NotificationDTO> findByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable).map(notificationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public long countUnreadByUserId(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    public void markAsRead(Long id, Long userId) {
        notificationRepository
            .findById(id)
            .ifPresent(n -> {
                if (n.getUserId().equals(userId)) {
                    n.setReadAt(Instant.now());
                    notificationRepository.save(n);
                }
            });
    }

    public void markAllAsRead(Long userId) {
        java.util.List<Notification> unread = notificationRepository.findUnreadByUserId(userId);
        Instant now = Instant.now();
        for (Notification n : unread) {
            n.setReadAt(now);
        }
        notificationRepository.saveAll(unread);
    }

    public void delete(Long id, Long userId) {
        notificationRepository
            .findById(id)
            .ifPresent(n -> {
                if (n.getUserId().equals(userId)) {
                    notificationRepository.delete(n);
                }
            });
    }
}
