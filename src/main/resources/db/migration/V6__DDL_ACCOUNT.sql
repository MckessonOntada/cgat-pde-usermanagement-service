CREATE INDEX IF NOT EXISTS account_salesorg_distchannel_index ON usermanagement.account 
USING btree(sales_org, distribution_channel);