CREATE TABLE IF NOT EXISTS catalog.material_config (
        id BIGINT PRIMARY KEY,
        material_number VARCHAR(18) NOT NULL UNIQUE,
        coi_id BOOLEAN NOT NULL,
        patient_information BOOLEAN NOT NULL
    );

CREATE SEQUENCE catalog.material_config_id_seq START 1 INCREMENT 1;

CREATE INDEX IF NOT EXISTS idx_material_number ON catalog.material_config(material_number);
