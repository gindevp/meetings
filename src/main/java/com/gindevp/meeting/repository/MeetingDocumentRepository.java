package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.MeetingDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MeetingDocument entity.
 */
@Repository
public interface MeetingDocumentRepository extends JpaRepository<MeetingDocument, Long> {
    @Query("select meetingDocument from MeetingDocument meetingDocument where meetingDocument.uploadedBy.login = ?#{authentication.name}")
    List<MeetingDocument> findByUploadedByIsCurrentUser();

    default Optional<MeetingDocument> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MeetingDocument> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MeetingDocument> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select meetingDocument from MeetingDocument meetingDocument left join fetch meetingDocument.uploadedBy",
        countQuery = "select count(meetingDocument) from MeetingDocument meetingDocument"
    )
    Page<MeetingDocument> findAllWithToOneRelationships(Pageable pageable);

    @Query("select meetingDocument from MeetingDocument meetingDocument left join fetch meetingDocument.uploadedBy")
    List<MeetingDocument> findAllWithToOneRelationships();

    @Query(
        "select meetingDocument from MeetingDocument meetingDocument left join fetch meetingDocument.uploadedBy where meetingDocument.id =:id"
    )
    Optional<MeetingDocument> findOneWithToOneRelationships(@Param("id") Long id);
}
