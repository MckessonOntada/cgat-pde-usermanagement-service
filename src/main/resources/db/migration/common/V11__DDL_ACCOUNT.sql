CREATE INDEX IF NOT EXISTS account_distchannel_cgatidentifier_index ON usermanagement.account
USING btree(distribution_channel, cgat_identifier);
