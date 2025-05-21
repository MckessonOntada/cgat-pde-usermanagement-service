ALTER TABLE catalog.catalog_configuration
ALTER COLUMN ship_to_account TYPE VARCHAR(20),
ALTER COLUMN sold_to_account TYPE VARCHAR(20),
ALTER COLUMN material_id TYPE VARCHAR(50);