package com.gindevp.meeting.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.meeting.domain.AgendaItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgendaItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer itemOrder;

    @NotNull
    private String topic;

    private String presenterName;

    private Integer durationMinutes;

    @Lob
    private String note;

    @NotNull
    private MeetingDTO meeting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPresenterName() {
        return presenterName;
    }

    public void setPresenterName(String presenterName) {
        this.presenterName = presenterName;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        if (!(o instanceof AgendaItemDTO)) {
            return false;
        }

        AgendaItemDTO agendaItemDTO = (AgendaItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, agendaItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgendaItemDTO{" +
            "id=" + getId() +
            ", itemOrder=" + getItemOrder() +
            ", topic='" + getTopic() + "'" +
            ", presenterName='" + getPresenterName() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", note='" + getNote() + "'" +
            ", meeting=" + getMeeting() +
            "}";
    }
}
