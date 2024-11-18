CREATE TABLE IF NOT EXISTS practice
(
	id 				INTEGER PRIMARY KEY,
    name           	VARCHAR(100) UNIQUE NOT NULL,
    logo_url       	VARCHAR(255),
    first_name  	VARCHAR(255) NOT NULL,
    middle_name	    VARCHAR(255),
    last_name 		VARCHAR(255) NOT NULL,
    address1		VARCHAR(255)NOT NULL,
    address2		VARCHAR(255),
    city		    VARCHAR(255) NOT NULL,
    state			VARCHAR(255) NOT NULL,
    zip				VARCHAR(50) NOT NULL,
    email			VARCHAR(255) UNIQUE NOT NULL,
    country_code	VARCHAR(50) NOT NULL,
    phone			VARCHAR(50) UNIQUE NOT NULL,
    fax				VARCHAR(50),
    is_active       INTEGER,
    created_at 		TIMESTAMP,
    created_by 		VARCHAR(255),
    updated_at 		TIMESTAMP,
    updated_by 		VARCHAR(255)
);

CREATE SEQUENCE practice_sequence START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;

CREATE TABLE  IF NOT EXISTS role (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    practice_id INTEGER,
    is_active INTEGER,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE SEQUENCE role_sequence START WITH 2 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;

CREATE TABLE IF NOT EXISTS permission (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    label VARCHAR(255),
    is_active INTEGER,
    created_at TIMESTAMP, 
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE SEQUENCE permission_sequence START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE CACHE 20;

CREATE TABLE IF NOT EXISTS role_permissions_mapping (
    role_id INTEGER NOT NULL,
    permission VARCHAR(50) NOT NULL,
    PRIMARY KEY (role_id, permission),
    CONSTRAINT fk_role
        FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);




CREATE TABLE IF NOT EXISTS "user"
(
    id        					INTEGER 		NOT NULL,
    practice_id					INTEGER,
    first_name  				VARCHAR(255) 	NOT NULL,
    middle_name 				VARCHAR(255),
    last_name	 				VARCHAR(255) 	NOT NULL,
    address1					VARCHAR(255) 	NOT NULL,
    address2					VARCHAR(255),
    city						VARCHAR(255) 	NOT NULL,
    state 						VARCHAR(255) 	NOT NULL,
    zip 						VARCHAR(50) 	NOT NULL,
    email	 					VARCHAR(255) UNIQUE	NOT NULL,
    country_code				VARCHAR(50) 	NOT NULL,
    phone						VARCHAR(50) UNIQUE	NOT NULL,
    fax							VARCHAR(50),
    role_id						INTEGER,
    CONSTRAINT user_pk			PRIMARY KEY (id),
    is_active      				INTEGER,
    created_at   				TIMESTAMP,
    created_by 					VARCHAR(255),
    updated_at  				TIMESTAMP,
    updated_by 					VARCHAR(255),
    provisioned_in_okta_status  VARCHAR(50)
    
);


-- CREATE TABLE IF NOT EXISTS account
-- (
--     id          				INTEGER NOT NULL,
--     practice_id					INTEGER,
--     name  						VARCHAR(255) 	UNIQUE NOT NULL,
--     dc_id 						VARCHAR(255) ,
--     dc_name						VARCHAR(50) 	NOT NULL ,
--     is_active					INTEGER ,
--     created_at   				TIMESTAMP,
--     created_by 					VARCHAR(255),
--     updated_at  				TIMESTAMP,
--     updated_by 					VARCHAR(255),
--     CONSTRAINT account_pk 		PRIMARY KEY (id)
-- );



CREATE TABLE IF NOT EXISTS account (
        id                          INTEGER NOT NULL,
        tenant_id					INTEGER,
        vistex_customer             VARCHAR(10) NOT NULL,
        sap_customer                VARCHAR(20) NOT NULL,
        partner_function            VARCHAR(50) NOT NULL,
        vistex_partner_number       VARCHAR(10),
        sap_partner                 VARCHAR(10),
        vistex_customer_type        VARCHAR(4),
        sales_org                   VARCHAR(4) NOT NULL,
        distribution_channel        VARCHAR(2) NOT NULL,
        division                    VARCHAR(2) NOT NULL,
        company_code                VARCHAR(4),
        is_active                   INTEGER,
        name_1                      VARCHAR(40) NOT NULL,
        name_2                      VARCHAR(40),
        name_3                      VARCHAR(40),
        name_4                      VARCHAR(40),
        search_term_1               VARCHAR(20),
        house_number                VARCHAR(10) NOT NULL,
        street                      VARCHAR(60) NOT NULL,
        street_2                    VARCHAR(40),
        street_3                    VARCHAR(40),
        city                        VARCHAR(40) NOT NULL,
        district                    VARCHAR(40) NOT NULL,
        state                       VARCHAR(40) NOT NULL,   --(was 3 in customer entity)
        zip_code                    VARCHAR(10) NOT NULL,
        country                     VARCHAR(10) NOT NULL,   --(was 3 in customer entity)
        telephone                   VARCHAR(30) NOT NULL,
        email                       VARCHAR(255) NOT NULL,
        fax                         VARCHAR(40),
        product_type                VARCHAR(4),
        corporate_group             VARCHAR(10),
        gln                         VARCHAR(20),       --(was 13 in customer entity)
        sales_district              VARCHAR(6),
        sales_office                VARCHAR(4),
        sales_group                 VARCHAR(3),
        customer_group              VARCHAR(2),
        currency                    VARCHAR(5),
        relevant_for_pod            VARCHAR(1),
        pod_timeframe               NUMERIC,
        shipping_conditions         VARCHAR(2),
        transportation_zone         VARCHAR(10),
        delivering_plant            VARCHAR(4),
        order_combination           VARCHAR(1),
        delivery_priority           INTEGER,
        comp_code_terms_of_payment  VARCHAR(4),
        sales_terms_of_payment      VARCHAR(4),
        national_group              VARCHAR(10),
        cgat_identifier             VARCHAR(3),
        split_invoice               VARCHAR(2),
        deleted                     VARCHAR(1),
        customer_termination_date   TIMESTAMP,
        termination_reason          VARCHAR(2),
        central_order_block         VARCHAR(2),
        order_block_for_sales_area  VARCHAR(2),
        payment_method              VARCHAR(10),
        pmt_meth_supplement         VARCHAR(2),
        bank_statement              VARCHAR(1),
        CONSTRAINT account_pk 		PRIMARY KEY (id)
        );


CREATE TABLE IF NOT EXISTS user_account_mapping (
    user_id 					INTEGER NOT NULL,
    account_id 					INTEGER NOT NULL,
    PRIMARY KEY 				(user_id, account_id),
    FOREIGN KEY 				(user_id) REFERENCES "user"(id),
    FOREIGN KEY 				(account_id) REFERENCES account(id)
);



CREATE SEQUENCE USER_SEQUENCE START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;
    
    
CREATE SEQUENCE ACCOUNT_SEQUENCE START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;  
   
CREATE TABLE IF NOT EXISTS user_permission_mapping (
     user_id INTEGER NOT NULL,
     permission VARCHAR(50) NOT NULL,
     PRIMARY KEY (user_id, permission),
     CONSTRAINT fk_user
     FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE INDEX role_name_index  ON usermanagement.role
USING GIN (name gin_trgm_ops);

CREATE INDEX user_name_email_index ON usermanagement.user 
USING GIN (first_name gin_trgm_ops, middle_name gin_trgm_ops, last_name gin_trgm_ops, email gin_trgm_ops);

CREATE TABLE IF NOT EXISTS user_provisioning_request(
	
    id        					INTEGER 		NOT NULL,
    practice_id					INTEGER,
    first_name  				VARCHAR(255) 	UNIQUE NOT NULL,
    middle_name 				VARCHAR(255),
    last_name	 				VARCHAR(255) 	NOT NULL,
    address1					VARCHAR(255) 	NOT NULL,
    address2					VARCHAR(255),
    city						VARCHAR(255) 	NOT NULL,
    state 						VARCHAR(255) 	NOT NULL,
    zip 						VARCHAR(255) 	NOT NULL,
    email	 					VARCHAR(255) 	NOT NULL,
    phone						VARCHAR(255) 	NOT NULL,
    fax							VARCHAR(50),
    comment						VARCHAR(255),
    status						VARCHAR(255),
    is_active      				INTEGER,
    created_at   				TIMESTAMP,
    created_by 					VARCHAR(255),
    updated_at  				TIMESTAMP,
    updated_by 					VARCHAR(255)
    
);

CREATE INDEX user_status_createdat_index  ON usermanagement.user 
USING btree(is_active, created_at);

CREATE INDEX role_status_updatedat_index ON usermanagement.role 
USING btree(is_active, updated_at);

INSERT INTO ROLE (ID,  NAME, DESCRIPTION, IS_ACTIVE, CREATED_BY, UPDATED_BY) VALUES ('1', 'SYSTEM_ADMIN', 'System Admin', 1, 'SYSTEM', 'SYSTEM');

INSERT INTO ROLE_PERMISSIONS_MAPPING (ROLE_ID, PERMISSION) VALUES
('1','MANAGE_USERS_SCREEN'),
('1','ADD_USERS'),
('1','VIEW_USERS'),
('1','EDIT_USERS'),
('1','UPLOAD_USERS'),
('1','CREATE_USER_FOR_PENDING_USER_REQUEST'),
('1','ADD_SYSTEM_ADMINISTRATOR'),
('1','VIEW_SYSTEM_ADMINISTRATOR'),
('1','EDIT_SYSTEM_ADMINISTRATOR'),
('1','VIEW_ROLES'),
('1','ADD_ROLES'),
('1','ON_BEHALF_OF_USER'),
('1','MANAGE_ORGANIZATION_SCREEN'),
('1','VIEW_ORGANIZATION'),
('1','ADD_ORGANIZATION'),
('1','EDIT_ORGANIZATION'),
('1','MANAGE_ORDERS'),
('1','VIEW_ORDERS'),
('1','CREATE_ORDERS'),
('1','EDIT_ORDERS'),
('1','APPROVE_ORDERS'),
('1','MANAGE_CATALOG_CONFIGURATIONS'),
('1','MANAGE_SYSTEM_CONFIGURATIONS'),
('1','CREATE_QUESTIONNIARE'),
('1','VIEW_ACCOUNTS'),
('1','VIEW_INVOICES'),
('1','CREATE_INVOICES'),
('1','MANAGE_INVOICES'),
('1','VIEW_PAYMENT_HISTORY'),
('1','VIEW_CUSTOMER_STATEMENT'),
('1','VIEW_PAYMENT_METHOD'),
('1','ADD_PAYMENT_METHOD'),
('1','MANAGE_PAYMENT_METHODS');