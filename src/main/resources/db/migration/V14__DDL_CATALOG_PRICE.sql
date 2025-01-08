CREATE INDEX IF NOT EXISTS catalog_sap_material_index ON catalog.catalog_price 
USING btree(sap_material);

CREATE INDEX IF NOT EXISTS user_account_mapping_user_id_index ON usermanagement.user_account_mapping 
USING btree(user_id);
