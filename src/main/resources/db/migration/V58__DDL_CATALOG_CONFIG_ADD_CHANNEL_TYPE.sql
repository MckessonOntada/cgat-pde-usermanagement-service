ALTER TABLE catalog.catalog_configuration
    ADD COLUMN IF NOT EXISTS channel_type VARCHAR(20) NULL;

UPDATE catalog.catalog_configuration SET channel_type = 'BUY_AND_BILL' WHERE channel_type IS NULL;

ALTER TABLE catalog.catalog_configuration ALTER COLUMN channel_type SET NOT NULL;