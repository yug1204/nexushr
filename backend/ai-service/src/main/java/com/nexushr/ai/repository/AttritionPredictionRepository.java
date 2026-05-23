package com.nexushr.ai.repository;

import com.nexushr.ai.model.AttritionPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttritionPredictionRepository extends JpaRepository<AttritionPrediction, String> {

    List<AttritionPrediction> findByEmployeeId(String employeeId);

    @Query("SELECT p FROM AttritionPrediction p WHERE p.deleted = false ORDER BY p.attritionScore DESC")
    List<AttritionPrediction> findAllOrderByRiskDesc();

    @Query("SELECT p FROM AttritionPrediction p WHERE p.riskLevel = 'HIGH' OR p.riskLevel = 'CRITICAL' ORDER BY p.attritionScore DESC")
    List<AttritionPrediction> findHighRiskEmployees();

    @Query("SELECT p FROM AttritionPrediction p WHERE p.department = :department AND p.deleted = false ORDER BY p.attritionScore DESC")
    List<AttritionPrediction> findByDepartment(String department);

    @Query("SELECT p.riskLevel, COUNT(p) FROM AttritionPrediction p WHERE p.deleted = false GROUP BY p.riskLevel")
    List<Object[]> getRiskDistribution();

    @Query("SELECT p.department, AVG(p.attritionScore) FROM AttritionPrediction p WHERE p.deleted = false GROUP BY p.department ORDER BY AVG(p.attritionScore) DESC")
    List<Object[]> getAvgAttritionByDepartment();
}
