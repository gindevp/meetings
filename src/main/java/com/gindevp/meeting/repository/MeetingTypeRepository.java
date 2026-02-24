package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.MeetingType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MeetingType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MeetingTypeRepository extends JpaRepository<MeetingType, Long> {}
