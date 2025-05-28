DROP TABLE IF EXISTS catalog.crosswalk CASCADE;
DROP SEQUENCE IF EXISTS catalog.CROSSWALK_SEQUENCE;

CREATE TABLE IF NOT EXISTS catalog.crosswalk
(
    id                                  INTEGER         NOT NULL,
    bnb_ndc_number                      VARCHAR(50),
    sp_ndc_number                       VARCHAR(50)     NOT NULL,
    is_active                           INTEGER,
    created_at                          TIMESTAMP,
    created_by                          VARCHAR(255),
    updated_at                          TIMESTAMP,
    updated_by                          VARCHAR(255),
    CREATED_BY_IMPERSONATOR             VARCHAR(255),
    UPDATED_BY_IMPERSONATOR             VARCHAR(255),
    CREATED_BY_IMPERSONATOR_USER_NAME   VARCHAR(255),
    CREATED_BY_USER_NAME                VARCHAR(255),
    CONSTRAINT crosswalk_pk PRIMARY KEY (id),
    CONSTRAINT unique_bnb_ndcnumber_sp_ndcnumber UNIQUE (bnb_ndc_number, sp_ndc_number)
);

CREATE SEQUENCE IF NOT EXISTS catalog.CROSSWALK_SEQUENCE
START WITH 1
INCREMENT BY 1
MINVALUE 1
NO MAXVALUE
CACHE 20;

CREATE INDEX IF NOT EXISTS crosswalk_sp_ndc_number_index ON catalog.crosswalk
USING btree(sp_ndc_number);

CREATE INDEX IF NOT EXISTS crosswalk_bnb_ndc_number_index ON catalog.crosswalk
USING btree(bnb_ndc_number);