CREATE TABLE IF NOT EXISTS practice
(
	id 				INTEGER PRIMARY KEY,
    name           	VARCHAR(100) UNIQUE NOT NULL,
    logo_url       	VARCHAR(255),
    first_name  	VARCHAR(50) NOT NULL,
    middle_name	    VARCHAR(50),
    last_name 		VARCHAR(50) NOT NULL,
    address1		VARCHAR(250)NOT NULL,
    address2		VARCHAR(250),
    city		    VARCHAR(100) NOT NULL,
    state			VARCHAR(100) NOT NULL,
    zip				VARCHAR(20) NOT NULL,
    email			VARCHAR(40) NOT NULL,
    phone			VARCHAR(10) NOT NULL,
    fax				VARCHAR(15),
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
    first_name  				VARCHAR(255) 	UNIQUE NOT NULL,
    middle_name 				VARCHAR(255),
    last_name	 				VARCHAR(255) 	NOT NULL,
    suffix						VARCHAR(50),
    address1					VARCHAR(255) 	NOT NULL,
    address2					VARCHAR(255),
    city						VARCHAR(255) 	NOT NULL,
    state 						VARCHAR(255) 	NOT NULL,
    zip 						VARCHAR(255) 	NOT NULL,
    email	 					VARCHAR(255) 	NOT NULL,
    phone						VARCHAR(255) 	NOT NULL,
    fax							VARCHAR(50),
    role_id						INTEGER,
    CONSTRAINT user_pk			PRIMARY KEY (id),
    is_active      				INTEGER,
    created_at   				TIMESTAMP,
    created_by 					VARCHAR(255),
    updated_at  				TIMESTAMP,
    updated_by 					VARCHAR(255)
    
);


CREATE TABLE IF NOT EXISTS account
(
    id          				INTEGER NOT NULL,
    practice_id					INTEGER,
    name  						VARCHAR(255) 	UNIQUE NOT NULL,
    dc_id 						VARCHAR(255) ,
    dc_name						VARCHAR(50) 	NOT NULL ,
    is_active					INTEGER ,
    created_at   				TIMESTAMP,
    created_by 					VARCHAR(255),
    updated_at  				TIMESTAMP,
    updated_by 					VARCHAR(255),
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




