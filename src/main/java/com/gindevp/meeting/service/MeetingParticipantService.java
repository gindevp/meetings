package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingParticipant;
import com.gindevp.meeting.domain.MeetingTask;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.domain.enumeration.AttendanceStatus;
import com.gindevp.meeting.domain.enumeration.ConfirmationStatus;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import com.gindevp.meeting.domain.enumeration.ParticipantRole;
import com.gindevp.meeting.domain.enumeration.TaskStatus;
import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.MeetingTaskRepository;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.service.dto.MeetingParticipantDTO;
import com.gindevp.meeting.service.mapper.MeetingParticipantMapper;
import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Nghiệp vụ quản lý {@link com.gindevp.meeting.domain.MeetingParticipant}: mời cá nhân/phòng ban,
 * phản hồi lời mời, điểm danh, điểm danh bù, chọn đại diện phòng ban (cấp tổng công ty).
 */
@Service
@Transactional
public class MeetingParticipantService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingParticipantService.class);

    private final MeetingParticipantRepository meetingParticipantRepository;

    private final MeetingParticipantMapper meetingParticipantMapper;

    private final MeetingRepository meetingRepository;

    private final UserRepository userRepository;

    private final MeetingNotificationService meetingNotificationService;
    private final MeetingTaskRepository meetingTaskRepository;

    /**
     * Khởi tạo service với repository, mapper và các phụ thuộc thông báo / task.
     */
    public MeetingParticipantService(
        MeetingParticipantRepository meetingParticipantRepository,
        MeetingParticipantMapper meetingParticipantMapper,
        MeetingRepository meetingRepository,
        UserRepository userRepository,
        MeetingNotificationService meetingNotificationService,
        MeetingTaskRepository meetingTaskRepository
    ) {
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.meetingParticipantMapper = meetingParticipantMapper;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.meetingNotificationService = meetingNotificationService;
        this.meetingTaskRepository = meetingTaskRepository;
    }

    /**
     * Tạo mới người tham gia: kiểm tra chỉ có user hoặc chỉ có phòng ban, rồi lưu DB.
     *
     * @param meetingParticipantDTO dữ liệu người tham gia
     * @return bản ghi sau khi lưu (DTO)
     */
    public MeetingParticipantDTO save(MeetingParticipantDTO meetingParticipantDTO) {
        LOG.debug("Request to save MeetingParticipant : {}", meetingParticipantDTO);
        validateParticipantTarget(meetingParticipantDTO);
        MeetingParticipant meetingParticipant = meetingParticipantMapper.toEntity(meetingParticipantDTO);
        meetingParticipant = meetingParticipantRepository.save(meetingParticipant);
        return meetingParticipantMapper.toDto(meetingParticipant);
    }

    /**
     * Cập nhật toàn bộ thông tin người tham gia (ghi đè theo DTO).
     *
     * @param meetingParticipantDTO dữ liệu cập nhật
     * @return bản ghi sau khi lưu (DTO)
     */
    public MeetingParticipantDTO update(MeetingParticipantDTO meetingParticipantDTO) {
        LOG.debug("Request to update MeetingParticipant : {}", meetingParticipantDTO);
        validateParticipantTarget(meetingParticipantDTO);
        MeetingParticipant meetingParticipant = meetingParticipantMapper.toEntity(meetingParticipantDTO);
        meetingParticipant = meetingParticipantRepository.save(meetingParticipant);
        return meetingParticipantMapper.toDto(meetingParticipant);
    }

    /**
     * Cập nhật một phần trường (merge): các trường null trong DTO được bỏ qua.
     *
     * @param meetingParticipantDTO dữ liệu cần merge
     * @return DTO sau khi lưu nếu tìm thấy bản ghi
     */
    public Optional<MeetingParticipantDTO> partialUpdate(MeetingParticipantDTO meetingParticipantDTO) {
        LOG.debug("Request to partially update MeetingParticipant : {}", meetingParticipantDTO);

        return meetingParticipantRepository
            .findById(meetingParticipantDTO.getId())
            .map(existingMeetingParticipant -> {
                meetingParticipantMapper.partialUpdate(existingMeetingParticipant, meetingParticipantDTO);

                return existingMeetingParticipant;
            })
            .map(meetingParticipantRepository::save)
            .map(meetingParticipantMapper::toDto);
    }

    /**
     * Liệt kê tất cả người tham gia, eager load meeting + user/department liên quan.
     *
     * @return danh sách DTO
     */
    @Transactional(readOnly = true)
    public List<MeetingParticipantDTO> findAll() {
        LOG.debug("Request to get all MeetingParticipants");
        return meetingParticipantRepository
            .findAllWithMeetingAndUser()
            .stream()
            .map(meetingParticipantMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Phân trang danh sách người tham gia (eager user theo truy vấn JHipster).
     *
     * @param pageable tham số phân trang
     * @return một trang DTO
     */
    public Page<MeetingParticipantDTO> findAllWithEagerRelationships(Pageable pageable) {
        return meetingParticipantRepository.findAllWithEagerRelationships(pageable).map(meetingParticipantMapper::toDto);
    }

    /**
     * Lấy một người tham gia theo id (eager các quan hệ cần thiết).
     *
     * @param id khóa chính
     * @return DTO nếu tồn tại
     */
    @Transactional(readOnly = true)
    public Optional<MeetingParticipantDTO> findOne(Long id) {
        LOG.debug("Request to get MeetingParticipant : {}", id);
        return meetingParticipantRepository.findOneWithEagerRelationships(id).map(meetingParticipantMapper::toDto);
    }

    /**
     * Xóa người tham gia theo id (không kiểm tra quyền — dùng nội bộ / admin toàn quyền).
     *
     * @param id khóa chính
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MeetingParticipant : {}", id);
        meetingParticipantRepository.deleteById(id);
    }

    /**
     * Xóa lời mời (participant): admin được xóa bất kỳ lúc nào; người được mời chỉ được xóa bản thân sau khi cuộc họp đã kết thúc.
     */
    public void deleteWithPermission(Long participantId, String currentUserLogin) {
        MeetingParticipant participant = meetingParticipantRepository
            .findByIdWithMeetingAndUser(participantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        User currentUser = userRepository
            .findOneWithAuthoritiesByLogin(currentUserLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getName()));
        if (isAdmin) {
            meetingParticipantRepository.deleteById(participantId);
            return;
        }
        Meeting meeting = participant.getMeeting();
        if (participant.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ người được mời (cá nhân) mới được xóa lời mời");
        }
        if (!currentUserLogin.equals(participant.getUser().getLogin())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ được xóa lời mời của chính mình");
        }
        if (meeting == null || meeting.getEndTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể xóa: cuộc họp chưa có thời gian kết thúc");
        }
        if (Instant.now().isBefore(meeting.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được xóa lời mời sau khi cuộc họp đã kết thúc");
        }
        meetingParticipantRepository.deleteById(participantId);
    }

    /**
     * Người được mời (theo user) xác nhận hoặc từ chối lời mời; từ chối bắt buộc có lý do.
     * Sau khi cuộc họp kết thúc không cho đổi trạng thái xác nhận (chỉ còn điểm danh bù).
     */
    public MeetingParticipantDTO respondToInvitation(
        Long participantId,
        String currentUserLogin,
        ConfirmationStatus confirmationStatus,
        String absentReason
    ) {
        MeetingParticipant participant = meetingParticipantRepository
            .findByIdWithMeetingAndUser(participantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        if (participant.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot respond for department-only participant; assign user first");
        }
        if (!participant.getUser().getLogin().equalsIgnoreCase(currentUserLogin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the invited user can respond to this invitation");
        }
        if (confirmationStatus == ConfirmationStatus.DECLINED && (absentReason == null || absentReason.trim().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Absent reason is required when declining");
        }
        Meeting meeting = participant.getMeeting();
        if (meeting != null && meeting.getEndTime() != null && Instant.now().isAfter(meeting.getEndTime())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Đã quá thời gian cuộc họp, không thể xác nhận tham gia hoặc từ chối. Chỉ được yêu cầu điểm danh bù."
            );
        }
        participant.setConfirmationStatus(confirmationStatus);
        participant.setAbsentReason(confirmationStatus == ConfirmationStatus.DECLINED ? absentReason : null);
        participant = meetingParticipantRepository.save(participant);
        return meetingParticipantMapper.toDto(participant);
    }

    /**
     * Thư ký phòng ban được mời (hoặc admin) từ chối thay cho lời mời theo phòng ban (chưa gán user).
     * Chỉ áp dụng cuộc họp cấp tổng công ty; ghi nhận DECLINED và lý do vắng.
     */
    public MeetingParticipantDTO declineDepartmentInvitation(Long participantId, String currentUserLogin, String absentReason) {
        if (absentReason == null || absentReason.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Absent reason is required when declining");
        }

        MeetingParticipant deptParticipant = meetingParticipantRepository
            .findByIdWithMeetingAndDepartment(participantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));

        if (deptParticipant.getUser() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a department-only participant");
        }
        if (deptParticipant.getDepartment() == null || deptParticipant.getDepartment().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant must be department-based");
        }
        Meeting meeting = deptParticipant.getMeeting();
        if (meeting == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant has no meeting");
        }
        if (!isCorporateLevel(meeting)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting must be company-level");
        }
        if (meeting.getEndTime() != null && Instant.now().isAfter(meeting.getEndTime())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Đã quá thời gian cuộc họp, không thể từ chối. Vui lòng liên hệ quản trị nếu cần."
            );
        }

        User currentUser = userRepository
            .findOneWithAuthoritiesByLogin(currentUserLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getName()));
        boolean isSecretary = currentUser.getAuthorities().stream().anyMatch(a -> "ROLE_SECRETARY".equals(a.getName()));
        boolean sameDepartment =
            currentUser.getDepartment() != null && currentUser.getDepartment().getId().equals(deptParticipant.getDepartment().getId());

        if (!isAdmin && (!isSecretary || !sameDepartment)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Only secretary of the same department or admin can decline department invitation"
            );
        }

        deptParticipant.setConfirmationStatus(ConfirmationStatus.DECLINED);
        deptParticipant.setAbsentReason(absentReason.trim());
        deptParticipant = meetingParticipantRepository.save(deptParticipant);
        LOG.info(
            "Department invitation declined: participantId={}, meetingId={}, departmentId={}, by={}",
            participantId,
            meeting.getId(),
            deptParticipant.getDepartment().getId(),
            currentUserLogin
        );
        return meetingParticipantMapper.toDto(deptParticipant);
    }

    /**
     * Cập nhật trạng thái điểm danh: chủ trì/thư ký (hoặc admin) điểm danh hộ; người tham gia chỉ tự điểm danh bản thân.
     * Sau khi hết giờ kết thúc cuộc họp không cho điểm danh thường (trừ luồng điểm danh bù).
     */
    public MeetingParticipantDTO updateAttendance(Long participantId, String currentUserLogin, AttendanceStatus attendance) {
        MeetingParticipant participant = meetingParticipantRepository
            .findByIdWithMeetingAndUser(participantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        var meeting = participant.getMeeting();
        if (meeting == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant has no meeting");
        }
        if (meeting.getEndTime() != null && Instant.now().isAfter(meeting.getEndTime())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Đã quá thời gian cuộc họp, không thể điểm danh. Chỉ được yêu cầu điểm danh bù."
            );
        }
        boolean isHost = meeting.getHost() != null && currentUserLogin.equalsIgnoreCase(meeting.getHost().getLogin());
        boolean isSecretary = meeting.getSecretary() != null && currentUserLogin.equalsIgnoreCase(meeting.getSecretary().getLogin());
        boolean isSelf = participant.getUser() != null && currentUserLogin.equalsIgnoreCase(participant.getUser().getLogin());

        if (isHost || isSecretary) {
            participant.setAttendance(attendance);
        } else if (isAdmin(currentUserLogin)) {
            participant.setAttendance(attendance);
        } else if (isSelf) {
            participant.setAttendance(attendance);
        } else {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Only host/secretary can mark others; you can only mark yourself present"
            );
        }
        participant = meetingParticipantRepository.save(participant);
        return meetingParticipantMapper.toDto(participant);
    }

    /**
     * Người tham gia yêu cầu điểm danh bù (sau khi cuộc họp đã kết thúc); chỉ chính họ được gọi.
     * Cuộc họp phải đã duyệt (APPROVED); ghi {@code lateCheckInRequestedAt} để chủ trì/thư ký duyệt.
     */
    public MeetingParticipantDTO requestLateCheckIn(Long participantId, String currentUserLogin) {
        MeetingParticipant participant = meetingParticipantRepository
            .findByIdWithMeetingAndUser(participantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        if (participant.getUser() == null || !participant.getUser().getLogin().equalsIgnoreCase(currentUserLogin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the participant can request late check-in for themselves");
        }
        Meeting meeting = participant.getMeeting();
        if (meeting == null || meeting.getStatus() == null || meeting.getStatus() != MeetingStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting must be approved to request late check-in");
        }
        if (meeting.getEndTime() == null || Instant.now().isBefore(meeting.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được yêu cầu điểm danh bù sau khi cuộc họp đã kết thúc");
        }
        if (participant.getAttendance() == AttendanceStatus.PRESENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already marked present");
        }
        participant.setLateCheckInRequestedAt(Instant.now());
        participant = meetingParticipantRepository.save(participant);
        try {
            meetingNotificationService.notifyLateCheckInRequested(participant.getMeeting(), participant.getUser());
        } catch (Exception ex) {
            LOG.warn("Could not notify late check-in request for participant {}: {}", participantId, ex.getMessage());
        }
        return meetingParticipantMapper.toDto(participant);
    }

    /**
     * Chủ trì hoặc thư ký chấp nhận điểm danh bù: đặt điểm danh PRESENT và xóa cờ yêu cầu.
     */
    public MeetingParticipantDTO approveLateCheckIn(Long participantId, String currentUserLogin) {
        MeetingParticipant participant = meetingParticipantRepository
            .findByIdWithMeetingAndUser(participantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        Meeting meeting = participant.getMeeting();
        if (meeting == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant has no meeting");
        boolean isHost = meeting.getHost() != null && currentUserLogin.equalsIgnoreCase(meeting.getHost().getLogin());
        boolean isSecretary = meeting.getSecretary() != null && currentUserLogin.equalsIgnoreCase(meeting.getSecretary().getLogin());
        if (!isHost && !isSecretary && !isAdmin(currentUserLogin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only host or secretary can approve late check-in");
        }
        if (participant.getLateCheckInRequestedAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No pending late check-in request");
        }
        participant.setAttendance(AttendanceStatus.PRESENT);
        participant.setLateCheckInRequestedAt(null);
        participant = meetingParticipantRepository.save(participant);
        try {
            meetingNotificationService.notifyLateCheckInDecision(participant.getMeeting(), participant.getUser(), true);
        } catch (Exception ex) {
            LOG.warn("Could not notify late check-in approval for participant {}: {}", participantId, ex.getMessage());
        }
        return meetingParticipantMapper.toDto(participant);
    }

    /**
     * Chủ trì hoặc thư ký từ chối điểm danh bù: chỉ xóa yêu cầu (không đổi điểm danh).
     */
    public MeetingParticipantDTO rejectLateCheckIn(Long participantId, String currentUserLogin) {
        MeetingParticipant participant = meetingParticipantRepository
            .findByIdWithMeetingAndUser(participantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        Meeting meeting = participant.getMeeting();
        if (meeting == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant has no meeting");
        boolean isHost = meeting.getHost() != null && currentUserLogin.equalsIgnoreCase(meeting.getHost().getLogin());
        boolean isSecretary = meeting.getSecretary() != null && currentUserLogin.equalsIgnoreCase(meeting.getSecretary().getLogin());
        if (!isHost && !isSecretary && !isAdmin(currentUserLogin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only host or secretary can reject late check-in");
        }
        participant.setLateCheckInRequestedAt(null);
        participant = meetingParticipantRepository.save(participant);
        try {
            meetingNotificationService.notifyLateCheckInDecision(participant.getMeeting(), participant.getUser(), false);
        } catch (Exception ex) {
            LOG.warn("Could not notify late check-in rejection for participant {}: {}", participantId, ex.getMessage());
        }
        return meetingParticipantMapper.toDto(participant);
    }

    /**
     * Thư ký chọn đại diện cá nhân cho lời mời theo phòng ban: xác nhận participant phòng ban,
     * nâng task phòng ban từ TODO lên IN_PROGRESS, tạo participant mới cho từng user và gửi thông báo.
     */
    public List<MeetingParticipantDTO> selectRepresentatives(Long participantId, String currentUserLogin, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one user must be selected");
        }

        MeetingParticipant deptParticipant = meetingParticipantRepository
            .findById(participantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));

        if (deptParticipant.getUser() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant already has assigned users");
        }
        if (deptParticipant.getDepartment() == null || deptParticipant.getDepartment().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant must be department-based");
        }

        Meeting meeting = deptParticipant.getMeeting();
        if (meeting == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant has no meeting");
        }

        User currentUser = userRepository
            .findOneWithAuthoritiesByLogin(currentUserLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getName()));
        boolean isSecretary = currentUser.getAuthorities().stream().anyMatch(a -> "ROLE_SECRETARY".equals(a.getName()));
        boolean sameDepartment =
            currentUser.getDepartment() != null && currentUser.getDepartment().getId().equals(deptParticipant.getDepartment().getId());

        if (!isAdmin && (!isSecretary || !sameDepartment)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Only secretary of the same department or admin can select representatives"
            );
        }

        if (!isCorporateLevel(meeting)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting must be company-level");
        }

        Long deptId = deptParticipant.getDepartment().getId();
        List<User> selectedUsers = new ArrayList<>();
        for (Long uid : userIds) {
            User u = userRepository.findById(uid).orElse(null);
            if (u == null) continue;
            if (u.getDepartment() == null || !u.getDepartment().getId().equals(deptId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User " + uid + " is not in the invited department");
            }
            selectedUsers.add(u);
        }

        if (selectedUsers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid users selected");
        }

        // Tự động phòng ban xác nhận tham dự: set confirmationStatus = CONFIRMED cho participant phòng ban
        deptParticipant.setConfirmationStatus(ConfirmationStatus.CONFIRMED);
        deptParticipant.setAbsentReason(null);
        meetingParticipantRepository.save(deptParticipant);

        // Task của phòng ban đó chuyển từ TODO sang IN_PROGRESS khi đã có đại diện
        List<MeetingTask> deptTasks = meetingTaskRepository.findByMeetingIdAndDepartmentId(meeting.getId(), deptId);
        for (MeetingTask t : deptTasks) {
            if (t.getStatus() == TaskStatus.TODO) {
                t.setStatus(TaskStatus.IN_PROGRESS);
                meetingTaskRepository.save(t);
            }
        }

        Meeting meetingWithLevel = meetingRepository.findOneWithToOneRelationships(meeting.getId()).orElse(meeting);
        List<MeetingParticipantDTO> result = new ArrayList<>();
        result.add(meetingParticipantMapper.toDto(deptParticipant));

        for (User u : selectedUsers) {
            MeetingParticipant p = new MeetingParticipant();
            p.setMeeting(meeting);
            p.setUser(u);
            p.setDepartment(null);
            p.setRole(ParticipantRole.ATTENDEE);
            p.setIsRequired(true);
            p.setAttendance(AttendanceStatus.NOT_MARKED);
            p.setConfirmationStatus(ConfirmationStatus.PENDING);
            p = meetingParticipantRepository.save(p);
            result.add(meetingParticipantMapper.toDto(p));
            meetingNotificationService.notifyUserSelectedAsRepresentative(meetingWithLevel, u);
        }
        return result;
    }

    /**
     * Kiểm tra cấp cuộc họp có phải cấp tổng công ty (chuẩn hóa tên level, không phân biệt dấu).
     */
    private boolean isCorporateLevel(Meeting meeting) {
        if (meeting.getLevel() == null || meeting.getLevel().getName() == null) return false;
        String name = meeting.getLevel().getName();
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .trim()
            .toUpperCase()
            .replace('Đ', 'D')
            .replace(' ', '_')
            .replace('-', '_');
        return (
            "CORPORATE".equals(normalized) ||
            "COMPANY".equals(normalized) ||
            "TONG_CONG_TY".equals(normalized) ||
            "CAP_TONG_CONG_TY".equals(normalized)
        );
    }

    /**
     * Đảm bảo participant gắn đúng một trong hai: user hoặc department, không được cả hai hoặc không có gì.
     */
    private void validateParticipantTarget(MeetingParticipantDTO dto) {
        boolean hasUser = dto.getUser() != null && dto.getUser().getId() != null;
        boolean hasDepartment = dto.getDepartment() != null && dto.getDepartment().getId() != null;

        if (!hasUser && !hasDepartment) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant must have either user or department");
        }

        if (hasUser && hasDepartment) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant cannot have both user and department");
        }
    }

    /**
     * Kiểm tra user đăng nhập hiện tại có quyền ROLE_ADMIN hay không.
     */
    private boolean isAdmin(String login) {
        return userRepository
            .findOneWithAuthoritiesByLogin(login)
            .map(u -> u.getAuthorities() != null && u.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getName())))
            .orElse(false);
    }
}
