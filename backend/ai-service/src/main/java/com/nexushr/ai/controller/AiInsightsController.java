package com.nexushr.ai.controller;

import com.nexushr.ai.engine.AttritionPredictionEngine.EmployeeFeatures;
import com.nexushr.ai.model.AttritionPrediction;
import com.nexushr.ai.model.SkillGap;
import com.nexushr.ai.service.AiInsightsService;
import com.nexushr.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Workforce Intelligence", description = "Predictive attrition, skill gaps, engagement scoring")
public class AiInsightsController {

    private final AiInsightsService aiService;

    @PostMapping("/predict/attrition")
    @Operation(summary = "Predict attrition risk for an employee")
    public ResponseEntity<ApiResponse<AttritionPrediction>> predictAttrition(
            @RequestBody EmployeeFeatures features) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(aiService.predictAttrition(features)));
    }

    @PostMapping("/predict/attrition/batch")
    @Operation(summary = "Batch predict attrition for multiple employees")
    public ResponseEntity<ApiResponse<List<AttritionPrediction>>> batchPredict(
            @RequestBody List<EmployeeFeatures> featuresList) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(aiService.batchPredict(featuresList)));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get AI workforce intelligence dashboard insights")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(aiService.getDashboardInsights()));
    }

    @GetMapping("/attrition/high-risk")
    @Operation(summary = "Get employees with high/critical attrition risk")
    public ResponseEntity<ApiResponse<List<AttritionPrediction>>> getHighRisk() {
        return ResponseEntity.ok(ApiResponse.success(aiService.getHighRiskEmployees()));
    }

    @GetMapping("/skills/{employeeId}")
    @Operation(summary = "Get skill gaps for an employee")
    public ResponseEntity<ApiResponse<List<SkillGap>>> getSkillGaps(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(aiService.getEmployeeSkillGaps(employeeId)));
    }

    @PostMapping("/skills/analyze")
    @Operation(summary = "Analyze skill gaps between current and required skills")
    public ResponseEntity<ApiResponse<List<SkillGap>>> analyzeSkillGaps(
            @RequestBody SkillGapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(aiService.analyzeSkillGaps(
                        request.employeeId, request.department,
                        request.currentSkills, request.requiredSkills)));
    }

    @PostMapping("/demo/generate")
    @Operation(summary = "Generate demo predictions for showcase")
    public ResponseEntity<ApiResponse<List<AttritionPrediction>>> generateDemo() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(aiService.generateDemoPredictions()));
    }

    @lombok.Data
    public static class SkillGapRequest {
        private String employeeId;
        private String department;
        private Map<String, Integer> currentSkills;
        private Map<String, Integer> requiredSkills;
    }
}
