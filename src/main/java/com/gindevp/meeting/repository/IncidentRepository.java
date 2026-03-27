package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.Incident;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Incident entity.
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    @Query(
        value = "select i from Incident i left join fetch i.reportedBy left join fetch i.assignedTo left join fetch i.meeting " +
        "where (:status is null or :status = '' or i.status = :status) " +
        "and (:severity is null or :severity = '' or i.severity = :severity)",
        countQuery = "select count(i) from Incident i " +
        "where (:status is null or :status = '' or i.status = :status) " +
        "and (:severity is null or :severity = '' or i.severity = :severity)"
    )
    Page<Incident> findAllWithRelations(@Param("status") String status, @Param("severity") String severity, Pageable pageable);

    @Query("select incident from Incident incident where incident.reportedBy.login = ?#{authentication.name}")
    List<Incident> findByReportedByIsCurrentUser();

    default Optional<Incident> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Incident> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Incident> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select incident from Incident incident left join fetch incident.reportedBy left join fetch incident.assignedTo",
        countQuery = "select count(incident) from Incident incident"
    )
    Page<Incident> findAllWithToOneRelationships(Pageable pageable);

    @Query("select incident from Incident incident left join fetch incident.reportedBy left join fetch incident.assignedTo")
    List<Incident> findAllWithToOneRelationships();

    @Query(
        "select incident from Incident incident left join fetch incident.reportedBy left join fetch incident.assignedTo left join fetch incident.meeting where incident.id = :id"
    )
    Optional<Incident> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select count(i) from Incident i where i.meeting.id = :meetingId")
    long countByMeetingId(@Param("meetingId") Long meetingId);
}
