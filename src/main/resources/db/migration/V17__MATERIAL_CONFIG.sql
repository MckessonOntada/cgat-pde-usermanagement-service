CREATE TABLE IF NOT EXISTS catalog.material_config(
                                material_number VARCHAR(18) PRIMARY KEY,
                                coi_id BOOLEAN NOT NULL DEFAULT FALSE,
                                patient_information BOOLEAN NOT NULL DEFAULT FALSE
);
