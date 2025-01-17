ALTER TABLE usermanagement.account 
	ALTER partner_function drop NOT NULL,
    ALTER house_number drop NOT NULL,
    ALTER street drop NOT NULL,
    ALTER city drop NOT NULL,
    ALTER district drop NOT NULL,
    ALTER zip_code drop NOT NULL,
    ALTER telephone drop NOT NULL,
    ALTER email drop NOT NULL;

ALTER TABLE usermanagement.account
	ALTER COLUMN customer_termination_date TYPE DATE
	USING customer_termination_date::DATE;

ALTER TABLE usermanagement.account 
	ADD if not exists created_by VARCHAR(50) NULL, 
	ADD if not exists created_at timestamp NOT NULL DEFAULT NOW(), 
	ADD if not exists updated_by varchar(50) NULL, 
	ADD if not exists updated_at timestamp NULL;

