CREATE TABLE IF NOT EXISTS user_communication_preferences (
       id INTEGER PRIMARY KEY,
       user_id INTEGER NOT NULL,
       preferences TEXT NOT NULL,
       CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS user_communication_preferences_sequence START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;

CREATE INDEX IF NOT EXISTS idx_comm_pref_role_id ON communication_preferences(role_id);

CREATE INDEX IF NOT EXISTS idx_comm_pref_user_id ON user_communication_preferences(user_id);
