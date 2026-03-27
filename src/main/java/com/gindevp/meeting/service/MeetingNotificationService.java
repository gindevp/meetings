package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingParticipant;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.repository.UserRepository;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service to send notifications and emails for meeting events, respecting user notification settings.
 */
@Service
public class MeetingNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingNotificationService.class);

    private final NotificationService notificationService;
    private final MailService mailService;
    private final UserNotificationSettingsService notificationSettingsService;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final UserRepository userRepository;

    public MeetingNotificationService(
        NotificationService notificationService,
        MailService mailService,
        UserNotificationSettingsService notificationSettingsService,
        MeetingParticipantRepository meetingParticipantRepository,
        UserRepository userRepository
    ) {
        this.notificationService = notificationService;
        this.mailService = mailService;
        this.notificationSettingsService = notificationSettingsService;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.userRepository = userRepository;
    }

    /**
     * Notify requester (người tạo), and when rejected also host (chủ trì) and secretary (thư ký).
     */
    public void notifyApprovalOrRejection(Meeting meeting, boolean approved, String reason) {
        User requester = meeting.getRequester();
        if (requester != null && requester.getId() != null && notificationSettingsService.isApprovalNotifEnabled(requester.getId())) {
            String statusText = approved ? "đã được phê duyệt" : "đã bị từ chối";
            String title = "Cuộc họp " + meeting.getTitle() + " " + statusText;
            String message = approved
                ? "Cuộc họp của bạn đã được phê duyệt."
                : "Cuộc họp của bạn đã bị từ chối." + (reason != null && !reason.isBlank() ? " Lý do: " + reason : "");
            String linkUrl = "/plans?tab=" + (approved ? "approved" : "rejected") + "&meetingId=" + meeting.getId();

            notificationService.create(requester.getId(), title, message, "MEETING_APPROVAL", linkUrl);

            if (requester.getEmail() != null && !requester.getEmail().isBlank()) {
                String emailSubject = "Thông báo phê duyệt cuộc họp: " + meeting.getTitle();
                Map<String, Object> vars = new HashMap<>();
                vars.put("title", title);
                vars.put("message", message);
                vars.put("linkUrl", linkUrl);
                mailService.sendMeetingNotificationEmail(requester, emailSubject, "mail/meetingNotificationEmail", vars);
            }
        }

        if (approved) {
            return;
        }

        String statusText = "đã bị từ chối";
        String title = "Cuộc họp " + meeting.getTitle() + " " + statusText;
        String message = "Cuộc họp đã bị từ chối." + (reason != null && !reason.isBlank() ? " Lý do: " + reason : "");
        String linkUrl = "/plans?tab=rejected&meetingId=" + meeting.getId();

        User host = meeting.getHost();
        if (host != null && host.getId() != null && (requester == null || !host.getId().equals(requester.getId()))) {
            if (notificationSettingsService.isApprovalNotifEnabled(host.getId())) {
                notificationService.create(host.getId(), title, message, "MEETING_APPROVAL", linkUrl);
                if (host.getEmail() != null && !host.getEmail().isBlank()) {
                    String emailSubject = "Thông báo từ chối cuộc họp: " + meeting.getTitle();
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("title", title);
                    vars.put("message", message);
                    vars.put("linkUrl", linkUrl);
                    mailService.sendMeetingNotificationEmail(host, emailSubject, "mail/meetingNotificationEmail", vars);
                }
            }
        }

        User secretary = meeting.getSecretary();
        if (
            secretary != null &&
            secretary.getId() != null &&
            (requester == null || !secretary.getId().equals(requester.getId())) &&
            (host == null || !secretary.getId().equals(host.getId()))
        ) {
            if (notificationSettingsService.isApprovalNotifEnabled(secretary.getId())) {
                notificationService.create(secretary.getId(), title, message, "MEETING_APPROVAL", linkUrl);
                if (secretary.getEmail() != null && !secretary.getEmail().isBlank()) {
                    String emailSubject = "Thông báo từ chối cuộc họp: " + meeting.getTitle();
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("title", title);
                    vars.put("message", message);
                    vars.put("linkUrl", linkUrl);
                    mailService.sendMeetingNotificationEmail(secretary, emailSubject, "mail/meetingNotificationEmail", vars);
                }
            }
        }
    }

    /**
     * Notify participants and host (chủ trì) when meeting is created/updated/submitted (new or changed).
     */
    public void notifyMeetingCreatedOrUpdated(Meeting meeting, boolean isNew) {
        List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meeting.getId());
        Set<Long> notifiedUserIds = new HashSet<>();
        String actionText = isNew ? "mới" : "thay đổi";
        String title = "Cuộc họp " + actionText + ": " + meeting.getTitle();
        String message = isNew ? "Bạn được mời tham dự cuộc họp: " + meeting.getTitle() : "Cuộc họp đã có thay đổi: " + meeting.getTitle();
        String linkUrl = "/plans?tab=approved&meetingId=" + meeting.getId();

        for (MeetingParticipant p : participants) {
            User user = p.getUser();
            if (user == null || user.getId() == null) continue;

            if (!notificationSettingsService.isEmailMeetingsEnabled(user.getId())) continue;

            notificationService.create(user.getId(), title, message, "MEETING_INVITE", linkUrl);
            notifiedUserIds.add(user.getId());

            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                String emailSubject = "Thông báo cuộc họp " + actionText + ": " + meeting.getTitle();
                Map<String, Object> vars = new HashMap<>();
                vars.put("title", title);
                vars.put("message", message);
                vars.put("linkUrl", linkUrl);
                mailService.sendMeetingNotificationEmail(user, emailSubject, "mail/meetingNotificationEmail", vars);
            }
        }

        // Notify host (chủ trì) - cả thông báo và email - nếu chưa được thông báo qua participants
        User host = meeting.getHost();
        if (host != null && host.getId() != null && !notifiedUserIds.contains(host.getId())) {
            if (notificationSettingsService.isEmailMeetingsEnabled(host.getId())) {
                String hostMessage = isNew
                    ? "Bạn là chủ trì cuộc họp: " + meeting.getTitle()
                    : "Cuộc họp bạn chủ trì đã có thay đổi: " + meeting.getTitle();
                notificationService.create(host.getId(), title, hostMessage, "MEETING_INVITE", linkUrl);

                if (host.getEmail() != null && !host.getEmail().isBlank()) {
                    String emailSubject = "Thông báo cuộc họp " + actionText + ": " + meeting.getTitle();
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("title", title);
                    vars.put("message", hostMessage);
                    vars.put("linkUrl", linkUrl);
                    mailService.sendMeetingNotificationEmail(host, emailSubject, "mail/meetingNotificationEmail", vars);
                }
            }
        }

        // Notify secretary (thư ký) nếu có, và chưa được thông báo
        User secretary = meeting.getSecretary();
        if (secretary != null && secretary.getId() != null && !notifiedUserIds.contains(secretary.getId())) {
            if (notificationSettingsService.isEmailMeetingsEnabled(secretary.getId())) {
                String secMessage = isNew
                    ? "Bạn là thư ký cuộc họp: " + meeting.getTitle()
                    : "Cuộc họp bạn làm thư ký đã có thay đổi: " + meeting.getTitle();
                notificationService.create(secretary.getId(), title, secMessage, "MEETING_INVITE", linkUrl);

                if (secretary.getEmail() != null && !secretary.getEmail().isBlank()) {
                    String emailSubject = "Thông báo cuộc họp " + actionText + ": " + meeting.getTitle();
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("title", title);
                    vars.put("message", secMessage);
                    vars.put("linkUrl", linkUrl);
                    mailService.sendMeetingNotificationEmail(secretary, emailSubject, "mail/meetingNotificationEmail", vars);
                }
            }
        }

        if (meeting.getStatus() == MeetingStatus.APPROVED && isCorporateLevel(meeting)) {
            notifyDepartmentSecretariesForDepartmentParticipants(meeting);
        }
    }

    /**
     * Notify department secretaries when their department is invited to a company-level approved meeting.
     */
    public void notifyDepartmentSecretariesForDepartmentParticipants(Meeting meeting) {
        if (meeting == null || meeting.getId() == null || meeting.getStatus() != MeetingStatus.APPROVED) return;
        if (!isCorporateLevel(meeting)) return;

        List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meeting.getId());
        Set<Long> notifiedUserIds = new HashSet<>();

        String title = "Phòng ban của bạn được mời tham dự: " + meeting.getTitle();
        String message = "Vui lòng chọn cá nhân đại diện tham dự cuộc họp: " + meeting.getTitle();
        String linkUrl = "/invitations?tab=department&meetingId=" + meeting.getId();

        for (MeetingParticipant p : participants) {
            if (p.getUser() != null || p.getDepartment() == null || p.getDepartment().getId() == null) continue;

            Long deptId = p.getDepartment().getId();
            List<User> secretaries = userRepository.findSecretariesByDepartmentId(deptId);

            for (User sec : secretaries) {
                if (sec == null || sec.getId() == null || notifiedUserIds.contains(sec.getId())) continue;

                notificationService.create(sec.getId(), title, message, "MEETING_DEPARTMENT_INVITE", linkUrl);
                notifiedUserIds.add(sec.getId());

                if (
                    notificationSettingsService.isEmailMeetingsEnabled(sec.getId()) && sec.getEmail() != null && !sec.getEmail().isBlank()
                ) {
                    String emailSubject = "Lời mời phòng ban tham dự: " + meeting.getTitle();
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("title", title);
                    vars.put("message", message);
                    vars.put("linkUrl", linkUrl);
                    mailService.sendMeetingNotificationEmail(sec, emailSubject, "mail/meetingNotificationEmail", vars);
                }
            }
        }
    }

    /**
     * Notify host/secretary when a participant requests late check-in (điểm danh bù).
     */
    public void notifyLateCheckInRequested(Meeting meeting, User participantUser) {
        if (meeting == null || meeting.getId() == null || participantUser == null || participantUser.getId() == null) return;

        String title = "Yêu cầu điểm danh bù: " + meeting.getTitle();
        String message = (participantUser.getFirstName() != null || participantUser.getLastName() != null)
            ? "Người tham dự đã gửi yêu cầu điểm danh bù: " + (participantUser.getFirstName() + " " + participantUser.getLastName()).trim()
            : "Người tham dự đã gửi yêu cầu điểm danh bù.";
        String linkUrl = "/plans?tab=approved&meetingId=" + meeting.getId();

        // Host
        User host = meeting.getHost();
        if (host != null && host.getId() != null && notificationSettingsService.isApprovalNotifEnabled(host.getId())) {
            notificationService.create(host.getId(), title, message, "LATE_CHECKIN_REQUEST", linkUrl);
            if (host.getEmail() != null && !host.getEmail().isBlank()) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("title", title);
                vars.put("message", message);
                vars.put("linkUrl", linkUrl);
                mailService.sendMeetingNotificationEmail(host, title, "mail/meetingNotificationEmail", vars);
            }
        }

        // Secretary (optional)
        User secretary = meeting.getSecretary();
        if (
            secretary != null &&
            secretary.getId() != null &&
            (host == null || !secretary.getId().equals(host.getId())) &&
            notificationSettingsService.isApprovalNotifEnabled(secretary.getId())
        ) {
            notificationService.create(secretary.getId(), title, message, "LATE_CHECKIN_REQUEST", linkUrl);
            if (secretary.getEmail() != null && !secretary.getEmail().isBlank()) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("title", title);
                vars.put("message", message);
                vars.put("linkUrl", linkUrl);
                mailService.sendMeetingNotificationEmail(secretary, title, "mail/meetingNotificationEmail", vars);
            }
        }
    }

    /**
     * Notify participant when late check-in request is approved/rejected.
     */
    public void notifyLateCheckInDecision(Meeting meeting, User participantUser, boolean approved) {
        if (meeting == null || meeting.getId() == null || participantUser == null || participantUser.getId() == null) return;
        if (!notificationSettingsService.isApprovalNotifEnabled(participantUser.getId())) return;

        String statusText = approved ? "được chấp nhận" : "bị từ chối";
        String title = "Yêu cầu điểm danh bù " + statusText + ": " + meeting.getTitle();
        String message = approved ? "Yêu cầu điểm danh bù của bạn đã được chấp nhận." : "Yêu cầu điểm danh bù của bạn đã bị từ chối.";
        String linkUrl = "/plans?tab=approved&meetingId=" + meeting.getId();

        notificationService.create(participantUser.getId(), title, message, "LATE_CHECKIN_DECISION", linkUrl);
        if (participantUser.getEmail() != null && !participantUser.getEmail().isBlank()) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("title", title);
            vars.put("message", message);
            vars.put("linkUrl", linkUrl);
            mailService.sendMeetingNotificationEmail(participantUser, title, "mail/meetingNotificationEmail", vars);
        }
    }

    /**
     * Notify ROOM_MANAGER users when a meeting needs room approval (PENDING_APPROVAL).
     */
    public void notifyRoomManagersPendingApproval(Meeting meeting) {
        if (meeting == null || meeting.getId() == null) return;
        if (meeting.getStatus() != MeetingStatus.PENDING_APPROVAL) return;

        List<User> roomManagers = userRepository.findAllRoomManagersActivated();
        if (roomManagers == null || roomManagers.isEmpty()) return;

        String title = "Có cuộc họp cần phê duyệt: " + meeting.getTitle();
        String message = "Vui lòng phê duyệt/từ chối cuộc họp đang chờ phê duyệt.";
        String linkUrl = "/plans?tab=pending&meetingId=" + meeting.getId();

        for (User rm : roomManagers) {
            if (rm == null || rm.getId() == null) continue;
            if (!notificationSettingsService.isApprovalNotifEnabled(rm.getId())) continue;

            notificationService.create(rm.getId(), title, message, "MEETING_ROOM_APPROVAL", linkUrl);

            if (rm.getEmail() != null && !rm.getEmail().isBlank()) {
                String emailSubject = "Yêu cầu phê duyệt cuộc họp: " + meeting.getTitle();
                Map<String, Object> vars = new HashMap<>();
                vars.put("title", title);
                vars.put("message", message);
                vars.put("linkUrl", linkUrl);
                mailService.sendMeetingNotificationEmail(rm, emailSubject, "mail/meetingNotificationEmail", vars);
            }
        }
    }

    /**
     * Notify all related users when a meeting is cancelled (CANCELED).
     * Related: requester, host, secretary, invited users, and department secretaries (for department participants).
     */
    public void notifyMeetingCancelled(Meeting meeting, User cancelledBy) {
        if (meeting == null || meeting.getId() == null) return;
        if (meeting.getStatus() != MeetingStatus.CANCELED) return;

        String cancellerName = cancelledBy != null
            ? (cancelledBy.getFirstName() != null || cancelledBy.getLastName() != null
                    ? (String.valueOf(cancelledBy.getFirstName() == null ? "" : cancelledBy.getFirstName()).trim() +
                        " " +
                        String.valueOf(cancelledBy.getLastName() == null ? "" : cancelledBy.getLastName()).trim()).trim()
                    : cancelledBy.getLogin())
            : "hệ thống";

        String title = "Cuộc họp đã bị hủy: " + meeting.getTitle();
        String message = "Cuộc họp đã bị hủy bởi " + cancellerName + ".";
        String linkUrl = "/plans?tab=cancelled&meetingId=" + meeting.getId();

        Set<Long> notifiedUserIds = new HashSet<>();

        // requester
        User requester = meeting.getRequester();
        if (requester != null && requester.getId() != null) {
            notifyUserMeetingCancelled(requester, title, message, linkUrl);
            notifiedUserIds.add(requester.getId());
        }

        // host
        User host = meeting.getHost();
        if (host != null && host.getId() != null && !notifiedUserIds.contains(host.getId())) {
            notifyUserMeetingCancelled(host, title, message, linkUrl);
            notifiedUserIds.add(host.getId());
        }

        // secretary
        User secretary = meeting.getSecretary();
        if (secretary != null && secretary.getId() != null && !notifiedUserIds.contains(secretary.getId())) {
            notifyUserMeetingCancelled(secretary, title, message, linkUrl);
            notifiedUserIds.add(secretary.getId());
        }

        // participants
        List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meeting.getId());
        for (MeetingParticipant p : participants) {
            if (p == null) continue;
            if (p.getUser() != null && p.getUser().getId() != null) {
                Long uid = p.getUser().getId();
                if (notifiedUserIds.contains(uid)) continue;
                notifyUserMeetingCancelled(p.getUser(), title, message, linkUrl);
                notifiedUserIds.add(uid);
                continue;
            }
            if (p.getDepartment() != null && p.getDepartment().getId() != null) {
                Long deptId = p.getDepartment().getId();
                List<User> secs = userRepository.findSecretariesByDepartmentId(deptId);
                for (User sec : secs) {
                    if (sec == null || sec.getId() == null) continue;
                    if (notifiedUserIds.contains(sec.getId())) continue;
                    notifyUserMeetingCancelled(sec, title, message, linkUrl);
                    notifiedUserIds.add(sec.getId());
                }
            }
        }
    }

    private void notifyUserMeetingCancelled(User user, String title, String message, String linkUrl) {
        if (user == null || user.getId() == null) return;
        if (!notificationSettingsService.isEmailMeetingsEnabled(user.getId())) return;

        notificationService.create(user.getId(), title, message, "MEETING_CANCELED", linkUrl);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            String emailSubject = "Thông báo hủy cuộc họp: " + title.replace("Cuộc họp đã bị hủy: ", "");
            Map<String, Object> vars = new HashMap<>();
            vars.put("title", title);
            vars.put("message", message);
            vars.put("linkUrl", linkUrl);
            mailService.sendMeetingNotificationEmail(user, emailSubject, "mail/meetingNotificationEmail", vars);
        }
    }

    /**
     * Notify a user that they have been selected as department representative for a meeting.
     */
    public void notifyUserSelectedAsRepresentative(Meeting meeting, User user) {
        if (meeting == null || user == null || user.getId() == null) return;
        if (!notificationSettingsService.isEmailMeetingsEnabled(user.getId())) return;

        String title = "Bạn được chỉ định tham dự: " + meeting.getTitle();
        String message = "Bạn đã được chọn làm đại diện phòng ban tham dự cuộc họp: " + meeting.getTitle();
        String linkUrl = "/invitations?meetingId=" + meeting.getId();

        notificationService.create(user.getId(), title, message, "MEETING_INVITE", linkUrl);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            String emailSubject = "Thông báo tham dự cuộc họp: " + meeting.getTitle();
            Map<String, Object> vars = new HashMap<>();
            vars.put("title", title);
            vars.put("message", message);
            vars.put("linkUrl", linkUrl);
            mailService.sendMeetingNotificationEmail(user, emailSubject, "mail/meetingNotificationEmail", vars);
        }
    }

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
}
