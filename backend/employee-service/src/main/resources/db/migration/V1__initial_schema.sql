-- V1__initial_schema.sql
-- NexusHR Complete Database Schema
-- Flyway Migration V1: Core tables for Employee, Department, Attendance, Leave

-- Departments
CREATE TABLE IF NOT EXISTS departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    head_employee_id UUID,
    parent_department_id UUID REFERENCES departments(id),
    cost_centre VARCHAR(50),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

-- Employees
CREATE TABLE IF NOT EXISTS employees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(30) NOT NULL,
    hire_date DATE NOT NULL,
    termination_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    employment_type VARCHAR(20) NOT NULL,
    department_id UUID REFERENCES departments(id),
    designation VARCHAR(200),
    grade VARCHAR(10),
    manager_id UUID REFERENCES employees(id),
    ctc NUMERIC(15,2),
    basic_salary NUMERIC(15,2),
    hra NUMERIC(15,2),
    special_allowance NUMERIC(15,2),
    pan_number VARCHAR(20),
    aadhaar_number VARCHAR(20),
    bank_account_number VARCHAR(30),
    ifsc_code VARCHAR(15),
    pf_number VARCHAR(30),
    esi_number VARCHAR(30),
    uan_number VARCHAR(30),
    profile_photo_url TEXT,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(20),
    tax_regime VARCHAR(5) DEFAULT 'NEW',
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

CREATE INDEX idx_emp_dept ON employees(department_id);
CREATE INDEX idx_emp_manager ON employees(manager_id);
CREATE INDEX idx_emp_tenant ON employees(tenant_id);
CREATE INDEX idx_emp_status ON employees(status);

-- Attendance Records
CREATE TABLE IF NOT EXISTS attendance_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(id),
    clock_in TIMESTAMP NOT NULL,
    clock_out TIMESTAMP,
    work_hours NUMERIC(5,2),
    overtime_hours NUMERIC(5,2) DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    geo_lat DOUBLE PRECISION,
    geo_lng DOUBLE PRECISION,
    ip_address VARCHAR(45),
    notes TEXT,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

CREATE INDEX idx_att_employee ON attendance_records(employee_id);
CREATE INDEX idx_att_date ON attendance_records(clock_in);

-- Leave Types
CREATE TABLE IF NOT EXISTS leave_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    max_days_per_year INT NOT NULL,
    carry_forward_allowed BOOLEAN DEFAULT false,
    max_carry_forward INT DEFAULT 0,
    requires_approval BOOLEAN DEFAULT true,
    is_paid BOOLEAN DEFAULT true,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false
);

-- Leave Requests
CREATE TABLE IF NOT EXISTS leave_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(id),
    leave_type_id UUID NOT NULL REFERENCES leave_types(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days NUMERIC(4,1) NOT NULL,
    reason TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    approved_by UUID REFERENCES employees(id),
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

-- Leave Balances
CREATE TABLE IF NOT EXISTS leave_balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(id),
    leave_type_id UUID NOT NULL REFERENCES leave_types(id),
    year INT NOT NULL,
    total_entitled NUMERIC(4,1) NOT NULL,
    used NUMERIC(4,1) DEFAULT 0,
    carry_forward NUMERIC(4,1) DEFAULT 0,
    remaining NUMERIC(4,1) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    UNIQUE(employee_id, leave_type_id, year)
);

-- Audit Log (Immutable, Append-Only with Hash Chaining)
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    actor_id VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    before_json JSONB,
    after_json JSONB,
    ip_address VARCHAR(45),
    hash VARCHAR(128) NOT NULL,
    previous_hash VARCHAR(128),
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Insert default leave types
INSERT INTO leave_types (id, name, code, max_days_per_year, carry_forward_allowed, max_carry_forward, tenant_id)
VALUES
    (gen_random_uuid(), 'Casual Leave', 'CL', 12, false, 0, 'default'),
    (gen_random_uuid(), 'Sick Leave', 'SL', 12, false, 0, 'default'),
    (gen_random_uuid(), 'Earned Leave', 'EL', 15, true, 30, 'default'),
    (gen_random_uuid(), 'Maternity Leave', 'ML', 182, false, 0, 'default'),
    (gen_random_uuid(), 'Paternity Leave', 'PL', 15, false, 0, 'default');
