CREATE TABLE IF NOT EXISTS catalog.crosswalk
(
    id                                      INTEGER         NOT NULL,
    bnb_material_number                     VARCHAR(50),
    sp_product_id                           VARCHAR(50)     NOT NULL,
    is_active                               INTEGER,
    created_at                              TIMESTAMP,
    created_by                              VARCHAR(255),
    updated_at                              TIMESTAMP,
    updated_by                              VARCHAR(255),
    CREATED_BY_IMPERSONATOR                 VARCHAR(255),
    UPDATED_BY_IMPERSONATOR                 VARCHAR(255),
    CREATED_BY_IMPERSONATOR_USER_NAME       VARCHAR(255),
    CREATED_BY_USER_NAME                    VARCHAR(255),
    CONSTRAINT crosswalk_pk PRIMARY KEY (id),
    CONSTRAINT unique_bnb_materialnumber_sp_productid UNIQUE (bnb_material_number, sp_product_id)
);

CREATE SEQUENCE IF NOT EXISTS catalog.CROSSWALK_SEQUENCE
START WITH 1
INCREMENT BY 1
MINVALUE 1
NO MAXVALUE
CACHE 20;

INSERT INTO usermanagement.role_permissions_mapping(role_id, permission) VALUES
(1, 'VIEW_MATERIAL_CROSSWALK'),
(1, 'ADD_CROSSWALK');

INSERT INTO usermanagement.user_permission_mapping(user_id, permission) VALUES
(1, 'VIEW_MATERIAL_CROSSWALK'),
(1, 'ADD_CROSSWALK');

CREATE INDEX IF NOT EXISTS crosswalk_sp_product_id_index ON catalog.crosswalk
USING btree(sp_product_id);

CREATE INDEX IF NOT EXISTS crosswalk_bnb_material_number_index ON catalog.crosswalk
USING btree(bnb_material_number);
