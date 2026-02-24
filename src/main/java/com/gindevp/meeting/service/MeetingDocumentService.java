package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.MeetingDocument;
import com.gindevp.meeting.repository.MeetingDocumentRepository;
import com.gindevp.meeting.service.dto.MeetingDocumentDTO;
import com.gindevp.meeting.service.mapper.MeetingDocumentMapper;
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
 * Service Implementation for managing {@link com.gindevp.meeting.domain.MeetingDocument}.
 */
@Service
@Transactional
public class MeetingDocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingDocumentService.class);

    private final MeetingDocumentRepository meetingDocumentRepository;

    private final MeetingDocumentMapper meetingDocumentMapper;

    public MeetingDocumentService(MeetingDocumentRepository meetingDocumentRepository, MeetingDocumentMapper meetingDocumentMapper) {
        this.meetingDocumentRepository = meetingDocumentRepository;
        this.meetingDocumentMapper = meetingDocumentMapper;
    }

    /**
     * Save a meetingDocument.
     *
     * @param meetingDocumentDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingDocumentDTO save(MeetingDocumentDTO meetingDocumentDTO) {
        LOG.debug("Request to save MeetingDocument : {}", meetingDocumentDTO);
        MeetingDocument meetingDocument = meetingDocumentMapper.toEntity(meetingDocumentDTO);
        meetingDocument = meetingDocumentRepository.save(meetingDocument);
        return meetingDocumentMapper.toDto(meetingDocument);
    }

    /**
     * Update a meetingDocument.
     *
     * @param meetingDocumentDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingDocumentDTO update(MeetingDocumentDTO meetingDocumentDTO) {
        LOG.debug("Request to update MeetingDocument : {}", meetingDocumentDTO);
        MeetingDocument meetingDocument = meetingDocumentMapper.toEntity(meetingDocumentDTO);
        meetingDocument = meetingDocumentRepository.save(meetingDocument);
        return meetingDocumentMapper.toDto(meetingDocument);
    }

    /**
     * Partially update a meetingDocument.
     *
     * @param meetingDocumentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MeetingDocumentDTO> partialUpdate(MeetingDocumentDTO meetingDocumentDTO) {
        LOG.debug("Request to partially update MeetingDocument : {}", meetingDocumentDTO);

        return meetingDocumentRepository
            .findById(meetingDocumentDTO.getId())
            .map(existingMeetingDocument -> {
                meetingDocumentMapper.partialUpdate(existingMeetingDocument, meetingDocumentDTO);

                return existingMeetingDocument;
            })
            .map(meetingDocumentRepository::save)
            .map(meetingDocumentMapper::toDto);
    }

    /**
     * Get all the meetingDocuments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MeetingDocumentDTO> findAll() {
        LOG.debug("Request to get all MeetingDocuments");
        return meetingDocumentRepository
            .findAll()
            .stream()
            .map(meetingDocumentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the meetingDocuments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MeetingDocumentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return meetingDocumentRepository.findAllWithEagerRelationships(pageable).map(meetingDocumentMapper::toDto);
    }

    /**
     * Get one meetingDocument by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MeetingDocumentDTO> findOne(Long id) {
        LOG.debug("Request to get MeetingDocument : {}", id);
        return meetingDocumentRepository.findOneWithEagerRelationships(id).map(meetingDocumentMapper::toDto);
    }

    /**
     * Delete the meetingDocument by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MeetingDocument : {}", id);
        meetingDocumentRepository.deleteById(id);
    }
}
