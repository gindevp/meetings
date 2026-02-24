package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.RoomEquipmentRepository;
import com.gindevp.meeting.service.RoomEquipmentService;
import com.gindevp.meeting.service.dto.RoomEquipmentDTO;
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
 * REST controller for managing {@link com.gindevp.meeting.domain.RoomEquipment}.
 */
@RestController
@RequestMapping("/api/room-equipments")
public class RoomEquipmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(RoomEquipmentResource.class);

    private static final String ENTITY_NAME = "roomEquipment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoomEquipmentService roomEquipmentService;

    private final RoomEquipmentRepository roomEquipmentRepository;

    public RoomEquipmentResource(RoomEquipmentService roomEquipmentService, RoomEquipmentRepository roomEquipmentRepository) {
        this.roomEquipmentService = roomEquipmentService;
        this.roomEquipmentRepository = roomEquipmentRepository;
    }

    /**
     * {@code POST  /room-equipments} : Create a new roomEquipment.
     *
     * @param roomEquipmentDTO the roomEquipmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roomEquipmentDTO, or with status {@code 400 (Bad Request)} if the roomEquipment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RoomEquipmentDTO> createRoomEquipment(@Valid @RequestBody RoomEquipmentDTO roomEquipmentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RoomEquipment : {}", roomEquipmentDTO);
        if (roomEquipmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new roomEquipment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        roomEquipmentDTO = roomEquipmentService.save(roomEquipmentDTO);
        return ResponseEntity.created(new URI("/api/room-equipments/" + roomEquipmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, roomEquipmentDTO.getId().toString()))
            .body(roomEquipmentDTO);
    }

    /**
     * {@code PUT  /room-equipments/:id} : Updates an existing roomEquipment.
     *
     * @param id the id of the roomEquipmentDTO to save.
     * @param roomEquipmentDTO the roomEquipmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomEquipmentDTO,
     * or with status {@code 400 (Bad Request)} if the roomEquipmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roomEquipmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomEquipmentDTO> updateRoomEquipment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RoomEquipmentDTO roomEquipmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RoomEquipment : {}, {}", id, roomEquipmentDTO);
        if (roomEquipmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomEquipmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomEquipmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        roomEquipmentDTO = roomEquipmentService.update(roomEquipmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roomEquipmentDTO.getId().toString()))
            .body(roomEquipmentDTO);
    }

    /**
     * {@code PATCH  /room-equipments/:id} : Partial updates given fields of an existing roomEquipment, field will ignore if it is null
     *
     * @param id the id of the roomEquipmentDTO to save.
     * @param roomEquipmentDTO the roomEquipmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomEquipmentDTO,
     * or with status {@code 400 (Bad Request)} if the roomEquipmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the roomEquipmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the roomEquipmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RoomEquipmentDTO> partialUpdateRoomEquipment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RoomEquipmentDTO roomEquipmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RoomEquipment partially : {}, {}", id, roomEquipmentDTO);
        if (roomEquipmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomEquipmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomEquipmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoomEquipmentDTO> result = roomEquipmentService.partialUpdate(roomEquipmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roomEquipmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /room-equipments} : get all the roomEquipments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roomEquipments in body.
     */
    @GetMapping("")
    public List<RoomEquipmentDTO> getAllRoomEquipments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all RoomEquipments");
        return roomEquipmentService.findAll();
    }

    /**
     * {@code GET  /room-equipments/:id} : get the "id" roomEquipment.
     *
     * @param id the id of the roomEquipmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roomEquipmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomEquipmentDTO> getRoomEquipment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RoomEquipment : {}", id);
        Optional<RoomEquipmentDTO> roomEquipmentDTO = roomEquipmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roomEquipmentDTO);
    }

    /**
     * {@code DELETE  /room-equipments/:id} : delete the "id" roomEquipment.
     *
     * @param id the id of the roomEquipmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomEquipment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RoomEquipment : {}", id);
        roomEquipmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
