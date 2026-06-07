package com.nexushr.employee.repository;

import com.nexushr.employee.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmailAndDeletedFalse(String email);
    Optional<Employee> findByEmployeeCodeAndDeletedFalse(String code);
    Page<Employee> findByDeletedFalse(Pageable pageable);
    Page<Employee> findByDepartmentIdAndDeletedFalse(String deptId, Pageable pageable);
    List<Employee> findByManagerIdAndDeletedFalse(String managerId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String code);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.departmentId = :deptId AND e.deleted = false")
    long countByDepartment(String deptId);

    @Query("SELECT e FROM Employee e WHERE e.status = 'ACTIVE' AND e.deleted = false")
    List<Employee> findAllActive();

    @Query(value = """
        WITH RECURSIVE org_tree AS (
            SELECT id, first_name, last_name, designation, manager_id, department_id, 0 as level
            FROM employees WHERE id = :rootId AND is_deleted = false
            UNION ALL
            SELECT e.id, e.first_name, e.last_name, e.designation, e.manager_id, e.department_id, ot.level + 1
            FROM employees e INNER JOIN org_tree ot ON e.manager_id = ot.id
            WHERE e.is_deleted = false
        )
        SELECT * FROM org_tree ORDER BY level, last_name
        """, nativeQuery = true)
    List<Object[]> findOrgChart(String rootId);

    @Query("SELECT e FROM Employee e WHERE e.deleted = false AND " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Employee> search(String query, Pageable pageable);

    Optional<Employee> findByPersonalEmailAndDeletedFalse(String personalEmail);
}
