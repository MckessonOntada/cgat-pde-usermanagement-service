ALTER TABLE usermanagement.account
ADD COLUMN IF NOT EXISTS channel_type VARCHAR(100),
ADD COLUMN IF NOT EXISTS first_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS middle_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS last_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS fax VARCHAR(50),
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

UPDATE usermanagement.account 
SET channel_type = 'BUY_AND_BILL';

CREATE INDEX IF NOT EXISTS account_address_index ON usermanagement.account 
USING GIN (LOWER(full_address) gin_trgm_ops);

CREATE INDEX account_channel_type_index  ON usermanagement.account
USING btree(channel_type);
