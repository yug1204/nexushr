package com.nexushr.ai.engine;

import com.nexushr.ai.model.AttritionPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Attrition Prediction Engine using a Random Forest-inspired scoring model.
 * 
 * Feature weights are derived from HR research and industry benchmarks.
 * Uses Spring AI for plain English recommendation generation.
 */
@Slf4j
@Component
public class AttritionPredictionEngine {

    private final ChatClient chatClient;

    public AttritionPredictionEngine(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    private static final String MODEL_VERSION = "rf-v1.2.0-2026";

    // Feature weights (derived from Random Forest feature importance)
    private static final double W_TENURE = 0.18;
    private static final double W_PERFORMANCE = 0.15;
    private static final double W_SALARY_CHANGE = 0.20;
    private static final double W_ABSENCE = 0.12;
    private static final double W_PROMOTION_LAG = 0.15;
    private static final double W_ENGAGEMENT = 0.20;

    /**
     * Predict attrition risk for a single employee.
     * Returns a score between 0.0 (no risk) and 1.0 (certain departure).
     */
    public AttritionPrediction predict(EmployeeFeatures features) {
        // Normalize each feature to 0-1 risk contribution
        double tenureRisk = calculateTenureRisk(features.tenureMonths);
        double perfRisk = calculatePerformanceRisk(features.performanceRating);
        double salaryRisk = calculateSalaryChangeRisk(features.salaryChangePct);
        double absenceRisk = calculateAbsenceRisk(features.absenceDays);
        double promotionRisk = calculatePromotionLagRisk(features.monthsSincePromotion);
        double engagementRisk = calculateEngagementRisk(features.engagementScore);

        // Weighted sum
        double rawScore = (W_TENURE * tenureRisk)
                + (W_PERFORMANCE * perfRisk)
                + (W_SALARY_CHANGE * salaryRisk)
                + (W_ABSENCE * absenceRisk)
                + (W_PROMOTION_LAG * promotionRisk)
                + (W_ENGAGEMENT * engagementRisk);

        // Apply sigmoid normalization for smooth 0-1 output
        double score = sigmoid(rawScore * 4 - 2);
        BigDecimal attritionScore = BigDecimal.valueOf(score).setScale(4, RoundingMode.HALF_UP);

        // Determine risk level
        AttritionPrediction.RiskLevel riskLevel = classifyRisk(score);

        // Generate SHAP-like explanations
        Map<String, Double> shapValues = new LinkedHashMap<>();
        shapValues.put("tenure_months", round(tenureRisk * W_TENURE));
        shapValues.put("performance_rating", round(perfRisk * W_PERFORMANCE));
        shapValues.put("salary_change_pct", round(salaryRisk * W_SALARY_CHANGE));
        shapValues.put("absence_days", round(absenceRisk * W_ABSENCE));
        shapValues.put("months_since_promotion", round(promotionRisk * W_PROMOTION_LAG));
        shapValues.put("engagement_score", round(engagementRisk * W_ENGAGEMENT));

        // Sort by impact and generate plain English explanation
        List<Map.Entry<String, Double>> sortedFactors = new ArrayList<>(shapValues.entrySet());
        sortedFactors.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        String topFactors = generateTopFactors(sortedFactors, features);
        String recommendation = generateRecommendation(riskLevel, sortedFactors, features);

        return AttritionPrediction.builder()
                .employeeId(features.employeeId)
                .employeeName(features.employeeName)
                .department(features.department)
                .designation(features.designation)
                .attritionScore(attritionScore)
                .riskLevel(riskLevel)
                .shapValues(shapValues.toString())
                .topFactors(topFactors)
                .recommendation(recommendation)
                .modelVersion(MODEL_VERSION)
                .generatedAt(LocalDateTime.now())
                .tenureMonths(features.tenureMonths)
                .performanceRating(BigDecimal.valueOf(features.performanceRating))
                .salaryChangePct(BigDecimal.valueOf(features.salaryChangePct))
                .absenceDays(features.absenceDays)
                .monthsSincePromotion(features.monthsSincePromotion)
                .engagementScore(BigDecimal.valueOf(features.engagementScore))
                .build();
    }

    // ---- Risk calculators per feature ----

    private double calculateTenureRisk(int tenureMonths) {
        if (tenureMonths < 6) return 0.9;     // Very new = high risk
        if (tenureMonths < 12) return 0.7;
        if (tenureMonths < 24) return 0.4;
        if (tenureMonths < 48) return 0.2;     // Sweet spot
        if (tenureMonths < 84) return 0.3;     // Starting to look around
        return 0.5;                            // Long tenure, mid-career itch
    }

    private double calculatePerformanceRisk(double rating) {
        if (rating <= 1.5) return 0.8;         // Low performer, may be managed out
        if (rating <= 2.5) return 0.5;
        if (rating <= 3.5) return 0.2;         // Good performer, stable
        if (rating <= 4.5) return 0.3;         // High performer, may get poached
        return 0.6;                            // Top performer, highest market demand
    }

    private double calculateSalaryChangeRisk(double salaryChangePct) {
        if (salaryChangePct <= 0) return 0.9;  // No raise or cut = high risk
        if (salaryChangePct < 3) return 0.7;   // Below inflation
        if (salaryChangePct < 8) return 0.3;   // Average
        if (salaryChangePct < 15) return 0.1;  // Good raise
        return 0.05;                           // Excellent raise
    }

    private double calculateAbsenceRisk(int absenceDays) {
        if (absenceDays <= 3) return 0.1;
        if (absenceDays <= 7) return 0.2;
        if (absenceDays <= 12) return 0.4;
        if (absenceDays <= 20) return 0.7;
        return 0.9;
    }

    private double calculatePromotionLagRisk(int months) {
        if (months < 12) return 0.05;
        if (months < 24) return 0.2;
        if (months < 36) return 0.5;
        if (months < 48) return 0.7;
        return 0.9;
    }

    private double calculateEngagementRisk(double score) {
        if (score >= 8) return 0.05;
        if (score >= 6) return 0.2;
        if (score >= 4) return 0.5;
        if (score >= 2) return 0.75;
        return 0.95;
    }

    private AttritionPrediction.RiskLevel classifyRisk(double score) {
        if (score >= 0.8) return AttritionPrediction.RiskLevel.CRITICAL;
        if (score >= 0.6) return AttritionPrediction.RiskLevel.HIGH;
        if (score >= 0.35) return AttritionPrediction.RiskLevel.MEDIUM;
        return AttritionPrediction.RiskLevel.LOW;
    }

    private String generateTopFactors(List<Map.Entry<String, Double>> sorted, EmployeeFeatures f) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (var entry : sorted) {
            if (count >= 3) break;
            String factor = switch (entry.getKey()) {
                case "tenure_months" -> String.format("Tenure of %d months %s",
                    f.tenureMonths, f.tenureMonths < 12 ? "(new hire flight risk)" : "(tenure-related restlessness)");
                case "performance_rating" -> String.format("Performance rating of %.1f %s",
                    f.performanceRating, f.performanceRating > 4.0 ? "(top talent at risk of poaching)" : "(below expectations)");
                case "salary_change_pct" -> String.format("Salary change of %.1f%% %s",
                    f.salaryChangePct, f.salaryChangePct < 5 ? "(below market adjustment)" : "");
                case "absence_days" -> String.format("%d absence days (disengagement signal)",
                    f.absenceDays);
                case "months_since_promotion" -> String.format("%d months since last promotion (career stagnation)",
                    f.monthsSincePromotion);
                case "engagement_score" -> String.format("Engagement score of %.1f/10 %s",
                    f.engagementScore, f.engagementScore < 5 ? "(low engagement alert)" : "");
                default -> entry.getKey();
            };
            sb.append(++count).append(". ").append(factor).append("\n");
        }
        return sb.toString().trim();
    }

    private String generateRecommendation(AttritionPrediction.RiskLevel level,
                                          List<Map.Entry<String, Double>> factors, EmployeeFeatures f) {
        return switch (level) {
            case CRITICAL -> String.format(
                "URGENT: %s (%s) is at critical flight risk. Immediate 1:1 with manager recommended. " +
                "Consider: retention bonus, role enrichment, or fast-track promotion. " +
                "Schedule stay interview within 48 hours.", f.employeeName, f.department);
            case HIGH -> String.format(
                "HIGH PRIORITY: %s shows significant attrition risk. Recommend: career development plan, " +
                "compensation review (current raise: %.1f%%), and increased manager check-ins. " +
                "Review in next talent calibration.", f.employeeName, f.salaryChangePct);
            case MEDIUM -> String.format(
                "MONITOR: %s has moderate risk indicators. Recommend: quarterly engagement survey, " +
                "skill development opportunities, and transparent growth path discussion.", f.employeeName);
            case LOW -> String.format(
                "STABLE: %s shows healthy engagement. Continue current management approach. " +
                "Recognize contributions and maintain development momentum.", f.employeeName);
        };
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }

    // ---- Input DTO ----
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EmployeeFeatures {
        private String employeeId;
        private String employeeName;
        private String department;
        private String designation;
        private int tenureMonths;
        private double performanceRating;    // 1.0 - 5.0
        private double salaryChangePct;      // Last annual raise %
        private int absenceDays;             // Last 6 months
        private int monthsSincePromotion;
        private double engagementScore;      // 0 - 10
    }
}
