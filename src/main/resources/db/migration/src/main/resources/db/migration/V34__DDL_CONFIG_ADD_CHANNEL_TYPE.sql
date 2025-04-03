ALTER TABLE catalog.material_config
    ADD COLUMN IF NOT EXISTS channel_type VARCHAR(100) DEFAULT 'BUY_AND_BILL';


ALTER TABLE catalog.questionnaire_configuration
    ADD COLUMN IF NOT EXISTS channel_type VARCHAR(100) DEFAULT 'BUY_AND_BILL';