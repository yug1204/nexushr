package com.nexushr.auth.security;

import com.nexushr.auth.model.Permission;
import com.nexushr.auth.model.User.Role;

import java.util.*;

/**
 * Static mapping of roles to fine-grained permissions.
 * Adapted from batch 8 RoleBasedPermission class,
 * extended to cover NexusHR's role hierarchy.
 */
public class RolePermissionMapping {

    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = new HashMap<>();

    static {
        // SUPER_ADMIN has all permissions
        ROLE_PERMISSIONS.put(Role.ROLE_SUPER_ADMIN, new HashSet<>(Arrays.asList(Permission.values())));

        // HR_ADMIN — full HR access
        ROLE_PERMISSIONS.put(Role.ROLE_HR_ADMIN, new HashSet<>(Arrays.asList(
                Permission.EMPLOYEE_VIEW, Permission.EMPLOYEE_CREATE, Permission.EMPLOYEE_EDIT, Permission.EMPLOYEE_DELETE,
                Permission.PAYROLL_VIEW, Permission.PAYROLL_RUN,
                Permission.ATTENDANCE_VIEW, Permission.ATTENDANCE_MANAGE,
                Permission.LEAVE_APPROVE,
                Permission.PERFORMANCE_VIEW, Permission.PERFORMANCE_MANAGE,
                Permission.REPORT_VIEW, Permission.REPORT_EXPORT,
                Permission.USER_MANAGE,
                Permission.ISSUE_VIEW, Permission.ISSUE_CREATE, Permission.ISSUE_EDIT, Permission.ISSUE_DELETE,
                Permission.COMMENT_ADD, Permission.COMMENT_DELETE
        )));

        // HR_MANAGER — department-scoped HR access
        ROLE_PERMISSIONS.put(Role.ROLE_HR_MANAGER, new HashSet<>(Arrays.asList(
                Permission.EMPLOYEE_VIEW, Permission.EMPLOYEE_EDIT,
                Permission.ATTENDANCE_VIEW, Permission.ATTENDANCE_MANAGE,
                Permission.LEAVE_APPROVE,
                Permission.PERFORMANCE_VIEW, Permission.PERFORMANCE_MANAGE,
                Permission.REPORT_VIEW,
                Permission.ISSUE_VIEW, Permission.ISSUE_CREATE, Permission.ISSUE_EDIT,
                Permission.COMMENT_ADD
        )));

        // LINE_MANAGER — team-scoped access
        ROLE_PERMISSIONS.put(Role.ROLE_LINE_MANAGER, new HashSet<>(Arrays.asList(
                Permission.EMPLOYEE_VIEW,
                Permission.ATTENDANCE_VIEW,
                Permission.LEAVE_APPROVE,
                Permission.PERFORMANCE_VIEW, Permission.PERFORMANCE_MANAGE,
                Permission.ISSUE_VIEW, Permission.ISSUE_EDIT,
                Permission.COMMENT_ADD
        )));

        // EMPLOYEE — self-service only
        ROLE_PERMISSIONS.put(Role.ROLE_EMPLOYEE, new HashSet<>(Arrays.asList(
                Permission.ISSUE_VIEW,
                Permission.COMMENT_ADD,
                Permission.ATTENDANCE_VIEW,
                Permission.PERFORMANCE_VIEW
        )));

        // FINANCE — payroll and reports
        ROLE_PERMISSIONS.put(Role.ROLE_FINANCE, new HashSet<>(Arrays.asList(
                Permission.PAYROLL_VIEW, Permission.PAYROLL_RUN,
                Permission.REPORT_VIEW, Permission.REPORT_EXPORT
        )));

        // IT_ADMIN — system management
        ROLE_PERMISSIONS.put(Role.ROLE_IT_ADMIN, new HashSet<>(Arrays.asList(
                Permission.USER_MANAGE,
                Permission.EMPLOYEE_VIEW,
                Permission.REPORT_VIEW
        )));

        // EXECUTIVE — read-only analytics
        ROLE_PERMISSIONS.put(Role.ROLE_EXECUTIVE, new HashSet<>(Arrays.asList(
                Permission.EMPLOYEE_VIEW,
                Permission.REPORT_VIEW,
                Permission.PERFORMANCE_VIEW,
                Permission.PAYROLL_VIEW
        )));
    }

    public static Map<Role, Set<Permission>> getRolePermissions() {
        return Collections.unmodifiableMap(ROLE_PERMISSIONS);
    }

    public static Set<Permission> getPermissionsForRole(Role role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }

    public static boolean hasPermission(Role role, Permission permission) {
        return getPermissionsForRole(role).contains(permission);
    }
}
