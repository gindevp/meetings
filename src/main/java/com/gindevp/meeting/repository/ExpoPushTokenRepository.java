package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.ExpoPushToken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpoPushTokenRepository extends JpaRepository<ExpoPushToken, Long> {
    List<ExpoPushToken> findByUserId(Long userId);

    List<ExpoPushToken> findByToken(String token);

    void deleteByToken(String token);
}
