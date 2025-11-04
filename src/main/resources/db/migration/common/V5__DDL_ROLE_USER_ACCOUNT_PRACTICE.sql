
--CREATE INDEX role_name_index  ON usermanagement.role 
--USING GIN (name gin_trgm_ops);

--CREATE INDEX user_name_email_index  ON usermanagement.user
--USING GIN (first_name gin_trgm_ops, middle_name gin_trgm_ops, last_name gin_trgm_ops, email gin_trgm_ops);

--CREATE INDEX account_name_number_index  ON usermanagement.account
--USING GIN (name_1 gin_trgm_ops, sap_customer gin_trgm_ops);

--CREATE INDEX practice_name_index ON usermanagement.practice
--USING GIN (name gin_trgm_ops);

CREATE INDEX practice_status_state_index  ON usermanagement.practice
USING btree(is_active, state);

CREATE INDEX user_roleid_index ON usermanagement.user
USING btree(role_id);