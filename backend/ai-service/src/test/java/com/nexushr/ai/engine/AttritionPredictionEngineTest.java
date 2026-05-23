package com.nexushr.ai.engine;

import com.nexushr.ai.engine.AttritionPredictionEngine.EmployeeFeatures;
import com.nexushr.ai.model.AttritionPrediction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AttritionPredictionEngine Unit Tests")
class AttritionPredictionEngineTest {

    private AttritionPredictionEngine engine;

    @BeforeEach
    void setUp() {
        engine = new AttritionPredictionEngine();
    }

    @Test
    @DisplayName("High risk: low tenure, no raise, high absence")
    void predict_HighRisk() {
        EmployeeFeatures f = EmployeeFeatures.builder()
                .employeeId("E001").employeeName("Test High Risk").department("Engineering")
                .designation("Junior Developer").tenureMonths(3).performanceRating(2.0)
                .salaryChangePct(0).absenceDays(18).monthsSincePromotion(3).engagementScore(2.5)
                .build();

        AttritionPrediction p = engine.predict(f);

        assertThat(p.getAttritionScore()).isGreaterThan(new java.math.BigDecimal("0.5"));
        assertThat(p.getRiskLevel()).isIn(
                AttritionPrediction.RiskLevel.HIGH, AttritionPrediction.RiskLevel.CRITICAL);
        assertThat(p.getRecommendation()).isNotEmpty();
        assertThat(p.getTopFactors()).isNotEmpty();
    }

    @Test
    @DisplayName("Low risk: good tenure, good raise, engaged")
    void predict_LowRisk() {
        EmployeeFeatures f = EmployeeFeatures.builder()
                .employeeId("E002").employeeName("Test Stable").department("HR")
                .designation("HRBP").tenureMonths(36).performanceRating(3.8)
                .salaryChangePct(12.0).absenceDays(2).monthsSincePromotion(12).engagementScore(8.0)
                .build();

        AttritionPrediction p = engine.predict(f);

        assertThat(p.getAttritionScore()).isLessThan(new java.math.BigDecimal("0.4"));
        assertThat(p.getRiskLevel()).isEqualTo(AttritionPrediction.RiskLevel.LOW);
    }

    @Test
    @DisplayName("Score is always between 0 and 1")
    void predict_ScoreBounds() {
        EmployeeFeatures f = EmployeeFeatures.builder()
                .employeeId("E003").employeeName("Boundary").department("Ops")
                .designation("Manager").tenureMonths(100).performanceRating(5.0)
                .salaryChangePct(-5).absenceDays(30).monthsSincePromotion(72).engagementScore(0)
                .build();

        AttritionPrediction p = engine.predict(f);

        assertThat(p.getAttritionScore().doubleValue()).isBetween(0.0, 1.0);
    }

    @Test
    @DisplayName("SHAP values are populated")
    void predict_ShapValues() {
        EmployeeFeatures f = EmployeeFeatures.builder()
                .employeeId("E004").employeeName("SHAP Test").department("Sales")
                .designation("Rep").tenureMonths(24).performanceRating(3.0)
                .salaryChangePct(5).absenceDays(5).monthsSincePromotion(24).engagementScore(6.0)
                .build();

        AttritionPrediction p = engine.predict(f);

        assertThat(p.getShapValues()).contains("tenure_months", "performance_rating");
        assertThat(p.getModelVersion()).isNotNull();
    }
}
