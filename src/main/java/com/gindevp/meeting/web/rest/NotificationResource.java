package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.security.SecurityUtils;
import com.gindevp.meeting.service.NotificationService;
import com.gindevp.meeting.service.dto.NotificationDTO;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for current user notifications.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationResource.class);
    private static final String ENTITY_NAME = "notification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationResource(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId()
            .or(() -> SecurityUtils.getCurrentUserLogin().flatMap(login -> userRepository.findOneByLogin(login).map(User::getId)))
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
    }

    /** GET /api/notifications : Danh sách thông báo của user (phân trang) */
    @GetMapping("")
    public Page<NotificationDTO> getNotifications(Pageable pageable) {
        Long userId = currentUserId();
        return notificationService.findByUserId(userId, pageable);
    }

    /** GET /api/notifications/unread-count : Số thông báo chưa đọc */
    @GetMapping("/unread-count")
    public long getUnreadCount() {
        Long userId = currentUserId();
        return notificationService.countUnreadByUserId(userId);
    }

    /** GET /api/notifications/:id : Chi tiết một thông báo */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable Long id) {
        Long userId = currentUserId();
        return ResponseUtil.wrapOrNotFound(notificationService.findOne(id).filter(dto -> dto.getUserId().equals(userId)));
    }

    /** POST /api/notifications : Tạo thông báo (nội bộ hoặc từ service khác gọi) */
    @PostMapping("")
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO dto) throws URISyntaxException {
        if (dto.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Long userId = dto.getUserId() != null ? dto.getUserId() : currentUserId();
        NotificationDTO result = notificationService.create(userId, dto.getTitle(), dto.getMessage(), dto.getType(), dto.getLinkUrl());
        return ResponseEntity.created(new URI("/api/notifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /** PATCH /api/notifications/:id/read : Đánh dấu đã đọc */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Long userId = currentUserId();
        notificationService.markAsRead(id, userId);
        return ResponseEntity.noContent().build();
    }

    /** POST /api/notifications/mark-all-read : Đánh dấu tất cả đã đọc */
    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead() {
        Long userId = currentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/notifications/:id : Xóa thông báo (chỉ của user hiện tại) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        Long userId = currentUserId();
        notificationService.delete(id, userId);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
