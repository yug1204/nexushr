-- V1__attendance_schema.sql
-- Attendance & Leave Management (F-02)

CREATE TABLE IF NOT EXISTS attendance_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    clock_in TIMESTAMP,
    clock_out TIMESTAMP,
    work_hours DECIMAL(4,2),
    status VARCHAR(20) DEFAULT 'PRESENT',
    location_lat DECIMAL(10,8),
    location_lng DECIMAL(11,8),
    ip_address VARCHAR(45),
    device_info VARCHAR(200),
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false,
    UNIQUE(employee_id, date, tenant_id)
);

CREATE TABLE IF NOT EXISTS leave_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(100) NOT NULL,
    leave_type VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days DECIMAL(3,1) NOT NULL,
    reason TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    rejection_reason VARCHAR(500),
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS leave_balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(100) NOT NULL,
    leave_type VARCHAR(30) NOT NULL,
    fiscal_year INT NOT NULL,
    total DECIMAL(4,1) NOT NULL DEFAULT 0,
    used DECIMAL(4,1) NOT NULL DEFAULT 0,
    balance DECIMAL(4,1) NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(employee_id, leave_type, fiscal_year, tenant_id)
);

-- Indexes
CREATE INDEX idx_attendance_employee ON attendance_records(employee_id);
CREATE INDEX idx_attendance_date ON attendance_records(date);
CREATE INDEX idx_attendance_status ON attendance_records(status);
CREATE INDEX idx_leave_employee ON leave_requests(employee_id);
CREATE INDEX idx_leave_status ON leave_requests(status);
CREATE INDEX idx_leave_type ON leave_requests(leave_type);
CREATE INDEX idx_leave_bal_emp ON leave_balances(employee_id, fiscal_year);
