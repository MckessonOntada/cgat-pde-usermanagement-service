ALTER TABLE usermanagement.user 
ADD COLUMN full_name TEXT 
GENERATED ALWAYS AS (
	first_name || COALESCE(' ' || middle_name, '') || ' ' || last_name
) STORED;

ALTER TABLE usermanagement.user_provisioning_request 
ADD COLUMN full_name TEXT 
GENERATED ALWAYS AS (
	first_name || COALESCE(' ' || middle_name, '') || ' ' || last_name
) STORED;

CREATE EXTENSION IF NOT EXISTS PG_TRGM;

CREATE INDEX IF NOT EXISTS catalog_ndc_udi_distchannel_sap_material_index  ON catalog.catalog_price 
USING GIN (ndc gin_trgm_ops, udi gin_trgm_ops, dist_channel gin_trgm_ops, sap_material gin_trgm_ops);

CREATE INDEX IF NOT EXISTS material_ndc_material_number_description_generic_name_index  ON catalog.material 
USING GIN (ndc_number gin_trgm_ops, material_number gin_trgm_ops, material_description gin_trgm_ops, generic_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS account_sapcustomer_name1_index ON usermanagement.account 
USING GIN (sap_customer gin_trgm_ops, name_1 gin_trgm_ops);

CREATE INDEX IF NOT EXISTS practice_name_index ON usermanagement.practice
USING GIN (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS role_name_index  ON usermanagement.role 
USING GIN (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS user_name_email_index 
ON usermanagement.user USING GIN (LOWER(full_name) gin_trgm_ops, LOWER(email) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS user_provisioning_request_name_email_index 
ON usermanagement.user_provisioning_request USING GIN (LOWER(full_name) gin_trgm_ops, LOWER(email) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS questionnaire_template_name_index 
ON catalog.questionnaire 
USING GIN (template_name gin_trgm_ops);
