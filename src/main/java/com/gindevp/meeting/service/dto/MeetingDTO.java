package com.gindevp.meeting.service.dto;

import com.gindevp.meeting.domain.enumeration.MeetingMode;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.meeting.domain.Meeting} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    @NotNull
    private MeetingMode mode;

    private String onlineLink;

    @Lob
    private String objectives;

    @Lob
    private String note;

    @NotNull
    private MeetingStatus status;

    @NotNull
    private Instant createdAt;

    private Instant submittedAt;

    private Instant approvedAt;

    private Instant canceledAt;

    @NotNull
    private MeetingTypeDTO type;

    @NotNull
    private MeetingLevelDTO level;

    @NotNull
    private DepartmentDTO organizerDepartment;

    private RoomDTO room;

    @NotNull
    private UserDTO requester;

    @NotNull
    private UserDTO host;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public MeetingMode getMode() {
        return mode;
    }

    public void setMode(MeetingMode mode) {
        this.mode = mode;
    }

    public String getOnlineLink() {
        return onlineLink;
    }

    public void setOnlineLink(String onlineLink) {
        this.onlineLink = onlineLink;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Instant getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(Instant canceledAt) {
        this.canceledAt = canceledAt;
    }

    public MeetingTypeDTO getType() {
        return type;
    }

    public void setType(MeetingTypeDTO type) {
        this.type = type;
    }

    public MeetingLevelDTO getLevel() {
        return level;
    }

    public void setLevel(MeetingLevelDTO level) {
        this.level = level;
    }

    public DepartmentDTO getOrganizerDepartment() {
        return organizerDepartment;
    }

    public void setOrganizerDepartment(DepartmentDTO organizerDepartment) {
        this.organizerDepartment = organizerDepartment;
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void setRoom(RoomDTO room) {
        this.room = room;
    }

    public UserDTO getRequester() {
        return requester;
    }

    public void setRequester(UserDTO requester) {
        this.requester = requester;
    }

    public UserDTO getHost() {
        return host;
    }

    public void setHost(UserDTO host) {
        this.host = host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeetingDTO)) {
            return false;
        }

        MeetingDTO meetingDTO = (MeetingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, meetingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", mode='" + getMode() + "'" +
            ", onlineLink='" + getOnlineLink() + "'" +
            ", objectives='" + getObjectives() + "'" +
            ", note='" + getNote() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", approvedAt='" + getApprovedAt() + "'" +
            ", canceledAt='" + getCanceledAt() + "'" +
            ", type=" + getType() +
            ", level=" + getLevel() +
            ", organizerDepartment=" + getOrganizerDepartment() +
            ", room=" + getRoom() +
            ", requester=" + getRequester() +
            ", host=" + getHost() +
            "}";
    }
}
