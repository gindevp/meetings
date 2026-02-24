package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.MeetingTypeRepository;
import com.gindevp.meeting.service.MeetingTypeService;
import com.gindevp.meeting.service.dto.MeetingTypeDTO;
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
 * REST controller for managing {@link com.gindevp.meeting.domain.MeetingType}.
 */
@RestController
@RequestMapping("/api/meeting-types")
public class MeetingTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingTypeResource.class);

    private static final String ENTITY_NAME = "meetingType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingTypeService meetingTypeService;

    private final MeetingTypeRepository meetingTypeRepository;

    public MeetingTypeResource(MeetingTypeService meetingTypeService, MeetingTypeRepository meetingTypeRepository) {
        this.meetingTypeService = meetingTypeService;
        this.meetingTypeRepository = meetingTypeRepository;
    }

    /**
     * {@code POST  /meeting-types} : Create a new meetingType.
     *
     * @param meetingTypeDTO the meetingTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meetingTypeDTO, or with status {@code 400 (Bad Request)} if the meetingType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MeetingTypeDTO> createMeetingType(@Valid @RequestBody MeetingTypeDTO meetingTypeDTO) throws URISyntaxException {
        LOG.debug("REST request to save MeetingType : {}", meetingTypeDTO);
        if (meetingTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new meetingType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingTypeDTO = meetingTypeService.save(meetingTypeDTO);
        return ResponseEntity.created(new URI("/api/meeting-types/" + meetingTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingTypeDTO.getId().toString()))
            .body(meetingTypeDTO);
    }

    /**
     * {@code PUT  /meeting-types/:id} : Updates an existing meetingType.
     *
     * @param id the id of the meetingTypeDTO to save.
     * @param meetingTypeDTO the meetingTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingTypeDTO,
     * or with status {@code 400 (Bad Request)} if the meetingTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meetingTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingTypeDTO> updateMeetingType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingTypeDTO meetingTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MeetingType : {}, {}", id, meetingTypeDTO);
        if (meetingTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        meetingTypeDTO = meetingTypeService.update(meetingTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingTypeDTO.getId().toString()))
            .body(meetingTypeDTO);
    }

    /**
     * {@code PATCH  /meeting-types/:id} : Partial updates given fields of an existing meetingType, field will ignore if it is null
     *
     * @param id the id of the meetingTypeDTO to save.
     * @param meetingTypeDTO the meetingTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingTypeDTO,
     * or with status {@code 400 (Bad Request)} if the meetingTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the meetingTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the meetingTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingTypeDTO> partialUpdateMeetingType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingTypeDTO meetingTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MeetingType partially : {}, {}", id, meetingTypeDTO);
        if (meetingTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingTypeDTO> result = meetingTypeService.partialUpdate(meetingTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /meeting-types} : get all the meetingTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meetingTypes in body.
     */
    @GetMapping("")
    public List<MeetingTypeDTO> getAllMeetingTypes() {
        LOG.debug("REST request to get all MeetingTypes");
        return meetingTypeService.findAll();
    }

    /**
     * {@code GET  /meeting-types/:id} : get the "id" meetingType.
     *
     * @param id the id of the meetingTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meetingTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingTypeDTO> getMeetingType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MeetingType : {}", id);
        Optional<MeetingTypeDTO> meetingTypeDTO = meetingTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingTypeDTO);
    }

    /**
     * {@code DELETE  /meeting-types/:id} : delete the "id" meetingType.
     *
     * @param id the id of the meetingTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MeetingType : {}", id);
        meetingTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
