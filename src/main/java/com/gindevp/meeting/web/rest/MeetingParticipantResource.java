package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.service.MeetingParticipantService;
import com.gindevp.meeting.service.dto.MeetingParticipantDTO;
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
 * REST controller for managing {@link com.gindevp.meeting.domain.MeetingParticipant}.
 */
@RestController
@RequestMapping("/api/meeting-participants")
public class MeetingParticipantResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingParticipantResource.class);

    private static final String ENTITY_NAME = "meetingParticipant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingParticipantService meetingParticipantService;

    private final MeetingParticipantRepository meetingParticipantRepository;

    public MeetingParticipantResource(
        MeetingParticipantService meetingParticipantService,
        MeetingParticipantRepository meetingParticipantRepository
    ) {
        this.meetingParticipantService = meetingParticipantService;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    /**
     * {@code POST  /meeting-participants} : Create a new meetingParticipant.
     *
     * @param meetingParticipantDTO the meetingParticipantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meetingParticipantDTO, or with status {@code 400 (Bad Request)} if the meetingParticipant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MeetingParticipantDTO> createMeetingParticipant(@Valid @RequestBody MeetingParticipantDTO meetingParticipantDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MeetingParticipant : {}", meetingParticipantDTO);
        if (meetingParticipantDTO.getId() != null) {
            throw new BadRequestAlertException("A new meetingParticipant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingParticipantDTO = meetingParticipantService.save(meetingParticipantDTO);
        return ResponseEntity.created(new URI("/api/meeting-participants/" + meetingParticipantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingParticipantDTO.getId().toString()))
            .body(meetingParticipantDTO);
    }

    /**
     * {@code PUT  /meeting-participants/:id} : Updates an existing meetingParticipant.
     *
     * @param id the id of the meetingParticipantDTO to save.
     * @param meetingParticipantDTO the meetingParticipantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingParticipantDTO,
     * or with status {@code 400 (Bad Request)} if the meetingParticipantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meetingParticipantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingParticipantDTO> updateMeetingParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingParticipantDTO meetingParticipantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MeetingParticipant : {}, {}", id, meetingParticipantDTO);
        if (meetingParticipantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingParticipantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingParticipantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        meetingParticipantDTO = meetingParticipantService.update(meetingParticipantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingParticipantDTO.getId().toString()))
            .body(meetingParticipantDTO);
    }

    /**
     * {@code PATCH  /meeting-participants/:id} : Partial updates given fields of an existing meetingParticipant, field will ignore if it is null
     *
     * @param id the id of the meetingParticipantDTO to save.
     * @param meetingParticipantDTO the meetingParticipantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingParticipantDTO,
     * or with status {@code 400 (Bad Request)} if the meetingParticipantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the meetingParticipantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the meetingParticipantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingParticipantDTO> partialUpdateMeetingParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingParticipantDTO meetingParticipantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MeetingParticipant partially : {}, {}", id, meetingParticipantDTO);
        if (meetingParticipantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingParticipantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingParticipantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingParticipantDTO> result = meetingParticipantService.partialUpdate(meetingParticipantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingParticipantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /meeting-participants} : get all the meetingParticipants.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meetingParticipants in body.
     */
    @GetMapping("")
    public List<MeetingParticipantDTO> getAllMeetingParticipants(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MeetingParticipants");
        return meetingParticipantService.findAll();
    }

    /**
     * {@code GET  /meeting-participants/:id} : get the "id" meetingParticipant.
     *
     * @param id the id of the meetingParticipantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meetingParticipantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingParticipantDTO> getMeetingParticipant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MeetingParticipant : {}", id);
        Optional<MeetingParticipantDTO> meetingParticipantDTO = meetingParticipantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingParticipantDTO);
    }

    /**
     * {@code DELETE  /meeting-participants/:id} : delete the "id" meetingParticipant.
     *
     * @param id the id of the meetingParticipantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingParticipant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MeetingParticipant : {}", id);
        meetingParticipantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
