CREATE INDEX IF NOT EXISTS idx_material_number_channel_status
ON catalog.material (material_number, channel_type, item_status);

CREATE INDEX IF NOT EXISTS idx_cp_material_channel_deleted_soldto
ON catalog.catalog_price (sap_material, channel_type, deleted, sold_to_customer);

CREATE INDEX IF NOT EXISTS idx_cw_bnb_sp_active
ON catalog.crosswalk (bnb_material_number, sp_product_id, is_active);

CREATE INDEX IF NOT EXISTS idx_uam_account_id_casted
ON usermanagement.user_account_mapping ((CAST(account_id AS VARCHAR)), user_id);
