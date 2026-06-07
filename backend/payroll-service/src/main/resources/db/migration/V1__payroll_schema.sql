-- V1__payroll_schema.sql
-- Payroll Engine with Indian Tax Compliance (F-03)

CREATE TABLE IF NOT EXISTS salary_structures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(100) NOT NULL,
    basic BIGINT NOT NULL,
    hra_pct DECIMAL(5,2) DEFAULT 40.00,
    special_allowance BIGINT DEFAULT 0,
    effective_from DATE NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS payroll_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    month INT NOT NULL,
    year INT NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    total_employees INT DEFAULT 0,
    total_gross BIGINT DEFAULT 0,
    total_deductions BIGINT DEFAULT 0,
    total_net BIGINT DEFAULT 0,
    total_employer_pf BIGINT DEFAULT 0,
    total_employer_esi BIGINT DEFAULT 0,
    initiated_by VARCHAR(100),
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    completed_at TIMESTAMP,
    error_message TEXT,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false,
    UNIQUE(month, year, tenant_id)
);

CREATE TABLE IF NOT EXISTS payslips (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_run_id UUID REFERENCES payroll_runs(id),
    employee_id VARCHAR(100) NOT NULL,
    employee_name VARCHAR(200),
    department VARCHAR(100),
    -- Earnings
    basic_salary BIGINT NOT NULL,
    hra BIGINT DEFAULT 0,
    special_allowance BIGINT DEFAULT 0,
    other_allowances BIGINT DEFAULT 0,
    gross_salary BIGINT NOT NULL,
    -- Deductions (Employee)
    pf_employee BIGINT DEFAULT 0,
    esi_employee BIGINT DEFAULT 0,
    professional_tax BIGINT DEFAULT 0,
    tds BIGINT DEFAULT 0,
    other_deductions BIGINT DEFAULT 0,
    total_deductions BIGINT NOT NULL,
    -- Employer contributions
    pf_employer BIGINT DEFAULT 0,
    esi_employer BIGINT DEFAULT 0,
    -- Net
    net_salary BIGINT NOT NULL,
    tax_regime VARCHAR(10) DEFAULT 'NEW',
    -- Metadata
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_deleted BOOLEAN DEFAULT false
);

-- Indexes
CREATE INDEX idx_salary_employee ON salary_structures(employee_id);
CREATE INDEX idx_payroll_run_period ON payroll_runs(month, year);
CREATE INDEX idx_payroll_run_status ON payroll_runs(status);
CREATE INDEX idx_payslip_run ON payslips(payroll_run_id);
CREATE INDEX idx_payslip_employee ON payslips(employee_id);
