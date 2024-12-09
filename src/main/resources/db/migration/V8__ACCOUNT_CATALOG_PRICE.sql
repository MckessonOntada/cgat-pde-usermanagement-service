CREATE INDEX IF NOT EXISTS catalog_dist_channel_index ON catalog.catalog_price 
USING btree(dist_channel, material_identifier_for_cgat, ship_to_customer);

CREATE INDEX IF NOT EXISTS account_sap_customer_index ON usermanagement.account
USING btree(sap_customer);