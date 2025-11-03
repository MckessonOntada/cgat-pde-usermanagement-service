CREATE TABLE catalog.sp_therapy_config_diagnosis_code (
sp_therapy_config_id BIGINT NOT NULL,
diagnosis_code_id BIGINT NOT NULL,
PRIMARY KEY (sp_therapy_config_id, diagnosis_code_id),
FOREIGN KEY (sp_therapy_config_id) REFERENCES catalog.sp_therapy_config (id) ON DELETE CASCADE,
FOREIGN KEY (diagnosis_code_id) REFERENCES usermanagement.diagnosis_code (id) ON DELETE CASCADE
);