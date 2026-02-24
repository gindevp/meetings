package com.gindevp.meeting.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.meeting.domain.MeetingDocument} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingDocumentDTO implements Serializable {

    private Long id;

    @NotNull
    private String docType;

    @NotNull
    private String fileName;

    private String contentType;

    @Lob
    private byte[] file;

    private String fileContentType;

    @NotNull
    private Instant uploadedAt;

    @NotNull
    private UserDTO uploadedBy;

    @NotNull
    private MeetingDTO meeting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public UserDTO getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(UserDTO uploadedBy) {
        this.uploadedBy = uploadedBy;
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
        if (!(o instanceof MeetingDocumentDTO)) {
            return false;
        }

        MeetingDocumentDTO meetingDocumentDTO = (MeetingDocumentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, meetingDocumentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingDocumentDTO{" +
            "id=" + getId() +
            ", docType='" + getDocType() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", file='" + getFile() + "'" +
            ", uploadedAt='" + getUploadedAt() + "'" +
            ", uploadedBy=" + getUploadedBy() +
            ", meeting=" + getMeeting() +
            "}";
    }
}
