package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.IncidentRepository;
import com.gindevp.meeting.service.IncidentService;
import com.gindevp.meeting.service.dto.IncidentDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gindevp.meeting.domain.Incident}.
 */
@RestController
@RequestMapping("/api/incidents")
public class IncidentResource {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentResource.class);

    private static final String ENTITY_NAME = "incident";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IncidentService incidentService;

    private final IncidentRepository incidentRepository;

    public IncidentResource(IncidentService incidentService, IncidentRepository incidentRepository) {
        this.incidentService = incidentService;
        this.incidentRepository = incidentRepository;
    }

    /**
     * {@code POST  /incidents} : Create a new incident.
     *
     * @param incidentDTO the incidentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new incidentDTO, or with status {@code 400 (Bad Request)} if the incident has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<IncidentDTO> createIncident(@Valid @RequestBody IncidentDTO incidentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Incident : {}", incidentDTO);
        if (incidentDTO.getId() != null) {
            throw new BadRequestAlertException("A new incident cannot already have an ID", ENTITY_NAME, "idexists");
        }
        incidentDTO = incidentService.save(incidentDTO);
        return ResponseEntity.created(new URI("/api/incidents/" + incidentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, incidentDTO.getId().toString()))
            .body(incidentDTO);
    }

    /**
     * {@code PUT  /incidents/:id} : Updates an existing incident.
     *
     * @param id the id of the incidentDTO to save.
     * @param incidentDTO the incidentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidentDTO,
     * or with status {@code 400 (Bad Request)} if the incidentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the incidentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<IncidentDTO> updateIncident(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IncidentDTO incidentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Incident : {}, {}", id, incidentDTO);
        if (incidentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!incidentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        incidentDTO = incidentService.update(incidentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, incidentDTO.getId().toString()))
            .body(incidentDTO);
    }

    /**
     * {@code PATCH  /incidents/:id} : Partial updates given fields of an existing incident, field will ignore if it is null
     *
     * @param id the id of the incidentDTO to save.
     * @param incidentDTO the incidentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidentDTO,
     * or with status {@code 400 (Bad Request)} if the incidentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the incidentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the incidentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IncidentDTO> partialUpdateIncident(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IncidentDTO incidentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Incident partially : {}, {}", id, incidentDTO);
        if (incidentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!incidentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IncidentDTO> result = incidentService.partialUpdate(incidentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, incidentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /incidents} : get all the incidents with optional filters.
     *
     * @param pageable the pagination information.
     * @param status optional filter by status (e.g. OPEN, IN_PROGRESS, RESOLVED).
     * @param severity optional filter by severity (e.g. LOW, MEDIUM, HIGH).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of incidents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<IncidentDTO>> getAllIncidents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String severity
    ) {
        LOG.debug("REST request to get a page of Incidents with filters");
        Page<IncidentDTO> page = incidentService.findAll(pageable, status, severity);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /incidents/:id} : get the "id" incident.
     *
     * @param id the id of the incidentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the incidentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IncidentDTO> getIncident(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Incident : {}", id);
        Optional<IncidentDTO> incidentDTO = incidentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(incidentDTO);
    }

    /**
     * {@code DELETE  /incidents/:id} : delete the "id" incident.
     *
     * @param id the id of the incidentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Incident : {}", id);
        incidentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
