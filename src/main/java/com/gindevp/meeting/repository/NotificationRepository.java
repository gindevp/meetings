package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.Notification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.readAt IS NULL")
    long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.readAt IS NULL")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
}
