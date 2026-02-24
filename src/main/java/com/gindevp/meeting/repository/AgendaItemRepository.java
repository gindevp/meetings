package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.AgendaItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AgendaItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {}
