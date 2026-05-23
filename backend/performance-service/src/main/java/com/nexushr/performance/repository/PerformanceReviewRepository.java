package com.nexushr.performance.repository;

import com.nexushr.performance.model.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, String> {
    List<PerformanceReview> findByRevieweeIdOrderByCreatedAtDesc(String revieweeId);
    List<PerformanceReview> findByReviewCycleIdAndStage(String cycleId, PerformanceReview.ReviewStage stage);
    List<PerformanceReview> findByReviewerIdOrderByCreatedAtDesc(String reviewerId);
}
