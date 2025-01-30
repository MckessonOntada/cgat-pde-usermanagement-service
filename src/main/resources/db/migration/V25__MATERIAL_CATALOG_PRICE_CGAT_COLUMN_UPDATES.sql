ALTER TABLE catalog.catalog_price
DROP COLUMN IF EXISTS cgat_type;

ALTER TABLE catalog.catalog_price
DROP COLUMN IF EXISTS cgat_identifier;

ALTER TABLE catalog.catalog_price
ADD COLUMN IF NOT EXISTS cgat_therapy_type VARCHAR(10);