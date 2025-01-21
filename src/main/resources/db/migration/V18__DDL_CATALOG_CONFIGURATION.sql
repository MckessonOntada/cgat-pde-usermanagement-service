CREATE TABLE IF NOT EXISTS catalog.catalog_configuration (
    id INTEGER PRIMARY KEY NOT NULL,
    material_id VARCHAR(255) NOT NULL,
    variant VARCHAR(255) NOT NULL,
    ship_to_account VARCHAR(255) NOT NULL,
    sold_to_account VARCHAR(255) NOT NULL,
    steps jsonb NOT NULL,
    CONSTRAINT "Configuration is already present for this combination"
        UNIQUE (material_id, variant, ship_to_account, sold_to_account)
);

CREATE SEQUENCE catalog.CATALOG_CONFIG_SEQUENCE START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;

CREATE INDEX IF NOT EXISTS idx_material_variant_shipto_soldto
    ON catalog.catalog_configuration (material_id, variant, ship_to_account, sold_to_account);
