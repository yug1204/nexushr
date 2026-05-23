package com.nexushr.ai.repository;

import com.nexushr.ai.model.SkillGap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillGapRepository extends JpaRepository<SkillGap, String> {

    List<SkillGap> findByEmployeeIdOrderByGapScoreDesc(String employeeId);

    @Query("SELECT s FROM SkillGap s WHERE s.department = :department AND s.deleted = false ORDER BY s.gapScore DESC")
    List<SkillGap> findByDepartment(String department);

    @Query("SELECT s.skillName, AVG(s.gapScore), COUNT(s) FROM SkillGap s WHERE s.deleted = false GROUP BY s.skillName ORDER BY AVG(s.gapScore) DESC")
    List<Object[]> getTopSkillGaps();

    @Query("SELECT s.department, AVG(s.gapScore) FROM SkillGap s WHERE s.deleted = false GROUP BY s.department ORDER BY AVG(s.gapScore) DESC")
    List<Object[]> getAvgGapByDepartment();
}
