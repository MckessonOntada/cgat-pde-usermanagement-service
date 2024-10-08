ALTER TYPE Permission_category ADD VALUE 'Manage Document';
COMMIT;

UPDATE PERMISSION SET category='Manage Document' WHERE id ='5ec5de42-dffb-11ec-9d64-0242ac111143';
UPDATE PERMISSION SET category='Manage Document' WHERE id ='5ec5de42-dffb-11ec-9d64-0242ac111144';
UPDATE PERMISSION SET category='Manage Document' WHERE id ='5ec5de42-dffb-11ec-9d64-0242ac111145';
UPDATE PERMISSION SET category='Manage Document' WHERE id ='5ec5de42-dffb-11ec-9d64-0242ac111146';
UPDATE PERMISSION SET category='Manage Document' WHERE id ='5ec5de42-dffb-11ec-9d64-0242ac111147';
UPDATE PERMISSION SET category='Manage Document' WHERE id ='5ec5de42-dffb-11ec-9d64-0242ac111148';
UPDATE PERMISSION SET category='Manage Document' WHERE id ='5ec5de42-dffb-11ec-9d64-0242ac111149';
UPDATE PERMISSION SET category='Manage Document' WHERE id ='5ec5de42-dffb-11ec-9d64-0242ac111150';
COMMIT;

UPDATE ROLE_PERMISSION_MPNG SET role_id = '5ec5de42-dffb-11ec-9d64-00242ac12007' WHERE id = '2e9a5344-5cfd-11ee-8c99-0242ac120169';
UPDATE ROLE_PERMISSION_MPNG SET role_id = '5ec5de42-dffb-11ec-9d64-00242ac12007' WHERE id = '2e9a5344-5cfd-11ee-8c99-0242ac120170';
UPDATE ROLE_PERMISSION_MPNG SET role_id = '5ec5de42-dffb-11ec-9d64-00242ac12007' WHERE id = '2e9a5344-5cfd-11ee-8c99-0242ac120171';
UPDATE ROLE_PERMISSION_MPNG SET role_id = '5ec5de42-dffb-11ec-9d64-00242ac12007' WHERE id = '2e9a5344-5cfd-11ee-8c99-0242ac120172';
UPDATE ROLE_PERMISSION_MPNG SET role_id = '5ec5de42-dffb-11ec-9d64-00242ac12007' WHERE id = '2e9a5344-5cfd-11ee-8c99-0242ac120173';
COMMIT;