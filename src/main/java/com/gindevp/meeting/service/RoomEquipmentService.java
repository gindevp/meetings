package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Equipment;
import com.gindevp.meeting.domain.Room;
import com.gindevp.meeting.domain.RoomEquipment;
import com.gindevp.meeting.repository.EquipmentRepository;
import com.gindevp.meeting.repository.RoomEquipmentRepository;
import com.gindevp.meeting.repository.RoomRepository;
import com.gindevp.meeting.service.dto.RoomEquipmentDTO;
import com.gindevp.meeting.service.mapper.RoomEquipmentMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.meeting.domain.RoomEquipment}.
 */
@Service
@Transactional
public class RoomEquipmentService {

    private static final Logger LOG = LoggerFactory.getLogger(RoomEquipmentService.class);

    private final RoomEquipmentRepository roomEquipmentRepository;

    private final RoomEquipmentMapper roomEquipmentMapper;

    private final RoomRepository roomRepository;

    private final EquipmentRepository equipmentRepository;

    public RoomEquipmentService(
        RoomEquipmentRepository roomEquipmentRepository,
        RoomEquipmentMapper roomEquipmentMapper,
        RoomRepository roomRepository,
        EquipmentRepository equipmentRepository
    ) {
        this.roomEquipmentRepository = roomEquipmentRepository;
        this.roomEquipmentMapper = roomEquipmentMapper;
        this.roomRepository = roomRepository;
        this.equipmentRepository = equipmentRepository;
    }

    /**
     * Save a roomEquipment.
     *
     * @param roomEquipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public RoomEquipmentDTO save(RoomEquipmentDTO roomEquipmentDTO) {
        LOG.debug("Request to save RoomEquipment : {}", roomEquipmentDTO);
        if (roomEquipmentDTO.getRoom() == null || roomEquipmentDTO.getRoom().getId() == null) {
            throw new IllegalArgumentException("Room is required and must have an id");
        }
        if (roomEquipmentDTO.getEquipment() == null || roomEquipmentDTO.getEquipment().getId() == null) {
            throw new IllegalArgumentException("Equipment is required and must have an id");
        }
        Room room = roomRepository
            .findById(roomEquipmentDTO.getRoom().getId())
            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomEquipmentDTO.getRoom().getId()));
        Equipment equipment = equipmentRepository
            .findById(roomEquipmentDTO.getEquipment().getId())
            .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + roomEquipmentDTO.getEquipment().getId()));

        RoomEquipment roomEquipment = new RoomEquipment();
        roomEquipment.setQuantity(roomEquipmentDTO.getQuantity() != null ? roomEquipmentDTO.getQuantity() : 1);
        roomEquipment.setRoom(room);
        roomEquipment.setEquipment(equipment);

        roomEquipment = roomEquipmentRepository.save(roomEquipment);
        return roomEquipmentMapper.toDto(roomEquipment);
    }

    /**
     * Save a roomEquipment from room and equipment IDs.
     *
     * @param roomId the room id.
     * @param equipmentId the equipment id.
     * @param quantity the quantity.
     * @return the persisted DTO.
     */
    public RoomEquipmentDTO saveFromIds(Long roomId, Long equipmentId, Integer quantity) {
        LOG.debug("Request to save RoomEquipment : roomId={}, equipmentId={}, quantity={}", roomId, equipmentId, quantity);
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        Equipment equipment = equipmentRepository
            .findById(equipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + equipmentId));

        int qty = quantity != null && quantity > 0 ? quantity : 1;
        int totalAvailable = equipment.getTotalQuantity() != null ? equipment.getTotalQuantity() : 999;

        int usedByOtherRooms = roomEquipmentRepository
            .findAllWithToOneRelationships()
            .stream()
            .filter(re -> re.getEquipment().getId().equals(equipmentId) && !re.getRoom().getId().equals(roomId))
            .mapToInt(re -> re.getQuantity() != null ? re.getQuantity() : 0)
            .sum();

        int usedByThisRoom = roomEquipmentRepository
            .findAllWithToOneRelationships()
            .stream()
            .filter(re -> re.getEquipment().getId().equals(equipmentId) && re.getRoom().getId().equals(roomId))
            .mapToInt(re -> re.getQuantity() != null ? re.getQuantity() : 0)
            .sum();

        int totalUsedAfter = usedByOtherRooms + qty;
        if (totalUsedAfter > totalAvailable) {
            throw new IllegalArgumentException(
                String.format(
                    "Thiết bị \"%s\" không đủ. Tổng có: %d, các phòng khác dùng: %d, còn lại: %d. Yêu cầu: %d.",
                    equipment.getName(),
                    totalAvailable,
                    usedByOtherRooms,
                    totalAvailable - usedByOtherRooms,
                    qty
                )
            );
        }

        RoomEquipment roomEquipment = new RoomEquipment();
        roomEquipment.setQuantity(qty);
        roomEquipment.setRoom(room);
        roomEquipment.setEquipment(equipment);

        roomEquipment = roomEquipmentRepository.save(roomEquipment);
        return roomEquipmentMapper.toDto(roomEquipment);
    }

    /**
     * Update a roomEquipment.
     *
     * @param roomEquipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public RoomEquipmentDTO update(RoomEquipmentDTO roomEquipmentDTO) {
        LOG.debug("Request to update RoomEquipment : {}", roomEquipmentDTO);
        if (roomEquipmentDTO.getId() == null) {
            throw new IllegalArgumentException("RoomEquipment id is required for update");
        }
        RoomEquipment existing = roomEquipmentRepository
            .findById(roomEquipmentDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException("RoomEquipment not found: " + roomEquipmentDTO.getId()));

        if (roomEquipmentDTO.getQuantity() != null) {
            existing.setQuantity(roomEquipmentDTO.getQuantity());
        }
        if (roomEquipmentDTO.getRoom() != null && roomEquipmentDTO.getRoom().getId() != null) {
            Room room = roomRepository
                .findById(roomEquipmentDTO.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomEquipmentDTO.getRoom().getId()));
            existing.setRoom(room);
        }
        if (roomEquipmentDTO.getEquipment() != null && roomEquipmentDTO.getEquipment().getId() != null) {
            Equipment equipment = equipmentRepository
                .findById(roomEquipmentDTO.getEquipment().getId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + roomEquipmentDTO.getEquipment().getId()));
            existing.setEquipment(equipment);
        }

        existing = roomEquipmentRepository.save(existing);
        return roomEquipmentMapper.toDto(existing);
    }

    /**
     * Partially update a roomEquipment.
     *
     * @param roomEquipmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RoomEquipmentDTO> partialUpdate(RoomEquipmentDTO roomEquipmentDTO) {
        LOG.debug("Request to partially update RoomEquipment : {}", roomEquipmentDTO);

        return roomEquipmentRepository
            .findById(roomEquipmentDTO.getId())
            .map(existingRoomEquipment -> {
                roomEquipmentMapper.partialUpdate(existingRoomEquipment, roomEquipmentDTO);

                return existingRoomEquipment;
            })
            .map(roomEquipmentRepository::save)
            .map(roomEquipmentMapper::toDto);
    }

    /**
     * Get all the roomEquipments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RoomEquipmentDTO> findAll() {
        LOG.debug("Request to get all RoomEquipments");
        return roomEquipmentRepository
            .findAllWithToOneRelationships()
            .stream()
            .map(roomEquipmentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the roomEquipments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RoomEquipmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return roomEquipmentRepository.findAllWithEagerRelationships(pageable).map(roomEquipmentMapper::toDto);
    }

    /**
     * Get one roomEquipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RoomEquipmentDTO> findOne(Long id) {
        LOG.debug("Request to get RoomEquipment : {}", id);
        return roomEquipmentRepository.findOneWithEagerRelationships(id).map(roomEquipmentMapper::toDto);
    }

    /**
     * Delete the roomEquipment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RoomEquipment : {}", id);
        roomEquipmentRepository.deleteById(id);
    }
}
