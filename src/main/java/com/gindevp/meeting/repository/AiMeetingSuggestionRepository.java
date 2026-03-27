package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.AiMeetingSuggestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AiMeetingSuggestionRepository extends JpaRepository<AiMeetingSuggestion, Long> {
    @Query("select s from AiMeetingSuggestion s where s.meeting.id = :meetingId order by s.createdDate desc")
    List<AiMeetingSuggestion> findByMeetingIdOrderByCreatedDateDesc(@Param("meetingId") Long meetingId);

    @Query("select s from AiMeetingSuggestion s where s.meeting.id = :meetingId and s.status = 'DRAFT' order by s.createdDate desc")
    Optional<AiMeetingSuggestion> findLatestDraftByMeetingId(@Param("meetingId") Long meetingId);
}
