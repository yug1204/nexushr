package com.nexushr.auth.repository;

import com.nexushr.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.tenantId = :tenantId AND u.deleted = false")
    Optional<User> findByEmailAndTenant(String email, String tenantId);

    @Query("SELECT u FROM User u WHERE u.employeeId = :employeeId AND u.deleted = false")
    Optional<User> findByEmployeeId(String employeeId);
}
