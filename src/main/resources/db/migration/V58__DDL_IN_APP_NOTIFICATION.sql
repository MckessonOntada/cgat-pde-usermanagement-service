CREATE TABLE IF NOT EXISTS "in_app_notification_event"
(
    id                          BIGINT          NOT NULL,
    trigger_id                     BIGINT    NOT NULL,
    notification_picked           INTEGER           NOT NULL DEFAULT 0,
    notification_event_type   VARCHAR(50)     NOT NULL,
    notification_category VARCHAR(50)   NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    CONSTRAINT in_app_notification_event_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS IN_APP_NOTIFICATION_SEQUENCE
START WITH 1
INCREMENT BY 1
MINVALUE 1
NO MAXVALUE
CACHE 20;