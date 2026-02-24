package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.AgendaItemRepository;
import com.gindevp.meeting.service.AgendaItemService;
import com.gindevp.meeting.service.dto.AgendaItemDTO;
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
 * REST controller for managing {@link com.gindevp.meeting.domain.AgendaItem}.
 */
@RestController
@RequestMapping("/api/agenda-items")
public class AgendaItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgendaItemResource.class);

    private static final String ENTITY_NAME = "agendaItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgendaItemService agendaItemService;

    private final AgendaItemRepository agendaItemRepository;

    public AgendaItemResource(AgendaItemService agendaItemService, AgendaItemRepository agendaItemRepository) {
        this.agendaItemService = agendaItemService;
        this.agendaItemRepository = agendaItemRepository;
    }

    /**
     * {@code POST  /agenda-items} : Create a new agendaItem.
     *
     * @param agendaItemDTO the agendaItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new agendaItemDTO, or with status {@code 400 (Bad Request)} if the agendaItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AgendaItemDTO> createAgendaItem(@Valid @RequestBody AgendaItemDTO agendaItemDTO) throws URISyntaxException {
        LOG.debug("REST request to save AgendaItem : {}", agendaItemDTO);
        if (agendaItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new agendaItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        agendaItemDTO = agendaItemService.save(agendaItemDTO);
        return ResponseEntity.created(new URI("/api/agenda-items/" + agendaItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, agendaItemDTO.getId().toString()))
            .body(agendaItemDTO);
    }

    /**
     * {@code PUT  /agenda-items/:id} : Updates an existing agendaItem.
     *
     * @param id the id of the agendaItemDTO to save.
     * @param agendaItemDTO the agendaItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agendaItemDTO,
     * or with status {@code 400 (Bad Request)} if the agendaItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the agendaItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AgendaItemDTO> updateAgendaItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AgendaItemDTO agendaItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AgendaItem : {}, {}", id, agendaItemDTO);
        if (agendaItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agendaItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agendaItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        agendaItemDTO = agendaItemService.update(agendaItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agendaItemDTO.getId().toString()))
            .body(agendaItemDTO);
    }

    /**
     * {@code PATCH  /agenda-items/:id} : Partial updates given fields of an existing agendaItem, field will ignore if it is null
     *
     * @param id the id of the agendaItemDTO to save.
     * @param agendaItemDTO the agendaItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agendaItemDTO,
     * or with status {@code 400 (Bad Request)} if the agendaItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the agendaItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the agendaItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AgendaItemDTO> partialUpdateAgendaItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AgendaItemDTO agendaItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AgendaItem partially : {}, {}", id, agendaItemDTO);
        if (agendaItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agendaItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agendaItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AgendaItemDTO> result = agendaItemService.partialUpdate(agendaItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agendaItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /agenda-items} : get all the agendaItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of agendaItems in body.
     */
    @GetMapping("")
    public List<AgendaItemDTO> getAllAgendaItems() {
        LOG.debug("REST request to get all AgendaItems");
        return agendaItemService.findAll();
    }

    /**
     * {@code GET  /agenda-items/:id} : get the "id" agendaItem.
     *
     * @param id the id of the agendaItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the agendaItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgendaItemDTO> getAgendaItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AgendaItem : {}", id);
        Optional<AgendaItemDTO> agendaItemDTO = agendaItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(agendaItemDTO);
    }

    /**
     * {@code DELETE  /agenda-items/:id} : delete the "id" agendaItem.
     *
     * @param id the id of the agendaItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgendaItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AgendaItem : {}", id);
        agendaItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
