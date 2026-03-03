package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.security.SecurityUtils;
import com.gindevp.meeting.service.AgendaItemService;
import com.gindevp.meeting.service.MeetingParticipantService;
import com.gindevp.meeting.service.MeetingService;
import com.gindevp.meeting.service.MeetingWorkflowService;
import com.gindevp.meeting.service.dto.AgendaItemDTO;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.MeetingParticipantDTO;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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

    public MeetingResource(
        MeetingService meetingService,
        UserRepository userRepository,
        MeetingRepository meetingRepository,
        MeetingWorkflowService meetingWorkflowService,
        MeetingParticipantService meetingParticipantService,
        AgendaItemService agendaItemService
    ) {
        this.meetingService = meetingService;
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.meetingWorkflowService = meetingWorkflowService;
        this.meetingParticipantService = meetingParticipantService;
        this.agendaItemService = agendaItemService;
    }

    /**
     * {@code POST  /meetings} : Create a new meeting.
     *
     * @param meetingDTO the meetingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meetingDTO, or with status {@code 400 (Bad Request)} if the meeting has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    // DTO for creating meeting with participants and agenda
    public record CreateMeetingRequest(MeetingDTO meeting, List<ParticipantRequest> participants, List<AgendaRequest> agendaItems) {}

    public record ParticipantRequest(Long userId, String role, Boolean isRequired) {}

    public record AgendaRequest(String topic, String presenterName, Integer durationMinutes, Integer itemOrder) {}

    /**
     * {@code POST  /meetings/with-details} : Create a new meeting with participants and agenda.
     */
    @PostMapping("/with-details")
    public ResponseEntity<MeetingDTO> createMeetingWithDetails(@RequestBody CreateMeetingRequest request) throws URISyntaxException {
        LOG.debug("REST request to save Meeting with participants and agenda");

        // Save meeting first
        MeetingDTO meetingDTO = request.meeting();
        if (meetingDTO.getId() != null) {
            throw new BadRequestAlertException("A new meeting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingDTO = meetingService.save(meetingDTO);
        Long meetingId = meetingDTO.getId();

        // Save participants
        if (request.participants() != null) {
            for (ParticipantRequest p : request.participants()) {
                MeetingParticipantDTO participantDTO = new MeetingParticipantDTO();
                participantDTO.setRole(com.gindevp.meeting.domain.enumeration.ParticipantRole.ATTENDEE);
                participantDTO.setIsRequired(p.isRequired() != null ? p.isRequired() : true);
                participantDTO.setAttendance(com.gindevp.meeting.domain.enumeration.AttendanceStatus.NOT_MARKED);

                com.gindevp.meeting.service.dto.UserDTO userDTO = new com.gindevp.meeting.service.dto.UserDTO();
                userDTO.setId(p.userId());
                participantDTO.setUser(userDTO);

                participantDTO.setMeeting(meetingDTO);
                meetingParticipantService.save(participantDTO);
            }
        }

        // Save agenda items
        if (request.agendaItems() != null) {
            for (AgendaRequest a : request.agendaItems()) {
                AgendaItemDTO agendaDTO = new AgendaItemDTO();
                agendaDTO.setTopic(a.topic());
                agendaDTO.setPresenterName(a.presenterName());
                agendaDTO.setDurationMinutes(a.durationMinutes());
                agendaDTO.setItemOrder(a.itemOrder());
                agendaDTO.setMeeting(meetingDTO);
                agendaItemService.save(agendaDTO);
            }
        }

        // Reload meeting with relationships
        meetingDTO = meetingService.findOne(meetingId).orElse(meetingDTO);

        return ResponseEntity.created(new URI("/api/meetings/" + meetingId))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingId.toString()))
            .body(meetingDTO);
    }

    /**
     * {@code PUT  /meetings/:id} : Updates an existing meeting.
     *
     * @param id the id of the meetingDTO to save.
     * @param meetingDTO the meetingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingDTO,
     * or with status {@code 400 (Bad Request)} if the meetingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meetingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code PATCH  /meetings/:id} : Partial updates given fields of an existing meeting, field will ignore if it is null
     *
     * @param id the id of the meetingDTO to save.
     * @param meetingDTO the meetingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingDTO,
     * or with status {@code 400 (Bad Request)} if the meetingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the meetingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the meetingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code GET  /meetings} : get all the meetings.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meetings in body.
     */
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

    /**
     * {@code GET  /meetings/:id} : get the "id" meeting.
     *
     * @param id the id of the meetingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meetingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingDTO> getMeeting(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Meeting : {}", id);
        Optional<MeetingDTO> meetingDTO = meetingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingDTO);
    }

    /**
     * {@code DELETE  /meetings/:id} : delete the "id" meeting.
     *
     * @param id the id of the meetingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
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

    public record RejectRequest(String reason) {}

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOM_MANAGER','ROLE_UNIT_MANAGER')")
    public ResponseEntity<MeetingDTO> reject(@PathVariable Long id, @RequestBody RejectRequest req) {
        User approver = currentUser();
        return ResponseEntity.ok(meetingWorkflowService.reject(id, req.reason(), approver));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<MeetingDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(meetingWorkflowService.cancel(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<MeetingDTO> complete(@PathVariable Long id) {
        return ResponseEntity.ok(meetingWorkflowService.complete(id));
    }

    private User currentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("No current user"));
        return userRepository.findOneByLogin(login).orElseThrow(() -> new RuntimeException("User not found: " + login));
    }
}
