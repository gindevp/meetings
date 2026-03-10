package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.Authority;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingTask;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.repository.MeetingDocumentRepository;
import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.MeetingTaskRepository;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.security.SecurityUtils;
import com.gindevp.meeting.service.MeetingDocumentService;
import com.gindevp.meeting.service.dto.MeetingDocumentDTO;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gindevp.meeting.domain.MeetingDocument}.
 */
@RestController
@RequestMapping("/api/meeting-documents")
public class MeetingDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingDocumentResource.class);

    private static final String ENTITY_NAME = "meetingDocument";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingDocumentService meetingDocumentService;

    private final MeetingDocumentRepository meetingDocumentRepository;

    private final MeetingRepository meetingRepository;

    private final UserRepository userRepository;

    private final MeetingTaskRepository meetingTaskRepository;

    private final MeetingParticipantRepository meetingParticipantRepository;

    public MeetingDocumentResource(
        MeetingDocumentService meetingDocumentService,
        MeetingDocumentRepository meetingDocumentRepository,
        MeetingRepository meetingRepository,
        UserRepository userRepository,
        MeetingTaskRepository meetingTaskRepository,
        MeetingParticipantRepository meetingParticipantRepository
    ) {
        this.meetingDocumentService = meetingDocumentService;
        this.meetingDocumentRepository = meetingDocumentRepository;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.meetingTaskRepository = meetingTaskRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    /**
     * {@code POST  /meeting-documents} : Create a new meetingDocument.
     *
     * @param meetingDocumentDTO the meetingDocumentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meetingDocumentDTO, or with status {@code 400 (Bad Request)} if the meetingDocument has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MeetingDocumentDTO> createMeetingDocument(@Valid @RequestBody MeetingDocumentDTO meetingDocumentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MeetingDocument : {}", meetingDocumentDTO);
        if (meetingDocumentDTO.getId() != null) {
            throw new BadRequestAlertException("A new meetingDocument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ensureCanManageMeetingDocument(meetingDocumentDTO);
        meetingDocumentDTO = meetingDocumentService.save(meetingDocumentDTO);
        return ResponseEntity.created(new URI("/api/meeting-documents/" + meetingDocumentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingDocumentDTO.getId().toString()))
            .body(meetingDocumentDTO);
    }

    /**
     * {@code PUT  /meeting-documents/:id} : Updates an existing meetingDocument.
     *
     * @param id the id of the meetingDocumentDTO to save.
     * @param meetingDocumentDTO the meetingDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the meetingDocumentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meetingDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingDocumentDTO> updateMeetingDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingDocumentDTO meetingDocumentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MeetingDocument : {}, {}", id, meetingDocumentDTO);
        if (meetingDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ensureCanManageMeetingDocument(meetingDocumentDTO);

        meetingDocumentDTO = meetingDocumentService.update(meetingDocumentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingDocumentDTO.getId().toString()))
            .body(meetingDocumentDTO);
    }

    /**
     * {@code PATCH  /meeting-documents/:id} : Partial updates given fields of an existing meetingDocument, field will ignore if it is null
     *
     * @param id the id of the meetingDocumentDTO to save.
     * @param meetingDocumentDTO the meetingDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the meetingDocumentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the meetingDocumentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the meetingDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingDocumentDTO> partialUpdateMeetingDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingDocumentDTO meetingDocumentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MeetingDocument partially : {}, {}", id, meetingDocumentDTO);
        if (meetingDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingDocumentDTO> result = meetingDocumentService.partialUpdate(meetingDocumentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingDocumentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /meeting-documents} : get all the meetingDocuments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meetingDocuments in body.
     */
    @GetMapping("")
    public List<MeetingDocumentDTO> getAllMeetingDocuments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MeetingDocuments");
        return meetingDocumentService.findAll();
    }

    /**
     * {@code GET  /meeting-documents/:id} : get the "id" meetingDocument.
     *
     * @param id the id of the meetingDocumentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meetingDocumentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingDocumentDTO> getMeetingDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MeetingDocument : {}", id);
        Optional<MeetingDocumentDTO> meetingDocumentDTO = meetingDocumentService.findOne(id);
        return meetingDocumentDTO
            .map(doc -> {
                if (doc.getMeeting() != null && doc.getMeeting().getId() != null) {
                    ensureCanViewMeetingDocument(doc.getMeeting().getId());
                }
                return ResponseEntity.ok().body(doc);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /meeting-documents/:id/download} : download file of the meetingDocument.
     *
     * @param id the id of the meetingDocumentDTO.
     * @return the file content with Content-Disposition attachment.
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadMeetingDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to download MeetingDocument : {}", id);
        Optional<MeetingDocumentDTO> meetingDocumentDTO = meetingDocumentService.findOne(id);
        return meetingDocumentDTO
            .map(doc -> {
                if (doc.getMeeting() == null || doc.getMeeting().getId() == null) {
                    throw new BadRequestAlertException("Document has no meeting", ENTITY_NAME, "nomeeting");
                }
                ensureCanViewMeetingDocument(doc.getMeeting().getId());
                byte[] file = doc.getFile();
                if (file == null) {
                    return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body((byte[]) null);
                }
                String filename = doc.getFileName() != null ? doc.getFileName() : "document";
                String contentType = doc.getFileContentType() != null ? doc.getFileContentType() : "application/octet-stream";
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(file);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /meeting-documents/:id} : delete the "id" meetingDocument.
     *
     * @param id the id of the meetingDocumentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MeetingDocument : {}", id);
        MeetingDocumentDTO doc = meetingDocumentService
            .findOne(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        ensureCanDeleteMeetingDocument(doc);
        meetingDocumentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private void ensureCanDeleteMeetingDocument(MeetingDocumentDTO doc) {
        if (doc.getMeeting() == null || doc.getMeeting().getId() == null) {
            throw new BadRequestAlertException("Document has no meeting", ENTITY_NAME, "nomeeting");
        }
        User user = userRepository
            .findOneByLogin(
                SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"))
            )
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));

        Meeting meeting = meetingRepository
            .findOneWithToOneRelationships(doc.getMeeting().getId())
            .orElseThrow(() -> new BadRequestAlertException("Meeting not found", ENTITY_NAME, "meetingnotfound"));

        if (canManageMeeting(meeting, user)) {
            return;
        }
        if (doc.getUploadedBy() != null && doc.getUploadedBy().getId() != null && doc.getUploadedBy().getId().equals(user.getId())) {
            return;
        }
        if (doc.getTask() != null && doc.getTask().getId() != null) {
            Optional<MeetingTask> taskOpt = meetingTaskRepository.findOneWithToOneRelationships(doc.getTask().getId());
            if (taskOpt.map(MeetingTask::getAssignee).filter(a -> a != null && a.getId().equals(user.getId())).isPresent()) {
                return;
            }
        }
        throw new BadRequestAlertException(
            "Only requester, host, secretary, document uploader or task assignee can delete this document",
            ENTITY_NAME,
            "forbidden"
        );
    }

    private void ensureCanManageMeetingDocument(MeetingDocumentDTO dto) {
        if (dto.getMeeting() == null || dto.getMeeting().getId() == null) {
            return;
        }
        User user = userRepository
            .findOneByLogin(
                SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"))
            )
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));

        // Task assignee can upload document for their task
        if (dto.getTask() != null && dto.getTask().getId() != null) {
            Optional<MeetingTask> taskOpt = meetingTaskRepository.findOneWithToOneRelationships(dto.getTask().getId());
            if (taskOpt.map(MeetingTask::getAssignee).filter(a -> a != null && a.getId().equals(user.getId())).isPresent()) {
                return;
            }
        }

        Meeting meeting = meetingRepository
            .findOneWithToOneRelationships(dto.getMeeting().getId())
            .orElseThrow(() -> new BadRequestAlertException("Meeting not found", ENTITY_NAME, "meetingnotfound"));
        if (!canManageMeeting(meeting, user)) {
            throw new BadRequestAlertException(
                "Only requester, host, secretary or task assignee can upload documents",
                ENTITY_NAME,
                "forbidden"
            );
        }
    }

    private void ensureCanViewMeetingDocument(Long meetingId) {
        User user = userRepository
            .findOneWithAuthoritiesByLogin(
                SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"))
            )
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));
        Meeting meeting = meetingRepository
            .findOneWithToOneRelationships(meetingId)
            .orElseThrow(() -> new BadRequestAlertException("Meeting not found", ENTITY_NAME, "meetingnotfound"));
        boolean canView =
            canManageMeeting(meeting, user) ||
            meetingParticipantRepository.countByMeetingIdAndCurrentUser(meetingId) > 0 ||
            canViewAsDepartmentSecretary(meetingId, user);
        if (!canView) {
            throw new BadRequestAlertException("You do not have permission to view this document", ENTITY_NAME, "forbidden");
        }
    }

    private boolean canViewAsDepartmentSecretary(Long meetingId, User user) {
        if (user.getDepartment() == null || user.getDepartment().getId() == null) return false;
        boolean isSecretary = user.getAuthorities().stream().map(Authority::getName).anyMatch("ROLE_SECRETARY"::equals);
        if (!isSecretary) return false;
        return meetingParticipantRepository.countByMeetingIdAndDepartmentId(meetingId, user.getDepartment().getId()) > 0;
    }

    private boolean canManageMeeting(Meeting meeting, User user) {
        if (user.getAuthorities() != null && user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getName()))) return true;
        if (meeting.getRequester() != null && meeting.getRequester().getId().equals(user.getId())) return true;
        if (meeting.getHost() != null && meeting.getHost().getId().equals(user.getId())) return true;
        if (meeting.getSecretary() != null && meeting.getSecretary().getId().equals(user.getId())) return true;
        return false;
    }
}
