ALTER TABLE catalog.therapy_config
ADD COLUMN ship_to_po_requirement VARCHAR(50) NOT NULL DEFAULT 'NOT_REQUIRED';