package com.nexushr.performance.controller;

import com.nexushr.performance.model.Goal;
import com.nexushr.performance.model.PerformanceReview;
import com.nexushr.performance.model.ReviewCycle;
import com.nexushr.performance.repository.GoalRepository;
import com.nexushr.performance.repository.PerformanceReviewRepository;
import com.nexushr.performance.repository.ReviewCycleRepository;
import com.nexushr.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/performance")
@RequiredArgsConstructor
@Tag(name = "Performance", description = "OKRs, reviews, 360° feedback, calibration")
public class PerformanceController {

    private final GoalRepository goalRepository;
    private final PerformanceReviewRepository reviewRepository;
    private final ReviewCycleRepository cycleRepository;

    // === Review Cycles ===
    @PostMapping("/cycles")
    @Operation(summary = "Create review cycle")
    public ResponseEntity<ApiResponse<ReviewCycle>> createCycle(@RequestBody ReviewCycle cycle) {
        cycle.setTenantId("default");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cycleRepository.save(cycle)));
    }

    @GetMapping("/cycles")
    @Operation(summary = "List review cycles")
    public ResponseEntity<ApiResponse<List<ReviewCycle>>> listCycles() {
        return ResponseEntity.ok(ApiResponse.success(cycleRepository.findAll()));
    }

    // === Goals (OKR) ===
    @PostMapping("/goals")
    @Operation(summary = "Create a goal/OKR")
    public ResponseEntity<ApiResponse<Goal>> createGoal(@RequestBody Goal goal) {
        goal.setTenantId("default");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(goalRepository.save(goal)));
    }

    @GetMapping("/goals/employee/{employeeId}")
    @Operation(summary = "Get goals for an employee")
    public ResponseEntity<ApiResponse<List<Goal>>> getGoals(@PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(goalRepository.findByEmployeeIdAndDeletedFalse(employeeId)));
    }

    @PutMapping("/goals/{id}/progress")
    @Operation(summary = "Update goal progress")
    public ResponseEntity<ApiResponse<Goal>> updateProgress(@PathVariable String id, @RequestParam java.math.BigDecimal progress) {
        Goal goal = goalRepository.findById(id).orElseThrow();
        goal.setProgress(progress);
        if (progress.compareTo(new java.math.BigDecimal("100")) >= 0) goal.setStatus(Goal.GoalStatus.COMPLETED);
        else if (progress.compareTo(java.math.BigDecimal.ZERO) > 0) goal.setStatus(Goal.GoalStatus.IN_PROGRESS);
        return ResponseEntity.ok(ApiResponse.success(goalRepository.save(goal)));
    }

    // === Performance Reviews ===
    @PostMapping("/reviews")
    @Operation(summary = "Create a performance review")
    public ResponseEntity<ApiResponse<PerformanceReview>> createReview(@RequestBody PerformanceReview review) {
        review.setTenantId("default");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(reviewRepository.save(review)));
    }

    @GetMapping("/reviews/employee/{employeeId}")
    @Operation(summary = "Get reviews for an employee")
    public ResponseEntity<ApiResponse<List<PerformanceReview>>> getReviews(@PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(employeeId)));
    }

    @PutMapping("/reviews/{id}/submit")
    @Operation(summary = "Submit a review (advance stage)")
    public ResponseEntity<ApiResponse<PerformanceReview>> submitReview(@PathVariable String id) {
        PerformanceReview review = reviewRepository.findById(id).orElseThrow();
        review.setStage(PerformanceReview.ReviewStage.SUBMITTED);
        review.setSubmittedAt(java.time.LocalDate.now());
        return ResponseEntity.ok(ApiResponse.success(reviewRepository.save(review)));
    }

    @PutMapping("/reviews/{id}/calibrate")
    @Operation(summary = "Calibrate a review (HR)")
    public ResponseEntity<ApiResponse<PerformanceReview>> calibrateReview(
            @PathVariable String id, @RequestParam java.math.BigDecimal finalRating) {
        PerformanceReview review = reviewRepository.findById(id).orElseThrow();
        review.setFinalRating(finalRating);
        review.setStage(PerformanceReview.ReviewStage.CALIBRATION);
        review.setCalibratedAt(java.time.LocalDate.now());
        return ResponseEntity.ok(ApiResponse.success(reviewRepository.save(review)));
    }

    @PutMapping("/reviews/{id}/publish")
    @Operation(summary = "Publish review to employee")
    public ResponseEntity<ApiResponse<PerformanceReview>> publishReview(@PathVariable String id) {
        PerformanceReview review = reviewRepository.findById(id).orElseThrow();
        review.setStage(PerformanceReview.ReviewStage.PUBLISHED);
        review.setPublishedAt(java.time.LocalDate.now());
        return ResponseEntity.ok(ApiResponse.success(reviewRepository.save(review)));
    }
}
