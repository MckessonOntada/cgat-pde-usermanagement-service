ALTER TABLE catalog.material
ADD COLUMN IF NOT EXISTS site_name VARCHAR(50);

ALTER TABLE catalog.material
ADD COLUMN IF NOT EXISTS channel_type VARCHAR(20) NULL;

ALTER TABLE catalog.material
ALTER COLUMN material_number TYPE VARCHAR(50);

ALTER TABLE catalog.material
ALTER COLUMN material_description TYPE VARCHAR(100);

UPDATE catalog.material SET channel_type = 'BUY_AND_BILL' WHERE channel_type IS NULL;

ALTER TABLE catalog.material ALTER COLUMN channel_type SET NOT NULL;

CREATE INDEX IF NOT EXISTS material_site_name_index  ON catalog.material
USING GIN (site_name gin_trgm_ops);