-------------------------------------------------
-- JBAT-1222 | Script is designed to create the first user in the applications's database as System Admin.
-- 
-- Created by Boney Dhawan 1/05/2024 6:00 PM
-------------------------------------------------
INSERT INTO usermanagement.users VALUES ('7b9c1714-b4c7-48b7-b913-721eaa164144', 'sawsan.dabit@mckesson.com', 'Sawsan Dabit',1);
INSERT INTO usermanagement.user_role VALUES ('57e0ce30-c7c8-4629-bb31-181a9439c899','7b9c1714-b4c7-48b7-b913-721eaa164144', '5ec5de42-dffb-11ec-9d64-00242ac12001',1);

INSERT INTO usermanagement.users VALUES ('6955cd4a-c34a-47c5-b638-fc3698007efe', 'disha.patel3@mckesson.com', 'Disha Patel',1);
INSERT INTO usermanagement.user_role VALUES ('dca0ce5e-b72f-4a5a-b423-e41c097c8ac3','6955cd4a-c34a-47c5-b638-fc3698007efe', '5ec5de42-dffb-11ec-9d64-00242ac12001',1);