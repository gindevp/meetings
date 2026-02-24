package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.MeetingType;
import com.gindevp.meeting.repository.MeetingTypeRepository;
import com.gindevp.meeting.service.dto.MeetingTypeDTO;
import com.gindevp.meeting.service.mapper.MeetingTypeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.meeting.domain.MeetingType}.
 */
@Service
@Transactional
public class MeetingTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingTypeService.class);

    private final MeetingTypeRepository meetingTypeRepository;

    private final MeetingTypeMapper meetingTypeMapper;

    public MeetingTypeService(MeetingTypeRepository meetingTypeRepository, MeetingTypeMapper meetingTypeMapper) {
        this.meetingTypeRepository = meetingTypeRepository;
        this.meetingTypeMapper = meetingTypeMapper;
    }

    /**
     * Save a meetingType.
     *
     * @param meetingTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingTypeDTO save(MeetingTypeDTO meetingTypeDTO) {
        LOG.debug("Request to save MeetingType : {}", meetingTypeDTO);
        MeetingType meetingType = meetingTypeMapper.toEntity(meetingTypeDTO);
        meetingType = meetingTypeRepository.save(meetingType);
        return meetingTypeMapper.toDto(meetingType);
    }

    /**
     * Update a meetingType.
     *
     * @param meetingTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingTypeDTO update(MeetingTypeDTO meetingTypeDTO) {
        LOG.debug("Request to update MeetingType : {}", meetingTypeDTO);
        MeetingType meetingType = meetingTypeMapper.toEntity(meetingTypeDTO);
        meetingType = meetingTypeRepository.save(meetingType);
        return meetingTypeMapper.toDto(meetingType);
    }

    /**
     * Partially update a meetingType.
     *
     * @param meetingTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MeetingTypeDTO> partialUpdate(MeetingTypeDTO meetingTypeDTO) {
        LOG.debug("Request to partially update MeetingType : {}", meetingTypeDTO);

        return meetingTypeRepository
            .findById(meetingTypeDTO.getId())
            .map(existingMeetingType -> {
                meetingTypeMapper.partialUpdate(existingMeetingType, meetingTypeDTO);

                return existingMeetingType;
            })
            .map(meetingTypeRepository::save)
            .map(meetingTypeMapper::toDto);
    }

    /**
     * Get all the meetingTypes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MeetingTypeDTO> findAll() {
        LOG.debug("Request to get all MeetingTypes");
        return meetingTypeRepository.findAll().stream().map(meetingTypeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one meetingType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MeetingTypeDTO> findOne(Long id) {
        LOG.debug("Request to get MeetingType : {}", id);
        return meetingTypeRepository.findById(id).map(meetingTypeMapper::toDto);
    }

    /**
     * Delete the meetingType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MeetingType : {}", id);
        meetingTypeRepository.deleteById(id);
    }
}
