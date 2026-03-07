package com.gindevp.meeting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * Thông báo gửi đến user.
 */
@Entity
@Table(
    name = "notification",
    indexes = {
        @Index(name = "idx_notification_user_id", columnList = "user_id"),
        @Index(name = "idx_notification_read_created", columnList = "user_id, read_at, created_date"),
    }
)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Size(max = 500)
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "read_at")
    private Instant readAt; // null = chưa đọc

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type; // MEETING_INVITE, TASK_ASSIGNED, MEETING_APPROVED, etc.

    @Size(max = 500)
    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
