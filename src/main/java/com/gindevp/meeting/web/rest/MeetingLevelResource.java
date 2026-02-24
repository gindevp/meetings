package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.MeetingLevelRepository;
import com.gindevp.meeting.service.MeetingLevelService;
import com.gindevp.meeting.service.dto.MeetingLevelDTO;
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
 * REST controller for managing {@link com.gindevp.meeting.domain.MeetingLevel}.
 */
@RestController
@RequestMapping("/api/meeting-levels")
public class MeetingLevelResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingLevelResource.class);

    private static final String ENTITY_NAME = "meetingLevel";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingLevelService meetingLevelService;

    private final MeetingLevelRepository meetingLevelRepository;

    public MeetingLevelResource(MeetingLevelService meetingLevelService, MeetingLevelRepository meetingLevelRepository) {
        this.meetingLevelService = meetingLevelService;
        this.meetingLevelRepository = meetingLevelRepository;
    }

    /**
     * {@code POST  /meeting-levels} : Create a new meetingLevel.
     *
     * @param meetingLevelDTO the meetingLevelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meetingLevelDTO, or with status {@code 400 (Bad Request)} if the meetingLevel has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MeetingLevelDTO> createMeetingLevel(@Valid @RequestBody MeetingLevelDTO meetingLevelDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MeetingLevel : {}", meetingLevelDTO);
        if (meetingLevelDTO.getId() != null) {
            throw new BadRequestAlertException("A new meetingLevel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingLevelDTO = meetingLevelService.save(meetingLevelDTO);
        return ResponseEntity.created(new URI("/api/meeting-levels/" + meetingLevelDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingLevelDTO.getId().toString()))
            .body(meetingLevelDTO);
    }

    /**
     * {@code PUT  /meeting-levels/:id} : Updates an existing meetingLevel.
     *
     * @param id the id of the meetingLevelDTO to save.
     * @param meetingLevelDTO the meetingLevelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingLevelDTO,
     * or with status {@code 400 (Bad Request)} if the meetingLevelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meetingLevelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingLevelDTO> updateMeetingLevel(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingLevelDTO meetingLevelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MeetingLevel : {}, {}", id, meetingLevelDTO);
        if (meetingLevelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingLevelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingLevelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        meetingLevelDTO = meetingLevelService.update(meetingLevelDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingLevelDTO.getId().toString()))
            .body(meetingLevelDTO);
    }

    /**
     * {@code PATCH  /meeting-levels/:id} : Partial updates given fields of an existing meetingLevel, field will ignore if it is null
     *
     * @param id the id of the meetingLevelDTO to save.
     * @param meetingLevelDTO the meetingLevelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingLevelDTO,
     * or with status {@code 400 (Bad Request)} if the meetingLevelDTO is not valid,
     * or with status {@code 404 (Not Found)} if the meetingLevelDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the meetingLevelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingLevelDTO> partialUpdateMeetingLevel(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingLevelDTO meetingLevelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MeetingLevel partially : {}, {}", id, meetingLevelDTO);
        if (meetingLevelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingLevelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingLevelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingLevelDTO> result = meetingLevelService.partialUpdate(meetingLevelDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingLevelDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /meeting-levels} : get all the meetingLevels.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meetingLevels in body.
     */
    @GetMapping("")
    public List<MeetingLevelDTO> getAllMeetingLevels() {
        LOG.debug("REST request to get all MeetingLevels");
        return meetingLevelService.findAll();
    }

    /**
     * {@code GET  /meeting-levels/:id} : get the "id" meetingLevel.
     *
     * @param id the id of the meetingLevelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meetingLevelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingLevelDTO> getMeetingLevel(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MeetingLevel : {}", id);
        Optional<MeetingLevelDTO> meetingLevelDTO = meetingLevelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingLevelDTO);
    }

    /**
     * {@code DELETE  /meeting-levels/:id} : delete the "id" meetingLevel.
     *
     * @param id the id of the meetingLevelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingLevel(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MeetingLevel : {}", id);
        meetingLevelService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
