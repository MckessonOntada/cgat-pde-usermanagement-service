-- Step 1: Backup existing requirement columns (optional but recommended)
ALTER TABLE catalog.therapy_config
    ADD COLUMN requirement_backup JSONB;

UPDATE catalog.therapy_config
SET requirement_backup = jsonb_build_object(
    'isPatientInformationRequired', is_patient_information_required,
    'patientIdentifierRequirement', patient_identifier_requirement,
    'mrnRequirement', mrn__requirement,
    'coiIdRequirement', coi_id_requirement,
    'poNumberRequirement', po_number_requirement,
    'dateOfAdministrationRequirement', date_of_administration_requirement,
    'dateOfApheresisRequirement', date_of_apheresis_requirement,
    'shipToPORequirement', ship_to_po_requirement
);

-- Step 2: Drop old requirement columns
ALTER TABLE catalog.therapy_config
    DROP COLUMN is_patient_information_required,
    DROP COLUMN patient_identifier_requirement,
    DROP COLUMN mrn__requirement,
    DROP COLUMN coi_id_requirement,
    DROP COLUMN po_number_requirement,
    DROP COLUMN date_of_administration_requirement,
    DROP COLUMN date_of_apheresis_requirement,
    DROP COLUMN ship_to_po_requirement;

-- Step 3: Add new JSONB config column
ALTER TABLE catalog.therapy_config
    ADD COLUMN config JSONB NOT NULL DEFAULT '{}'::jsonb;
