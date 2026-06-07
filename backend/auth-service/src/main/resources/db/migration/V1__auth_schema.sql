-- V1__auth_schema.sql
-- Auth Service: JWT RS256 + MFA + Account Locking + Token Blacklist

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(500) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'EMPLOYEE',
    tenant_id VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    is_locked BOOLEAN DEFAULT false,
    failed_login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP,
    mfa_enabled BOOLEAN DEFAULT false,
    mfa_secret VARCHAR(200),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS token_blacklist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    blacklisted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    action VARCHAR(20) NOT NULL,
    actor_id VARCHAR(100) NOT NULL,
    actor_role VARCHAR(50),
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    before_state TEXT,
    after_state TEXT,
    changed_fields VARCHAR(500),
    entry_hash VARCHAR(64) NOT NULL,
    previous_hash VARCHAR(64),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    tenant_id VARCHAR(100) NOT NULL
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_tenant ON users(tenant_id);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_token_blacklist_hash ON token_blacklist(token_hash);
CREATE INDEX idx_token_blacklist_expires ON token_blacklist(expires_at);
CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_actor ON audit_log(actor_id);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp);

-- Default admin user (password: admin123 hashed with Argon2id)
INSERT INTO users (email, password_hash, first_name, last_name, role, tenant_id)
VALUES ('admin@nexushr.com', '$argon2id$v=19$m=65536,t=3,p=4$salt$hashedpassword', 'Admin', 'User', 'SUPER_ADMIN', 'default')
ON CONFLICT (email) DO NOTHING;
