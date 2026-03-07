package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingApproval;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.domain.enumeration.ApprovalDecision;
import com.gindevp.meeting.domain.enumeration.MeetingLevel;
import com.gindevp.meeting.domain.enumeration.MeetingMode;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import com.gindevp.meeting.repository.MeetingApprovalRepository;
import com.gindevp.meeting.repository.MeetingLevelRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.mapper.MeetingMapper;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import java.text.Normalizer;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeetingWorkflowService {

    private final MeetingRepository meetingRepository;
    private final MeetingApprovalRepository meetingApprovalRepository;
    private final MeetingLevelRepository meetingLevelRepository;
    private final MeetingValidationService meetingValidationService;
    private final MeetingMapper meetingMapper;
    private final MeetingNotificationService meetingNotificationService;

    public MeetingWorkflowService(
        MeetingRepository meetingRepository,
        MeetingApprovalRepository meetingApprovalRepository,
        MeetingLevelRepository meetingLevelRepository,
        MeetingValidationService meetingValidationService,
        MeetingMapper meetingMapper,
        MeetingNotificationService meetingNotificationService
    ) {
        this.meetingRepository = meetingRepository;
        this.meetingApprovalRepository = meetingApprovalRepository;
        this.meetingLevelRepository = meetingLevelRepository;
        this.meetingValidationService = meetingValidationService;
        this.meetingMapper = meetingMapper;
        this.meetingNotificationService = meetingNotificationService;
    }

    public MeetingDTO submit(Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        if (!(meeting.getStatus() == MeetingStatus.DRAFT || meeting.getStatus() == MeetingStatus.REJECTED)) {
            throw new BadRequestAlertException("Only DRAFT/REJECTED can be submitted", "meeting", "invalidState");
        }
        meetingValidationService.validateBeforeSubmit(meeting);

        // Cấp Tổng công ty: tự động phê duyệt
        if (requiresUnitApproval(meeting)) {
            meeting.setStatus(MeetingStatus.APPROVED);
            meeting.setApprovedAt(Instant.now());
            meeting.setSubmittedAt(Instant.now());
        } else {
            // Cấp Phòng ban: cần phê duyệt từ ROOM_MANAGER
            meeting.setStatus(MeetingStatus.PENDING_APPROVAL);
            meeting.setSubmittedAt(Instant.now());
        }

        Meeting saved = meetingRepository.save(meeting);
        meetingNotificationService.notifyMeetingCreatedOrUpdated(saved, true);

        return meetingMapper.toDto(saved);
    }

    public MeetingDTO approveRoom(Long meetingId, User approver) {
        Meeting meeting = getMeeting(meetingId);
        if (meeting.getStatus() != MeetingStatus.PENDING_APPROVAL) {
            throw new BadRequestAlertException("Meeting not pending approval", "meeting", "invalidState");
        }

        meetingValidationService.validateBeforeSubmit(meeting);

        // Log approval
        MeetingApproval log = new MeetingApproval();
        log.setMeeting(meeting);
        log.setStep(1);
        log.setDecision(ApprovalDecision.APPROVED);
        log.setDecidedAt(Instant.now());
        log.setDecidedBy(approver);
        meetingApprovalRepository.save(log);

        // Update meeting status to APPROVED
        meeting.setStatus(MeetingStatus.APPROVED);
        meeting.setApprovedAt(Instant.now());
        meetingRepository.save(meeting);

        meetingRepository
            .findOneWithToOneRelationships(meetingId)
            .ifPresent(m -> {
                meetingNotificationService.notifyApprovalOrRejection(m, true, null);
                meetingNotificationService.notifyDepartmentSecretariesForDepartmentParticipants(m);
            });

        return meetingMapper.toDto(meeting);
    }

    public MeetingDTO approveUnit(Long meetingId, User approver) {
        Meeting meeting = getMeeting(meetingId);
        Instant cycleFrom = meeting.getSubmittedAt();
        if (meeting.getStatus() != MeetingStatus.PENDING_APPROVAL) {
            throw new BadRequestAlertException("Meeting not pending approval", "meeting", "invalidState");
        }
        if (!requiresUnitApproval(meeting)) {
            throw new BadRequestAlertException("Unit approval is not required for this meeting", "meeting", "unitApprovalNotRequired");
        } // Nếu meeting cần room approval, phải xong step 1 trước
        if (
            //            requiresRoomApproval(meeting) &&
            !meetingApprovalRepository.existsByMeetingIdAndStepAndDecisionAndDecidedAtGreaterThanEqual(
                meetingId,
                1,
                ApprovalDecision.APPROVED,
                cycleFrom
            )
        ) {
            throw new BadRequestAlertException("Room approval step must be completed first", "meeting", "roomStepMissing");
        }
        if (
            meetingApprovalRepository.existsByMeetingIdAndStepAndDecisionAndDecidedAtGreaterThanEqual(
                meetingId,
                2,
                ApprovalDecision.APPROVED,
                cycleFrom
            )
        ) {
            throw new BadRequestAlertException("Unit already approved", "meeting", "alreadyApproved");
        }
        meetingValidationService.validateBeforeSubmit(meeting);
        MeetingApproval log = new MeetingApproval();
        log.setMeeting(meeting);
        log.setStep(2);
        log.setDecision(ApprovalDecision.APPROVED);
        log.setDecidedAt(Instant.now());
        log.setDecidedBy(approver);
        meetingApprovalRepository.save(log);
        meeting.setStatus(MeetingStatus.APPROVED);
        meeting.setApprovedAt(Instant.now());
        meetingRepository.save(meeting);

        meetingRepository
            .findOneWithToOneRelationships(meetingId)
            .ifPresent(m -> {
                meetingNotificationService.notifyApprovalOrRejection(m, true, null);
                meetingNotificationService.notifyDepartmentSecretariesForDepartmentParticipants(m);
            });

        return meetingMapper.toDto(meeting);
    }

    public MeetingDTO reject(Long meetingId, String reason, User approver) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BadRequestAlertException("Reject reason is required", "meeting", "reasonRequired");
        }
        Meeting meeting = getMeeting(meetingId);
        if (meeting.getStatus() != MeetingStatus.PENDING_APPROVAL) {
            throw new BadRequestAlertException("Meeting not pending approval", "meeting", "invalidState");
        }
        int step = resolvePendingStep(meeting);
        MeetingApproval log = new MeetingApproval();
        log.setMeeting(meeting);
        log.setStep(step);
        log.setDecision(ApprovalDecision.REJECTED);
        log.setReason(reason);
        log.setDecidedAt(Instant.now());
        log.setDecidedBy(approver);
        meetingApprovalRepository.save(log);
        meeting.setStatus(MeetingStatus.REJECTED);
        meetingRepository.save(meeting);
        return meetingMapper.toDto(meeting);
    }

    public MeetingDTO cancel(Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        if (meeting.getStatus() == MeetingStatus.CANCELED) {
            throw new BadRequestAlertException("Meeting already cancelled", "meeting", "alreadyCancelled");
        }
        meeting.setStatus(MeetingStatus.CANCELED);
        meeting.setCanceledAt(Instant.now());
        return meetingMapper.toDto(meetingRepository.save(meeting));
    }

    public MeetingDTO complete(Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        if (meeting.getStatus() != MeetingStatus.APPROVED) {
            throw new BadRequestAlertException("Only APPROVED meetings can be completed", "meeting", "invalidState");
        }
        meeting.setStatus(MeetingStatus.COMPLETED);
        return meetingMapper.toDto(meetingRepository.save(meeting));
    }

    private int resolvePendingStep(Meeting meeting) {
        Long id = meeting.getId();
        Instant cycleFrom = meeting.getSubmittedAt();
        boolean roomDone = meetingApprovalRepository.existsByMeetingIdAndStepAndDecisionAndDecidedAtGreaterThanEqual(
            id,
            1,
            ApprovalDecision.APPROVED,
            cycleFrom
        );
        if (
            //            requiresRoomApproval(meeting) &&
            !roomDone
        ) return 1;
        if (requiresUnitApproval(meeting)) return 2; // fallback
        return 1;
    }

    private boolean requiresRoomApproval(Meeting meeting) {
        return meeting.getMode() != MeetingMode.ONLINE; // IN_PERSON / HYBRID
    }

    private boolean requiresUnitApproval(Meeting meeting) {
        if (meeting.getLevel() == null) {
            return false;
        }

        String levelName = meeting.getLevel().getName();
        if (isCorporateLevelName(levelName)) {
            return true;
        }

        Long levelId = meeting.getLevel().getId();
        if (levelId == null) {
            return false;
        }

        return meetingLevelRepository.findById(levelId).map(level -> isCorporateLevelName(level.getName())).orElse(false);
    }

    private boolean isCorporateLevelName(String levelName) {
        if (levelName == null) {
            return false;
        }

        String normalized = Normalizer.normalize(levelName, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .trim()
            .toUpperCase()
            .replace('Đ', 'D')
            .replace(' ', '_')
            .replace('-', '_');

        return (
            MeetingLevel.CORPORATE.name().equals(normalized) ||
            "COMPANY".equals(normalized) ||
            "TONG_CONG_TY".equals(normalized) ||
            "CAP_TONG_CONG_TY".equals(normalized)
        );
    }

    private Meeting getMeeting(Long id) {
        return meetingRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("Meeting not found", "meeting", "notFound"));
    }
}
