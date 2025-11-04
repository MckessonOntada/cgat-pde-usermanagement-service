ALTER TABLE usermanagement.provider
    ALTER COLUMN dea DROP NOT NULL;

ALTER TABLE usermanagement.provider
    ALTER COLUMN medicaid_number DROP NOT NULL;
