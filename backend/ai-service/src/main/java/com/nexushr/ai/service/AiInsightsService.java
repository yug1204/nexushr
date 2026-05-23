package com.nexushr.ai.service;

import com.nexushr.ai.engine.AttritionPredictionEngine;
import com.nexushr.ai.engine.AttritionPredictionEngine.EmployeeFeatures;
import com.nexushr.ai.model.AttritionPrediction;
import com.nexushr.ai.model.SkillGap;
import com.nexushr.ai.repository.AttritionPredictionRepository;
import com.nexushr.ai.repository.SkillGapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiInsightsService {

    private final AttritionPredictionEngine predictionEngine;
    private final AttritionPredictionRepository predictionRepo;
    private final SkillGapRepository skillGapRepo;

    @Transactional
    public AttritionPrediction predictAttrition(EmployeeFeatures features) {
        AttritionPrediction prediction = predictionEngine.predict(features);
        prediction.setTenantId("default");
        return predictionRepo.save(prediction);
    }

    @Transactional
    public List<AttritionPrediction> batchPredict(List<EmployeeFeatures> featuresList) {
        List<AttritionPrediction> predictions = featuresList.parallelStream()
                .map(predictionEngine::predict)
                .peek(p -> p.setTenantId("default"))
                .toList();
        return predictionRepo.saveAll(predictions);
    }

    public Map<String, Object> getDashboardInsights() {
        Map<String, Object> insights = new LinkedHashMap<>();
        insights.put("riskDistribution", predictionRepo.getRiskDistribution());
        insights.put("topAtRiskEmployees", predictionRepo.findHighRiskEmployees().stream().limit(10).toList());
        insights.put("attritionByDepartment", predictionRepo.getAvgAttritionByDepartment());
        insights.put("topSkillGaps", skillGapRepo.getTopSkillGaps());
        insights.put("generatedAt", LocalDateTime.now());
        return insights;
    }

    public List<AttritionPrediction> getHighRiskEmployees() {
        return predictionRepo.findHighRiskEmployees();
    }

    public List<SkillGap> getEmployeeSkillGaps(String employeeId) {
        return skillGapRepo.findByEmployeeIdOrderByGapScoreDesc(employeeId);
    }

    @Transactional
    public List<SkillGap> analyzeSkillGaps(String empId, String dept,
                                           Map<String, Integer> current, Map<String, Integer> required) {
        List<SkillGap> gaps = new ArrayList<>();
        Set<String> all = new HashSet<>(current.keySet());
        all.addAll(required.keySet());
        for (String skill : all) {
            int cur = current.getOrDefault(skill, 0);
            int req = required.getOrDefault(skill, 0);
            int gap = req - cur;
            if (gap > 0) {
                SkillGap sg = SkillGap.builder()
                    .employeeId(empId).department(dept).skillName(skill)
                    .currentLevel(BigDecimal.valueOf(cur)).requiredLevel(BigDecimal.valueOf(req))
                    .gapScore(BigDecimal.valueOf(gap))
                    .priority(gap > 30 ? SkillGap.Priority.HIGH : gap > 15 ? SkillGap.Priority.MEDIUM : SkillGap.Priority.LOW)
                    .learningRecommendation("Complete " + skill + " training program on internal LMS.")
                    .build();
                sg.setTenantId("default");
                gaps.add(sg);
            }
        }
        return skillGapRepo.saveAll(gaps);
    }

    @Transactional
    public List<AttritionPrediction> generateDemoPredictions() {
        List<EmployeeFeatures> demo = List.of(
            EmployeeFeatures.builder().employeeId("EMP001").employeeName("Priya Sharma").department("Engineering").designation("Senior Developer").tenureMonths(8).performanceRating(4.5).salaryChangePct(2.0).absenceDays(3).monthsSincePromotion(18).engagementScore(6.0).build(),
            EmployeeFeatures.builder().employeeId("EMP002").employeeName("Rahul Verma").department("Sales").designation("Account Manager").tenureMonths(36).performanceRating(2.0).salaryChangePct(0).absenceDays(15).monthsSincePromotion(42).engagementScore(3.5).build(),
            EmployeeFeatures.builder().employeeId("EMP003").employeeName("Anita Desai").department("Marketing").designation("Brand Manager").tenureMonths(24).performanceRating(3.8).salaryChangePct(12.0).absenceDays(5).monthsSincePromotion(12).engagementScore(7.5).build(),
            EmployeeFeatures.builder().employeeId("EMP004").employeeName("Vikram Singh").department("Engineering").designation("Tech Lead").tenureMonths(60).performanceRating(4.8).salaryChangePct(3.0).absenceDays(2).monthsSincePromotion(48).engagementScore(5.0).build(),
            EmployeeFeatures.builder().employeeId("EMP005").employeeName("Deepa Nair").department("Finance").designation("Analyst").tenureMonths(4).performanceRating(3.0).salaryChangePct(0).absenceDays(8).monthsSincePromotion(4).engagementScore(4.0).build(),
            EmployeeFeatures.builder().employeeId("EMP006").employeeName("Arjun Patel").department("Operations").designation("Ops Manager").tenureMonths(48).performanceRating(3.5).salaryChangePct(8.0).absenceDays(4).monthsSincePromotion(24).engagementScore(7.0).build(),
            EmployeeFeatures.builder().employeeId("EMP007").employeeName("Kavita Menon").department("HR").designation("HRBP").tenureMonths(18).performanceRating(4.2).salaryChangePct(15.0).absenceDays(1).monthsSincePromotion(6).engagementScore(8.5).build(),
            EmployeeFeatures.builder().employeeId("EMP008").employeeName("Suresh Kumar").department("Support").designation("Support Lead").tenureMonths(72).performanceRating(2.5).salaryChangePct(1.0).absenceDays(20).monthsSincePromotion(60).engagementScore(2.0).build()
        );
        return batchPredict(demo);
    }
}
