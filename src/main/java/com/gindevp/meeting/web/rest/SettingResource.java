package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.security.SecurityUtils;
import com.gindevp.meeting.service.SettingService;
import com.gindevp.meeting.service.dto.SettingDTO;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for user and system settings.
 * - /api/settings (current user): GET list, POST/PUT save, DELETE by id or key
 * - /api/admin/settings (admin): GET list system, POST/PUT system, DELETE system
 */
@RestController
@RequestMapping("/api")
public class SettingResource {

    private static final Logger LOG = LoggerFactory.getLogger(SettingResource.class);
    private static final String ENTITY_NAME = "setting";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SettingService settingService;
    private final UserRepository userRepository;

    public SettingResource(SettingService settingService, UserRepository userRepository) {
        this.settingService = settingService;
        this.userRepository = userRepository;
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId()
            .or(() -> SecurityUtils.getCurrentUserLogin().flatMap(login -> userRepository.findOneByLogin(login).map(User::getId)))
            .orElseThrow(() -> new BadRequestAlertException("Not authenticated", ENTITY_NAME, "unauthorized"));
    }

    /** GET /api/settings : Lấy tất cả cấu hình của user hiện tại (tạo mặc định nếu chưa có) */
    @GetMapping("/settings")
    public List<SettingDTO> getCurrentUserSettings() {
        Long userId = currentUserId();
        return settingService.findAllByUserIdOrCreateDefaults(userId);
    }

    /** GET /api/settings/{key} : Lấy một cấu hình theo key (user hiện tại) */
    @GetMapping("/settings/key/{key}")
    public ResponseEntity<SettingDTO> getCurrentUserSettingByKey(@PathVariable String key) {
        Long userId = currentUserId();
        return ResponseUtil.wrapOrNotFound(settingService.findByUserIdAndKey(userId, key));
    }

    /** POST /api/settings : Lưu cấu hình user (tạo mới hoặc cập nhật theo key) */
    @PostMapping("/settings")
    public ResponseEntity<SettingDTO> saveCurrentUserSetting(@Valid @RequestBody SettingDTO settingDTO) throws URISyntaxException {
        Long userId = currentUserId();
        settingDTO.setUserId(userId);
        settingDTO.setCategory("USER");
        SettingDTO result = settingService.saveUserSetting(userId, settingDTO);
        return ResponseEntity.created(new URI("/api/settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /** PUT /api/settings/:id : Cập nhật cấu hình (chỉ của user hiện tại) */
    @PutMapping("/settings/{id}")
    public ResponseEntity<SettingDTO> updateSetting(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SettingDTO settingDTO
    ) throws URISyntaxException {
        if (settingDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, settingDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        Long userId = currentUserId();
        settingDTO.setUserId(userId);
        settingDTO.setCategory("USER");
        SettingDTO result = settingService.saveUserSetting(userId, settingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /** DELETE /api/settings/:id : Xóa cấu hình theo id (chỉ của user hiện tại) */
    @DeleteMapping("/settings/{id}")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        Optional<SettingDTO> opt = settingService.findOne(id);
        return opt
            .<ResponseEntity<Void>>map(setting -> {
                if (!setting.getUserId().equals(currentUserId())) {
                    throw new BadRequestAlertException("Forbidden", ENTITY_NAME, "forbidden");
                }
                settingService.delete(id);
                return ResponseEntity.noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/settings/key/:key : Xóa cấu hình theo key (user hiện tại) */
    @DeleteMapping("/settings/key/{key}")
    public ResponseEntity<Void> deleteSettingByKey(@PathVariable String key) {
        Long userId = currentUserId();
        settingService.deleteByUserIdAndKey(userId, key);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, key)).build();
    }

    // ---------- Admin: system settings ----------

    /** GET /api/admin/settings : Lấy tất cả cấu hình hệ thống (admin) */
    @GetMapping("/admin/settings")
    public List<SettingDTO> getAllSystemSettings() {
        return settingService.findAllSystemSettings();
    }

    /** POST /api/admin/settings : Lưu cấu hình hệ thống (admin) */
    @PostMapping("/admin/settings")
    public ResponseEntity<SettingDTO> saveSystemSetting(@Valid @RequestBody SettingDTO settingDTO) throws URISyntaxException {
        settingDTO.setUserId(null);
        settingDTO.setCategory("SYSTEM");
        SettingDTO result = settingService.saveSystemSetting(settingDTO);
        return ResponseEntity.created(new URI("/api/admin/settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /** PUT /api/admin/settings/:id : Cập nhật cấu hình hệ thống (admin) */
    @PutMapping("/admin/settings/{id}")
    public ResponseEntity<SettingDTO> updateSystemSetting(@PathVariable Long id, @Valid @RequestBody SettingDTO settingDTO)
        throws URISyntaxException {
        if (settingDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, settingDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        settingDTO.setUserId(null);
        settingDTO.setCategory("SYSTEM");
        SettingDTO result = settingService.saveSystemSetting(settingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /** DELETE /api/admin/settings/:id : Xóa cấu hình hệ thống (admin) */
    @DeleteMapping("/admin/settings/{id}")
    public ResponseEntity<Void> deleteSystemSetting(@PathVariable Long id) {
        settingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
