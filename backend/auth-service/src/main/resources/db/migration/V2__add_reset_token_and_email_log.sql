-- Add password reset fields to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token_expiry TIMESTAMP;

-- Create email_log table for email audit trail
CREATE TABLE IF NOT EXISTS email_log (
    id VARCHAR(255) PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT,
    sent_status BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for reset token lookup
CREATE INDEX IF NOT EXISTS idx_user_reset_token ON users(reset_token);
