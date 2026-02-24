package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.MeetingLevel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MeetingLevel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MeetingLevelRepository extends JpaRepository<MeetingLevel, Long> {}
