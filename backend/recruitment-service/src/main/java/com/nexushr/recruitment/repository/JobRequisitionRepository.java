package com.nexushr.recruitment.repository;

import com.nexushr.recruitment.model.JobRequisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRequisitionRepository extends JpaRepository<JobRequisition, String> {

    List<JobRequisition> findByStatusOrderByCreatedAtDesc(JobRequisition.RequisitionStatus status);

    List<JobRequisition> findByDepartmentOrderByCreatedAtDesc(String department);

    List<JobRequisition> findByHiringManagerIdOrderByCreatedAtDesc(String managerId);

    @Query("SELECT r.status, COUNT(r) FROM JobRequisition r WHERE r.deleted = false GROUP BY r.status")
    List<Object[]> getStatusDistribution();

    @Query("SELECT r.department, COUNT(r) FROM JobRequisition r WHERE r.status = 'OPEN' GROUP BY r.department")
    List<Object[]> getOpenPositionsByDepartment();
}
