package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.MeetingApproval;
import com.gindevp.meeting.repository.MeetingApprovalRepository;
import com.gindevp.meeting.service.dto.MeetingApprovalDTO;
import com.gindevp.meeting.service.mapper.MeetingApprovalMapper;
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
 * Service Implementation for managing {@link com.gindevp.meeting.domain.MeetingApproval}.
 */
@Service
@Transactional
public class MeetingApprovalService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingApprovalService.class);

    private final MeetingApprovalRepository meetingApprovalRepository;

    private final MeetingApprovalMapper meetingApprovalMapper;

    public MeetingApprovalService(MeetingApprovalRepository meetingApprovalRepository, MeetingApprovalMapper meetingApprovalMapper) {
        this.meetingApprovalRepository = meetingApprovalRepository;
        this.meetingApprovalMapper = meetingApprovalMapper;
    }

    /**
     * Save a meetingApproval.
     *
     * @param meetingApprovalDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingApprovalDTO save(MeetingApprovalDTO meetingApprovalDTO) {
        LOG.debug("Request to save MeetingApproval : {}", meetingApprovalDTO);
        MeetingApproval meetingApproval = meetingApprovalMapper.toEntity(meetingApprovalDTO);
        meetingApproval = meetingApprovalRepository.save(meetingApproval);
        return meetingApprovalMapper.toDto(meetingApproval);
    }

    /**
     * Update a meetingApproval.
     *
     * @param meetingApprovalDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingApprovalDTO update(MeetingApprovalDTO meetingApprovalDTO) {
        LOG.debug("Request to update MeetingApproval : {}", meetingApprovalDTO);
        MeetingApproval meetingApproval = meetingApprovalMapper.toEntity(meetingApprovalDTO);
        meetingApproval = meetingApprovalRepository.save(meetingApproval);
        return meetingApprovalMapper.toDto(meetingApproval);
    }

    /**
     * Partially update a meetingApproval.
     *
     * @param meetingApprovalDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MeetingApprovalDTO> partialUpdate(MeetingApprovalDTO meetingApprovalDTO) {
        LOG.debug("Request to partially update MeetingApproval : {}", meetingApprovalDTO);

        return meetingApprovalRepository
            .findById(meetingApprovalDTO.getId())
            .map(existingMeetingApproval -> {
                meetingApprovalMapper.partialUpdate(existingMeetingApproval, meetingApprovalDTO);

                return existingMeetingApproval;
            })
            .map(meetingApprovalRepository::save)
            .map(meetingApprovalMapper::toDto);
    }

    /**
     * Get all the meetingApprovals.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MeetingApprovalDTO> findAll() {
        LOG.debug("Request to get all MeetingApprovals");
        return meetingApprovalRepository
            .findAll()
            .stream()
            .map(meetingApprovalMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the meetingApprovals with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MeetingApprovalDTO> findAllWithEagerRelationships(Pageable pageable) {
        return meetingApprovalRepository.findAllWithEagerRelationships(pageable).map(meetingApprovalMapper::toDto);
    }

    /**
     * Get one meetingApproval by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MeetingApprovalDTO> findOne(Long id) {
        LOG.debug("Request to get MeetingApproval : {}", id);
        return meetingApprovalRepository.findOneWithEagerRelationships(id).map(meetingApprovalMapper::toDto);
    }

    /**
     * Delete the meetingApproval by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MeetingApproval : {}", id);
        meetingApprovalRepository.deleteById(id);
    }
}
