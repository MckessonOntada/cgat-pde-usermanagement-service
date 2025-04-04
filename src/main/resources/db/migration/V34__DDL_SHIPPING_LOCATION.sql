CREATE TABLE IF NOT EXISTS shipping_location
(
    id                      BIGINT PRIMARY KEY,
    practice_id             BIGINT NOT NULL,
    name                    VARCHAR(100) NOT NULL,
    address1                VARCHAR(255) NOT NULL,
    address2                VARCHAR(255),
    city                    VARCHAR(255) NOT NULL,
    zip                     VARCHAR(50) NOT NULL,
    state                   VARCHAR(255) NOT NULL,
    account_id              BIGINT NOT NULL,
    is_active               INTEGER DEFAULT 1 NOT NULL,

    CONSTRAINT fk_tenant FOREIGN KEY (practice_id) REFERENCES practice(id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE SEQUENCE IF NOT EXISTS SHIPPING_ACCOUNT_NUMBER_SEQUENCE
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 20;

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_shipping_location_practice_id ON shipping_location USING btree (practice_id);
CREATE INDEX idx_shipping_location_account_id ON shipping_location USING btree (account_id);
CREATE INDEX idx_shipping_location_is_active ON shipping_location USING btree (is_active);

CREATE INDEX idx_shipping_location_id_text_gin ON shipping_location

USING gin ((CAST(id AS VARCHAR)) gin_trgm_ops);

CREATE INDEX idx_shipping_location_name_gin ON shipping_location
USING gin (name gin_trgm_ops);

CREATE INDEX idx_shipping_location_address1_gin ON shipping_location
USING gin (address1 gin_trgm_ops);

CREATE INDEX idx_shipping_location_address2_gin ON shipping_location
USING gin (address2 gin_trgm_ops);

CREATE INDEX idx_shipping_location_city_gin ON shipping_location
USING gin (city gin_trgm_ops);

CREATE INDEX idx_shipping_location_state_gin ON shipping_location
USING gin (state gin_trgm_ops);

CREATE INDEX idx_shipping_location_zip_text_gin ON shipping_location
USING gin ((CAST(zip AS VARCHAR)) gin_trgm_ops);