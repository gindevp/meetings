package com.gindevp.meeting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * Cấu hình: user (user_id != null) hoặc hệ thống (user_id = null).
 */
@Entity
@Table(
    name = "setting",
    indexes = {
        @Index(name = "idx_setting_user_key", columnList = "user_id, setting_key"),
        @Index(name = "idx_setting_category", columnList = "category"),
    }
)
public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Size(max = 50)
    @Column(name = "category", length = 50, nullable = false)
    private String category; // USER, SYSTEM

    @NotNull
    @Size(max = 255)
    @Column(name = "setting_key", nullable = false)
    private String key;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String value;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(o instanceof Setting)) return false;
        return id != null && id.equals(((Setting) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
