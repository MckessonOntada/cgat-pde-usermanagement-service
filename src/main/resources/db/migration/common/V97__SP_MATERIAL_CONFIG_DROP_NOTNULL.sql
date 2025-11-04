ALTER TABLE catalog.sp_therapy_config
ALTER COLUMN patient_identifier_requirement DROP NOT NULL,
ALTER COLUMN mrn__requirement DROP NOT NULL,
ALTER COLUMN coi_id_requirement DROP NOT NULL,
ALTER COLUMN is_prescription_details_required DROP NOT NULL;