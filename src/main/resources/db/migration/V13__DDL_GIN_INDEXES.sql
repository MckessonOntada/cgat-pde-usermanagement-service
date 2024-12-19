CREATE INDEX IF NOT EXISTS catalog_ndc_distchannel_materialidentifier_index  ON catalog.catalog_price 
USING GIN (ndc gin_trgm_ops, dist_channel gin_trgm_ops, material_identifier_for_cgat gin_trgm_ops);

CREATE INDEX IF NOT EXISTS material_generic_name_index  ON catalog.material 
USING GIN ( generic_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS account_sapcustomer_name1_index ON usermanagement.account 
USING GIN (sap_customer gin_trgm_ops, name_1 gin_trgm_ops);

CREATE INDEX IF NOT EXISTS practice_name_index ON usermanagement.practice
USING GIN (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS role_name_index  ON usermanagement.role 
USING GIN (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS user_name_email_index  ON usermanagement.user
USING GIN (first_name gin_trgm_ops, middle_name gin_trgm_ops, last_name gin_trgm_ops, email gin_trgm_ops);

CREATE INDEX IF NOT EXISTS user_provisioning_request_name_email_index  ON usermanagement.user_provisioning_request 
USING GIN (first_name gin_trgm_ops, middle_name gin_trgm_ops, last_name gin_trgm_ops, email gin_trgm_ops);
