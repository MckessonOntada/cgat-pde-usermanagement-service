CREATE TABLE IF NOT EXISTS catalog.material_cross_walk_config (
        id BIGINT PRIMARY KEY,
        bnb_material_number VARCHAR(50) NOT NULL,
        sp_product_id VARCHAR(50) NOT NULL,
        is_active       INTEGER
    );

CREATE SEQUENCE catalog.material_cross_walk_config_id_seq START 1 INCREMENT 1;

CREATE INDEX IF NOT EXISTS idx_material_number ON catalog.material_cross_walk_config(bnb_material_number);
