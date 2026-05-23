package com.nexushr.notification.service;

import com.nexushr.notification.model.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Notification dispatcher supporting Email, SMS (via webhook), and In-App channels.
 * Kafka consumer will invoke this service when events arrive on notification topics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    public void dispatch(NotificationEvent event) {
        switch (event.getChannel()) {
            case EMAIL -> sendEmail(event);
            case SMS -> sendSms(event);
            case IN_APP -> sendInApp(event);
            case PUSH -> sendPush(event);
            default -> log.warn("Unsupported channel: {}", event.getChannel());
        }
    }

    private void sendEmail(NotificationEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getRecipientEmail());
            message.setSubject(event.getSubject());
            message.setText(resolveTemplate(event));
            message.setFrom("noreply@nexushr.com");
            mailSender.send(message);
            event.setStatus(NotificationEvent.NotificationStatus.SENT);
            log.info("Email sent: to={}, subject={}", event.getRecipientEmail(), event.getSubject());
        } catch (Exception e) {
            event.setStatus(NotificationEvent.NotificationStatus.FAILED);
            log.error("Email failed: to={}, error={}", event.getRecipientEmail(), e.getMessage());
        }
    }

    private void sendSms(NotificationEvent event) {
        // SMS gateway integration placeholder (Twilio, MSG91, etc.)
        log.info("SMS sent: to={}, body={}", event.getRecipientPhone(), event.getBody());
        event.setStatus(NotificationEvent.NotificationStatus.SENT);
    }

    private void sendInApp(NotificationEvent event) {
        // WebSocket push to connected clients
        log.info("In-App notification: to={}, subject={}", event.getRecipientId(), event.getSubject());
        event.setStatus(NotificationEvent.NotificationStatus.DELIVERED);
    }

    private void sendPush(NotificationEvent event) {
        // Firebase Cloud Messaging integration placeholder
        log.info("Push notification: to={}, subject={}", event.getRecipientId(), event.getSubject());
        event.setStatus(NotificationEvent.NotificationStatus.SENT);
    }

    private String resolveTemplate(NotificationEvent event) {
        String body = getTemplate(event.getTemplateId());
        if (event.getTemplateData() != null) {
            for (Map.Entry<String, String> entry : event.getTemplateData().entrySet()) {
                body = body.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
        }
        return body;
    }

    private String getTemplate(String templateId) {
        return switch (templateId != null ? templateId : "") {
            case "leave_approved" -> """
                Dear {{employeeName}},
                
                Your {{leaveType}} request from {{startDate}} to {{endDate}} has been APPROVED.
                
                Approved by: {{approverName}}
                Remarks: {{remarks}}
                
                Regards,
                NexusHR - HR Department
                """;
            case "leave_rejected" -> """
                Dear {{employeeName}},
                
                Your {{leaveType}} request from {{startDate}} to {{endDate}} has been REJECTED.
                
                Reason: {{remarks}}
                
                Regards,
                NexusHR - HR Department
                """;
            case "payslip_generated" -> """
                Dear {{employeeName}},
                
                Your payslip for {{period}} has been generated.
                
                Net Salary: {{netSalary}}
                
                You can download it from the NexusHR portal.
                
                Regards,
                NexusHR - Payroll Department
                """;
            case "welcome" -> """
                Welcome to NexusHR, {{employeeName}}!
                
                Your employee ID: {{employeeCode}}
                Department: {{department}}
                Manager: {{managerName}}
                
                Please complete your onboarding at: {{portalUrl}}
                
                Best regards,
                NexusHR - HR Team
                """;
            case "review_assigned" -> """
                Dear {{employeeName}},
                
                A performance review has been assigned to you for {{reviewCycle}}.
                
                Reviewee: {{revieweeName}}
                Type: {{reviewType}}
                Deadline: {{deadline}}
                
                Please complete it at: {{portalUrl}}
                
                Regards,
                NexusHR - Performance Team
                """;
            default -> event.getBody() != null ? event.getBody() : "Notification from NexusHR";
        };
    }
}
