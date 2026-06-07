package com.nexushr.auth.model;

/**
 * Fine-grained permissions for role-based access control.
 * Adapted from batch 8 TaskManagementTool_B8 permission system.
 */
public enum Permission {
    ISSUE_VIEW,
    ISSUE_CREATE,
    ISSUE_EDIT,
    ISSUE_DELETE,
    COMMENT_ADD,
    COMMENT_DELETE,
    USER_MANAGE,
    EMPLOYEE_VIEW,
    EMPLOYEE_CREATE,
    EMPLOYEE_EDIT,
    EMPLOYEE_DELETE,
    PAYROLL_VIEW,
    PAYROLL_RUN,
    ATTENDANCE_VIEW,
    ATTENDANCE_MANAGE,
    LEAVE_APPROVE,
    PERFORMANCE_VIEW,
    PERFORMANCE_MANAGE,
    REPORT_VIEW,
    REPORT_EXPORT
}
