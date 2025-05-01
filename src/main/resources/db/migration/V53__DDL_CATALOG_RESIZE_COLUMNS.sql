ALTER TABLE catalog.catalog_price
    ALTER COLUMN material_description_in_language_en TYPE VARCHAR(100),
    ALTER COLUMN sap_material TYPE VARCHAR(50),
    ALTER COLUMN ship_to_customer_name TYPE VARCHAR(100),
    ALTER COLUMN ship_to_customer TYPE VARCHAR(20),
    ALTER COLUMN sold_to_customer_name TYPE VARCHAR(100),
    ALTER COLUMN sold_to_customer TYPE VARCHAR(20);