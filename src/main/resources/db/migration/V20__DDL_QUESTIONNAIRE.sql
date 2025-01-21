CREATE TABLE IF NOT EXISTS catalog.questionnaire (
    id INTEGER PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    questions jsonb NOT NULL,
    configured_at VARCHAR[],
    is_active INTEGER NOT NULL
);

CREATE SEQUENCE catalog.questionnaire_id_seq START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;
