DELETE FROM usermanagement.footer_config WHERE id = 1;

INSERT INTO usermanagement.footer_config (id, config_json) VALUES (
  1,
  '{
    "footerSection": [
      {
        "links": [
          {
            "url": "/privacy-notice/",
            "text": "footer.privacy"
          },
          {
            "url": "/terms-of-use/",
            "text": "footer.termsOfUse"
          }
        ],
        "heading": "footer.terms"
      },
      {
        "links": [
          {
            "url": "",
            "text": "footer.help.faq"
          }
        ],
        "heading": "footer.help"
      },
      {
        "links": [
          {
            "url": "/contact/",
            "text": "footer.contactUs"
          }
        ],
        "heading": "footer.heading.contact"
      }
    ],
    "supportContactNumber": "9999123456"
  }'
);