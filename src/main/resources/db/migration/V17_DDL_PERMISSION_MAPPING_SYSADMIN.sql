-- Update QUESTIONIARE to QUESTIONNAIRE
UPDATE usermanagement.role_permissions_mapping
SET permission = 'VIEW_QUESTIONNAIRE'
WHERE permission = 'VIEW_QUESTIONIARE';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'CONFIGURE_QUESTIONNAIRE'
WHERE permission = 'CONFIGURE_QUESTIONNIARE';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'DEACTIVATE_QUESTIONNAIRE'
WHERE permission = 'DEACTIVATE_QUESTIONNIARE';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'ADD_QUESTIONNAIRE'
WHERE permission = 'ADD_QUESTIONNIARE';

UPDATE usermanagement.user_permission_mapping
SET permission = 'VIEW_QUESTIONNAIRE'
WHERE permission = 'VIEW_QUESTIONIARE';

UPDATE usermanagement.user_permission_mapping
SET permission = 'CONFIGURE_QUESTIONNAIRE'
WHERE permission = 'CONFIGURE_QUESTIONNIARE';

UPDATE usermanagement.user_permission_mapping
SET permission = 'DEACTIVATE_QUESTIONNAIRE'
WHERE permission = 'DEACTIVATE_QUESTIONNIARE';

UPDATE usermanagement.user_permission_mapping
SET permission = 'ADD_QUESTIONNAIRE'
WHERE permission = 'ADD_QUESTIONNIARE';

-- Update generic payment method permissions to CARD
UPDATE usermanagement.role_permissions_mapping
SET permission = 'VIEW_PAYMENT_METHOD_CARD'
WHERE permission = 'VIEW_PAYMENT_METHOD';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'ADD_PAYMENT_METHOD_CARD'
WHERE permission = 'ADD_PAYMENT_METHOD';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'MANAGE_PAYMENT_METHOD_CARD'
WHERE permission = 'MANAGE_PAYMENT_METHODS';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'DELETE_PAYMENT_METHOD_CARD'
WHERE permission = 'DELETE_PAYMENT_METHODS';

UPDATE usermanagement.user_permission_mapping
SET permission = 'VIEW_PAYMENT_METHOD_CARD'
WHERE permission = 'VIEW_PAYMENT_METHOD';

UPDATE usermanagement.user_permission_mapping
SET permission = 'ADD_PAYMENT_METHOD_CARD'
WHERE permission = 'ADD_PAYMENT_METHOD';

UPDATE usermanagement.user_permission_mapping
SET permission = 'MANAGE_PAYMENT_METHOD_CARD'
WHERE permission = 'MANAGE_PAYMENT_METHODS';

UPDATE usermanagement.user_permission_mapping
SET permission = 'DELETE_PAYMENT_METHOD_CARD'
WHERE permission = 'DELETE_PAYMENT_METHODS';

-- Update ACCOUNT to BANK
UPDATE usermanagement.role_permissions_mapping
SET permission = 'VIEW_PAYMENT_METHOD_BANK'
WHERE permission = 'VIEW_PAYMENT_METHOD_ACCOUNT';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'ADD_PAYMENT_METHOD_BANK'
WHERE permission = 'ADD_PAYMENT_METHOD_ACCOUNT';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'MANAGE_PAYMENT_METHOD_BANK'
WHERE permission = 'MANAGE_PAYMENT_METHODS_ACCOUNT';

UPDATE usermanagement.role_permissions_mapping
SET permission = 'DELETE_PAYMENT_METHOD_BANK'
WHERE permission = 'DELETE_PAYMENT_METHODS_ACCOUNT';

UPDATE usermanagement.user_permission_mapping
SET permission = 'VIEW_PAYMENT_METHOD_BANK'
WHERE permission = 'VIEW_PAYMENT_METHOD_ACCOUNT';

UPDATE usermanagement.user_permission_mapping
SET permission = 'ADD_PAYMENT_METHOD_BANK'
WHERE permission = 'ADD_PAYMENT_METHOD_ACCOUNT';

UPDATE usermanagement.user_permission_mapping
SET permission = 'MANAGE_PAYMENT_METHOD_BANK'
WHERE permission = 'MANAGE_PAYMENT_METHODS_ACCOUNT';

UPDATE usermanagement.user_permission_mapping
SET permission = 'DELETE_PAYMENT_METHOD_BANK'
WHERE permission = 'DELETE_PAYMENT_METHODS_ACCOUNT';