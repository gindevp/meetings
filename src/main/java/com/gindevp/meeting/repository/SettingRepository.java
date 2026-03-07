package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.Setting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    List<Setting> findByUserIdAndCategory(Long userId, String category);

    List<Setting> findByUserId(Long userId);

    @Query("SELECT s FROM Setting s WHERE s.userId IS NULL AND s.category = :category")
    List<Setting> findSystemSettingsByCategory(@Param("category") String category);

    @Query("SELECT s FROM Setting s WHERE s.userId IS NULL")
    List<Setting> findAllSystemSettings();

    Optional<Setting> findByUserIdAndKey(Long userId, String key);

    @Query("SELECT s FROM Setting s WHERE s.userId IS NULL AND s.key = :key")
    Optional<Setting> findSystemSettingByKey(@Param("key") String key);
}
