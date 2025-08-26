ALTER TABLE usermanagement."user"
    ADD COLUMN IF NOT EXISTS last_login TIMESTAMP;

ALTER TABLE audit.user_aud
    ADD COLUMN IF NOT EXISTS last_login TIMESTAMP;