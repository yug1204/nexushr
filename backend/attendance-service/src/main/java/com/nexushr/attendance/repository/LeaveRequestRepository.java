package com.nexushr.attendance.repository;

import com.nexushr.attendance.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {

    List<LeaveRequest> findByEmployeeIdAndDeletedFalseOrderByCreatedAtDesc(String employeeId);

    List<LeaveRequest> findByStatusAndTenantIdOrderByCreatedAtDesc(
            LeaveRequest.LeaveStatus status, String tenantId);

    List<LeaveRequest> findByApproverIdAndStatusOrderByCreatedAtDesc(
            String approverId, LeaveRequest.LeaveStatus status);
}
