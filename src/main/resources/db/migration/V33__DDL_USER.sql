ALTER TABLE usermanagement.user 
DROP COLUMN full_name;

ALTER TABLE usermanagement.user 
ADD COLUMN full_name TEXT 
GENERATED ALWAYS AS (
    first_name || 
    CASE 
        WHEN middle_name IS NOT NULL AND middle_name <> '' 
        THEN ' ' || middle_name 
        ELSE '' 
    END || 
    ' ' || last_name
) STORED;

ALTER TABLE usermanagement.user_provisioning_request 
DROP COLUMN full_name;

ALTER TABLE usermanagement.user_provisioning_request 
ADD COLUMN full_name TEXT 
GENERATED ALWAYS AS (
    first_name || 
    CASE 
        WHEN middle_name IS NOT NULL AND middle_name <> '' 
        THEN ' ' || middle_name 
        ELSE '' 
    END || 
    ' ' || last_name
) STORED;
