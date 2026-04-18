package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.enumeration.AttendanceStatus;
import com.gindevp.meeting.domain.enumeration.ConfirmationStatus;
import com.gindevp.meeting.repository.MeetingParticipantRepository;
import com.gindevp.meeting.security.SecurityUtils;
import com.gindevp.meeting.service.MeetingParticipantService;
import com.gindevp.meeting.service.dto.MeetingParticipantDTO;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * API REST cho {@link com.gindevp.meeting.domain.MeetingParticipant}: CRUD, phản hồi lời mời, điểm danh, điểm danh bù, chọn đại diện.
 */
@RestController
@RequestMapping("/api/meeting-participants")
public class MeetingParticipantResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingParticipantResource.class);

    private static final String ENTITY_NAME = "meetingParticipant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingParticipantService meetingParticipantService;

    private final MeetingParticipantRepository meetingParticipantRepository;

    /**
     * @param meetingParticipantService nghiệp vụ participant
     * @param meetingParticipantRepository kiểm tra tồn tại bản ghi (update/patch)
     */
    public MeetingParticipantResource(
        MeetingParticipantService meetingParticipantService,
        MeetingParticipantRepository meetingParticipantRepository
    ) {
        this.meetingParticipantService = meetingParticipantService;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    /**
     * {@code POST /meeting-participants} — Tạo participant mới (body không được có id).
     */
    @PostMapping("")
    public ResponseEntity<MeetingParticipantDTO> createMeetingParticipant(@Valid @RequestBody MeetingParticipantDTO meetingParticipantDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MeetingParticipant : {}", meetingParticipantDTO);
        if (meetingParticipantDTO.getId() != null) {
            throw new BadRequestAlertException("A new meetingParticipant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingParticipantDTO = meetingParticipantService.save(meetingParticipantDTO);
        return ResponseEntity.created(new URI("/api/meeting-participants/" + meetingParticipantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, meetingParticipantDTO.getId().toString()))
            .body(meetingParticipantDTO);
    }

    /**
     * {@code PUT /meeting-participants/:id} — Cập nhật toàn bộ participant (id trên path khớp body).
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingParticipantDTO> updateMeetingParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingParticipantDTO meetingParticipantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MeetingParticipant : {}, {}", id, meetingParticipantDTO);
        if (meetingParticipantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingParticipantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingParticipantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        meetingParticipantDTO = meetingParticipantService.update(meetingParticipantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingParticipantDTO.getId().toString()))
            .body(meetingParticipantDTO);
    }

    /**
     * {@code PATCH /meeting-participants/:id} — Cập nhật một phần (merge-patch / JSON).
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingParticipantDTO> partialUpdateMeetingParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingParticipantDTO meetingParticipantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MeetingParticipant partially : {}, {}", id, meetingParticipantDTO);
        if (meetingParticipantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingParticipantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingParticipantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingParticipantDTO> result = meetingParticipantService.partialUpdate(meetingParticipantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meetingParticipantDTO.getId().toString())
        );
    }

    /**
     * {@code GET /meeting-participants} — Danh sách participant (hiện luôn eager theo service).
     */
    @GetMapping("")
    public List<MeetingParticipantDTO> getAllMeetingParticipants(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MeetingParticipants");
        return meetingParticipantService.findAll();
    }

    /**
     * {@code GET /meeting-participants/:id} — Chi tiết một participant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingParticipantDTO> getMeetingParticipant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MeetingParticipant : {}", id);
        Optional<MeetingParticipantDTO> meetingParticipantDTO = meetingParticipantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingParticipantDTO);
    }

    /**
     * {@code DELETE  /meeting-participants/:id} : delete the "id" meetingParticipant.
     * Admin: xóa bất kỳ lúc nào. Người được mời: chỉ được xóa lời mời của chính mình sau khi cuộc họp đã kết thúc.
     *
     * @param id the id of the meetingParticipantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingParticipant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MeetingParticipant : {}", id);
        String currentLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
        meetingParticipantService.deleteWithPermission(id, currentLogin);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST /meeting-participants/:id/respond} — Xác nhận/từ chối lời mời (chỉ user được mời).
     */
    @PostMapping("/{id}/respond")
    public ResponseEntity<MeetingParticipantDTO> respondToInvitation(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String currentLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
        String statusStr = body.get("confirmationStatus");
        if (statusStr == null || (!"CONFIRMED".equals(statusStr) && !"DECLINED".equals(statusStr))) {
            throw new BadRequestAlertException("confirmationStatus must be CONFIRMED or DECLINED", ENTITY_NAME, "invalidStatus");
        }
        ConfirmationStatus status = ConfirmationStatus.valueOf(statusStr);
        String absentReason = body.get("absentReason");
        MeetingParticipantDTO dto = meetingParticipantService.respondToInvitation(id, currentLogin, status, absentReason);
        return ResponseEntity.ok(dto);
    }

    /**
     * {@code POST /meeting-participants/:id/decline-department} — Thư ký phòng ban / admin từ chối lời mời theo phòng ban.
     */
    @PostMapping("/{id}/decline-department")
    public ResponseEntity<MeetingParticipantDTO> declineDepartmentInvitation(
        @PathVariable("id") Long id,
        @RequestBody Map<String, String> body
    ) {
        String currentLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
        String absentReason = body.get("absentReason");
        MeetingParticipantDTO dto = meetingParticipantService.declineDepartmentInvitation(id, currentLogin, absentReason);
        return ResponseEntity.ok(dto);
    }

    /**
     * {@code POST /meeting-participants/:id/select-representatives} — Chọn đại diện cá nhân cho lời mời phòng ban.
     */
    @PostMapping("/{id}/select-representatives")
    public ResponseEntity<List<MeetingParticipantDTO>> selectRepresentatives(
        @PathVariable("id") Long id,
        @RequestBody Map<String, Object> body
    ) {
        String currentLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
        @SuppressWarnings("unchecked")
        List<Number> raw = (List<Number>) body.get("userIds");
        if (raw == null || raw.isEmpty()) {
            throw new BadRequestAlertException("userIds is required and must not be empty", ENTITY_NAME, "userIdsRequired");
        }
        List<Long> userIds = raw.stream().map(Number::longValue).collect(Collectors.toList());
        List<MeetingParticipantDTO> result = meetingParticipantService.selectRepresentatives(id, currentLogin, userIds);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code PATCH /meeting-participants/:id/attendance} — Cập nhật điểm danh (theo quyền host/secretary/self).
     */
    @PatchMapping("/{id}/attendance")
    public ResponseEntity<MeetingParticipantDTO> updateAttendance(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String currentLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
        String statusStr = body.get("attendance");
        if (statusStr == null) {
            throw new BadRequestAlertException("attendance is required", ENTITY_NAME, "invalidAttendance");
        }
        AttendanceStatus attendance;
        try {
            attendance = AttendanceStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(
                "attendance must be PRESENT, ABSENT, NOT_MARKED or EXCUSED",
                ENTITY_NAME,
                "invalidAttendance"
            );
        }
        MeetingParticipantDTO dto = meetingParticipantService.updateAttendance(id, currentLogin, attendance);
        return ResponseEntity.ok(dto);
    }

    /**
     * {@code POST /meeting-participants/:id/request-late-check-in} — Yêu cầu điểm danh bù (chính participant).
     */
    @PostMapping("/{id}/request-late-check-in")
    public ResponseEntity<MeetingParticipantDTO> requestLateCheckIn(@PathVariable("id") Long id) {
        String currentLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
        MeetingParticipantDTO dto = meetingParticipantService.requestLateCheckIn(id, currentLogin);
        return ResponseEntity.ok(dto);
    }

    /**
     * {@code POST /meeting-participants/:id/approve-late-check-in} — Duyệt điểm danh bù (host/thư ký).
     */
    @PostMapping("/{id}/approve-late-check-in")
    public ResponseEntity<MeetingParticipantDTO> approveLateCheckIn(@PathVariable("id") Long id) {
        String currentLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
        MeetingParticipantDTO dto = meetingParticipantService.approveLateCheckIn(id, currentLogin);
        return ResponseEntity.ok(dto);
    }

    /**
     * {@code POST /meeting-participants/:id/reject-late-check-in} — Từ chối yêu cầu điểm danh bù.
     */
    @PostMapping("/{id}/reject-late-check-in")
    public ResponseEntity<MeetingParticipantDTO> rejectLateCheckIn(@PathVariable("id") Long id) {
        String currentLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
        MeetingParticipantDTO dto = meetingParticipantService.rejectLateCheckIn(id, currentLogin);
        return ResponseEntity.ok(dto);
    }
}
