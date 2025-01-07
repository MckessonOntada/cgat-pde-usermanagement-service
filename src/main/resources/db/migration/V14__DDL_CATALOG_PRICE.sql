CREATE INDEX IF NOT EXISTS catalog_sap_material_index ON catalog.catalog_price 
USING btree(sap_material);