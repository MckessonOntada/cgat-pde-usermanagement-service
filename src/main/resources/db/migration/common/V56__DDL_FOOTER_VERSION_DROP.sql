ALTER TABLE usermanagement.footer_config DROP COLUMN version;

DELETE FROM usermanagement.footer_config WHERE id = 1;
INSERT INTO usermanagement.footer_config (id, config_json)
VALUES (1, '{"sections":[{"heading":"footer.terms","links":[{"text":"footer.privacy","url":"https://qa.ontadahealth.com/privacy-notice"},{"text":"forgot.terms.agreement","url":"https://qa.ontadahealth.com/terms-of-use"},{"text":"footer.info","url":"https://privacyportal.onetrust.com/webform/599133ba-bafa-4e24-8173-6e59b6c96dab/f081c4ff-0e3b-4d07-a79f-30effc0fc255"}]},{"heading":"footer.help","links":[{"text":"footer.help.content","url":"https://patientportal.help.ontada.com/kb/en/"}]},{"heading":"footer.contant","links":[{"text":"footer.contant.no","url":"tel:1-855-887-6788"}]}]}');