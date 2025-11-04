ALTER TABLE usermanagement.user_communication_preferences
    ADD if not exists created_by VARCHAR(50) NULL,
    ADD if not exists created_at timestamp NOT NULL DEFAULT NOW(),
    ADD if not exists updated_by varchar(50) NULL,
    ADD if not exists updated_at timestamp  NOT NULL DEFAULT NOW(),
    ADD if not exists CREATED_BY_USER_NAME VARCHAR(255),
    ADD if not exists CREATED_BY_IMPERSONATOR VARCHAR(255),
    ADD if not exists CREATED_BY_IMPERSONATOR_USER_NAME VARCHAR(255),
    ADD if not exists UPDATED_BY_IMPERSONATOR VARCHAR(255);

ALTER TABLE usermanagement.communication_preferences
    ADD if not exists created_by VARCHAR(50) NULL,
    ADD if not exists created_at timestamp NOT NULL DEFAULT NOW(),
    ADD if not exists updated_by varchar(50) NULL,
    ADD if not exists updated_at timestamp NOT NULL DEFAULT NOW(),
    ADD if not exists CREATED_BY_USER_NAME VARCHAR(255),
    ADD if not exists CREATED_BY_IMPERSONATOR VARCHAR(255),
    ADD if not exists CREATED_BY_IMPERSONATOR_USER_NAME VARCHAR(255),
    ADD if not exists UPDATED_BY_IMPERSONATOR VARCHAR(255);