package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.MeetingTask;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MeetingTask entity.
 */
@Repository
public interface MeetingTaskRepository extends JpaRepository<MeetingTask, Long> {
    @Query("select meetingTask from MeetingTask meetingTask where meetingTask.assignee.login = ?#{authentication.name}")
    List<MeetingTask> findByAssigneeIsCurrentUser();

    @Query("select meetingTask from MeetingTask meetingTask where meetingTask.assignedBy.login = ?#{authentication.name}")
    List<MeetingTask> findByAssignedByIsCurrentUser();

    default Optional<MeetingTask> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MeetingTask> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MeetingTask> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select meetingTask from MeetingTask meetingTask left join fetch meetingTask.assignee left join fetch meetingTask.assignedBy",
        countQuery = "select count(meetingTask) from MeetingTask meetingTask"
    )
    Page<MeetingTask> findAllWithToOneRelationships(Pageable pageable);

    @Query("select meetingTask from MeetingTask meetingTask left join fetch meetingTask.assignee left join fetch meetingTask.assignedBy")
    List<MeetingTask> findAllWithToOneRelationships();

    @Query(
        "select meetingTask from MeetingTask meetingTask left join fetch meetingTask.assignee left join fetch meetingTask.assignedBy where meetingTask.id =:id"
    )
    Optional<MeetingTask> findOneWithToOneRelationships(@Param("id") Long id);
}
