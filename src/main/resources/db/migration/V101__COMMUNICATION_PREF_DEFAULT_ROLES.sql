delete from usermanagement.communication_preferences
where role_id in (1,2,3,4);

INSERT INTO usermanagement.communication_preferences (id, role_id, preferences) VALUES (
nextval('communication_preferences_sequence'),1,'{"USER_MANAGEMENT":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ORDER_MANAGEMENT":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ORDER_APPROVAL":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ON_BEHALF_OF":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false}}}'
);


INSERT INTO usermanagement.communication_preferences (id, role_id, preferences) VALUES (
  nextval('communication_preferences_sequence'),  2,'{"USER_MANAGEMENT":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ORDER_MANAGEMENT":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ORDER_APPROVAL":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ON_BEHALF_OF":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false}}}'
);

INSERT INTO usermanagement.communication_preferences (id, role_id, preferences) VALUES (
nextval('communication_preferences_sequence'),  3,'{"USER_MANAGEMENT":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ORDER_MANAGEMENT":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ORDER_APPROVAL":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ON_BEHALF_OF":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false}}}'
);

INSERT INTO usermanagement.communication_preferences (id, role_id, preferences) VALUES (
nextval('communication_preferences_sequence'),  4,'{"USER_MANAGEMENT":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ORDER_MANAGEMENT":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ORDER_APPROVAL":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false},"REMINDERS":{"email":false,"inApp":false,"sms":false}},"ON_BEHALF_OF":{"PROCESS_UPDATES":{"email":false,"inApp":false,"sms":false}}}'
);