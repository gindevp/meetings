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
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

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
     * POST /register : register the user.
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
     * GET /activate : activate the registered user.
     * Returns HTML page with success message.
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
     * GET /activate : activate the registered user (redirect version).
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
     * GET /account : get the current user.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * GET /account/avatar : get the current user's profile image.
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
     * POST /account/avatar : update the current user's profile image.
     * Body: { "file": "base64...", "fileContentType": "image/jpeg" }
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
     * DELETE /account/avatar : remove the current user's profile image.
     */
    @DeleteMapping("/account/avatar")
    public ResponseEntity<Void> deleteAccountAvatar() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        userService.clearAvatar(login);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /account : update the current user information.
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
     * POST /account/change-password : changes the current user's password.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * POST /account/reset-password/init : Send an email to reset the password.
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
     * POST /account/reset-password/finish : Finish to reset the password.
     * Returns HTML page with success message.
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
     * POST /account/reset-password/finish : Finish to reset the password (redirect version).
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

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
