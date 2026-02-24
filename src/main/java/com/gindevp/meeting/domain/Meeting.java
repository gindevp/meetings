package com.gindevp.meeting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.meeting.domain.enumeration.MeetingMode;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Meeting.
 */
@Entity
@Table(name = "meeting")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Meeting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private MeetingMode mode;

    @Column(name = "online_link")
    private String onlineLink;

    @Lob
    @Column(name = "objectives")
    private String objectives;

    @Lob
    @Column(name = "note")
    private String note;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MeetingStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "canceled_at")
    private Instant canceledAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meeting")
    @JsonIgnoreProperties(value = { "meeting" }, allowSetters = true)
    private Set<AgendaItem> agendaItems = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meeting")
    @JsonIgnoreProperties(value = { "user", "meeting" }, allowSetters = true)
    private Set<MeetingParticipant> participants = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meeting")
    @JsonIgnoreProperties(value = { "assignee", "assignedBy", "meeting" }, allowSetters = true)
    private Set<MeetingTask> tasks = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meeting")
    @JsonIgnoreProperties(value = { "decidedBy", "meeting" }, allowSetters = true)
    private Set<MeetingApproval> approvals = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meeting")
    @JsonIgnoreProperties(value = { "uploadedBy", "meeting" }, allowSetters = true)
    private Set<MeetingDocument> documents = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meeting")
    @JsonIgnoreProperties(value = { "reportedBy", "meeting" }, allowSetters = true)
    private Set<Incident> incidents = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private MeetingType type;

    @ManyToOne(optional = false)
    @NotNull
    private MeetingLevel level;

    @ManyToOne(optional = false)
    @NotNull
    private Department organizerDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(optional = false)
    @NotNull
    private User requester;

    @ManyToOne(optional = false)
    @NotNull
    private User host;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Meeting id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Meeting title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Meeting startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Meeting endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public MeetingMode getMode() {
        return this.mode;
    }

    public Meeting mode(MeetingMode mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(MeetingMode mode) {
        this.mode = mode;
    }

    public String getOnlineLink() {
        return this.onlineLink;
    }

    public Meeting onlineLink(String onlineLink) {
        this.setOnlineLink(onlineLink);
        return this;
    }

    public void setOnlineLink(String onlineLink) {
        this.onlineLink = onlineLink;
    }

    public String getObjectives() {
        return this.objectives;
    }

    public Meeting objectives(String objectives) {
        this.setObjectives(objectives);
        return this;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getNote() {
        return this.note;
    }

    public Meeting note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public MeetingStatus getStatus() {
        return this.status;
    }

    public Meeting status(MeetingStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Meeting createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public Meeting submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getApprovedAt() {
        return this.approvedAt;
    }

    public Meeting approvedAt(Instant approvedAt) {
        this.setApprovedAt(approvedAt);
        return this;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Instant getCanceledAt() {
        return this.canceledAt;
    }

    public Meeting canceledAt(Instant canceledAt) {
        this.setCanceledAt(canceledAt);
        return this;
    }

    public void setCanceledAt(Instant canceledAt) {
        this.canceledAt = canceledAt;
    }

    public Set<AgendaItem> getAgendaItems() {
        return this.agendaItems;
    }

    public void setAgendaItems(Set<AgendaItem> agendaItems) {
        if (this.agendaItems != null) {
            this.agendaItems.forEach(i -> i.setMeeting(null));
        }
        if (agendaItems != null) {
            agendaItems.forEach(i -> i.setMeeting(this));
        }
        this.agendaItems = agendaItems;
    }

    public Meeting agendaItems(Set<AgendaItem> agendaItems) {
        this.setAgendaItems(agendaItems);
        return this;
    }

    public Meeting addAgendaItems(AgendaItem agendaItem) {
        this.agendaItems.add(agendaItem);
        agendaItem.setMeeting(this);
        return this;
    }

    public Meeting removeAgendaItems(AgendaItem agendaItem) {
        this.agendaItems.remove(agendaItem);
        agendaItem.setMeeting(null);
        return this;
    }

    public Set<MeetingParticipant> getParticipants() {
        return this.participants;
    }

    public void setParticipants(Set<MeetingParticipant> meetingParticipants) {
        if (this.participants != null) {
            this.participants.forEach(i -> i.setMeeting(null));
        }
        if (meetingParticipants != null) {
            meetingParticipants.forEach(i -> i.setMeeting(this));
        }
        this.participants = meetingParticipants;
    }

    public Meeting participants(Set<MeetingParticipant> meetingParticipants) {
        this.setParticipants(meetingParticipants);
        return this;
    }

    public Meeting addParticipants(MeetingParticipant meetingParticipant) {
        this.participants.add(meetingParticipant);
        meetingParticipant.setMeeting(this);
        return this;
    }

    public Meeting removeParticipants(MeetingParticipant meetingParticipant) {
        this.participants.remove(meetingParticipant);
        meetingParticipant.setMeeting(null);
        return this;
    }

    public Set<MeetingTask> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<MeetingTask> meetingTasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setMeeting(null));
        }
        if (meetingTasks != null) {
            meetingTasks.forEach(i -> i.setMeeting(this));
        }
        this.tasks = meetingTasks;
    }

    public Meeting tasks(Set<MeetingTask> meetingTasks) {
        this.setTasks(meetingTasks);
        return this;
    }

    public Meeting addTasks(MeetingTask meetingTask) {
        this.tasks.add(meetingTask);
        meetingTask.setMeeting(this);
        return this;
    }

    public Meeting removeTasks(MeetingTask meetingTask) {
        this.tasks.remove(meetingTask);
        meetingTask.setMeeting(null);
        return this;
    }

    public Set<MeetingApproval> getApprovals() {
        return this.approvals;
    }

    public void setApprovals(Set<MeetingApproval> meetingApprovals) {
        if (this.approvals != null) {
            this.approvals.forEach(i -> i.setMeeting(null));
        }
        if (meetingApprovals != null) {
            meetingApprovals.forEach(i -> i.setMeeting(this));
        }
        this.approvals = meetingApprovals;
    }

    public Meeting approvals(Set<MeetingApproval> meetingApprovals) {
        this.setApprovals(meetingApprovals);
        return this;
    }

    public Meeting addApprovals(MeetingApproval meetingApproval) {
        this.approvals.add(meetingApproval);
        meetingApproval.setMeeting(this);
        return this;
    }

    public Meeting removeApprovals(MeetingApproval meetingApproval) {
        this.approvals.remove(meetingApproval);
        meetingApproval.setMeeting(null);
        return this;
    }

    public Set<MeetingDocument> getDocuments() {
        return this.documents;
    }

    public void setDocuments(Set<MeetingDocument> meetingDocuments) {
        if (this.documents != null) {
            this.documents.forEach(i -> i.setMeeting(null));
        }
        if (meetingDocuments != null) {
            meetingDocuments.forEach(i -> i.setMeeting(this));
        }
        this.documents = meetingDocuments;
    }

    public Meeting documents(Set<MeetingDocument> meetingDocuments) {
        this.setDocuments(meetingDocuments);
        return this;
    }

    public Meeting addDocuments(MeetingDocument meetingDocument) {
        this.documents.add(meetingDocument);
        meetingDocument.setMeeting(this);
        return this;
    }

    public Meeting removeDocuments(MeetingDocument meetingDocument) {
        this.documents.remove(meetingDocument);
        meetingDocument.setMeeting(null);
        return this;
    }

    public Set<Incident> getIncidents() {
        return this.incidents;
    }

    public void setIncidents(Set<Incident> incidents) {
        if (this.incidents != null) {
            this.incidents.forEach(i -> i.setMeeting(null));
        }
        if (incidents != null) {
            incidents.forEach(i -> i.setMeeting(this));
        }
        this.incidents = incidents;
    }

    public Meeting incidents(Set<Incident> incidents) {
        this.setIncidents(incidents);
        return this;
    }

    public Meeting addIncidents(Incident incident) {
        this.incidents.add(incident);
        incident.setMeeting(this);
        return this;
    }

    public Meeting removeIncidents(Incident incident) {
        this.incidents.remove(incident);
        incident.setMeeting(null);
        return this;
    }

    public MeetingType getType() {
        return this.type;
    }

    public void setType(MeetingType meetingType) {
        this.type = meetingType;
    }

    public Meeting type(MeetingType meetingType) {
        this.setType(meetingType);
        return this;
    }

    public MeetingLevel getLevel() {
        return this.level;
    }

    public void setLevel(MeetingLevel meetingLevel) {
        this.level = meetingLevel;
    }

    public Meeting level(MeetingLevel meetingLevel) {
        this.setLevel(meetingLevel);
        return this;
    }

    public Department getOrganizerDepartment() {
        return this.organizerDepartment;
    }

    public void setOrganizerDepartment(Department department) {
        this.organizerDepartment = department;
    }

    public Meeting organizerDepartment(Department department) {
        this.setOrganizerDepartment(department);
        return this;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Meeting room(Room room) {
        this.setRoom(room);
        return this;
    }

    public User getRequester() {
        return this.requester;
    }

    public void setRequester(User user) {
        this.requester = user;
    }

    public Meeting requester(User user) {
        this.setRequester(user);
        return this;
    }

    public User getHost() {
        return this.host;
    }

    public void setHost(User user) {
        this.host = user;
    }

    public Meeting host(User user) {
        this.setHost(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Meeting)) {
            return false;
        }
        return getId() != null && getId().equals(((Meeting) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Meeting{" +
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
            "}";
    }
}
