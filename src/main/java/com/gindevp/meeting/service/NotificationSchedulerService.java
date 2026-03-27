package com.gindevp.meeting.service;

import com.gindevp.meeting.config.ApplicationProperties;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingParticipant;
import com.gindevp.meeting.domain.MeetingTask;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.repository.MeetingTaskRepository;
import com.gindevp.meeting.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduled jobs for meeting reminders and task deadline reminders.
 */
@Service
public class NotificationSchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationSchedulerService.class);

    private static final int MEETING_REMINDER_MINUTES = 15;
    private static final int TASK_REMINDER_WINDOW_MINUTES = 60;

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MeetingTaskRepository meetingTaskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final MailService mailService;
    private final UserNotificationSettingsService notificationSettingsService;
    private final ApplicationProperties applicationProperties;
    private final MeetingWorkflowService meetingWorkflowService;

    public NotificationSchedulerService(
        MeetingRepository meetingRepository,
        MeetingParticipantRepository meetingParticipantRepository,
        MeetingTaskRepository meetingTaskRepository,
        UserRepository userRepository,
        NotificationService notificationService,
        MailService mailService,
        UserNotificationSettingsService notificationSettingsService,
        ApplicationProperties applicationProperties,
        MeetingWorkflowService meetingWorkflowService
    ) {
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.meetingTaskRepository = meetingTaskRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.mailService = mailService;
        this.notificationSettingsService = notificationSettingsService;
        this.applicationProperties = applicationProperties;
        this.meetingWorkflowService = meetingWorkflowService;
    }

    /**
     * Send meeting reminders to participants 15 minutes before start.
     * Runs every 5 minutes.
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void sendMeetingReminders() {
        Instant now = Instant.now();
        Instant from = now.plus(MEETING_REMINDER_MINUTES - 5, ChronoUnit.MINUTES);
        Instant to = now.plus(MEETING_REMINDER_MINUTES + 5, ChronoUnit.MINUTES);

        List<Meeting> meetings = meetingRepository.findByStatusAndStartTimeBetween(MeetingStatus.APPROVED, from, to);

        for (Meeting meeting : meetings) {
            List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meeting.getId());
            String linkUrl = "/plans?tab=approved&meetingId=" + meeting.getId();
            String title = "Nhắc lịch họp: " + meeting.getTitle();
            String message = "Cuộc họp bắt đầu trong " + MEETING_REMINDER_MINUTES + " phút: " + meeting.getTitle();

            for (MeetingParticipant p : participants) {
                User user = p.getUser();
                if (user == null || user.getId() == null) continue;
                if (!notificationSettingsService.isReminderMeetingsEnabled(user.getId())) continue;

                notificationService.create(user.getId(), title, message, "MEETING_REMINDER", linkUrl);

                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    String emailSubject = "Nhắc lịch họp: " + meeting.getTitle();
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("title", title);
                    vars.put("message", message);
                    vars.put("linkUrl", linkUrl);
                    mailService.sendMeetingNotificationEmail(user, emailSubject, "mail/meetingNotificationEmail", vars);
                }
            }
        }
    }

    /**
     * Rule 3: Nếu đến giờ họp mà vẫn chưa phê duyệt thì hệ thống tự từ chối (REJECTED).
     *
     * Lý do: cuộc họp đang ở trạng thái PENDING_APPROVAL quá hạn xử lý.
     * Chạy định kỳ để bắt các trường hợp cron bị trễ.
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void rejectPendingMeetingsAtStartTime() {
        Instant now = Instant.now();
        // allow some cron delay margin
        Instant from = now.minus(60, ChronoUnit.MINUTES);
        Instant to = now;

        // Chỉ xử lý trong một khoảng thời gian để tránh quét quá nhiều dữ liệu.
        List<Meeting> meetingsToReject = meetingRepository.findByStatusAndStartTimeBetween(MeetingStatus.PENDING_APPROVAL, from, to);

        if (meetingsToReject == null || meetingsToReject.isEmpty()) return;

        // dùng một admin làm "người quyết định" cho record meeting_approval
        List<User> admins = userRepository.findAllAdminsActivated();
        if (admins == null || admins.isEmpty()) return;
        User approver = admins.get(0);

        String reason = "Quá thời gian phê duyệt (đến giờ họp nhưng chưa được phê duyệt)";

        for (Meeting meeting : meetingsToReject) {
            if (meeting == null || meeting.getId() == null) continue;
            // meetingRepository query đã filter PENDING_APPROVAL, nhưng thêm guard để tránh race condition.
            if (meeting.getStatus() != MeetingStatus.PENDING_APPROVAL) continue;
            try {
                meetingWorkflowService.reject(meeting.getId(), reason, approver);
            } catch (Exception ex) {
                LOG.warn("Failed to auto-reject meeting {} due to pending approval timeout: {}", meeting.getId(), ex.getMessage());
            }
        }
    }

    /**
     * Send task deadline reminders to assignees.
     * Runs every 30 minutes.
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void sendTaskDeadlineReminders() {
        Instant now = Instant.now();
        Instant from = now;
        Instant to = now.plus(TASK_REMINDER_WINDOW_MINUTES, ChronoUnit.MINUTES);

        List<MeetingTask> tasks = meetingTaskRepository.findByDueAtBetweenAndStatusNotDone(from, to);

        for (MeetingTask task : tasks) {
            User assignee = task.getAssignee();
            if (assignee == null || assignee.getId() == null) continue;
            if (!notificationSettingsService.isTaskDeadlineReminderEnabled(assignee.getId())) continue;

            String taskTitle = task.getTitle();
            String meetingTitle = task.getMeeting() != null ? task.getMeeting().getTitle() : "Cuộc họp";
            Long meetingId = task.getMeeting() != null ? task.getMeeting().getId() : null;
            String linkUrl = meetingId != null ? "/plans?tab=approved&meetingId=" + meetingId : "/plans";

            String title = "Nhắc deadline nhiệm vụ: " + taskTitle;
            String message = "Nhiệm vụ \"" + taskTitle + "\" (từ " + meetingTitle + ") sắp đến hạn.";

            notificationService.create(assignee.getId(), title, message, "TASK_DEADLINE", linkUrl);

            if (assignee.getEmail() != null && !assignee.getEmail().isBlank()) {
                String emailSubject = "Nhắc deadline: " + taskTitle;
                Map<String, Object> vars = new HashMap<>();
                vars.put("title", title);
                vars.put("message", message);
                vars.put("linkUrl", linkUrl);
                mailService.sendMeetingNotificationEmail(assignee, emailSubject, "mail/meetingNotificationEmail", vars);
            }
        }
    }

    /**
     * Gửi báo cáo tổng hợp cuối tuần ngay khi server khởi động xong.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void sendWeeklyReportOnStartup() {
        if (
            applicationProperties == null ||
            applicationProperties.getNotifications() == null ||
            !Boolean.TRUE.equals(applicationProperties.getNotifications().getWeeklyReportOnStartup())
        ) {
            return;
        }
        try {
            sendWeeklyReports();
        } catch (Exception e) {
            LOG.warn("Could not send weekly report on startup: {}", e.getMessage());
        }
    }

    /**
     * Gửi báo cáo tổng hợp cuối tuần cho admin có bật weeklyReport.
     * Chạy mỗi Chủ nhật lúc 8:00 sáng (theo timezone server).
     */
    @Scheduled(cron = "0 0 8 ? * SUN")
    public void sendWeeklyReports() {
        Instant now = Instant.now();
        Instant weekStart = now.minus(7, ChronoUnit.DAYS);
        Instant weekEnd = now;

        long totalMeetings =
            meetingRepository.countByStatusAndStartTimeBetween(MeetingStatus.APPROVED, weekStart, weekEnd) +
            meetingRepository.countByStatusAndStartTimeBetween(MeetingStatus.REJECTED, weekStart, weekEnd) +
            meetingRepository.countByStatusAndStartTimeBetween(MeetingStatus.COMPLETED, weekStart, weekEnd) +
            meetingRepository.countByStatusAndStartTimeBetween(MeetingStatus.PENDING_APPROVAL, weekStart, weekEnd);
        long approvedMeetings = meetingRepository.countByStatusAndStartTimeBetween(MeetingStatus.APPROVED, weekStart, weekEnd);
        long rejectedMeetings = meetingRepository.countByStatusAndStartTimeBetween(MeetingStatus.REJECTED, weekStart, weekEnd);
        long completedMeetings = meetingRepository.countByStatusAndStartTimeBetween(MeetingStatus.COMPLETED, weekStart, weekEnd);
        long pendingTasks = meetingTaskRepository.countByStatusNotDone();

        List<User> admins = userRepository.findAllAdminsActivated();
        for (User admin : admins) {
            if (!notificationSettingsService.isWeeklyReportEnabled(admin.getId())) continue;
            if (admin.getEmail() == null || admin.getEmail().isBlank()) continue;

            String emailSubject = "Báo cáo tổng hợp cuối tuần - Hệ thống Quản lý cuộc họp";
            Map<String, Object> vars = new HashMap<>();
            vars.put("title", "Báo cáo tổng hợp cuối tuần");
            vars.put("totalMeetings", totalMeetings);
            vars.put("approvedMeetings", approvedMeetings);
            vars.put("rejectedMeetings", rejectedMeetings);
            vars.put("completedMeetings", completedMeetings);
            vars.put("pendingTasks", pendingTasks);
            vars.put("linkUrl", "/plans");
            mailService.sendMeetingNotificationEmail(admin, emailSubject, "mail/weeklyReportEmail", vars);
        }
    }
}
