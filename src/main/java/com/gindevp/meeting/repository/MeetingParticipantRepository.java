package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.MeetingParticipant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MeetingParticipant entity.
 */
@Repository
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {
    @Query(
        "select meetingParticipant from MeetingParticipant meetingParticipant where meetingParticipant.user.login = ?#{authentication.name}"
    )
    List<MeetingParticipant> findByUserIsCurrentUser();

    default Optional<MeetingParticipant> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MeetingParticipant> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MeetingParticipant> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select meetingParticipant from MeetingParticipant meetingParticipant left join fetch meetingParticipant.user",
        countQuery = "select count(meetingParticipant) from MeetingParticipant meetingParticipant"
    )
    Page<MeetingParticipant> findAllWithToOneRelationships(Pageable pageable);

    @Query("select meetingParticipant from MeetingParticipant meetingParticipant left join fetch meetingParticipant.user")
    List<MeetingParticipant> findAllWithToOneRelationships();

    @Query(
        "select meetingParticipant from MeetingParticipant meetingParticipant left join fetch meetingParticipant.user where meetingParticipant.id =:id"
    )
    Optional<MeetingParticipant> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select p from MeetingParticipant p left join fetch p.user left join fetch p.meeting where p.id = :id")
    Optional<MeetingParticipant> findByIdWithMeetingAndUser(@Param("id") Long id);

    @Query(
        "select p from MeetingParticipant p left join fetch p.user left join fetch p.department left join fetch p.meeting left join fetch p.meeting.host left join fetch p.meeting.requester left join fetch p.meeting.secretary left join fetch p.meeting.organizerDepartment left join fetch p.meeting.level"
    )
    List<MeetingParticipant> findAllWithMeetingAndUser();

    @Modifying
    @Query("delete from MeetingParticipant mp where mp.meeting.id = :meetingId")
    void deleteByMeetingId(@Param("meetingId") Long meetingId);

    @Query("select count(p) from MeetingParticipant p where p.meeting.id = :meetingId and p.user.login = ?#{authentication.name}")
    long countByMeetingIdAndCurrentUser(@Param("meetingId") Long meetingId);

    @Query("select p from MeetingParticipant p left join fetch p.user left join fetch p.department where p.meeting.id = :meetingId")
    List<MeetingParticipant> findByMeetingId(@Param("meetingId") Long meetingId);

    @Query(
        "select count(p) from MeetingParticipant p where p.meeting.id = :meetingId and (" +
        "p.department.id = :departmentId or (p.user is not null and p.user.department.id = :departmentId))"
    )
    long countByMeetingIdAndDepartmentId(@Param("meetingId") Long meetingId, @Param("departmentId") Long departmentId);
}
