CREATE TABLE IF NOT EXISTS catalog.catalog_configuration (
material_id VARCHAR(255) NOT NULL,
    variant VARCHAR(255) NOT NULL,
    ship_to_account VARCHAR(255) NOT NULL,
    sold_to_account VARCHAR(255) NOT NULL,
    steps JSONB,
    PRIMARY KEY (material_id, variant, ship_to_account, sold_to_account)
);


