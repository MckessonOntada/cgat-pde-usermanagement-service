ALTER TABLE usermanagement.account
ADD COLUMN IF NOT EXISTS account_type VARCHAR(255) DEFAULT 'BnB';

UPDATE usermanagement.account 
SET account_type = 'BnB';

-- Insert SP Account related pemissions to system admin
INSERT INTO usermanagement.role_permissions_mapping(role_id, permission) VALUES
(1, 'MANAGE_SP_ACCOUNT'),
(1, 'ADD_SP_ACCOUNT'),
(1, 'VIEW_SP_ACCOUNT'),
(1, 'EDIT_SP_ACCOUNT');

INSERT into usermanagement.user_permission_mapping(user_id, permission) VALUES
(1, 'MANAGE_SP_ACCOUNT'),
(1, 'ADD_SP_ACCOUNT'),
(1, 'VIEW_SP_ACCOUNT'),
(1, 'EDIT_SP_ACCOUNT');