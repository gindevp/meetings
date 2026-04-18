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
 * Truy vấn JPA cho {@link MeetingParticipant}: eager fetch, đếm theo cuộc họp/user/phòng ban.
 */
@Repository
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {
    /** Các participant mà user đăng nhập hiện tại là user được gán. */
    @Query(
        "select meetingParticipant from MeetingParticipant meetingParticipant where meetingParticipant.user.login = ?#{authentication.name}"
    )
    List<MeetingParticipant> findByUserIsCurrentUser();

    /** Alias: một bản ghi kèm eager quan hệ 1-1 (theo convention JHipster). */
    default Optional<MeetingParticipant> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    /** Danh sách tất cả với eager user (không phân trang). */
    default List<MeetingParticipant> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    /** Phân trang với eager user. */
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

    /** Một participant kèm user và meeting (dùng cho điểm danh, phản hồi lời mời). */
    @Query("select p from MeetingParticipant p left join fetch p.user left join fetch p.meeting where p.id = :id")
    Optional<MeetingParticipant> findByIdWithMeetingAndUser(@Param("id") Long id);

    /** Participant theo phòng ban + meeting + cấp họp (dùng từ chối lời mời phòng ban). */
    @Query(
        "select p from MeetingParticipant p left join fetch p.department left join fetch p.meeting left join fetch p.meeting.level where p.id = :id"
    )
    Optional<MeetingParticipant> findByIdWithMeetingAndDepartment(@Param("id") Long id);

    /** Toàn bộ participant với đủ quan hệ meeting (host, secretary, level...). */
    @Query(
        "select p from MeetingParticipant p left join fetch p.user left join fetch p.department left join fetch p.meeting left join fetch p.meeting.host left join fetch p.meeting.requester left join fetch p.meeting.secretary left join fetch p.meeting.organizerDepartment left join fetch p.meeting.level"
    )
    List<MeetingParticipant> findAllWithMeetingAndUser();

    /** Xóa hết người tham gia của một cuộc họp (khi xóa meeting hoặc reset). */
    @Modifying
    @Query("delete from MeetingParticipant mp where mp.meeting.id = :meetingId")
    void deleteByMeetingId(@Param("meetingId") Long meetingId);

    /** Số participant của cuộc họp mà user hiện tại tham gia với tư cách user. */
    @Query("select count(p) from MeetingParticipant p where p.meeting.id = :meetingId and p.user.login = ?#{authentication.name}")
    long countByMeetingIdAndCurrentUser(@Param("meetingId") Long meetingId);

    /** Danh sách participant của một cuộc họp (user + department). */
    @Query("select p from MeetingParticipant p left join fetch p.user left join fetch p.department where p.meeting.id = :meetingId")
    List<MeetingParticipant> findByMeetingId(@Param("meetingId") Long meetingId);

    /** Tổng số người tham gia của cuộc họp. */
    @Query("select count(p) from MeetingParticipant p where p.meeting.id = :meetingId")
    long countByMeetingId(@Param("meetingId") Long meetingId);

    /** Các participant của user đăng nhập, kèm meeting. */
    @Query("select p from MeetingParticipant p left join fetch p.meeting where p.user.login = ?#{authentication.name}")
    List<MeetingParticipant> findByCurrentUserWithMeeting();

    /**
     * Đếm participant liên quan tới một phòng ban trong cuộc họp (theo department trực tiếp hoặc user thuộc phòng ban).
     */
    @Query(
        "select count(p) from MeetingParticipant p where p.meeting.id = :meetingId and (" +
        "p.department.id = :departmentId or (p.user is not null and p.user.department.id = :departmentId))"
    )
    long countByMeetingIdAndDepartmentId(@Param("meetingId") Long meetingId, @Param("departmentId") Long departmentId);
}
