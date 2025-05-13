CREATE TABLE IF NOT EXISTS "email_notification_event"
(
    id                          BIGINT          NOT NULL,
    user_id                     VARCHAR(255)    NOT NULL,
    email_notification          TEXT            NOT NULL,
    email_notification_status   VARCHAR(50)     NOT NULL,
    email_notification_event_type VARCHAR(50)   NOT NULL,
    created_at                  TIMESTAMP,
    created_by                  VARCHAR(255),
    updated_at                  TIMESTAMP,
    updated_by                  VARCHAR(255),
    CREATED_BY_IMPERSONATOR     VARCHAR(255),
    UPDATED_BY_IMPERSONATOR     VARCHAR(255),
    CREATED_BY_IMPERSONATOR_USER_NAME VARCHAR(255),
    CREATED_BY_USER_NAME        VARCHAR(255),
    CONSTRAINT email_notification_event_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS EMAIL_NOTIFICATION_SEQUENCE
START WITH 1
INCREMENT BY 1
MINVALUE 1
NO MAXVALUE
CACHE 20;