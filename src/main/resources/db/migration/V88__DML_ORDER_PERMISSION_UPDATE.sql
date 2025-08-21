UPDATE usermanagement.role_permissions_mapping
SET permission = 'UPLOAD_ORDER_DOCUMENTS'
WHERE permission = 'MANAGE_ORDERS';

UPDATE usermanagement.user_permission_mapping
SET permission = 'UPLOAD_ORDER_DOCUMENTS'
WHERE permission = 'MANAGE_ORDERS';