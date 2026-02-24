package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.MeetingLevel;
import com.gindevp.meeting.repository.MeetingLevelRepository;
import com.gindevp.meeting.service.dto.MeetingLevelDTO;
import com.gindevp.meeting.service.mapper.MeetingLevelMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.meeting.domain.MeetingLevel}.
 */
@Service
@Transactional
public class MeetingLevelService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingLevelService.class);

    private final MeetingLevelRepository meetingLevelRepository;

    private final MeetingLevelMapper meetingLevelMapper;

    public MeetingLevelService(MeetingLevelRepository meetingLevelRepository, MeetingLevelMapper meetingLevelMapper) {
        this.meetingLevelRepository = meetingLevelRepository;
        this.meetingLevelMapper = meetingLevelMapper;
    }

    /**
     * Save a meetingLevel.
     *
     * @param meetingLevelDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingLevelDTO save(MeetingLevelDTO meetingLevelDTO) {
        LOG.debug("Request to save MeetingLevel : {}", meetingLevelDTO);
        MeetingLevel meetingLevel = meetingLevelMapper.toEntity(meetingLevelDTO);
        meetingLevel = meetingLevelRepository.save(meetingLevel);
        return meetingLevelMapper.toDto(meetingLevel);
    }

    /**
     * Update a meetingLevel.
     *
     * @param meetingLevelDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingLevelDTO update(MeetingLevelDTO meetingLevelDTO) {
        LOG.debug("Request to update MeetingLevel : {}", meetingLevelDTO);
        MeetingLevel meetingLevel = meetingLevelMapper.toEntity(meetingLevelDTO);
        meetingLevel = meetingLevelRepository.save(meetingLevel);
        return meetingLevelMapper.toDto(meetingLevel);
    }

    /**
     * Partially update a meetingLevel.
     *
     * @param meetingLevelDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MeetingLevelDTO> partialUpdate(MeetingLevelDTO meetingLevelDTO) {
        LOG.debug("Request to partially update MeetingLevel : {}", meetingLevelDTO);

        return meetingLevelRepository
            .findById(meetingLevelDTO.getId())
            .map(existingMeetingLevel -> {
                meetingLevelMapper.partialUpdate(existingMeetingLevel, meetingLevelDTO);

                return existingMeetingLevel;
            })
            .map(meetingLevelRepository::save)
            .map(meetingLevelMapper::toDto);
    }

    /**
     * Get all the meetingLevels.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MeetingLevelDTO> findAll() {
        LOG.debug("Request to get all MeetingLevels");
        return meetingLevelRepository.findAll().stream().map(meetingLevelMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one meetingLevel by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MeetingLevelDTO> findOne(Long id) {
        LOG.debug("Request to get MeetingLevel : {}", id);
        return meetingLevelRepository.findById(id).map(meetingLevelMapper::toDto);
    }

    /**
     * Delete the meetingLevel by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MeetingLevel : {}", id);
        meetingLevelRepository.deleteById(id);
    }
}
