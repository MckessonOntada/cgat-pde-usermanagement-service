ALTER TABLE catalog.sp_material_config
ALTER COLUMN is_coi_id_required DROP NOT NULL,
ALTER COLUMN is_patient_identifier_required DROP NOT NULL,
ALTER COLUMN is_mrn_required DROP NOT NULL,
ALTER COLUMN is_prescription_details_required DROP NOT NULL;