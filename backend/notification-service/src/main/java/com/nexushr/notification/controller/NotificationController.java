package com.nexushr.notification.controller;

import com.nexushr.notification.model.NotificationEvent;
import com.nexushr.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> send(@RequestBody NotificationEvent event) {
        notificationService.dispatch(event);
        return ResponseEntity.ok(Map.of(
                "status", event.getStatus().name(),
                "message", "Notification processed"
        ));
    }

    @PostMapping("/test-email")
    public ResponseEntity<Map<String, String>> testEmail(
            @RequestParam String to, @RequestParam String subject) {
        NotificationEvent event = NotificationEvent.builder()
                .recipientEmail(to)
                .subject(subject)
                .body("This is a test notification from NexusHR.")
                .channel(NotificationEvent.NotificationChannel.EMAIL)
                .type(NotificationEvent.NotificationType.SYSTEM_ALERT)
                .build();
        notificationService.dispatch(event);
        return ResponseEntity.ok(Map.of("status", "sent"));
    }
}
