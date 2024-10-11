ALTER TABLE USER_ROLE
DROP CONSTRAINT USER_ROLE_PK;
ALTER TABLE USER_ROLE
    ALTER COLUMN ID DROP NOT NULL;
ALTER TABLE USER_ROLE
    ALTER COLUMN IS_ACTIVE SET DEFAULT 1;
ALTER TABLE ROLE
    ADD SHORT_LABEL VARCHAR(50);


update role set short_label='ADMIN' where id = '5ec5de42-dffb-11ec-9d64-0242ac12001';
update role set short_label='STUDY MGR' where id = '5ec5de42-dffb-11ec-9d64-0242ac12002';
update role set short_label='QC ONTADA' where id = '5ec5de42-dffb-11ec-9d64-0242ac12003';
update role set short_label='DATA SPC.' where id = '5ec5de42-dffb-11ec-9d64-0242ac12004';
update role set short_label='MONITOR' where id = '5ec5de42-dffb-11ec-9d64-0242ac12005';
update role set short_label='CA' where id = '5ec5de42-dffb-11ec-9d64-0242ac12006';
update role set short_label='CA MGR' where id = '5ec5de42-dffb-11ec-9d64-0242ac12007';
COMMIT;
