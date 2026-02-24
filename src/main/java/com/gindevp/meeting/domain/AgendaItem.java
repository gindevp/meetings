package com.gindevp.meeting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A AgendaItem.
 */
@Entity
@Table(name = "agenda_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgendaItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "item_order", nullable = false)
    private Integer itemOrder;

    @NotNull
    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "presenter_name")
    private String presenterName;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Lob
    @Column(name = "note")
    private String note;

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

    public AgendaItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItemOrder() {
        return this.itemOrder;
    }

    public AgendaItem itemOrder(Integer itemOrder) {
        this.setItemOrder(itemOrder);
        return this;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public String getTopic() {
        return this.topic;
    }

    public AgendaItem topic(String topic) {
        this.setTopic(topic);
        return this;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPresenterName() {
        return this.presenterName;
    }

    public AgendaItem presenterName(String presenterName) {
        this.setPresenterName(presenterName);
        return this;
    }

    public void setPresenterName(String presenterName) {
        this.presenterName = presenterName;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public AgendaItem durationMinutes(Integer durationMinutes) {
        this.setDurationMinutes(durationMinutes);
        return this;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNote() {
        return this.note;
    }

    public AgendaItem note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Meeting getMeeting() {
        return this.meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public AgendaItem meeting(Meeting meeting) {
        this.setMeeting(meeting);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgendaItem)) {
            return false;
        }
        return getId() != null && getId().equals(((AgendaItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgendaItem{" +
            "id=" + getId() +
            ", itemOrder=" + getItemOrder() +
            ", topic='" + getTopic() + "'" +
            ", presenterName='" + getPresenterName() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", note='" + getNote() + "'" +
            "}";
    }
}
