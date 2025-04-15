ALTER TABLE catalog.catalog_price
ADD COLUMN IF NOT EXISTS channel_type VARCHAR(20) NULL;

UPDATE catalog.catalog_price SET channel_type = 'BUY_AND_BILL' WHERE channel_type IS NULL;

ALTER TABLE catalog.catalog_price ALTER COLUMN channel_type SET NOT NULL;