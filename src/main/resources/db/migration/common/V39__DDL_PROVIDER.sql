CREATE TABLE IF NOT EXISTS "provider"
(
    id                                      INTEGER         NOT NULL,
    practice_id                             INTEGER,
    first_name                              VARCHAR(50)     NOT NULL,
    middle_name                             VARCHAR(50),
    last_name                               VARCHAR(50)     NOT NULL,
    dea                                     VARCHAR(9)      NOT NULL,
    npi                                     VARCHAR(10)     NOT NULL,
    address1                                VARCHAR(100)    NOT NULL,
    address2                                VARCHAR(100),
    city                                    VARCHAR(50)     NOT NULL,
    state                                   VARCHAR(20)     NOT NULL,
    zip                                     VARCHAR(10)     NOT NULL,
    phone                                   VARCHAR(50)     NOT NULL,
    is_active                               INTEGER,
    created_at                              TIMESTAMP,
    created_by                              VARCHAR(255),
    updated_at                              TIMESTAMP,
    updated_by                              VARCHAR(255),
    CREATED_BY_IMPERSONATOR                 VARCHAR(255),
    UPDATED_BY_IMPERSONATOR                 VARCHAR(255),
    CREATED_BY_IMPERSONATOR_USER_NAME       VARCHAR(255),
    CREATED_BY_USER_NAME                    VARCHAR(255),
    full_name                               TEXT GENERATED ALWAYS AS (
        first_name ||
        CASE
            WHEN middle_name IS NOT NULL AND middle_name <> ''
            THEN ' ' || middle_name
            ELSE ''
        END ||
        ' ' || last_name
    ) STORED,
    full_address TEXT GENERATED ALWAYS AS (
        address1 ||
        CASE
            WHEN address2 IS NOT NULL AND address2 <> ''
            THEN ' ' || address2
            ELSE ''
        END ||
        ' ' || city ||
        ' ' || state ||
        ' ' || zip
    ) STORED,
    CONSTRAINT provider_pk PRIMARY KEY (id),
    CONSTRAINT unique_practice_dea UNIQUE (practice_id, dea),
    CONSTRAINT unique_practice_npi UNIQUE (practice_id, npi)
);

CREATE SEQUENCE IF NOT EXISTS PROVIDER_SEQUENCE
START WITH 1
INCREMENT BY 1
MINVALUE 1
NO MAXVALUE
CACHE 20;

CREATE INDEX IF NOT EXISTS provider_full_name_gin_trgm_ops
ON usermanagement.provider
USING GIN (LOWER(full_name) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS provider_full_address_gin_trgm_ops
ON usermanagement.provider
USING GIN (LOWER(full_address) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS provider_dea_gin_trgm_ops
ON usermanagement.provider
USING GIN (LOWER(dea) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS provider_npi_gin_trgm_ops
ON usermanagement.provider
USING GIN (npi gin_trgm_ops);