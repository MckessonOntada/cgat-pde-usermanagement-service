ALTER TABLE catalog.therapy_config
    DROP COLUMN is_patient_information_required,
    DROP COLUMN patient_identifier_requirement,
    DROP COLUMN mrn__requirement,
    DROP COLUMN coi_id_requirement,
    DROP COLUMN po_number_requirement,
    DROP COLUMN date_of_administration_requirement,
    DROP COLUMN date_of_apheresis_requirement,
    DROP COLUMN ship_to_po_requirement;

ALTER TABLE catalog.therapy_config
    ADD COLUMN config JSONB NOT NULL DEFAULT '{}'::jsonb;