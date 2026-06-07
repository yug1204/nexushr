package com.nexushr.auth.service;

import com.nexushr.auth.model.EmailLog;
import com.nexushr.auth.repository.EmailLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email service for sending transactional emails and logging them.
 * Adapted from batch 8 TaskManagementTool_B8 EmailService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    /**
     * Send a password reset email with a reset link.
     */
    public void sendResetPasswordEmail(String to, String token) {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Your Password - NexusHR");
        message.setText("Hello,\n\nClick the link below to reset your password:\n\n"
                + resetLink + "\n\n"
                + "This link will expire in 15 minutes.\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Regards,\nNexusHR Platform");

        try {
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
            saveEmailLog(to, "Reset Your Password - NexusHR",
                    "Password reset link sent", true);
        } catch (Exception e) {
            log.error("Failed to send reset email to {}: {}", to, e.getMessage());
            saveEmailLog(to, "Reset Your Password - NexusHR",
                    "Password reset link sent", false);
        }
    }

    /**
     * Send a generic email with HTML content support and log the result.
     */
    public String sendEmail(EmailLog emailLog) {
        boolean sentStatus = false;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(emailLog.getRecipientEmail());
            helper.setSubject(emailLog.getSubject());
            helper.setText(emailLog.getBody(), true);

            mailSender.send(message);
            sentStatus = true;
            log.info("Email sent successfully to: {}", emailLog.getRecipientEmail());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", emailLog.getRecipientEmail(), e.getMessage());
            sentStatus = false;
        }

        saveEmailLog(emailLog.getRecipientEmail(), emailLog.getSubject(),
                emailLog.getBody(), sentStatus);

        return sentStatus ? "Email sent successfully" : "Email sending failed";
    }

    private void saveEmailLog(String recipientEmail, String subject, String body, boolean sentStatus) {
        EmailLog log = new EmailLog(recipientEmail, subject, body, sentStatus);
        emailLogRepository.save(log);
    }
}
