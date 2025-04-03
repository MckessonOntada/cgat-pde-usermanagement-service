CREATE TABLE IF NOT EXISTS shipping_location
(
    shipping_account_number  BIGINT PRIMARY KEY,
    tenant_id                BIGINT NOT NULL,
    shipping_location_name   VARCHAR(100) NOT NULL,
    address1                VARCHAR(255) NOT NULL,
    address2                VARCHAR(255),
    city                     VARCHAR(255) NOT NULL,
    zip                      VARCHAR(50) NOT NULL,
    state                    VARCHAR(255) NOT NULL,
    account_id               BIGINT NOT NULL,
    is_active                INTEGER DEFAULT 1 NOT NULL,

    CONSTRAINT fk_tenant FOREIGN KEY (tenant_id) REFERENCES practice(id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id)
);

-- Sequence for Generating Shipping Account Numbers
CREATE SEQUENCE IF NOT EXISTS SHIPPING_ACCOUNT_NUMBER_SEQUENCE
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 20;