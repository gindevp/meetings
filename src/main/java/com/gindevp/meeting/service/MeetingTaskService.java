package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.MeetingTask;
import com.gindevp.meeting.repository.DepartmentRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.MeetingTaskRepository;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.service.dto.MeetingTaskDTO;
import com.gindevp.meeting.service.mapper.MeetingTaskMapper;
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
 * Service Implementation for managing {@link com.gindevp.meeting.domain.MeetingTask}.
 */
@Service
@Transactional
public class MeetingTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingTaskService.class);

    private final MeetingTaskRepository meetingTaskRepository;

    private final MeetingTaskMapper meetingTaskMapper;

    private final MeetingRepository meetingRepository;

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    public MeetingTaskService(
        MeetingTaskRepository meetingTaskRepository,
        MeetingTaskMapper meetingTaskMapper,
        MeetingRepository meetingRepository,
        UserRepository userRepository,
        DepartmentRepository departmentRepository
    ) {
        this.meetingTaskRepository = meetingTaskRepository;
        this.meetingTaskMapper = meetingTaskMapper;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * Save a meetingTask. Resolves ManyToOne references from repositories to avoid
     * persisting transient entities (which can cause 500 errors).
     *
     * @param meetingTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingTaskDTO save(MeetingTaskDTO meetingTaskDTO) {
        LOG.debug("Request to save MeetingTask : {}", meetingTaskDTO);
        MeetingTask meetingTask = meetingTaskMapper.toEntity(meetingTaskDTO);
        if (meetingTaskDTO.getMeeting() != null && meetingTaskDTO.getMeeting().getId() != null) {
            meetingTask.setMeeting(meetingRepository.getReferenceById(meetingTaskDTO.getMeeting().getId()));
        }
        if (meetingTaskDTO.getAssignedBy() != null && meetingTaskDTO.getAssignedBy().getId() != null) {
            meetingTask.setAssignedBy(userRepository.getReferenceById(meetingTaskDTO.getAssignedBy().getId()));
        }
        if (meetingTaskDTO.getAssignee() != null && meetingTaskDTO.getAssignee().getId() != null) {
            meetingTask.setAssignee(userRepository.getReferenceById(meetingTaskDTO.getAssignee().getId()));
        }
        if (meetingTaskDTO.getDepartment() != null && meetingTaskDTO.getDepartment().getId() != null) {
            meetingTask.setDepartment(departmentRepository.getReferenceById(meetingTaskDTO.getDepartment().getId()));
        }
        meetingTask = meetingTaskRepository.save(meetingTask);
        return meetingTaskMapper.toDto(meetingTask);
    }

    /**
     * Update a meetingTask.
     *
     * @param meetingTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingTaskDTO update(MeetingTaskDTO meetingTaskDTO) {
        LOG.debug("Request to update MeetingTask : {}", meetingTaskDTO);
        MeetingTask meetingTask = meetingTaskMapper.toEntity(meetingTaskDTO);
        meetingTask = meetingTaskRepository.save(meetingTask);
        return meetingTaskMapper.toDto(meetingTask);
    }

    /**
     * Partially update a meetingTask.
     *
     * @param meetingTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MeetingTaskDTO> partialUpdate(MeetingTaskDTO meetingTaskDTO) {
        LOG.debug("Request to partially update MeetingTask : {}", meetingTaskDTO);

        return meetingTaskRepository
            .findById(meetingTaskDTO.getId())
            .map(existingMeetingTask -> {
                meetingTaskMapper.partialUpdate(existingMeetingTask, meetingTaskDTO);

                return existingMeetingTask;
            })
            .map(meetingTaskRepository::save)
            .map(meetingTaskMapper::toDto);
    }

    /**
     * Get all the meetingTasks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MeetingTaskDTO> findAll() {
        LOG.debug("Request to get all MeetingTasks");
        return meetingTaskRepository.findAll().stream().map(meetingTaskMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the meetingTasks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MeetingTaskDTO> findAllWithEagerRelationships(Pageable pageable) {
        return meetingTaskRepository.findAllWithEagerRelationships(pageable).map(meetingTaskMapper::toDto);
    }

    /**
     * Get one meetingTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MeetingTaskDTO> findOne(Long id) {
        LOG.debug("Request to get MeetingTask : {}", id);
        return meetingTaskRepository.findOneWithEagerRelationships(id).map(meetingTaskMapper::toDto);
    }

    /**
     * Delete the meetingTask by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MeetingTask : {}", id);
        meetingTaskRepository.deleteById(id);
    }
}
