-- V1__recruitment_schema.sql
-- F-06: Recruitment & Onboarding

CREATE TABLE IF NOT EXISTS job_requisitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    department VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    employment_type VARCHAR(30),
    min_experience INT,
    max_experience INT,
    min_salary BIGINT,
    max_salary BIGINT,
    description TEXT,
    required_skills TEXT,
    hiring_manager_id VARCHAR(100),
    number_of_openings INT DEFAULT 1,
    status VARCHAR(30) DEFAULT 'DRAFT',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    target_date DATE,
    approved_by VARCHAR(100),
    approved_at DATE,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS candidates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    phone VARCHAR(20),
    resume_url VARCHAR(500),
    linkedin_url VARCHAR(300),
    current_company VARCHAR(200),
    current_designation VARCHAR(200),
    total_experience INT,
    current_ctc BIGINT,
    expected_ctc BIGINT,
    notice_period_days INT,
    skills TEXT,
    requisition_id UUID REFERENCES job_requisitions(id),
    pipeline_stage VARCHAR(30) DEFAULT 'APPLIED',
    source VARCHAR(30),
    recruiter_notes TEXT,
    interview_score INT,
    ai_match_score INT,
    offer_date DATE,
    joining_date DATE,
    rejection_reason VARCHAR(500),
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

CREATE INDEX idx_candidate_email ON candidates(email);
CREATE INDEX idx_candidate_stage ON candidates(pipeline_stage);
CREATE INDEX idx_candidate_req ON candidates(requisition_id);
CREATE INDEX idx_req_status ON job_requisitions(status);
