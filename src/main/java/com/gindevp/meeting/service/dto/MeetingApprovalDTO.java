package com.gindevp.meeting.service.dto;

import com.gindevp.meeting.domain.enumeration.ApprovalDecision;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.meeting.domain.MeetingApproval} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingApprovalDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer step;

    @NotNull
    private ApprovalDecision decision;

    private String reason;

    @NotNull
    private Instant decidedAt;

    @NotNull
    private UserDTO decidedBy;

    @NotNull
    private MeetingDTO meeting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public ApprovalDecision getDecision() {
        return decision;
    }

    public void setDecision(ApprovalDecision decision) {
        this.decision = decision;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public UserDTO getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(UserDTO decidedBy) {
        this.decidedBy = decidedBy;
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
        if (!(o instanceof MeetingApprovalDTO)) {
            return false;
        }

        MeetingApprovalDTO meetingApprovalDTO = (MeetingApprovalDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, meetingApprovalDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingApprovalDTO{" +
            "id=" + getId() +
            ", step=" + getStep() +
            ", decision='" + getDecision() + "'" +
            ", reason='" + getReason() + "'" +
            ", decidedAt='" + getDecidedAt() + "'" +
            ", decidedBy=" + getDecidedBy() +
            ", meeting=" + getMeeting() +
            "}";
    }
}
