ALTER TABLE practice DROP CONSTRAINT practice_name_key;

CREATE UNIQUE INDEX unique_practice_name
    ON usermanagement.practice (LOWER(name));
