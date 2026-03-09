package com.gindevp.meeting.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.meeting.domain.Room;
import com.gindevp.meeting.repository.RoomRepository;
import com.gindevp.meeting.service.dto.RoomDTO;
import com.gindevp.meeting.service.mapper.RoomMapper;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.meeting.domain.Room}.
 */
@Service
@Transactional
public class RoomService {

    private static final Logger LOG = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;

    private final ObjectMapper objectMapper;

    private static final String ENTITY_NAME = "room";

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper, ObjectMapper objectMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * Save a room.
     *
     * @param roomDTO the entity to save.
     * @return the persisted entity.
     */
    public RoomDTO save(RoomDTO roomDTO) {
        LOG.debug("Request to save Room : {}", roomDTO);
        validateLayoutCapacity(roomDTO.getLayoutData(), roomDTO.getCapacity());
        Room room = roomMapper.toEntity(roomDTO);
        syncActiveFromStatus(room);
        room = roomRepository.save(room);
        return toDtoWithImageUrl(room);
    }

    public RoomDTO update(RoomDTO roomDTO) {
        LOG.debug("Request to update Room : {}", roomDTO);
        validateLayoutCapacity(roomDTO.getLayoutData(), roomDTO.getCapacity());
        Room existing = roomRepository
            .findById(roomDTO.getId())
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Room not found: " + roomDTO.getId()));
        roomMapper.partialUpdate(existing, roomDTO);
        syncActiveFromStatus(existing);
        Room room = roomRepository.save(existing);
        return toDtoWithImageUrl(room);
    }

    /** Rule 1: Số ghế trong layout không được vượt quá sức chứa phòng. */
    private void validateLayoutCapacity(String layoutData, Integer capacity) {
        if (layoutData == null || layoutData.isBlank() || capacity == null) return;
        try {
            JsonNode root = objectMapper.readTree(layoutData);
            int chairs = 0;
            if (root.isArray()) {
                for (JsonNode node : root) {
                    if (node.has("type") && "chair".equals(node.get("type").asText(null))) {
                        chairs++;
                    }
                }
            }
            if (chairs > capacity) {
                throw new BadRequestAlertException(
                    "Số ghế trong layout (" + chairs + ") vượt quá sức chứa phòng (" + capacity + ").",
                    ENTITY_NAME,
                    "layoutChairsExceedCapacity"
                );
            }
        } catch (BadRequestAlertException e) {
            throw e;
        } catch (Exception e) {
            LOG.debug("Failed to parse layout JSON, skipping validation: {}", e.getMessage());
        }
    }

    /** Count chairs in layout JSON. */
    @Transactional(readOnly = true)
    public int countLayoutChairs(String layoutData) {
        if (layoutData == null || layoutData.isBlank()) return 0;
        try {
            JsonNode root = objectMapper.readTree(layoutData);
            int chairs = 0;
            if (root.isArray()) {
                for (JsonNode node : root) {
                    if (node.has("type") && "chair".equals(node.get("type").asText(null))) {
                        chairs++;
                    }
                }
            }
            return chairs;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Copy layout from source room to target room. */
    public void copyLayoutFrom(Long targetRoomId, Long sourceRoomId) {
        Room source = roomRepository
            .findById(sourceRoomId)
            .orElseThrow(() -> new BadRequestAlertException("Phòng nguồn không tồn tại", ENTITY_NAME, "idnotfound"));
        Room target = roomRepository
            .findById(targetRoomId)
            .orElseThrow(() -> new BadRequestAlertException("Phòng đích không tồn tại", ENTITY_NAME, "idnotfound"));
        if (source.getLayoutData() != null && !source.getLayoutData().isBlank()) {
            validateLayoutCapacity(source.getLayoutData(), target.getCapacity());
            target.setLayoutData(source.getLayoutData());
            roomRepository.save(target);
        }
    }

    /** Sync active flag from status: only ACTIVE allows booking. */
    private void syncActiveFromStatus(Room room) {
        if (room.getStatus() != null) {
            room.setActive("ACTIVE".equals(room.getStatus()));
        }
    }

    /**
     * Partially update a room.
     *
     * @param roomDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RoomDTO> partialUpdate(RoomDTO roomDTO) {
        LOG.debug("Request to partially update Room : {}", roomDTO);

        return roomRepository
            .findById(roomDTO.getId())
            .map(existingRoom -> {
                roomMapper.partialUpdate(existingRoom, roomDTO);

                return existingRoom;
            })
            .map(roomRepository::save)
            .map(this::toDtoWithImageUrl);
    }

    /**
     * Get all the rooms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RoomDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Rooms");
        return roomRepository.findAll(pageable).map(this::toDtoWithImageUrl);
    }

    /**
     * Get all rooms with optional filters.
     */
    @Transactional(readOnly = true)
    public Page<RoomDTO> findAll(Pageable pageable, String location, Integer minCapacity, Integer maxCapacity, String status) {
        LOG.debug("Request to get all Rooms with filters");
        boolean hasFilter =
            (location != null && !location.isBlank()) ||
            minCapacity != null ||
            maxCapacity != null ||
            (status != null && !status.isBlank());
        if (!hasFilter) {
            return roomRepository.findAll(pageable).map(this::toDtoWithImageUrl);
        }
        Specification<Room> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (minCapacity != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity));
            }
            if (maxCapacity != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("capacity"), maxCapacity));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return roomRepository.findAll(spec, pageable).map(this::toDtoWithImageUrl);
    }

    /** Build DTO and set imageUrl to API path when image is stored as blob. */
    private RoomDTO toDtoWithImageUrl(Room room) {
        RoomDTO dto = roomMapper.toDto(room);
        if (room.getImageData() != null && room.getImageData().length > 0) {
            dto.setImageUrl("/api/rooms/" + room.getId() + "/image");
        }
        return dto;
    }

    /**
     * Get one room by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RoomDTO> findOne(Long id) {
        LOG.debug("Request to get Room : {}", id);
        return roomRepository.findById(id).map(this::toDtoWithImageUrl);
    }

    /**
     * Delete the room by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Room : {}", id);
        roomRepository.deleteById(id);
    }

    /**
     * Store room image as blob. Clears external imageUrl when blob is set.
     */
    public void saveImage(Long roomId, String contentType, byte[] data) {
        Room room = roomRepository
            .findById(roomId)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Room not found: " + roomId));
        room.setImageContentType(contentType);
        room.setImageData(data);
        room.setImageUrl(null); // prefer blob over external URL
        roomRepository.save(room);
    }

    /**
     * Clear room image blob (keeps imageUrl if any).
     */
    public void clearImage(Long roomId) {
        Room room = roomRepository
            .findById(roomId)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Room not found: " + roomId));
        room.setImageContentType(null);
        room.setImageData(null);
        roomRepository.save(room);
    }
}
