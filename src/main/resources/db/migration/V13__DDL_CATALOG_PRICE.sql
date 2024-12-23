ALTER TABLE catalog.catalog_price
ALTER COLUMN material_identifier_for_cgat TYPE VARCHAR(1),
ALTER COLUMN material_identifier_for_cgat SET NOT NULL;