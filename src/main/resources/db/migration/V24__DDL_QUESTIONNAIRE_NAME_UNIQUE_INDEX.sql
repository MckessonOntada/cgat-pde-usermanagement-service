CREATE UNIQUE INDEX uq_template_name_lower
    ON catalog.questionnaire (LOWER(template_name));