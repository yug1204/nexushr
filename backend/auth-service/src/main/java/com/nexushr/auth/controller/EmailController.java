package com.nexushr.auth.controller;

import com.nexushr.auth.model.EmailLog;
import com.nexushr.auth.service.EmailService;
import com.nexushr.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Email controller for sending transactional emails.
 * Adapted from batch 8 TaskManagementTool_B8 EmailLogController.
 */
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Email sending endpoints")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "Send an email (admin only)")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_HR_ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody EmailLog emailLog) {
        String result = emailService.sendEmail(emailLog);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
