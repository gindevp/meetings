package com.gindevp.meeting.domain;

import com.gindevp.meeting.domain.enumeration.AiSuggestionStatus;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "ai_meeting_suggestion")
public class AiMeetingSuggestion extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AiSuggestionStatus status = AiSuggestionStatus.DRAFT;

    @Column(name = "model", length = 120)
    private String model;

    @Column(name = "prompt_version", length = 50)
    private String promptVersion;

    @Column(name = "input_hash", length = 64)
    private String inputHash;

    @Lob
    @Column(name = "summary")
    private String summary;

    @Lob
    @Column(name = "decisions")
    private String decisions;

    @Lob
    @Column(name = "suggested_tasks_json")
    private String suggestedTasksJson;

    @Override
    public Long getId() {
        return this.id;
    }

    public AiMeetingSuggestion id(Long id) {
        this.id = id;
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public AiSuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(AiSuggestionStatus status) {
        this.status = status;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public String getInputHash() {
        return inputHash;
    }

    public void setInputHash(String inputHash) {
        this.inputHash = inputHash;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDecisions() {
        return decisions;
    }

    public void setDecisions(String decisions) {
        this.decisions = decisions;
    }

    public String getSuggestedTasksJson() {
        return suggestedTasksJson;
    }

    public void setSuggestedTasksJson(String suggestedTasksJson) {
        this.suggestedTasksJson = suggestedTasksJson;
    }

    public Instant getCreatedAt() {
        return getCreatedDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AiMeetingSuggestion)) return false;
        return getId() != null && Objects.equals(getId(), ((AiMeetingSuggestion) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
