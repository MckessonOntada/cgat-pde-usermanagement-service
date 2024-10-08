CREATE TABLE IF NOT EXISTS practice
(
	id INTEGER PRIMARY KEY,
    name           	VARCHAR(255) UNIQUE NOT NULL,
    logo_url       	VARCHAR(255),
    first_name  	VARCHAR(50) NOT NULL,
    middle_name	    VARCHAR(50),
    last_name 		VARCHAR(50) NOT NULL,
    address1		VARCHAR(255)NOT NULL,
    address2		VARCHAR(255),
    city		    VARCHAR(50) NOT NULL,
    state			VARCHAR(50) NOT NULL,
    zip				VARCHAR(50) NOT NULL,
    email			VARCHAR(50) NOT NULL,
    phone	VARCHAR(50) NOT NULL,
    fax				VARCHAR(50),
    is_active       INTEGER,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE SEQUENCE practice_sequence START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;

CREATE TABLE  IF NOT EXISTS role (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE SEQUENCE role_sequence START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE  CACHE 20;

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

CREATE TABLE role_permissions_mapping (
    role_id INTEGER,
    permission_id INTEGER,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS users
(
    id          				VARCHAR(50) 	NOT NULL,
    first_name  				VARCHAR(255) 	UNIQUE NOT NULL,
    middle_name 				VARCHAR(255),
    last_name	 				VARCHAR(50) 	NOT NULL,
    suffix						VARCHAR(50),
    address_1					VARCHAR(255) 	NOT NULL,
    address_2					VARCHAR(255),
    city						VARCHAR(255) 	NOT NULL,
    state 						VARCHAR(255) 	NOT NULL,
    zip 						VARCHAR(255) 	NOT NULL,
    emai_id 					VARCHAR(255) 	NOT NULL,
    phone_number 				VARCHAR(255) 	NOT NULL,
    created_date				TIMESTAMP(6) 	DEFAULT	CURRENT_TIMESTAMP  	NOT NULL,
    updated_by					VARCHAR(50),
    CONSTRAINT users_pk 		PRIMARY KEY (id)
);





