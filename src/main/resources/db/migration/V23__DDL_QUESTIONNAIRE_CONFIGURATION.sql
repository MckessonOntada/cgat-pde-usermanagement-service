CREATE TABLE IF NOT EXISTS catalog.questionnaire_configuration (
    id INTEGER PRIMARY KEY NOT NULL,
    questionnaire_id INTEGER NOT NULL,
    configured_type VARCHAR NOT NULL,
    entity_id VARCHAR NOT NULL,
    created_at 		TIMESTAMP,
    created_by 		VARCHAR(255),
    updated_at 		TIMESTAMP,
    updated_by 		VARCHAR(255),
    CREATED_BY_IMPERSONATOR VARCHAR(255),
    UPDATED_BY_IMPERSONATOR VARCHAR(255),
    FOREIGN KEY (questionnaire_id) REFERENCES catalog.questionnaire(id) ON DELETE CASCADE,
    CONSTRAINT unique_questionnaire_configuration UNIQUE (questionnaire_id, configured_type, entity_id)
);

CREATE SEQUENCE catalog.questionnaire_configuration_id_seq START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;