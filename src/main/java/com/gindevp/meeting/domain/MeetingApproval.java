package com.gindevp.meeting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.meeting.domain.enumeration.ApprovalDecision;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MeetingApproval.
 */
@Entity
@Table(name = "meeting_approval")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingApproval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "step", nullable = false)
    private Integer step;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false)
    private ApprovalDecision decision;

    @Column(name = "reason")
    private String reason;

    @NotNull
    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;

    @ManyToOne(optional = false)
    @NotNull
    private User decidedBy;

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

    public MeetingApproval id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStep() {
        return this.step;
    }

    public MeetingApproval step(Integer step) {
        this.setStep(step);
        return this;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public ApprovalDecision getDecision() {
        return this.decision;
    }

    public MeetingApproval decision(ApprovalDecision decision) {
        this.setDecision(decision);
        return this;
    }

    public void setDecision(ApprovalDecision decision) {
        this.decision = decision;
    }

    public String getReason() {
        return this.reason;
    }

    public MeetingApproval reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getDecidedAt() {
        return this.decidedAt;
    }

    public MeetingApproval decidedAt(Instant decidedAt) {
        this.setDecidedAt(decidedAt);
        return this;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public User getDecidedBy() {
        return this.decidedBy;
    }

    public void setDecidedBy(User user) {
        this.decidedBy = user;
    }

    public MeetingApproval decidedBy(User user) {
        this.setDecidedBy(user);
        return this;
    }

    public Meeting getMeeting() {
        return this.meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public MeetingApproval meeting(Meeting meeting) {
        this.setMeeting(meeting);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeetingApproval)) {
            return false;
        }
        return getId() != null && getId().equals(((MeetingApproval) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingApproval{" +
            "id=" + getId() +
            ", step=" + getStep() +
            ", decision='" + getDecision() + "'" +
            ", reason='" + getReason() + "'" +
            ", decidedAt='" + getDecidedAt() + "'" +
            "}";
    }
}
