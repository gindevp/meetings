package com.gindevp.meeting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.meeting.domain.enumeration.AttendanceStatus;
import com.gindevp.meeting.domain.enumeration.ParticipantRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A MeetingParticipant.
 */
@Entity
@Table(name = "meeting_participant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingParticipant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ParticipantRole role;

    @NotNull
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance", nullable = false)
    private AttendanceStatus attendance;

    @Column(name = "absent_reason")
    private String absentReason;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

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

    public MeetingParticipant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParticipantRole getRole() {
        return this.role;
    }

    public MeetingParticipant role(ParticipantRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(ParticipantRole role) {
        this.role = role;
    }

    public Boolean getIsRequired() {
        return this.isRequired;
    }

    public MeetingParticipant isRequired(Boolean isRequired) {
        this.setIsRequired(isRequired);
        return this;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public AttendanceStatus getAttendance() {
        return this.attendance;
    }

    public MeetingParticipant attendance(AttendanceStatus attendance) {
        this.setAttendance(attendance);
        return this;
    }

    public void setAttendance(AttendanceStatus attendance) {
        this.attendance = attendance;
    }

    public String getAbsentReason() {
        return this.absentReason;
    }

    public MeetingParticipant absentReason(String absentReason) {
        this.setAbsentReason(absentReason);
        return this;
    }

    public void setAbsentReason(String absentReason) {
        this.absentReason = absentReason;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MeetingParticipant user(User user) {
        this.setUser(user);
        return this;
    }

    public Meeting getMeeting() {
        return this.meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public MeetingParticipant meeting(Meeting meeting) {
        this.setMeeting(meeting);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeetingParticipant)) {
            return false;
        }
        return getId() != null && getId().equals(((MeetingParticipant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingParticipant{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", isRequired='" + getIsRequired() + "'" +
            ", attendance='" + getAttendance() + "'" +
            ", absentReason='" + getAbsentReason() + "'" +
            "}";
    }
}
