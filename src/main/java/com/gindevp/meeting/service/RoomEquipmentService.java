package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.RoomEquipment;
import com.gindevp.meeting.repository.RoomEquipmentRepository;
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

    public RoomEquipmentService(RoomEquipmentRepository roomEquipmentRepository, RoomEquipmentMapper roomEquipmentMapper) {
        this.roomEquipmentRepository = roomEquipmentRepository;
        this.roomEquipmentMapper = roomEquipmentMapper;
    }

    /**
     * Save a roomEquipment.
     *
     * @param roomEquipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public RoomEquipmentDTO save(RoomEquipmentDTO roomEquipmentDTO) {
        LOG.debug("Request to save RoomEquipment : {}", roomEquipmentDTO);
        RoomEquipment roomEquipment = roomEquipmentMapper.toEntity(roomEquipmentDTO);
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
        RoomEquipment roomEquipment = roomEquipmentMapper.toEntity(roomEquipmentDTO);
        roomEquipment = roomEquipmentRepository.save(roomEquipment);
        return roomEquipmentMapper.toDto(roomEquipment);
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
        return roomEquipmentRepository.findAll().stream().map(roomEquipmentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
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
