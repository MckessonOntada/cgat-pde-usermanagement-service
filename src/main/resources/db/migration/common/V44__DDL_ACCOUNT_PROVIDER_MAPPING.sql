
CREATE TABLE IF NOT EXISTS account_provider_mapping (
    account_id 					INTEGER NOT NULL,
    provider_id 				INTEGER NOT NULL,
    PRIMARY KEY 				(account_id, provider_id),
    FOREIGN KEY 				(account_id) REFERENCES account(id),
    FOREIGN KEY 				(provider_id) REFERENCES provider(id)
);

CREATE INDEX IF NOT EXISTS account_provider_mapping_account_id_index ON usermanagement.account_provider_mapping
USING btree(account_id);

CREATE INDEX IF NOT EXISTS account_provider_mapping_provider_id_index ON usermanagement.account_provider_mapping
USING btree(provider_id);
