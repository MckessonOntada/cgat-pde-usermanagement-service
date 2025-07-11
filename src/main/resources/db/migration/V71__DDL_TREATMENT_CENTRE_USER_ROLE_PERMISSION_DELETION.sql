DELETE FROM usermanagement.role_permissions_mapping
where role_id=3 and permission like '%ORDER%';

DELETE FROM usermanagement.role_permissions_mapping
where role_id=3 and permission like '%APPROVER%';

DELETE FROM usermanagement.role_permissions_mapping
where role_id=3 and permission like '%INVOICES%';

DELETE FROM usermanagement.role_permissions_mapping
where role_id=3 and permission ='VIEW_CREDIT_LISTING';

DELETE FROM usermanagement.role_permissions_mapping
where role_id=3 and permission like '%PAYMENT%';

DELETE FROM usermanagement.role_permissions_mapping
where role_id=3 and permission = 'MANAGE_CUSTOMER_STATEMENT';
