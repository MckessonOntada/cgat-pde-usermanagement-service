CREATE INDEX IF NOT EXISTS gin_idx_material_dosage_trgm
ON catalog.material
USING gin (LOWER(dosage) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS gin_idx_material_strength_trgm
ON catalog.material
USING gin (LOWER(strength) gin_trgm_ops);