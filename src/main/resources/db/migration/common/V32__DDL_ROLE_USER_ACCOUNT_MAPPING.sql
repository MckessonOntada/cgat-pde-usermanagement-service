CREATE INDEX IF NOT EXISTS role_practice_id_index ON usermanagement.role
USING btree(practice_id);

CREATE INDEX IF NOT EXISTS user_account_mapping_account_id_index ON usermanagement.user_account_mapping
USING btree(account_id);
