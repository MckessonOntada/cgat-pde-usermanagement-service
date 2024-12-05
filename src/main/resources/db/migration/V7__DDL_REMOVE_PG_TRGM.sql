CREATE INDEX catalog_ndc_udi_dc_materialidentifier_index  ON catalog.catalog_price 
USING GIN (ndc gin_trgm_ops, udi gin_trgm_ops, dist_channel gin_trgm_ops, material_identifier_for_cgat gin_trgm_ops);

CREATE INDEX catalog_salesorg_soldtocustomer_index ON catalog.catalog_price 
USING btree(sales_org, sold_to_customer);

CREATE INDEX account_partnerfunction_index ON usermanagement.account
USING btree(partner_function);

CREATE INDEX account_sapcustomer_name1_index ON usermanagement.account 
USING GIN (sap_customer gin_trgm_ops, name_1 gin_trgm_ops);

CREATE INDEX material_materialnumber_index ON catalog.material
USING btree(material_number);

CREATE INDEX role_name_index  ON usermanagement.role 
USING GIN (name gin_trgm_ops);

CREATE INDEX user_name_email_index  ON usermanagement.user
USING GIN (first_name gin_trgm_ops, middle_name gin_trgm_ops, last_name gin_trgm_ops, email gin_trgm_ops);

CREATE INDEX account_name_number_index  ON usermanagement.account
USING GIN (name_1 gin_trgm_ops, sap_customer gin_trgm_ops);

CREATE INDEX practice_name_index ON usermanagement.practice
USING GIN (name gin_trgm_ops);

CREATE INDEX user_provisioning_request_name_index  ON usermanagement.user_provisioning_request 
USING GIN (first_name gin_trgm_ops, middle_name gin_trgm_ops, last_name gin_trgm_ops, email gin_trgm_ops);
