package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.AgendaItem;
import com.gindevp.meeting.repository.AgendaItemRepository;
import com.gindevp.meeting.service.dto.AgendaItemDTO;
import com.gindevp.meeting.service.mapper.AgendaItemMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.meeting.domain.AgendaItem}.
 */
@Service
@Transactional
public class AgendaItemService {

    private static final Logger LOG = LoggerFactory.getLogger(AgendaItemService.class);

    private final AgendaItemRepository agendaItemRepository;

    private final AgendaItemMapper agendaItemMapper;

    public AgendaItemService(AgendaItemRepository agendaItemRepository, AgendaItemMapper agendaItemMapper) {
        this.agendaItemRepository = agendaItemRepository;
        this.agendaItemMapper = agendaItemMapper;
    }

    /**
     * Save a agendaItem.
     *
     * @param agendaItemDTO the entity to save.
     * @return the persisted entity.
     */
    public AgendaItemDTO save(AgendaItemDTO agendaItemDTO) {
        LOG.debug("Request to save AgendaItem : {}", agendaItemDTO);
        AgendaItem agendaItem = agendaItemMapper.toEntity(agendaItemDTO);
        agendaItem = agendaItemRepository.save(agendaItem);
        return agendaItemMapper.toDto(agendaItem);
    }

    /**
     * Update a agendaItem.
     *
     * @param agendaItemDTO the entity to save.
     * @return the persisted entity.
     */
    public AgendaItemDTO update(AgendaItemDTO agendaItemDTO) {
        LOG.debug("Request to update AgendaItem : {}", agendaItemDTO);
        AgendaItem agendaItem = agendaItemMapper.toEntity(agendaItemDTO);
        agendaItem = agendaItemRepository.save(agendaItem);
        return agendaItemMapper.toDto(agendaItem);
    }

    /**
     * Partially update a agendaItem.
     *
     * @param agendaItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AgendaItemDTO> partialUpdate(AgendaItemDTO agendaItemDTO) {
        LOG.debug("Request to partially update AgendaItem : {}", agendaItemDTO);

        return agendaItemRepository
            .findById(agendaItemDTO.getId())
            .map(existingAgendaItem -> {
                agendaItemMapper.partialUpdate(existingAgendaItem, agendaItemDTO);

                return existingAgendaItem;
            })
            .map(agendaItemRepository::save)
            .map(agendaItemMapper::toDto);
    }

    /**
     * Get all the agendaItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AgendaItemDTO> findAll() {
        LOG.debug("Request to get all AgendaItems");
        return agendaItemRepository.findAll().stream().map(agendaItemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one agendaItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AgendaItemDTO> findOne(Long id) {
        LOG.debug("Request to get AgendaItem : {}", id);
        return agendaItemRepository.findById(id).map(agendaItemMapper::toDto);
    }

    /**
     * Delete the agendaItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AgendaItem : {}", id);
        agendaItemRepository.deleteById(id);
    }
}
