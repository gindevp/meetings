package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.enumeration.MeetingStatus;
import com.gindevp.meeting.repository.MeetingRepository;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MeetingValidationService {

    private final MeetingRepository meetingRepository;

    public MeetingValidationService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public void validateBeforeSubmit(Meeting meeting) {
        if (meeting.getStartTime() == null || meeting.getEndTime() == null) {
            throw badRequest("startTime/endTime is required", "timeRequired");
        }
        if (!meeting.getStartTime().isBefore(meeting.getEndTime())) {
            throw badRequest("startTime must be before endTime", "timeInvalid");
        } // 1) Rule theo hình thức họp
        switch (meeting.getMode()) {
            case IN_PERSON -> {
                if (meeting.getRoom() == null) throw badRequest("Room is required for IN_PERSON", "roomRequired");
            }
            case ONLINE -> {
                if (isBlank(meeting.getOnlineLink())) throw badRequest("Online link is required for ONLINE", "linkRequired");
            }
            case HYBRID -> {
                if (meeting.getRoom() == null) throw badRequest("Room is required for HYBRID", "roomRequired");
                if (isBlank(meeting.getOnlineLink())) throw badRequest("Online link is required for HYBRID", "linkRequired");
            }
        } // 2) Conflict checks (định nghĩa active = pending/approved)
        var active = List.of(MeetingStatus.PENDING_APPROVAL, MeetingStatus.APPROVED);
        // Room conflict nếu có room
        if (meeting.getRoom() != null) {
            long c = meetingRepository.countRoomConflicts(
                meeting.getRoom().getId(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                active,
                meeting.getId()
            );
            if (c > 0) throw badRequest("Room time conflict", "roomConflict");
        }
        // Host conflict
        if (meeting.getHost() != null) {
            long c = meetingRepository.countHostConflicts(
                meeting.getHost().getId(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                active,
                meeting.getId()
            );
            if (c > 0) throw badRequest("Host time conflict", "hostConflict");
        } // Key participants conflict
        var keyIds = meeting.getParticipants() == null
            ? List.<Long>of()
            : meeting
                .getParticipants()
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsRequired()) && p.getUser() != null)
                .map(p -> p.getUser().getId())
                .distinct()
                .toList();
        if (!keyIds.isEmpty()) {
            long c = meetingRepository.countKeyParticipantConflicts(
                keyIds,
                meeting.getStartTime(),
                meeting.getEndTime(),
                active,
                meeting.getId()
            );
            if (c > 0) throw badRequest("Key participants conflict", "participantConflict");
        } // 3) Capacity check (đơn giản)
        if (meeting.getRoom() != null && meeting.getParticipants() != null) {
            int participantCount = meeting.getParticipants().size();
            if (meeting.getRoom().getCapacity() != null && participantCount > meeting.getRoom().getCapacity()) {
                throw badRequest("Room capacity is not enough", "capacityNotEnough");
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static BadRequestAlertException badRequest(String msg, String errorKey) {
        return new BadRequestAlertException(msg, "meeting", errorKey);
    }
}
