package com.gindevp.meeting.repository;

import com.gindevp.meeting.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByActivationKey(String activationKey);
    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);
    Optional<User> findOneByResetKey(String resetKey);
    Optional<User> findOneByEmailIgnoreCase(String email);
    Optional<User> findOneByLogin(String login);

    @EntityGraph(attributePaths = { "authorities", "department" })
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    List<User> findByDepartmentIdAndActivatedTrue(Long departmentId);

    @EntityGraph(attributePaths = "authorities")
    @Query("select u from User u join u.authorities a where a.name = 'ROLE_ADMIN' and u.activated = true")
    List<User> findAllAdminsActivated();

    @EntityGraph(attributePaths = "authorities")
    @Query(
        "select u from User u join u.authorities a join u.department d where a.name = 'ROLE_SECRETARY' and u.activated = true and d.id = :departmentId"
    )
    List<User> findSecretariesByDepartmentId(@Param("departmentId") Long departmentId);
}
