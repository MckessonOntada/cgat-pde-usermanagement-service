CREATE INDEX IF NOT EXISTS gin_idx_sold_to_customer_trgm
ON catalog.catalog_price
USING gin (LOWER(sold_to_customer) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS gin_idx_material_description_trgm
ON catalog.catalog_price
USING gin (LOWER(material_description_in_language_en) gin_trgm_ops);
