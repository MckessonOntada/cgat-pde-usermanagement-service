ALTER TABLE usermanagement.account
ADD COLUMN IF NOT EXISTS channel_type VARCHAR(100) DEFAULT 'BUY_AND_BILL';

UPDATE usermanagement.account 
SET channel_type = 'BUY_AND_BILL';

-- Insert SP Account related pemissions to system admin
INSERT INTO usermanagement.role_permissions_mapping(role_id, permission) VALUES
(1, 'MANAGE_SP_ACCOUNT'),
(1, 'ADD_SP_ACCOUNT');

INSERT into usermanagement.user_permission_mapping(user_id, permission) VALUES
(1, 'MANAGE_SP_ACCOUNT'),
(1, 'ADD_SP_ACCOUNT');