-- V1__notification_schema.sql
-- Notification Engine: Kafka Consumer + Multi-Channel (F-08)

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
    subject VARCHAR(300),
    body TEXT,
    payload TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    retry_count INT DEFAULT 0,
    error_message TEXT,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS notification_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(100) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    tenant_id VARCHAR(100) NOT NULL,
    UNIQUE(user_id, channel, tenant_id)
);

-- Indexes
CREATE INDEX idx_notif_recipient ON notifications(recipient_id);
CREATE INDEX idx_notif_status ON notifications(status);
CREATE INDEX idx_notif_type ON notifications(type);
CREATE INDEX idx_notif_pref_user ON notification_preferences(user_id);
