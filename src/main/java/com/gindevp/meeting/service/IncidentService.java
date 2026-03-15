package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Incident;
import com.gindevp.meeting.repository.IncidentRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.service.dto.IncidentDTO;
import com.gindevp.meeting.service.mapper.IncidentMapper;
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
 * Service Implementation for managing {@link com.gindevp.meeting.domain.Incident}.
 */
@Service
@Transactional
public class IncidentService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentService.class);

    private final IncidentRepository incidentRepository;

    private final IncidentMapper incidentMapper;

    private final MeetingRepository meetingRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public IncidentService(
        IncidentRepository incidentRepository,
        IncidentMapper incidentMapper,
        MeetingRepository meetingRepository,
        UserRepository userRepository,
        NotificationService notificationService
    ) {
        this.incidentRepository = incidentRepository;
        this.incidentMapper = incidentMapper;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Save a incident.
     *
     * @param incidentDTO the entity to save.
     * @return the persisted entity.
     */
    public IncidentDTO save(IncidentDTO incidentDTO) {
        LOG.debug("Request to save Incident : {}", incidentDTO);
        Incident incident = incidentMapper.toEntity(incidentDTO);
        if (incidentDTO.getMeeting() != null && incidentDTO.getMeeting().getId() != null) {
            incident.setMeeting(meetingRepository.getReferenceById(incidentDTO.getMeeting().getId()));
        } else {
            incident.setMeeting(null);
        }
        if (incidentDTO.getReportedBy() != null && incidentDTO.getReportedBy().getId() != null) {
            incident.setReportedBy(userRepository.getReferenceById(incidentDTO.getReportedBy().getId()));
        }
        if (incidentDTO.getAssignedTo() != null && incidentDTO.getAssignedTo().getId() != null) {
            incident.setAssignedTo(userRepository.getReferenceById(incidentDTO.getAssignedTo().getId()));
        } else {
            incident.setAssignedTo(null);
        }
        incident = incidentRepository.save(incident);
        IncidentDTO dto = incidentMapper.toDto(incident);
        notifyAssignedUserIfPresent(incident);
        return dto;
    }

    /**
     * Update a incident.
     *
     * @param incidentDTO the entity to save.
     * @return the persisted entity.
     */
    public IncidentDTO update(IncidentDTO incidentDTO) {
        LOG.debug("Request to update Incident : {}", incidentDTO);
        Incident incident = incidentMapper.toEntity(incidentDTO);
        if (incidentDTO.getMeeting() != null && incidentDTO.getMeeting().getId() != null) {
            incident.setMeeting(meetingRepository.getReferenceById(incidentDTO.getMeeting().getId()));
        } else {
            incident.setMeeting(null);
        }
        if (incidentDTO.getReportedBy() != null && incidentDTO.getReportedBy().getId() != null) {
            incident.setReportedBy(userRepository.getReferenceById(incidentDTO.getReportedBy().getId()));
        }
        if (incidentDTO.getAssignedTo() != null && incidentDTO.getAssignedTo().getId() != null) {
            incident.setAssignedTo(userRepository.getReferenceById(incidentDTO.getAssignedTo().getId()));
        } else {
            incident.setAssignedTo(null);
        }
        incident = incidentRepository.save(incident);
        IncidentDTO dto = incidentMapper.toDto(incident);
        notifyAssignedUserIfPresent(incident);
        return dto;
    }

    /**
     * Partially update a incident.
     *
     * @param incidentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IncidentDTO> partialUpdate(IncidentDTO incidentDTO) {
        LOG.debug("Request to partially update Incident : {}", incidentDTO);

        return incidentRepository
            .findById(incidentDTO.getId())
            .map(existingIncident -> {
                incidentMapper.partialUpdate(existingIncident, incidentDTO);

                return existingIncident;
            })
            .map(incidentRepository::save)
            .map(incidentMapper::toDto);
    }

    /**
     * Get all the incidents.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<IncidentDTO> findAll() {
        LOG.debug("Request to get all Incidents");
        return incidentRepository.findAll().stream().map(incidentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all incidents with optional filters and pagination.
     */
    @Transactional(readOnly = true)
    public Page<IncidentDTO> findAll(Pageable pageable, String status, String severity) {
        LOG.debug("Request to get Incidents with filters");
        String statusFilter = (status == null || status.isBlank()) ? null : status;
        String severityFilter = (severity == null || severity.isBlank()) ? null : severity;
        return incidentRepository
            .findAllWithRelations(statusFilter, severityFilter, pageable)
            .map(incident -> {
                IncidentDTO dto = incidentMapper.toDto(incident);
                if (dto.getMeeting() != null && incident.getMeeting() != null) {
                    dto.getMeeting().setTitle(incident.getMeeting().getTitle());
                }
                return dto;
            });
    }

    /**
     * Get all the incidents with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<IncidentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return incidentRepository.findAllWithEagerRelationships(pageable).map(incidentMapper::toDto);
    }

    /**
     * Get one incident by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IncidentDTO> findOne(Long id) {
        LOG.debug("Request to get Incident : {}", id);
        return incidentRepository
            .findOneWithEagerRelationships(id)
            .map(incident -> {
                IncidentDTO dto = incidentMapper.toDto(incident);
                if (dto.getMeeting() != null && incident.getMeeting() != null) {
                    dto.getMeeting().setTitle(incident.getMeeting().getTitle());
                }
                return dto;
            });
    }

    /**
     * Delete the incident by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Incident : {}", id);
        incidentRepository.deleteById(id);
    }

    /**
     * Notify the assigned user when an incident is created or updated with an assignee.
     */
    private void notifyAssignedUserIfPresent(Incident incident) {
        if (incident == null || incident.getAssignedTo() == null || incident.getAssignedTo().getId() == null) return;
        Long assigneeId = incident.getAssignedTo().getId();
        String title = "Sự cố được giao cho bạn: " + incident.getTitle();
        String message = "Bạn được chỉ định xử lý sự cố: " + incident.getTitle();
        String linkUrl = "/incidents";
        try {
            notificationService.create(assigneeId, title, message, "INCIDENT_ASSIGNED", linkUrl);
        } catch (Exception e) {
            LOG.warn("Could not create notification for incident assignee: {}", e.getMessage());
        }
    }
}
