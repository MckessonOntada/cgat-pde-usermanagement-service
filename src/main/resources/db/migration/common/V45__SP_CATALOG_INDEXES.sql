CREATE INDEX IF NOT EXISTS idx_therapy_material_channel_status
ON catalog.material (material_number, channel_type, item_status);

CREATE INDEX IF NOT EXISTS idx_therapy_channel_status
ON catalog.material (channel_type, item_status);

CREATE INDEX IF NOT EXISTS idx_account_channel_type_is_active
ON usermanagement.account (channel_type, is_active);

CREATE INDEX IF NOT EXISTS idx_account_partner_channel_type_is_active
ON usermanagement.account (sap_partner, channel_type, is_active);

CREATE INDEX IF NOT EXISTS idx_catalog_price_sap_material_sold_to_customer_channel_type
ON catalog.catalog_price (sap_material, sold_to_customer, channel_type);



