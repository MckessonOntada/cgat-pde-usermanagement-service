ALTER TABLE catalog.therapy_config
    DROP COLUMN IF EXISTS is_patient_information_required,
    DROP COLUMN IF EXISTS patient_identifier_requirement,
    DROP COLUMN IF EXISTS mrn__requirement,
    DROP COLUMN IF EXISTS coi_id_requirement,
    DROP COLUMN IF EXISTS po_number_requirement,
    DROP COLUMN IF EXISTS date_of_administration_requirement,
    DROP COLUMN IF EXISTS date_of_apheresis_requirement,
    DROP COLUMN IF EXISTS ship_to_po_requirement;

ALTER TABLE catalog.therapy_config
    ADD COLUMN config JSONB NOT NULL DEFAULT '{}'::jsonb;