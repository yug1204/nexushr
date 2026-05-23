package com.nexushr.notification.kafka;

import com.nexushr.notification.model.NotificationEvent;
import com.nexushr.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for HR domain events.
 * Consumes events from topic-per-domain and dispatches notifications
 * via the appropriate channel (email, SMS, WebSocket, push).
 *
 * Dead-letter queue: Failed messages after 3 retries go to *.DLT topic
 * for manual investigation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltStrategy = org.springframework.kafka.retrytopic.DltStrategy.ALWAYS_RETRY_ON_ERROR
    )
    @KafkaListener(topics = "hr.leave.approved", groupId = "notification-service")
    public void onLeaveApproved(String payload) {
        log.info("Received leave.approved event: {}", payload);
        notificationService.sendNotification(NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.LEAVE_APPROVED)
                .channel(NotificationEvent.Channel.EMAIL)
                .subject("Leave Request Approved")
                .body("Your leave request has been approved by your manager.")
                .payload(payload)
                .build());
    }

    @KafkaListener(topics = "hr.leave.rejected", groupId = "notification-service")
    public void onLeaveRejected(String payload) {
        log.info("Received leave.rejected event: {}", payload);
        notificationService.sendNotification(NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.LEAVE_REJECTED)
                .channel(NotificationEvent.Channel.EMAIL)
                .subject("Leave Request Rejected")
                .body("Your leave request has been rejected.")
                .payload(payload)
                .build());
    }

    @KafkaListener(topics = "hr.payroll.completed", groupId = "notification-service")
    public void onPayrollCompleted(String payload) {
        log.info("Received payroll.completed event: {}", payload);
        notificationService.sendNotification(NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.PAYROLL_PROCESSED)
                .channel(NotificationEvent.Channel.EMAIL)
                .subject("Payslip Available")
                .body("Your payslip for this month is now available. Login to view details.")
                .payload(payload)
                .build());
    }

    @KafkaListener(topics = "hr.review.submitted", groupId = "notification-service")
    public void onReviewSubmitted(String payload) {
        log.info("Received review.submitted event: {}", payload);
        notificationService.sendNotification(NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.REVIEW_SUBMITTED)
                .channel(NotificationEvent.Channel.WEBSOCKET)
                .subject("Performance Review Submitted")
                .body("A performance review has been submitted for your approval.")
                .payload(payload)
                .build());
    }

    @KafkaListener(topics = "hr.attrition.alert", groupId = "notification-service")
    public void onAttritionAlert(String payload) {
        log.info("Received attrition.alert event: {}", payload);
        notificationService.sendNotification(NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.SYSTEM_ALERT)
                .channel(NotificationEvent.Channel.EMAIL)
                .subject("⚠️ High Attrition Risk Alert")
                .body("An employee has been flagged as high attrition risk. Review AI insights.")
                .payload(payload)
                .build());
    }
}
