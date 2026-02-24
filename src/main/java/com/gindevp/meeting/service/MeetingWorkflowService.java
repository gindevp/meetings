package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingApproval;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.domain.enumeration.ApprovalDecision;
import com.gindevp.meeting.domain.enumeration.MeetingLevel;
import com.gindevp.meeting.domain.enumeration.MeetingMode;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import com.gindevp.meeting.repository.MeetingApprovalRepository;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.mapper.MeetingMapper;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeetingWorkflowService {

    private final MeetingRepository meetingRepository;
    private final MeetingApprovalRepository meetingApprovalRepository;
    private final MeetingValidationService meetingValidationService;
    private final MeetingMapper meetingMapper; // JHipster MapStruct mapper

    public MeetingWorkflowService(
        MeetingRepository meetingRepository,
        MeetingApprovalRepository meetingApprovalRepository,
        MeetingValidationService meetingValidationService,
        MeetingMapper meetingMapper
    ) {
        this.meetingRepository = meetingRepository;
        this.meetingApprovalRepository = meetingApprovalRepository;
        this.meetingValidationService = meetingValidationService;
        this.meetingMapper = meetingMapper;
    }

    public MeetingDTO submit(Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        if (!(meeting.getStatus() == MeetingStatus.DRAFT || meeting.getStatus() == MeetingStatus.REJECTED)) {
            throw new BadRequestAlertException("Only DRAFT/REJECTED can be submitted", "meeting", "invalidState");
        }
        meetingValidationService.validateBeforeSubmit(meeting); // Nếu ONLINE và không cần unit approval => có thể auto approve (tuỳ bạn) // Ở đây mình theo đặc tả: gửi duyệt => chuyển PENDING
        meeting.setStatus(MeetingStatus.PENDING_APPROVAL);
        meeting.setSubmittedAt(Instant.now());
        return meetingMapper.toDto(meetingRepository.save(meeting));
    }

    public MeetingDTO approveRoom(Long meetingId, User approver) {
        Meeting meeting = getMeeting(meetingId);
        Instant cycleFrom = meeting.getSubmittedAt();
        if (meeting.getStatus() != MeetingStatus.PENDING_APPROVAL) {
            throw new BadRequestAlertException("Meeting not pending approval", "meeting", "invalidState");
        }
        //        if (!requiresRoomApproval(meeting)) {
        //            throw new BadRequestAlertException("Room approval is not required for this meeting", "meeting", "roomApprovalNotRequired");
        //        }
        if (
            meetingApprovalRepository.existsByMeetingIdAndStepAndDecisionAndDecidedAtGreaterThanEqual(
                meetingId,
                1,
                ApprovalDecision.APPROVED,
                cycleFrom
            )
        ) {
            throw new BadRequestAlertException("Room already approved", "meeting", "alreadyApproved");
        } // re-check conflict trước khi approve
        meetingValidationService.validateBeforeSubmit(meeting);
        MeetingApproval log = new MeetingApproval();
        log.setMeeting(meeting);
        log.setStep(1);
        log.setDecision(ApprovalDecision.APPROVED);
        log.setDecidedAt(Instant.now());
        log.setDecidedBy(approver);
        meetingApprovalRepository.save(log); // Nếu không cần unit approval => Approved luôn
        if (!requiresUnitApproval(meeting)) {
            meeting.setStatus(MeetingStatus.APPROVED);
            meeting.setApprovedAt(Instant.now());
            meetingRepository.save(meeting);
        }
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
        return MeetingLevel.CORPORATE.name().equals(meeting.getLevel().getName()); // map "cấp cao" -> corporate
    }

    private Meeting getMeeting(Long id) {
        return meetingRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("Meeting not found", "meeting", "notFound"));
    }
}
