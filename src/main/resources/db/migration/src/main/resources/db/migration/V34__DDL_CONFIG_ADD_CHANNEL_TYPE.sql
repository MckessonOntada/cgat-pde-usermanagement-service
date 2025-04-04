ALTER TABLE catalog.material_config
    ADD COLUMN IF NOT EXISTS channel_type VARCHAR(20) NULL;

UPDATE catalog.material_config SET channel_type = 'BUY_AND_BILL' WHERE channel_type IS NULL;

ALTER TABLE catalog.material_config ALTER COLUMN channel_type SET NOT NULL;


ALTER TABLE catalog.questionnaire_configuration
    ADD COLUMN IF NOT EXISTS channel_type VARCHAR(20) NULL;

UPDATE catalog.questionnaire_configuration SET channel_type = 'BUY_AND_BILL' WHERE channel_type IS NULL;

ALTER TABLE catalog.questionnaire_configuration ALTER COLUMN channel_type SET NOT NULL;