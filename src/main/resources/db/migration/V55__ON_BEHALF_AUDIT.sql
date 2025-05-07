CREATE SCHEMA IF NOT EXISTS audit;

CREATE TABLE IF NOT EXISTS audit.on_behalf_aud (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(50),
    user_name VARCHAR(255),
    user_email VARCHAR(255),
    practice_id BIGINT,
    impersonated_user_id VARCHAR(50),
    impersonated_user_name VARCHAR(255),
    impersonated_user_email VARCHAR(255),
    timestamp TIMESTAMP
);