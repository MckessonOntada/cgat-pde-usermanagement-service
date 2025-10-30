CREATE TABLE IF NOT EXISTS catalog.therapy_config (
        id BIGINT PRIMARY KEY,
        ndc_number VARCHAR(50) NOT NULL UNIQUE,
        is_patient_information_required BOOLEAN NOT NULL,
        patient_identifier_requirement VARCHAR(50) NOT NULL,
        mrn__requirement VARCHAR(50) NOT NULL,
        coi_id_requirement VARCHAR(50) NOT NULL,
        po_number_requirement VARCHAR(50) NOT NULL,
        date_of_administration_requirement VARCHAR(50) NOT NULL,
        date_of_apheresis_requirement VARCHAR(50) NOT NULL,
        channel_type VARCHAR(100)   NOT NULL
    );

CREATE SEQUENCE IF NOT EXISTS catalog.therapy_config_id_seq START 1 INCREMENT 1;

CREATE INDEX IF NOT EXISTS idx_ndc_number_therapy_config ON catalog.therapy_config(ndc_number);

CREATE TABLE IF NOT EXISTS catalog.sp_therapy_config (
                                                          id BIGINT PRIMARY KEY,
                                                          sp_product_id VARCHAR(50) NOT NULL UNIQUE,
            patient_identifier_requirement VARCHAR(50) NOT NULL,
            mrn__requirement VARCHAR(50) NOT NULL,
            coi_id_requirement VARCHAR(50) NOT NULL,
    is_prescription_details_required BOOLEAN NOT NULL DEFAULT FALSE,
    quantity BIGINT,
    days_supply BIGINT,
     refills BIGINT,
    instructions TEXT,
    consent_policy TEXT
    );

CREATE SEQUENCE IF NOT EXISTS catalog.sp_therapy_config_id_seq START 1 INCREMENT 1;

CREATE INDEX IF NOT EXISTS idx_sp_product_id_therapy_configs ON catalog.sp_therapy_config(sp_product_id);


DROP SEQUENCE IF EXISTS catalog.material_config_id_seq;

DROP SEQUENCE IF EXISTS catalog.sp_material_config_id_seq;

DROP INDEX IF EXISTS catalog.idx_material_number;

DROP INDEX IF EXISTS catalog.idx_sp_product_id_therapy_config;

DROP TABLE IF EXISTS catalog.material_config;

DROP TABLE IF EXISTS catalog.sp_material_config;

