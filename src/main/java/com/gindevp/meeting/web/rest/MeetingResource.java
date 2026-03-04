package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.domain.enumeration.TaskStatus;
import com.gindevp.meeting.domain.enumeration.TaskType;
import com.gindevp.meeting.repository.AgendaItemRepository;
import com.gindevp.meeting.repository.MeetingDocumentRepository;
import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.MeetingTaskRepository;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.security.SecurityUtils;
import com.gindevp.meeting.service.AgendaItemService;
import com.gindevp.meeting.service.MeetingDocumentService;
import com.gindevp.meeting.service.MeetingParticipantService;
import com.gindevp.meeting.service.MeetingService;
import com.gindevp.meeting.service.MeetingTaskService;
import com.gindevp.meeting.service.MeetingWorkflowService;
import com.gindevp.meeting.service.dto.AgendaItemDTO;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.MeetingDocumentDTO;
import com.gindevp.meeting.service.dto.MeetingParticipantDTO;
import com.gindevp.meeting.service.dto.MeetingTaskDTO;
import com.gindevp.meeting.service.dto.UserDTO;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gindevp.meeting.domain.Meeting}.
 */
@RestController
@RequestMapping("/api/meetings")
public class MeetingResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingResource.class);

    private static final String ENTITY_NAME = "meeting";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingService meetingService;

    private final UserRepository userRepository;

    private final MeetingRepository meetingRepository;

    private final MeetingWorkflowService meetingWorkflowService;

    private final MeetingParticipantService meetingParticipantService;

    private final AgendaItemService agendaItemService;

    private final MeetingTaskService meetingTaskService;

    private final MeetingDocumentService meetingDocumentService;

    private final MeetingParticipantRepository meetingParticipantRepository;

    private final AgendaItemRepository agendaItemRepository;

    private final MeetingTaskRepository meetingTaskRepository;

    private final MeetingDocumentRepository meetingDocumentRepository;

    public MeetingResource(
        MeetingService meetingService,
        UserRepository userRepository,
        MeetingRepository meetingRepository,
        MeetingWorkflowService meetingWorkflowService,
        MeetingParticipantService meetingParticipantService,
        AgendaItemService agendaItemService,
        MeetingTaskService meetingTaskService,
        MeetingDocumentService meetingDocumentService,
        MeetingParticipantRepository meetingParticipantRepository,
        AgendaItemRepository agendaItemRepository,
        MeetingTaskRepository meetingTaskRepository,
        MeetingDocumentRepository meetingDocumentRepository
    ) {
        this.meetingService = meetingService;
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.meetingWorkflowService = meetingWorkflowService;
        this.meetingParticipantService = meetingParticipantService;
        this.agendaItemService = agendaItemService;
        this.meetingTaskService = meetingTaskService;
        this.meetingDocumentService = meetingDocumentService;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.meetingTaskRepository = meetingTaskRepository;
        this.meetingDocumentRepository = meetingDocumentRepository;
    }

    @PostMapping("")
    public ResponseEntity<MeetingDTO> createMeeting(@Valid @RequestBody MeetingDTO meetingDTO) throws URISyntaxException {
        LOG.debug("REST request to save Meeting : {}", meetingDTO);
        if (meetingDTO.getId() != null) {
            throw new BadRequestAlertException("A new meeting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingDTO = meetingService.save(meetingDTO);
        return ResponseEntity.created(new URI("/api/meetings/" + meetingDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingDTO.getId().toString()))
            .body(meetingDTO);
    }

    public record CreateMeetingRequest(
        MeetingDTO meeting,
        List<ParticipantRequest> participants,
        List<AgendaRequest> agendaItems,
        List<TaskRequest> tasks,
        List<DocumentRequest> documents,
        Boolean submitAfterCreate
    ) {}

    public record ParticipantRequest(Long userId, String role, Boolean isRequired) {}

    public record AgendaRequest(String topic, String presenterName, Integer durationMinutes, Integer itemOrder) {}

    public record TaskRequest(
        String clientKey,
        String type,
        String title,
        String description,
        Instant dueAt,
        String status,
        Integer remindBeforeMinutes,
        Long assigneeId,
        Long assignedById
    ) {}

    public record DocumentRequest(
        String docType,
        String fileName,
        String contentType,
        byte[] file,
        String fileContentType,
        Instant uploadedAt,
        Long uploadedById,
        Long taskId,
        String taskClientKey
    ) {}

    @PostMapping("/with-details")
    @Transactional
    public ResponseEntity<MeetingDTO> createMeetingWithDetails(@RequestBody CreateMeetingRequest request) throws URISyntaxException {
        LOG.debug("REST request to save Meeting with participants, agenda, tasks and documents");

        MeetingDTO meetingDTO = request.meeting();
        if (meetingDTO.getId() != null) {
            throw new BadRequestAlertException("A new meeting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingDTO = meetingService.save(meetingDTO);
        Long meetingId = meetingDTO.getId();

        saveDetails(meetingDTO, request.participants(), request.agendaItems(), request.tasks(), request.documents());

        if (Boolean.TRUE.equals(request.submitAfterCreate())) {
            meetingDTO = meetingWorkflowService.submit(meetingId);
        } else {
            meetingDTO = meetingService.findOne(meetingId).orElse(meetingDTO);
        }

        return ResponseEntity.created(new URI("/api/meetings/" + meetingId))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingId.toString()))
            .body(meetingDTO);
    }

    @PutMapping("/{id}/with-details")
    @Transactional
    public ResponseEntity<MeetingDTO> updateMeetingWithDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CreateMeetingRequest request
    ) throws URISyntaxException {
        LOG.debug("REST request to update Meeting with participants, agenda, tasks and documents : {}", id);

        MeetingDTO meetingDTO = request.meeting();
        if (meetingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!meetingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MeetingDTO existingMeeting = meetingService
            .findOne(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        if (meetingDTO.getStatus() == null) {
            meetingDTO.setStatus(existingMeeting.getStatus());
        }
        if (meetingDTO.getCreatedAt() == null) {
            meetingDTO.setCreatedAt(existingMeeting.getCreatedAt());
        }
        if (meetingDTO.getType() == null) {
            meetingDTO.setType(existingMeeting.getType());
        }
        if (meetingDTO.getLevel() == null) {
            meetingDTO.setLevel(existingMeeting.getLevel());
        }
        if (meetingDTO.getOrganizerDepartment() == null) {
            meetingDTO.setOrganizerDepartment(existingMeeting.getOrganizerDepartment());
        }
        if (meetingDTO.getRequester() == null) {
            meetingDTO.setRequester(existingMeeting.getRequester());
        }
        if (meetingDTO.getHost() == null) {
            meetingDTO.setHost(existingMeeting.getHost());
        }

        meetingDTO = meetingService.update(meetingDTO);

        meetingDocumentRepository.deleteByMeetingId(id);
        meetingTaskRepository.deleteByMeetingId(id);
        agendaItemRepository.deleteByMeetingId(id);
        meetingParticipantRepository.deleteByMeetingId(id);

        saveDetails(meetingDTO, request.participants(), request.agendaItems(), request.tasks(), request.documents());

        meetingDTO = meetingService.findOne(id).orElse(meetingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingDTO.getId().toString()))
            .body(meetingDTO);
    }

    private void saveDetails(
        MeetingDTO meetingDTO,
        List<ParticipantRequest> participants,
        List<AgendaRequest> agendaItems,
        List<TaskRequest> tasks,
        List<DocumentRequest> documents
    ) {
        if (participants != null) {
            for (ParticipantRequest p : participants) {
                MeetingParticipantDTO participantDTO = new MeetingParticipantDTO();
                participantDTO.setRole(com.gindevp.meeting.domain.enumeration.ParticipantRole.ATTENDEE);
                participantDTO.setIsRequired(p.isRequired() != null ? p.isRequired() : true);
                participantDTO.setAttendance(com.gindevp.meeting.domain.enumeration.AttendanceStatus.NOT_MARKED);

                UserDTO userDTO = new UserDTO();
                userDTO.setId(p.userId());
                participantDTO.setUser(userDTO);

                participantDTO.setMeeting(meetingDTO);
                meetingParticipantService.save(participantDTO);
            }
        }

        if (agendaItems != null) {
            for (AgendaRequest a : agendaItems) {
                AgendaItemDTO agendaDTO = new AgendaItemDTO();
                agendaDTO.setTopic(a.topic());
                agendaDTO.setPresenterName(a.presenterName());
                agendaDTO.setDurationMinutes(a.durationMinutes());
                agendaDTO.setItemOrder(a.itemOrder());
                agendaDTO.setMeeting(meetingDTO);
                agendaItemService.save(agendaDTO);
            }
        }

        Map<String, Long> taskClientKeyToId = new HashMap<>();
        if (tasks != null) {
            for (TaskRequest t : tasks) {
                MeetingTaskDTO taskDTO = new MeetingTaskDTO();
                taskDTO.setType(t.type() != null ? TaskType.valueOf(t.type()) : TaskType.PRE_MEETING);
                taskDTO.setTitle(t.title());
                taskDTO.setDescription(t.description());
                taskDTO.setDueAt(t.dueAt());
                taskDTO.setStatus(t.status() != null ? TaskStatus.valueOf(t.status()) : TaskStatus.TODO);
                taskDTO.setRemindBeforeMinutes(t.remindBeforeMinutes());
                taskDTO.setMeeting(meetingDTO);

                if (t.assigneeId() != null) {
                    UserDTO assignee = new UserDTO();
                    assignee.setId(t.assigneeId());
                    taskDTO.setAssignee(assignee);
                }

                Long assignedById = t.assignedById();
                if (assignedById == null) {
                    assignedById = meetingDTO.getRequester() != null ? meetingDTO.getRequester().getId() : null;
                }
                if (assignedById != null) {
                    UserDTO assignedBy = new UserDTO();
                    assignedBy.setId(assignedById);
                    taskDTO.setAssignedBy(assignedBy);
                }

                MeetingTaskDTO savedTask = meetingTaskService.save(taskDTO);
                if (t.clientKey() != null && !t.clientKey().isBlank()) {
                    taskClientKeyToId.put(t.clientKey(), savedTask.getId());
                }
            }
        }

        if (documents != null) {
            for (DocumentRequest d : documents) {
                MeetingDocumentDTO docDTO = new MeetingDocumentDTO();
                docDTO.setDocType(d.docType());
                docDTO.setFileName(d.fileName());
                docDTO.setContentType(d.contentType());
                docDTO.setFile(d.file());
                docDTO.setFileContentType(d.fileContentType());
                docDTO.setUploadedAt(d.uploadedAt() != null ? d.uploadedAt() : Instant.now());
                docDTO.setMeeting(meetingDTO);

                Long uploadedById = d.uploadedById();
                if (uploadedById == null) {
                    uploadedById = meetingDTO.getRequester() != null ? meetingDTO.getRequester().getId() : null;
                }
                if (uploadedById == null) {
                    throw new BadRequestAlertException("uploadedById is required", ENTITY_NAME, "uploadedbyrequired");
                }
                UserDTO uploadedBy = new UserDTO();
                uploadedBy.setId(uploadedById);
                docDTO.setUploadedBy(uploadedBy);

                Long resolvedTaskId = d.taskId();
                if (resolvedTaskId == null && d.taskClientKey() != null) {
                    resolvedTaskId = taskClientKeyToId.get(d.taskClientKey());
                }
                if (resolvedTaskId != null) {
                    MeetingTaskDTO taskRef = new MeetingTaskDTO();
                    taskRef.setId(resolvedTaskId);
                    docDTO.setTask(taskRef);
                }

                meetingDocumentService.save(docDTO);
            }
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingDTO> updateMeeting(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingDTO meetingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Meeting : {}, {}", id, meetingDTO);
        if (meetingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        meetingDTO = meetingService.update(meetingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingDTO.getId().toString()))
            .body(meetingDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingDTO> partialUpdateMeeting(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingDTO meetingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Meeting partially : {}, {}", id, meetingDTO);
        if (meetingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingDTO> result = meetingService.partialUpdate(meetingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<MeetingDTO>> getAllMeetings(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Meetings");
        Page<MeetingDTO> page;
        if (eagerload) {
            page = meetingService.findAllWithEagerRelationships(pageable);
        } else {
            page = meetingService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingDTO> getMeeting(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Meeting : {}", id);
        Optional<MeetingDTO> meetingDTO = meetingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Meeting : {}", id);
        meetingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<MeetingDTO> submit(@PathVariable Long id) {
        MeetingDTO dto = meetingWorkflowService.submit(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/approve-room")
    @PreAuthorize("hasAuthority('ROLE_ROOM_MANAGER')")
    public ResponseEntity<MeetingDTO> approveRoom(@PathVariable Long id) {
        User approver = currentUser();
        return ResponseEntity.ok(meetingWorkflowService.approveRoom(id, approver));
    }

    @PostMapping("/{id}/approve-unit")
    @PreAuthorize("hasAuthority('ROLE_UNIT_MANAGER')")
    public ResponseEntity<MeetingDTO> approveUnit(@PathVariable Long id) {
        User approver = currentUser();
        return ResponseEntity.ok(meetingWorkflowService.approveUnit(id, approver));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ROLE_ROOM_MANAGER') or hasAuthority('ROLE_UNIT_MANAGER')")
    public ResponseEntity<MeetingDTO> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "");
        User approver = currentUser();
        return ResponseEntity.ok(meetingWorkflowService.reject(id, reason, approver));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<MeetingDTO> cancel(@PathVariable Long id) {
        MeetingDTO dto = meetingWorkflowService.cancel(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<MeetingDTO> complete(@PathVariable Long id) {
        MeetingDTO dto = meetingWorkflowService.complete(id);
        return ResponseEntity.ok(dto);
    }

    private User currentUser() {
        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));
        return userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));
    }
}
