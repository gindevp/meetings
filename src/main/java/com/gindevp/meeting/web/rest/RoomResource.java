package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.repository.RoomRepository;
import com.gindevp.meeting.service.RoomService;
import com.gindevp.meeting.service.dto.RoomDTO;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gindevp.meeting.domain.Room}.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomResource {

    private static final Logger LOG = LoggerFactory.getLogger(RoomResource.class);

    private static final String ENTITY_NAME = "room";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoomService roomService;

    private final RoomRepository roomRepository;

    public RoomResource(RoomService roomService, RoomRepository roomRepository) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
    }

    /**
     * {@code POST  /rooms} : Create a new room.
     *
     * @param roomDTO the roomDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roomDTO, or with status {@code 400 (Bad Request)} if the room has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO) throws URISyntaxException {
        LOG.debug("REST request to save Room : {}", roomDTO);
        if (roomDTO.getId() != null) {
            throw new BadRequestAlertException("A new room cannot already have an ID", ENTITY_NAME, "idexists");
        }
        roomDTO = roomService.save(roomDTO);
        return ResponseEntity.created(new URI("/api/rooms/" + roomDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, roomDTO.getId().toString()))
            .body(roomDTO);
    }

    /**
     * {@code PUT  /rooms/:id} : Updates an existing room.
     *
     * @param id the id of the roomDTO to save.
     * @param roomDTO the roomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomDTO,
     * or with status {@code 400 (Bad Request)} if the roomDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RoomDTO roomDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Room : {}, {}", id, roomDTO);
        if (roomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        roomDTO = roomService.update(roomDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roomDTO.getId().toString()))
            .body(roomDTO);
    }

    /**
     * {@code PATCH  /rooms/:id} : Partial updates given fields of an existing room, field will ignore if it is null
     *
     * @param id the id of the roomDTO to save.
     * @param roomDTO the roomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomDTO,
     * or with status {@code 400 (Bad Request)} if the roomDTO is not valid,
     * or with status {@code 404 (Not Found)} if the roomDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the roomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RoomDTO> partialUpdateRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RoomDTO roomDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Room partially : {}, {}", id, roomDTO);
        if (roomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoomDTO> result = roomService.partialUpdate(roomDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roomDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /rooms} : get all the rooms.
     *
     * @param pageable the pagination information.
     * @param location optional filter by location (contains, case-insensitive).
     * @param minCapacity optional filter minimum capacity.
     * @param maxCapacity optional filter maximum capacity.
     * @param status optional filter by status (ACTIVE, MAINTENANCE, DISABLED).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rooms in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RoomDTO>> getAllRooms(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) Integer minCapacity,
        @RequestParam(required = false) Integer maxCapacity,
        @RequestParam(required = false) String status
    ) {
        LOG.debug("REST request to get a page of Rooms with filters");
        Page<RoomDTO> page = roomService.findAll(pageable, location, minCapacity, maxCapacity, status);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rooms/:id} : get the "id" room.
     *
     * @param id the id of the roomDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roomDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Room : {}", id);
        Optional<RoomDTO> roomDTO = roomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roomDTO);
    }

    /**
     * {@code DELETE  /rooms/:id} : delete the "id" room.
     *
     * @param id the id of the roomDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Room : {}", id);
        roomService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /rooms/:id/image} : get the room image (blob).
     *
     * @param id the id of the room.
     * @return the image bytes with content type, or 404 if no image.
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getRoomImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Room image : {}", id);
        return roomRepository
            .findById(id)
            .filter(room -> room.getImageData() != null && room.getImageData().length > 0)
            .map(room ->
                ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(room.getImageContentType() != null ? room.getImageContentType() : "image/jpeg"))
                    .body(room.getImageData())
            )
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * {@code POST  /rooms/:id/image} : upload and store room image.
     *
     * @param id the id of the room.
     * @param file the image file (multipart).
     * @return 204 on success, 400 if room not found or invalid file.
     */
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadRoomImage(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        LOG.debug("REST request to upload Room image : {}", id);
        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Room not found", ENTITY_NAME, "idnotfound");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestAlertException("File must be an image", ENTITY_NAME, "invalidfile");
        }
        try {
            roomService.saveImage(id, contentType, file.getBytes());
        } catch (java.io.IOException e) {
            throw new BadRequestAlertException("Failed to read file", ENTITY_NAME, "fileread");
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "Room image updated", id.toString())).build();
    }

    /**
     * {@code DELETE  /rooms/:id/image} : remove room image blob.
     */
    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteRoomImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Room image : {}", id);
        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Room not found", ENTITY_NAME, "idnotfound");
        }
        roomService.clearImage(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST  /rooms/:targetId/layout/copy-from/:sourceId} : copy layout from source room to target room.
     */
    @PostMapping("/{targetId}/layout/copy-from/{sourceId}")
    public ResponseEntity<Void> copyLayoutFrom(@PathVariable("targetId") Long targetId, @PathVariable("sourceId") Long sourceId) {
        LOG.debug("REST request to copy layout from room {} to room {}", sourceId, targetId);
        roomService.copyLayoutFrom(targetId, sourceId);
        return ResponseEntity.noContent().build();
    }
}
