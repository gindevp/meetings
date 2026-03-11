package com.gindevp.meeting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * Expo push token đăng ký bởi user (app mobile) để nhận push notification.
 */
@Entity
@Table(
    name = "expo_push_token",
    indexes = {
        @Index(name = "idx_expo_push_token_user_id", columnList = "user_id"),
        @Index(name = "idx_expo_push_token_token", columnList = "token", unique = true),
    }
)
public class ExpoPushToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Size(max = 256)
    @Column(name = "token", nullable = false, unique = true, length = 256)
    private String token;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}
