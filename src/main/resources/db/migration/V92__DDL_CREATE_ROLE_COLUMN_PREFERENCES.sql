CREATE TABLE communication_preferences (
       id INTEGER PRIMARY KEY,
       role_id INTEGER NOT NULL,
       preferences TEXT NOT NULL,
       CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS communication_preferences_sequence START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;
