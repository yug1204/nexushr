package com.nexushr.performance.repository;

import com.nexushr.performance.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, String> {
    List<Goal> findByEmployeeIdAndDeletedFalse(String employeeId);
    List<Goal> findByReviewCycleIdAndDeletedFalse(String cycleId);
    List<Goal> findByParentGoalIdAndDeletedFalse(String parentId);
}
