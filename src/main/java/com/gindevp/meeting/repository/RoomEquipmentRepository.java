package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.RoomEquipment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RoomEquipment entity.
 */
@Repository
public interface RoomEquipmentRepository extends JpaRepository<RoomEquipment, Long> {
    default Optional<RoomEquipment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RoomEquipment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RoomEquipment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select roomEquipment from RoomEquipment roomEquipment left join fetch roomEquipment.room left join fetch roomEquipment.equipment",
        countQuery = "select count(roomEquipment) from RoomEquipment roomEquipment"
    )
    Page<RoomEquipment> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select roomEquipment from RoomEquipment roomEquipment left join fetch roomEquipment.room left join fetch roomEquipment.equipment"
    )
    List<RoomEquipment> findAllWithToOneRelationships();

    @Query(
        "select roomEquipment from RoomEquipment roomEquipment left join fetch roomEquipment.room left join fetch roomEquipment.equipment where roomEquipment.id =:id"
    )
    Optional<RoomEquipment> findOneWithToOneRelationships(@Param("id") Long id);
}
