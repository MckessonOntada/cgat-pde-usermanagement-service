ALTER TABLE usermanagement.account DROP COLUMN full_address;

ALTER TABLE usermanagement.account
    ALTER COLUMN name_1 TYPE VARCHAR(100),
    ALTER COLUMN street TYPE VARCHAR(100),
    ALTER COLUMN street_2 TYPE VARCHAR(100),
    ALTER COLUMN city TYPE VARCHAR(100),
    ADD COLUMN full_address TEXT 
		GENERATED ALWAYS AS (
    	street ||
    	CASE
    	    WHEN street_2 IS NOT NULL AND street_2 <> ''
    	    THEN ' ' || street_2
    	    ELSE ''
    	END || 
    	' ' || city ||
    	' ' || state ||
    	' ' || zip_code
	) STORED;

	
ALTER TABLE audit.account_aud
    ALTER COLUMN name_1 TYPE VARCHAR(100),
    ALTER COLUMN street TYPE VARCHAR(100),
    ALTER COLUMN street_2 TYPE VARCHAR(100),
    ALTER COLUMN city TYPE VARCHAR(100);

	