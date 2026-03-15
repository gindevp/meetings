package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.enumeration.TaskStatus;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.MeetingTaskRepository;
import com.gindevp.meeting.service.MeetingTaskService;
import com.gindevp.meeting.service.dto.CreateMeetingTaskRequest;
import com.gindevp.meeting.service.dto.MeetingTaskDTO;
import com.gindevp.meeting.service.mapper.MeetingMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gindevp.meeting.domain.MeetingTask}.
 */
@RestController
@RequestMapping("/api/meeting-tasks")
public class MeetingTaskResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingTaskResource.class);

    private static final String ENTITY_NAME = "meetingTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingTaskService meetingTaskService;

    private final MeetingTaskRepository meetingTaskRepository;

    private final MeetingRepository meetingRepository;

    private final MeetingMapper meetingMapper;

    public MeetingTaskResource(
        MeetingTaskService meetingTaskService,
        MeetingTaskRepository meetingTaskRepository,
        MeetingRepository meetingRepository,
        MeetingMapper meetingMapper
    ) {
        this.meetingTaskService = meetingTaskService;
        this.meetingTaskRepository = meetingTaskRepository;
        this.meetingRepository = meetingRepository;
        this.meetingMapper = meetingMapper;
    }

    /**
     * {@code POST  /meeting-tasks} : Create a new meetingTask.
     * Accepts {@link CreateMeetingTaskRequest} with meetingId to avoid validating full MeetingDTO.
     *
     * @param request the create request (meetingId, type, title, etc.).
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meetingTaskDTO, or with status {@code 400 (Bad Request)} if the meeting is not found.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MeetingTaskDTO> createMeetingTask(@Valid @RequestBody CreateMeetingTaskRequest request)
        throws URISyntaxException {
        LOG.debug("REST request to save MeetingTask : {}", request);
        Meeting meeting = meetingRepository
            .findById(request.getMeetingId())
            .orElseThrow(() -> new BadRequestAlertException("Meeting not found", ENTITY_NAME, "meetingnotfound"));
        MeetingTaskDTO meetingTaskDTO = new MeetingTaskDTO();
        meetingTaskDTO.setMeeting(meetingMapper.toDto(meeting));
        meetingTaskDTO.setType(request.getType());
        meetingTaskDTO.setTitle(request.getTitle());
        meetingTaskDTO.setDescription(request.getDescription() != null ? request.getDescription() : "");
        meetingTaskDTO.setStatus(request.getStatus());
        meetingTaskDTO.setDueAt(request.getDueAt());
        meetingTaskDTO.setAssignee(request.getAssignee());
        meetingTaskDTO.setAssignedBy(request.getAssignedBy());
        meetingTaskDTO.setDepartment(request.getDepartment());
        meetingTaskDTO = meetingTaskService.save(meetingTaskDTO);
        return ResponseEntity.created(new URI("/api/meeting-tasks/" + meetingTaskDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingTaskDTO.getId().toString()))
            .body(meetingTaskDTO);
    }

    /**
     * {@code PUT  /meeting-tasks/:id} : Updates an existing meetingTask.
     *
     * @param id the id of the meetingTaskDTO to save.
     * @param meetingTaskDTO the meetingTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingTaskDTO,
     * or with status {@code 400 (Bad Request)} if the meetingTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meetingTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingTaskDTO> updateMeetingTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingTaskDTO meetingTaskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MeetingTask : {}, {}", id, meetingTaskDTO);
        if (meetingTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        meetingTaskDTO = meetingTaskService.update(meetingTaskDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingTaskDTO.getId().toString()))
            .body(meetingTaskDTO);
    }

    /**
     * {@code PATCH  /meeting-tasks/:id} : Partial updates given fields of an existing meetingTask, field will ignore if it is null
     *
     * @param id the id of the meetingTaskDTO to save.
     * @param meetingTaskDTO the meetingTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingTaskDTO,
     * or with status {@code 400 (Bad Request)} if the meetingTaskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the meetingTaskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the meetingTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingTaskDTO> partialUpdateMeetingTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingTaskDTO meetingTaskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MeetingTask partially : {}, {}", id, meetingTaskDTO);
        if (meetingTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingTaskDTO> result = meetingTaskService.partialUpdate(meetingTaskDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingTaskDTO.getId().toString())
        );
    }

    /**
     * {@code PATCH  /meeting-tasks/:id/status} : Update only the status of a meetingTask.
     */
    @PatchMapping(value = "/{id}/status", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingTaskDTO> updateMeetingTaskStatus(
        @PathVariable(value = "id") Long id,
        @RequestBody java.util.Map<String, String> body
    ) throws URISyntaxException {
        String statusStr = body != null ? body.get("status") : null;
        if (statusStr == null || statusStr.isBlank()) {
            throw new BadRequestAlertException("status is required", ENTITY_NAME, "statusnull");
        }
        TaskStatus status;
        try {
            status = TaskStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException("Invalid status", ENTITY_NAME, "invalidstatus");
        }
        MeetingTaskDTO dto = meetingTaskService
            .findOne(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        dto.setStatus(status);
        meetingTaskService.update(dto);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(meetingTaskService.findOne(id).orElseThrow());
    }

    /**
     * {@code GET  /meeting-tasks} : get all the meetingTasks.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meetingTasks in body.
     */
    @GetMapping("")
    public List<MeetingTaskDTO> getAllMeetingTasks(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MeetingTasks");
        return meetingTaskService.findAll();
    }

    /**
     * {@code GET  /meeting-tasks/:id} : get the "id" meetingTask.
     *
     * @param id the id of the meetingTaskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meetingTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingTaskDTO> getMeetingTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MeetingTask : {}", id);
        Optional<MeetingTaskDTO> meetingTaskDTO = meetingTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingTaskDTO);
    }

    /**
     * {@code DELETE  /meeting-tasks/:id} : delete the "id" meetingTask.
     *
     * @param id the id of the meetingTaskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MeetingTask : {}", id);
        meetingTaskService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
