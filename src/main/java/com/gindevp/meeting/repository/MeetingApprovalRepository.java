package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.MeetingApproval;
import com.gindevp.meeting.domain.enumeration.ApprovalDecision;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MeetingApproval entity.
 */
@Repository
public interface MeetingApprovalRepository extends JpaRepository<MeetingApproval, Long> {
    @Query("select meetingApproval from MeetingApproval meetingApproval where meetingApproval.decidedBy.login = ?#{authentication.name}")
    List<MeetingApproval> findByDecidedByIsCurrentUser();

    default Optional<MeetingApproval> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MeetingApproval> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MeetingApproval> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select meetingApproval from MeetingApproval meetingApproval left join fetch meetingApproval.decidedBy",
        countQuery = "select count(meetingApproval) from MeetingApproval meetingApproval"
    )
    Page<MeetingApproval> findAllWithToOneRelationships(Pageable pageable);

    @Query("select meetingApproval from MeetingApproval meetingApproval left join fetch meetingApproval.decidedBy")
    List<MeetingApproval> findAllWithToOneRelationships();

    @Query(
        "select meetingApproval from MeetingApproval meetingApproval left join fetch meetingApproval.decidedBy where meetingApproval.id =:id"
    )
    Optional<MeetingApproval> findOneWithToOneRelationships(@Param("id") Long id);

    boolean existsByMeetingIdAndStepAndDecisionAndDecidedAtGreaterThanEqual(
        Long meetingId,
        Integer step,
        ApprovalDecision decision,
        Instant decidedAt
    );
}
