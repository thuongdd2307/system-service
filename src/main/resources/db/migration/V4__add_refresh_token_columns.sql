-- Add missing columns to sys_refresh_token table

-- Add access_token column
ALTER TABLE sys_refresh_token 
ADD COLUMN access_token VARCHAR(500) AFTER user_id;

-- Add revoked column
ALTER TABLE sys_refresh_token 
ADD COLUMN revoked BOOLEAN NOT NULL DEFAULT FALSE AFTER expires_at;

-- Add revoked_at column
ALTER TABLE sys_refresh_token 
ADD COLUMN revoked_at DATETIME AFTER revoked;

-- Add index for revoked column
CREATE INDEX idx_revoked ON sys_refresh_token(revoked);
