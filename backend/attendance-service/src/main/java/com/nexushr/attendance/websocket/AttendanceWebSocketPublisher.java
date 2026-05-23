package com.nexushr.attendance.websocket;

import com.nexushr.attendance.model.AttendanceRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Broadcasts real-time attendance events to connected WebSocket clients.
 * HR dashboard subscribes to /topic/attendance for live updates.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceWebSocketPublisher {

    private final SimpMessagingTemplate messaging;

    public void broadcastClockIn(AttendanceRecord record) {
        Map<String, Object> event = Map.of(
                "type", "CLOCK_IN",
                "employeeId", record.getEmployeeId(),
                "time", record.getClockInTime().toString(),
                "source", record.getClockSource().name(),
                "status", record.getStatus().name()
        );
        messaging.convertAndSend("/topic/attendance", event);
        log.debug("WebSocket: broadcast CLOCK_IN for {}", record.getEmployeeId());
    }

    public void broadcastClockOut(AttendanceRecord record) {
        Map<String, Object> event = Map.of(
                "type", "CLOCK_OUT",
                "employeeId", record.getEmployeeId(),
                "time", record.getClockOutTime().toString(),
                "hoursWorked", record.getTotalHoursWorked() != null ? record.getTotalHoursWorked() : 0,
                "overtime", record.getOvertimeHours() != null ? record.getOvertimeHours() : 0
        );
        messaging.convertAndSend("/topic/attendance", event);
        log.debug("WebSocket: broadcast CLOCK_OUT for {}", record.getEmployeeId());
    }

    public void broadcastLeaveEvent(String type, String employeeId, String leaveType, int days) {
        Map<String, Object> event = Map.of(
                "type", type,
                "employeeId", employeeId,
                "leaveType", leaveType,
                "days", days
        );
        messaging.convertAndSend("/topic/attendance", event);
    }
}
