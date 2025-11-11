CREATE TABLE IF NOT EXISTS catalog.user_material_exclusion (
       user_id INTEGER NOT NULL,
       material_id INTEGER NOT NULL,
       CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
       CONSTRAINT fk_material_id FOREIGN KEY (material_id) REFERENCES catalog.material(id) ON DELETE CASCADE
);