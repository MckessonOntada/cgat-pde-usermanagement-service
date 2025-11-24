CREATE TABLE IF NOT EXISTS catalog.sp_material_config (
        id BIGINT PRIMARY KEY,
        sp_product_id VARCHAR(50) NOT NULL UNIQUE,
        is_coi_id_required BOOLEAN NOT NULL,
        is_patient_identifier_required BOOLEAN NOT NULL,
        is_mrn_required BOOLEAN NOT NULL
    );

CREATE SEQUENCE catalog.sp_material_config_id_seq START 1 INCREMENT 1;

CREATE INDEX IF NOT EXISTS idx_sp_product_id_therapy_config ON catalog.sp_material_config(sp_product_id);
