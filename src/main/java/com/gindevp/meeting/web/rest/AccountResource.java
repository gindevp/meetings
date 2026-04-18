package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.security.SecurityUtils;
import com.gindevp.meeting.service.ExpoPushTokenService;
import com.gindevp.meeting.service.MailService;
import com.gindevp.meeting.service.UserService;
import com.gindevp.meeting.service.dto.AdminUserDTO;
import com.gindevp.meeting.service.dto.PasswordChangeDTO;
import com.gindevp.meeting.web.rest.errors.*;
import com.gindevp.meeting.web.rest.vm.KeyAndPasswordVM;
import com.gindevp.meeting.web.rest.vm.ManagedUserVM;
import jakarta.validation.Valid;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.config.JHipsterProperties;

/**
 * API tài khoản người dùng hiện tại: đăng ký, kích hoạt, đổi thông tin, avatar, mật khẩu, Expo push token.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    /** Lỗi nội bộ khi thao tác tài khoản thất bại (message hiển thị cho client). */
    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final ExpoPushTokenService expoPushTokenService;

    private final JHipsterProperties jHipsterProperties;

    /**
     * @param userRepository tra cứu user theo login/email
     * @param userService nghiệp vụ đăng ký, cập nhật, mật khẩu
     * @param mailService gửi mail (reset mật khẩu...)
     * @param expoPushTokenService đăng ký token push mobile
     * @param jHipsterProperties URL redirect sau kích hoạt / reset
     */
    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        MailService mailService,
        ExpoPushTokenService expoPushTokenService,
        JHipsterProperties jHipsterProperties
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.expoPushTokenService = expoPushTokenService;
        this.jHipsterProperties = jHipsterProperties;
    }

    /**
     * {@code POST /register} — Đăng ký tài khoản mới; mật khẩu mặc định cố định, không gửi mail kích hoạt (môi trường cloud).
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        // Luôn dùng pass mặc định 1234, không gửi mail kích hoạt (server cloud chặn mail)
        User user = userService.registerUser(managedUserVM, "1234");
        // mailService.sendActivationEmail(user); -- tắt vì không gửi được mail
    }

    /**
     * {@code GET /activate} — Kích hoạt tài khoản bằng key; trả về trang HTML thành công/thất bại.
     */
    @GetMapping(value = "/activate", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String activateAccountHtml(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            return "<html><body><h1>Activation Failed</h1><p>Invalid activation key.</p><a href=\"/\">Go to Home</a></body></html>";
        }
        return "<!DOCTYPE html><html><head><title>Activation Success</title></head><body style=\"font-family: Arial, sans-serif; text-align: center; padding: 50px;\"><h1 style=\"color: green;\">Account Activated Successfully!</h1><p>Your account has been activated. You can now login.</p><a href=\"http://localhost:5173/login\" style=\"background: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Go to Login</a></body></html>";
    }

    /**
     * {@code GET /activate-redirect} — Kích hoạt rồi redirect về URL cấu hình (phiên bản redirect).
     */
    @GetMapping("/activate-redirect")
    public String activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
        return "redirect:" + jHipsterProperties.getMail().getBaseUrl() + "/activate-success";
    }

    /**
     * {@code GET /account} — Lấy thông tin user đang đăng nhập (kèm quyền).
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code GET /account/avatar} — Trả về ảnh đại diện nhị phân (204 nếu chưa có).
     */
    @GetMapping("/account/avatar")
    public ResponseEntity<byte[]> getAccountAvatar() {
        return userService
            .getUserWithAuthorities()
            .filter(u -> u.getImageData() != null && u.getImageData().length > 0)
            .map(u -> {
                String contentType = u.getImageContentType() != null ? u.getImageContentType() : "image/jpeg";
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(org.springframework.http.CacheControl.noStore().mustRevalidate())
                    .body(u.getImageData());
            })
            .orElse(ResponseEntity.noContent().build());
    }

    /**
     * {@code POST /account/avatar} — Cập nhật ảnh đại diện (base64 trong JSON).
     */
    @PostMapping("/account/avatar")
    public ResponseEntity<Void> saveAccountAvatar(@RequestBody java.util.Map<String, String> body) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        String base64 = body.get("file");
        String fileContentType = body.get("fileContentType");
        if (base64 == null || base64.isBlank()) {
            throw new AccountResourceException("file (base64) is required");
        }
        byte[] imageData = java.util.Base64.getDecoder().decode(base64.trim());
        if (imageData.length == 0) {
            throw new AccountResourceException("Invalid image data");
        }
        userService.updateAvatar(login, imageData, fileContentType != null ? fileContentType : "image/jpeg");
        return ResponseEntity.ok().build();
    }

    /**
     * {@code DELETE /account/avatar} — Xóa ảnh đại diện đã lưu.
     */
    @DeleteMapping("/account/avatar")
    public ResponseEntity<Void> deleteAccountAvatar() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        userService.clearAvatar(login);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST /account} — Cập nhật họ tên, email, ngôn ngữ... (email không trùng user khác).
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );
    }

    /**
     * POST /account/expo-push-token : Đăng ký Expo push token cho thiết bị hiện tại (app mobile).
     * Body: { "token": "ExponentPushToken[xxx]" }
     */
    @PostMapping("/account/expo-push-token")
    public ResponseEntity<Void> registerExpoPushToken(@RequestBody java.util.Map<String, String> body) {
        String token = body != null ? body.get("token") : null;
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Long userId = userService.getUserWithAuthorities().map(u -> u.getId()).orElse(null);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        expoPushTokenService.registerToken(userId, token.trim());
        return ResponseEntity.ok().build();
    }

    /**
     * {@code POST /account/change-password} — Đổi mật khẩu (cần mật khẩu hiện tại).
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST /account/reset-password/init} — Gửi email chứa link/key reset mật khẩu.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<User> user = userService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.orElseThrow());
        } else {
            LOG.warn("Password reset requested for non existing mail");
        }
    }

    /**
     * {@code POST /account/reset-password/finish} — Hoàn tất đặt lại mật khẩu (HTML); key + mật khẩu mới trong body.
     */
    @PostMapping(path = "/account/reset-password/finish", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String finishPasswordResetHtml(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            return "<html><body><h1>Password Reset Failed</h1><p>Invalid reset key.</p><a href=\"/\">Go to Home</a></body></html>";
        }
        return "<!DOCTYPE html><html><head><title>Password Reset Success</title></head><body style=\"font-family: Arial, sans-serif; text-align: center; padding: 50px;\"><h1 style=\"color: green;\">Password Reset Successfully!</h1><p>Your password has been reset. You can now login with your new password.</p><a href=\"/login\" style=\"background: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Go to Login</a></body></html>";
    }

    /**
     * {@code POST /account/reset-password/finish} — Phiên bản redirect sau khi reset thành công.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public String finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
        return "redirect:" + jHipsterProperties.getMail().getBaseUrl() + "/password-reset-success";
    }

    /** Kiểm tra độ dài mật khẩu theo giới hạn ManagedUserVM. */
    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
