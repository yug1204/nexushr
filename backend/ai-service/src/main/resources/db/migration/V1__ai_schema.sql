-- V1__ai_schema.sql
-- AI Workforce Intelligence tables

CREATE TABLE IF NOT EXISTS ai_predictions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(100) NOT NULL,
    employee_name VARCHAR(200),
    department VARCHAR(200),
    designation VARCHAR(200),
    attrition_score NUMERIC(5,4) NOT NULL,
    risk_level VARCHAR(20),
    shap_values TEXT,
    top_factors TEXT,
    recommendation TEXT,
    model_version VARCHAR(50),
    generated_at TIMESTAMP,
    tenure_months INT,
    performance_rating NUMERIC(3,1),
    salary_change_pct NUMERIC(5,2),
    absence_days INT,
    months_since_promotion INT,
    engagement_score NUMERIC(3,1),
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

CREATE INDEX idx_pred_employee ON ai_predictions(employee_id);
CREATE INDEX idx_pred_score ON ai_predictions(attrition_score);
CREATE INDEX idx_pred_risk ON ai_predictions(risk_level);

CREATE TABLE IF NOT EXISTS skill_gaps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(100) NOT NULL,
    department VARCHAR(200),
    skill_name VARCHAR(100) NOT NULL,
    skill_category VARCHAR(50),
    current_level NUMERIC(5,2),
    required_level NUMERIC(5,2),
    gap_score NUMERIC(5,2),
    learning_recommendation TEXT,
    priority VARCHAR(20),
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

CREATE INDEX idx_skill_employee ON skill_gaps(employee_id);
CREATE INDEX idx_skill_dept ON skill_gaps(department);
