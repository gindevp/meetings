package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.MeetingDocumentRepository;
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

    public MeetingDocumentResource(MeetingDocumentService meetingDocumentService, MeetingDocumentRepository meetingDocumentRepository) {
        this.meetingDocumentService = meetingDocumentService;
        this.meetingDocumentRepository = meetingDocumentRepository;
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
        return ResponseUtil.wrapOrNotFound(meetingDocumentDTO);
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
        meetingDocumentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
