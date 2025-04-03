ALTER TABLE catalog.material
ADD COLUMN IF NOT EXISTS site_name VARCHAR(50);

ALTER TABLE catalog.material
ADD COLUMN IF NOT EXISTS material_type VARCHAR(20) DEFAULT 'BUY_BILL' NOT NULL;

ALTER TABLE catalog.material
ALTER COLUMN material_number TYPE VARCHAR(50);

ALTER TABLE catalog.material
ALTER COLUMN material_description TYPE VARCHAR(100);