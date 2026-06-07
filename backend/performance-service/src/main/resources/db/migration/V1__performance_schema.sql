-- V1__performance_schema.sql
-- Performance Management: OKR + 360° Reviews (F-04)

CREATE TABLE IF NOT EXISTS review_cycles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS performance_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cycle_id UUID REFERENCES review_cycles(id),
    employee_id VARCHAR(100) NOT NULL,
    reviewer_id VARCHAR(100) NOT NULL,
    review_type VARCHAR(20) NOT NULL,
    rating DECIMAL(3,2),
    strengths TEXT,
    improvements TEXT,
    comments TEXT,
    status VARCHAR(20) DEFAULT 'DRAFT',
    submitted_at TIMESTAMP,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(100) NOT NULL,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    category VARCHAR(20) DEFAULT 'OKR',
    target_value DECIMAL(10,2),
    current_value DECIMAL(10,2) DEFAULT 0,
    progress_pct INT DEFAULT 0,
    weight DECIMAL(5,2) DEFAULT 1.00,
    due_date DATE,
    status VARCHAR(20) DEFAULT 'NOT_STARTED',
    parent_goal_id UUID REFERENCES goals(id),
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false
);

-- Indexes
CREATE INDEX idx_review_cycle ON performance_reviews(cycle_id);
CREATE INDEX idx_review_employee ON performance_reviews(employee_id);
CREATE INDEX idx_review_status ON performance_reviews(status);
CREATE INDEX idx_goals_employee ON goals(employee_id);
CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_goals_parent ON goals(parent_goal_id);
