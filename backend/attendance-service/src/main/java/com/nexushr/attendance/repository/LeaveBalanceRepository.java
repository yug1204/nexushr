package com.nexushr.attendance.repository;

import com.nexushr.attendance.model.LeaveBalance;
import com.nexushr.attendance.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, String> {

    List<LeaveBalance> findByEmployeeIdAndYearAndDeletedFalse(String employeeId, int year);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeAndYearAndDeletedFalse(
            String employeeId, LeaveRequest.LeaveType leaveType, int year);
}
