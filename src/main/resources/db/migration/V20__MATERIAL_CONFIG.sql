CREATE TABLE IF NOT EXISTS catalog.material_config (
        id BIGINT PRIMARY KEY,
        material_number VARCHAR(18) NOT NULL UNIQUE,
        is_coi_id_required BOOLEAN NOT NULL,
        is_patient_information_required BOOLEAN NOT NULL
    );

CREATE SEQUENCE catalog.material_config_id_seq START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;

CREATE INDEX IF NOT EXISTS idx_material_number ON catalog.material_config(material_number);
