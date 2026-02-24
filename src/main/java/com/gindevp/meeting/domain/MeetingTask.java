package com.gindevp.meeting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.meeting.domain.enumeration.TaskStatus;
import com.gindevp.meeting.domain.enumeration.TaskType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MeetingTask.
 */
@Entity
@Table(name = "meeting_task")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TaskType type;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "due_at")
    private Instant dueAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Column(name = "remind_before_minutes")
    private Integer remindBeforeMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignedBy;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "agendaItems",
            "participants",
            "tasks",
            "approvals",
            "documents",
            "incidents",
            "type",
            "level",
            "organizerDepartment",
            "room",
            "requester",
            "host",
        },
        allowSetters = true
    )
    private Meeting meeting;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MeetingTask id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskType getType() {
        return this.type;
    }

    public MeetingTask type(TaskType type) {
        this.setType(type);
        return this;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public MeetingTask title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public MeetingTask description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDueAt() {
        return this.dueAt;
    }

    public MeetingTask dueAt(Instant dueAt) {
        this.setDueAt(dueAt);
        return this;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public MeetingTask status(TaskStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getRemindBeforeMinutes() {
        return this.remindBeforeMinutes;
    }

    public MeetingTask remindBeforeMinutes(Integer remindBeforeMinutes) {
        this.setRemindBeforeMinutes(remindBeforeMinutes);
        return this;
    }

    public void setRemindBeforeMinutes(Integer remindBeforeMinutes) {
        this.remindBeforeMinutes = remindBeforeMinutes;
    }

    public User getAssignee() {
        return this.assignee;
    }

    public void setAssignee(User user) {
        this.assignee = user;
    }

    public MeetingTask assignee(User user) {
        this.setAssignee(user);
        return this;
    }

    public User getAssignedBy() {
        return this.assignedBy;
    }

    public void setAssignedBy(User user) {
        this.assignedBy = user;
    }

    public MeetingTask assignedBy(User user) {
        this.setAssignedBy(user);
        return this;
    }

    public Meeting getMeeting() {
        return this.meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public MeetingTask meeting(Meeting meeting) {
        this.setMeeting(meeting);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeetingTask)) {
            return false;
        }
        return getId() != null && getId().equals(((MeetingTask) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingTask{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", dueAt='" + getDueAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", remindBeforeMinutes=" + getRemindBeforeMinutes() +
            "}";
    }
}
