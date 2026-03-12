package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Setting;
import com.gindevp.meeting.repository.SettingRepository;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.service.dto.SettingDTO;
import com.gindevp.meeting.service.mapper.SettingMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingService {

    private static final Logger LOG = LoggerFactory.getLogger(SettingService.class);
    private static final String CATEGORY_USER = "USER";
    private static final String CATEGORY_SYSTEM = "SYSTEM";

    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;
    private final UserRepository userRepository;

    public SettingService(SettingRepository settingRepository, SettingMapper settingMapper, UserRepository userRepository) {
        this.settingRepository = settingRepository;
        this.settingMapper = settingMapper;
        this.userRepository = userRepository;
    }

    public SettingDTO saveUserSetting(Long userId, SettingDTO dto) {
        LOG.debug("Save user setting for user {}: {} = {}", userId, dto.getKey(), dto.getValue());
        dto.setUserId(userId);
        dto.setCategory(CATEGORY_USER);
        return saveOrUpdate(userId, dto);
    }

    public SettingDTO saveSystemSetting(SettingDTO dto) {
        LOG.debug("Save system setting: {} = {}", dto.getKey(), dto.getValue());
        dto.setUserId(null);
        dto.setCategory(CATEGORY_SYSTEM);
        return saveOrUpdate(null, dto);
    }

    private SettingDTO saveOrUpdate(Long userId, SettingDTO dto) {
        Optional<Setting> existing = userId == null
            ? settingRepository.findSystemSettingByKey(dto.getKey())
            : settingRepository.findByUserIdAndKey(userId, dto.getKey());
        Setting entity = existing
            .map(e -> {
                e.setValue(dto.getValue());
                return e;
            })
            .orElseGet(() -> {
                Setting s = settingMapper.toEntity(dto);
                s.setUserId(userId);
                s.setCategory(dto.getCategory());
                return s;
            });
        entity = settingRepository.save(entity);
        return settingMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<SettingDTO> findAllByUserId(Long userId) {
        return settingRepository.findByUserId(userId).stream().map(settingMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Lấy settings của user; nếu chưa có thì tạo mặc định rồi trả về.
     * Dùng khi user cũ (tạo trước khi có logic ensure) lần đầu load settings.
     */
    @Transactional
    public List<SettingDTO> findAllByUserIdOrCreateDefaults(Long userId) {
        List<SettingDTO> list = findAllByUserId(userId);
        if (list.isEmpty()) {
            ensureDefaultSettingsForUser(userId);
            return findAllByUserId(userId);
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<SettingDTO> findAllSystemSettings() {
        return settingRepository.findAllSystemSettings().stream().map(settingMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<SettingDTO> findOne(Long id) {
        return settingRepository.findById(id).map(settingMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<SettingDTO> findByUserIdAndKey(Long userId, String key) {
        return settingRepository.findByUserIdAndKey(userId, key).map(settingMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<SettingDTO> findSystemSettingByKey(String key) {
        return settingRepository.findSystemSettingByKey(key).map(settingMapper::toDto);
    }

    public void delete(Long id) {
        settingRepository.deleteById(id);
    }

    public void deleteByUserIdAndKey(Long userId, String key) {
        settingRepository.findByUserIdAndKey(userId, key).ifPresent(settingRepository::delete);
    }

    /**
     * Xóa tất cả setting của user (dùng trước khi xóa user để thỏa FK).
     */
    public void deleteAllByUserId(Long userId) {
        if (userId == null) return;
        settingRepository.deleteByUserId(userId);
        LOG.debug("Deleted all settings for user id: {}", userId);
    }

    /**
     * Tạo cài đặt mặc định cho user mới nếu chưa có.
     * Gọi khi user đăng ký hoặc được admin tạo.
     */
    public void ensureDefaultSettingsForUser(Long userId) {
        if (userId == null) return;
        ensureSetting(
            userId,
            "settings.notifications",
            "{\"emailMeetings\":true,\"reminderMeetings\":true,\"approvalNotif\":true,\"taskDeadlineReminder\":false,\"weeklyReport\":false}"
        );
        ensureSetting(userId, "settings.security", "{\"autoLogout\":true}");
    }

    private void ensureSetting(Long userId, String key, String defaultValue) {
        if (settingRepository.findByUserIdAndKey(userId, key).isEmpty()) {
            SettingDTO dto = new SettingDTO();
            dto.setKey(key);
            dto.setValue(defaultValue);
            saveUserSetting(userId, dto);
            LOG.debug("Created default setting for user {}: {}", userId, key);
        }
    }
}
