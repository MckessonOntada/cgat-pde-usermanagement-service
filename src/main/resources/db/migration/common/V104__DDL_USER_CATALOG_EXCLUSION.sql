CREATE TABLE IF NOT EXISTS catalog.user_catalog_exclusion (
       user_id INTEGER NOT NULL,
       catalog_id INTEGER NOT NULL,
       CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
       CONSTRAINT fk_catalog_id FOREIGN KEY (catalog_id) REFERENCES catalog."catalog_price"(id) ON DELETE CASCADE
);