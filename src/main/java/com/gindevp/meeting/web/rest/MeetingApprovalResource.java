package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.MeetingApprovalRepository;
import com.gindevp.meeting.service.MeetingApprovalService;
import com.gindevp.meeting.service.dto.MeetingApprovalDTO;
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
 * REST controller for managing {@link com.gindevp.meeting.domain.MeetingApproval}.
 */
@RestController
@RequestMapping("/api/meeting-approvals")
public class MeetingApprovalResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingApprovalResource.class);

    private static final String ENTITY_NAME = "meetingApproval";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingApprovalService meetingApprovalService;

    private final MeetingApprovalRepository meetingApprovalRepository;

    public MeetingApprovalResource(MeetingApprovalService meetingApprovalService, MeetingApprovalRepository meetingApprovalRepository) {
        this.meetingApprovalService = meetingApprovalService;
        this.meetingApprovalRepository = meetingApprovalRepository;
    }

    /**
     * {@code POST  /meeting-approvals} : Create a new meetingApproval.
     *
     * @param meetingApprovalDTO the meetingApprovalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meetingApprovalDTO, or with status {@code 400 (Bad Request)} if the meetingApproval has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MeetingApprovalDTO> createMeetingApproval(@Valid @RequestBody MeetingApprovalDTO meetingApprovalDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MeetingApproval : {}", meetingApprovalDTO);
        if (meetingApprovalDTO.getId() != null) {
            throw new BadRequestAlertException("A new meetingApproval cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingApprovalDTO = meetingApprovalService.save(meetingApprovalDTO);
        return ResponseEntity.created(new URI("/api/meeting-approvals/" + meetingApprovalDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingApprovalDTO.getId().toString()))
            .body(meetingApprovalDTO);
    }

    /**
     * {@code PUT  /meeting-approvals/:id} : Updates an existing meetingApproval.
     *
     * @param id the id of the meetingApprovalDTO to save.
     * @param meetingApprovalDTO the meetingApprovalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingApprovalDTO,
     * or with status {@code 400 (Bad Request)} if the meetingApprovalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meetingApprovalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingApprovalDTO> updateMeetingApproval(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingApprovalDTO meetingApprovalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MeetingApproval : {}, {}", id, meetingApprovalDTO);
        if (meetingApprovalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingApprovalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingApprovalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        meetingApprovalDTO = meetingApprovalService.update(meetingApprovalDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingApprovalDTO.getId().toString()))
            .body(meetingApprovalDTO);
    }

    /**
     * {@code PATCH  /meeting-approvals/:id} : Partial updates given fields of an existing meetingApproval, field will ignore if it is null
     *
     * @param id the id of the meetingApprovalDTO to save.
     * @param meetingApprovalDTO the meetingApprovalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingApprovalDTO,
     * or with status {@code 400 (Bad Request)} if the meetingApprovalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the meetingApprovalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the meetingApprovalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingApprovalDTO> partialUpdateMeetingApproval(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingApprovalDTO meetingApprovalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MeetingApproval partially : {}, {}", id, meetingApprovalDTO);
        if (meetingApprovalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingApprovalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingApprovalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingApprovalDTO> result = meetingApprovalService.partialUpdate(meetingApprovalDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingApprovalDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /meeting-approvals} : get all the meetingApprovals.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meetingApprovals in body.
     */
    @GetMapping("")
    public List<MeetingApprovalDTO> getAllMeetingApprovals(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MeetingApprovals");
        return meetingApprovalService.findAll();
    }

    /**
     * {@code GET  /meeting-approvals/:id} : get the "id" meetingApproval.
     *
     * @param id the id of the meetingApprovalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meetingApprovalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingApprovalDTO> getMeetingApproval(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MeetingApproval : {}", id);
        Optional<MeetingApprovalDTO> meetingApprovalDTO = meetingApprovalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingApprovalDTO);
    }

    /**
     * {@code DELETE  /meeting-approvals/:id} : delete the "id" meetingApproval.
     *
     * @param id the id of the meetingApprovalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingApproval(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MeetingApproval : {}", id);
        meetingApprovalService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
