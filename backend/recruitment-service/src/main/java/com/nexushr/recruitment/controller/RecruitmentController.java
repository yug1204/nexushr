package com.nexushr.recruitment.controller;

import com.nexushr.common.dto.ApiResponse;
import com.nexushr.recruitment.model.Candidate;
import com.nexushr.recruitment.model.JobRequisition;
import com.nexushr.recruitment.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recruitment")
@RequiredArgsConstructor
@Tag(name = "Recruitment & Onboarding", description = "ATS pipeline, job requisitions, candidate management")
public class RecruitmentController {

    private final RecruitmentService service;

    // ========== JOB REQUISITIONS ==========

    @PostMapping("/requisitions")
    @Operation(summary = "Create a job requisition")
    public ResponseEntity<ApiResponse<JobRequisition>> createRequisition(@RequestBody JobRequisition req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.createRequisition(req)));
    }

    @PutMapping("/requisitions/{id}/submit")
    @Operation(summary = "Submit requisition for HR approval")
    public ResponseEntity<ApiResponse<JobRequisition>> submitForApproval(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(service.submitForApproval(id)));
    }

    @PutMapping("/requisitions/{id}/approve")
    @Operation(summary = "Approve requisition and open for applications")
    public ResponseEntity<ApiResponse<JobRequisition>> approve(
            @PathVariable String id, @RequestParam String approvedBy) {
        return ResponseEntity.ok(ApiResponse.success(service.approveRequisition(id, approvedBy)));
    }

    @GetMapping("/requisitions/open")
    @Operation(summary = "Get all open job requisitions")
    public ResponseEntity<ApiResponse<List<JobRequisition>>> getOpen() {
        return ResponseEntity.ok(ApiResponse.success(service.getOpenRequisitions()));
    }

    @GetMapping("/requisitions/department/{dept}")
    @Operation(summary = "Get requisitions by department")
    public ResponseEntity<ApiResponse<List<JobRequisition>>> getByDept(@PathVariable String dept) {
        return ResponseEntity.ok(ApiResponse.success(service.getByDepartment(dept)));
    }

    // ========== CANDIDATES ==========

    @PostMapping("/candidates/apply/{requisitionId}")
    @Operation(summary = "Submit a job application")
    public ResponseEntity<ApiResponse<Candidate>> apply(
            @PathVariable String requisitionId, @RequestBody Candidate candidate) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.applyForJob(requisitionId, candidate)));
    }

    @PutMapping("/candidates/{id}/advance")
    @Operation(summary = "Advance candidate to next pipeline stage")
    public ResponseEntity<ApiResponse<Candidate>> advance(
            @PathVariable String id, @RequestParam Candidate.PipelineStage stage) {
        return ResponseEntity.ok(ApiResponse.success(service.advanceCandidate(id, stage)));
    }

    @PutMapping("/candidates/{id}/reject")
    @Operation(summary = "Reject a candidate")
    public ResponseEntity<ApiResponse<Candidate>> reject(
            @PathVariable String id, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(service.rejectCandidate(id, reason)));
    }

    @GetMapping("/candidates/requisition/{reqId}")
    @Operation(summary = "Get candidates for a requisition (sorted by AI match score)")
    public ResponseEntity<ApiResponse<List<Candidate>>> getCandidates(@PathVariable String reqId) {
        return ResponseEntity.ok(ApiResponse.success(service.getCandidatesForRequisition(reqId)));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get recruitment dashboard analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(service.getRecruitmentDashboard()));
    }
}
