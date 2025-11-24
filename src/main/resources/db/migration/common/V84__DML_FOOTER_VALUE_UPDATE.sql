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
           },
           {
             "url": "https://privacyportal.onetrust.com/webform/599133ba-bafa-4e24-8173-6e59b6c96dab/f081c4ff-0e3b-4d07-a79f-30effc0fc255",
             "text": "footer.info"
           }
         ],
         "heading": "footer.terms"
       },
       {
         "links": [
           {
             "url": "/faq/",
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