ALTER TABLE "provider"
DROP COLUMN full_address;

ALTER TABLE "provider"
ALTER COLUMN state TYPE VARCHAR(50);

ALTER TABLE "provider"
ADD COLUMN full_address TEXT GENERATED ALWAYS AS (
    address1 ||
    CASE
        WHEN address2 IS NOT NULL AND address2 <> ''
        THEN ' ' || address2
        ELSE ''
    END ||
    ' ' || city ||
    ' ' || state ||
    ' ' || zip
) STORED;