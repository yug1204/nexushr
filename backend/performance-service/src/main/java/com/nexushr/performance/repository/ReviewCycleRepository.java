package com.nexushr.performance.repository;

import com.nexushr.performance.model.ReviewCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewCycleRepository extends JpaRepository<ReviewCycle, String> {
    List<ReviewCycle> findByYearOrderByStartDateDesc(int year);
    List<ReviewCycle> findByStatus(ReviewCycle.CycleStatus status);
}
