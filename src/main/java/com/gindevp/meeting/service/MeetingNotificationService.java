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
     * Notify requester when meeting is approved or rejected.
     */
    public void notifyApprovalOrRejection(Meeting meeting, boolean approved, String reason) {
        User requester = meeting.getRequester();
        if (requester == null || requester.getId() == null) return;

        if (!notificationSettingsService.isApprovalNotifEnabled(requester.getId())) return;

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

    /**
     * Notify participants when meeting is created/updated/submitted (new or changed).
     */
    public void notifyMeetingCreatedOrUpdated(Meeting meeting, boolean isNew) {
        List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meeting.getId());
        String actionText = isNew ? "mới" : "thay đổi";
        String title = "Cuộc họp " + actionText + ": " + meeting.getTitle();
        String message = isNew ? "Bạn được mời tham dự cuộc họp: " + meeting.getTitle() : "Cuộc họp đã có thay đổi: " + meeting.getTitle();
        String linkUrl = "/plans?tab=approved&meetingId=" + meeting.getId();

        for (MeetingParticipant p : participants) {
            User user = p.getUser();
            if (user == null || user.getId() == null) continue;

            if (!notificationSettingsService.isEmailMeetingsEnabled(user.getId())) continue;

            notificationService.create(user.getId(), title, message, "MEETING_INVITE", linkUrl);

            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                String emailSubject = "Thông báo cuộc họp " + actionText + ": " + meeting.getTitle();
                Map<String, Object> vars = new HashMap<>();
                vars.put("title", title);
                vars.put("message", message);
                vars.put("linkUrl", linkUrl);
                mailService.sendMeetingNotificationEmail(user, emailSubject, "mail/meetingNotificationEmail", vars);
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
