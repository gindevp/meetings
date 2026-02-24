package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.MeetingParticipant;
import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.service.dto.MeetingParticipantDTO;
import com.gindevp.meeting.service.mapper.MeetingParticipantMapper;
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
 * Service Implementation for managing {@link com.gindevp.meeting.domain.MeetingParticipant}.
 */
@Service
@Transactional
public class MeetingParticipantService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingParticipantService.class);

    private final MeetingParticipantRepository meetingParticipantRepository;

    private final MeetingParticipantMapper meetingParticipantMapper;

    public MeetingParticipantService(
        MeetingParticipantRepository meetingParticipantRepository,
        MeetingParticipantMapper meetingParticipantMapper
    ) {
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.meetingParticipantMapper = meetingParticipantMapper;
    }

    /**
     * Save a meetingParticipant.
     *
     * @param meetingParticipantDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingParticipantDTO save(MeetingParticipantDTO meetingParticipantDTO) {
        LOG.debug("Request to save MeetingParticipant : {}", meetingParticipantDTO);
        MeetingParticipant meetingParticipant = meetingParticipantMapper.toEntity(meetingParticipantDTO);
        meetingParticipant = meetingParticipantRepository.save(meetingParticipant);
        return meetingParticipantMapper.toDto(meetingParticipant);
    }

    /**
     * Update a meetingParticipant.
     *
     * @param meetingParticipantDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingParticipantDTO update(MeetingParticipantDTO meetingParticipantDTO) {
        LOG.debug("Request to update MeetingParticipant : {}", meetingParticipantDTO);
        MeetingParticipant meetingParticipant = meetingParticipantMapper.toEntity(meetingParticipantDTO);
        meetingParticipant = meetingParticipantRepository.save(meetingParticipant);
        return meetingParticipantMapper.toDto(meetingParticipant);
    }

    /**
     * Partially update a meetingParticipant.
     *
     * @param meetingParticipantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MeetingParticipantDTO> partialUpdate(MeetingParticipantDTO meetingParticipantDTO) {
        LOG.debug("Request to partially update MeetingParticipant : {}", meetingParticipantDTO);

        return meetingParticipantRepository
            .findById(meetingParticipantDTO.getId())
            .map(existingMeetingParticipant -> {
                meetingParticipantMapper.partialUpdate(existingMeetingParticipant, meetingParticipantDTO);

                return existingMeetingParticipant;
            })
            .map(meetingParticipantRepository::save)
            .map(meetingParticipantMapper::toDto);
    }

    /**
     * Get all the meetingParticipants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MeetingParticipantDTO> findAll() {
        LOG.debug("Request to get all MeetingParticipants");
        return meetingParticipantRepository
            .findAll()
            .stream()
            .map(meetingParticipantMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the meetingParticipants with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MeetingParticipantDTO> findAllWithEagerRelationships(Pageable pageable) {
        return meetingParticipantRepository.findAllWithEagerRelationships(pageable).map(meetingParticipantMapper::toDto);
    }

    /**
     * Get one meetingParticipant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MeetingParticipantDTO> findOne(Long id) {
        LOG.debug("Request to get MeetingParticipant : {}", id);
        return meetingParticipantRepository.findOneWithEagerRelationships(id).map(meetingParticipantMapper::toDto);
    }

    /**
     * Delete the meetingParticipant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MeetingParticipant : {}", id);
        meetingParticipantRepository.deleteById(id);
    }
}
