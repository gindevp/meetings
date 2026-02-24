package com.gindevp.meeting.service.dto;

import com.gindevp.meeting.domain.enumeration.AttendanceStatus;
import com.gindevp.meeting.domain.enumeration.ParticipantRole;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.meeting.domain.MeetingParticipant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingParticipantDTO implements Serializable {

    private Long id;

    @NotNull
    private ParticipantRole role;

    @NotNull
    private Boolean isRequired;

    @NotNull
    private AttendanceStatus attendance;

    private String absentReason;

    @NotNull
    private UserDTO user;

    @NotNull
    private MeetingDTO meeting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParticipantRole getRole() {
        return role;
    }

    public void setRole(ParticipantRole role) {
        this.role = role;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public AttendanceStatus getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceStatus attendance) {
        this.attendance = attendance;
    }

    public String getAbsentReason() {
        return absentReason;
    }

    public void setAbsentReason(String absentReason) {
        this.absentReason = absentReason;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
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
        if (!(o instanceof MeetingParticipantDTO)) {
            return false;
        }

        MeetingParticipantDTO meetingParticipantDTO = (MeetingParticipantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, meetingParticipantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingParticipantDTO{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", isRequired='" + getIsRequired() + "'" +
            ", attendance='" + getAttendance() + "'" +
            ", absentReason='" + getAbsentReason() + "'" +
            ", user=" + getUser() +
            ", meeting=" + getMeeting() +
            "}";
    }
}
