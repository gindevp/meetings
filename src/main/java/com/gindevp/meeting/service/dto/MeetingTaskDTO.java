package com.gindevp.meeting.service.dto;

import com.gindevp.meeting.domain.enumeration.TaskStatus;
import com.gindevp.meeting.domain.enumeration.TaskType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.meeting.domain.MeetingTask} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingTaskDTO implements Serializable {

    private Long id;

    @NotNull
    private TaskType type;

    @NotNull
    private String title;

    @Lob
    private String description;

    private Instant dueAt;

    @NotNull
    private TaskStatus status;

    private Integer remindBeforeMinutes;

    private UserDTO assignee;

    private UserDTO assignedBy;

    @NotNull
    private MeetingDTO meeting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getRemindBeforeMinutes() {
        return remindBeforeMinutes;
    }

    public void setRemindBeforeMinutes(Integer remindBeforeMinutes) {
        this.remindBeforeMinutes = remindBeforeMinutes;
    }

    public UserDTO getAssignee() {
        return assignee;
    }

    public void setAssignee(UserDTO assignee) {
        this.assignee = assignee;
    }

    public UserDTO getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(UserDTO assignedBy) {
        this.assignedBy = assignedBy;
    }

    public MeetingDTO getMeeting() {
        return meeting;
    }

    public void setMeeting(MeetingDTO meeting) {
        this.meeting = meeting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeetingTaskDTO)) {
            return false;
        }

        MeetingTaskDTO meetingTaskDTO = (MeetingTaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, meetingTaskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingTaskDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", dueAt='" + getDueAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", remindBeforeMinutes=" + getRemindBeforeMinutes() +
            ", assignee=" + getAssignee() +
            ", assignedBy=" + getAssignedBy() +
            ", meeting=" + getMeeting() +
            "}";
    }
}
