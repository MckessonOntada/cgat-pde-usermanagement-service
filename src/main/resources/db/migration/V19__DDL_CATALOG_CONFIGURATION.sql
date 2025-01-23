CREATE TABLE IF NOT EXISTS catalog.catalog_configuration (
material_id VARCHAR(18) NOT NULL,
    variant VARCHAR(30) NOT NULL,
    ship_to_account VARCHAR(10) NOT NULL,
    sold_to_account VARCHAR(10) NOT NULL,
    steps JSONB,
    PRIMARY KEY (material_id, variant, ship_to_account, sold_to_account)
);

CREATE INDEX IF NOT EXISTS idx_catalog_configuration_composite_key
ON catalog.catalog_configuration (material_id, variant, ship_to_account, sold_to_account);


