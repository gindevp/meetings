package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Meeting entity.
 */
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("select meeting from Meeting meeting where meeting.requester.login = ?#{authentication.name}")
    List<Meeting> findByRequesterIsCurrentUser();

    @Query("select meeting from Meeting meeting where meeting.host.login = ?#{authentication.name}")
    List<Meeting> findByHostIsCurrentUser();

    default Optional<Meeting> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Meeting> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Meeting> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select meeting from Meeting meeting left join fetch meeting.type left join fetch meeting.level left join fetch meeting.organizerDepartment left join fetch meeting.room left join fetch meeting.requester left join fetch meeting.host",
        countQuery = "select count(meeting) from Meeting meeting"
    )
    Page<Meeting> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select meeting from Meeting meeting left join fetch meeting.type left join fetch meeting.level left join fetch meeting.organizerDepartment left join fetch meeting.room left join fetch meeting.requester left join fetch meeting.host"
    )
    List<Meeting> findAllWithToOneRelationships();

    @Query(
        "select meeting from Meeting meeting left join fetch meeting.type left join fetch meeting.level left join fetch meeting.organizerDepartment left join fetch meeting.room left join fetch meeting.requester left join fetch meeting.host where meeting.id =:id"
    )
    Optional<Meeting> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        """
        select count(m) from Meeting m where m.room.id = :roomId and m.status in :activeStatuses and m.startTime < :endTime and m.endTime > :startTime and (:excludeMeetingId is null or m.id <> :excludeMeetingId) """
    )
    long countRoomConflicts(
        @Param("roomId") Long roomId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        @Param("activeStatuses") Collection<MeetingStatus> activeStatuses,
        @Param("excludeMeetingId") Long excludeMeetingId
    );

    @Query(
        """
        select count(m) from Meeting m where m.host.id = :hostId and m.status in :activeStatuses and m.startTime < :endTime and m.endTime > :startTime and (:excludeMeetingId is null or m.id <> :excludeMeetingId) """
    )
    long countHostConflicts(
        @Param("hostId") Long hostId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        @Param("activeStatuses") Collection<MeetingStatus> activeStatuses,
        @Param("excludeMeetingId") Long excludeMeetingId
    );

    @Query(
        """
        select count(distinct m) from Meeting m join m.participants p where p.user.id in :userIds and p.isRequired = true and m.status in :activeStatuses and m.startTime < :endTime and m.endTime > :startTime and (:excludeMeetingId is null or m.id <> :excludeMeetingId) """
    )
    long countKeyParticipantConflicts(
        @Param("userIds") Collection<Long> userIds,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        @Param("activeStatuses") Collection<MeetingStatus> activeStatuses,
        @Param("excludeMeetingId") Long excludeMeetingId
    );
}
