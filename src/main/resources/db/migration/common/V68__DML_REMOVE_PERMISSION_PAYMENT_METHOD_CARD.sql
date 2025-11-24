DELETE FROM usermanagement.user_permission_mapping
where permission like '%PAYMENT_METHOD_CARD%';

DELETE FROM usermanagement.role_permissions_mapping
where permission like '%PAYMENT_METHOD_CARD%';