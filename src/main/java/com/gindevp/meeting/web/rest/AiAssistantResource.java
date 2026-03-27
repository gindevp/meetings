package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.security.SecurityUtils;
import com.gindevp.meeting.service.ai.AiAssistantGatewayService;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantResource {

    private static final String ENTITY_NAME = "aiAssistant";

    private final AiAssistantGatewayService aiAssistantGatewayService;
    private final UserRepository userRepository;

    public AiAssistantResource(AiAssistantGatewayService aiAssistantGatewayService, UserRepository userRepository) {
        this.aiAssistantGatewayService = aiAssistantGatewayService;
        this.userRepository = userRepository;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiAssistantGatewayService.AiAssistantResponse> chat(
        @Valid @RequestBody AiAssistantGatewayService.AiAssistantRequest request
    ) {
        User currentUser = currentUser();
        AiAssistantGatewayService.AiAssistantResponse response = aiAssistantGatewayService.chat(request, currentUser);
        return ResponseEntity.ok(response);
    }

    private User currentUser() {
        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));
        return userRepository
            .findOneWithAuthoritiesByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));
    }
}
