ALTER TABLE usermanagement.account DROP COLUMN full_address;

ALTER TABLE usermanagement.account
ALTER COLUMN created_at SET DEFAULT now(),
ALTER COLUMN state TYPE VARCHAR(40),
ALTER COLUMN country TYPE VARCHAR(10),
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
