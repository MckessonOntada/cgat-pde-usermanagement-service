package com.mckesson.cgat.pde.usermanagement.notification.model;

import com.mckesson.cgat.pde.notification.payload.EmailNotification;
import com.mckesson.cgat.pde.usermanagement.model.AuditColumns;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationStatus;
import com.mckesson.cgat.pde.usermanagement.notification.model.converters.EmailNotificationConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.SequenceGenerator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class EmailNotificationEvent extends AuditColumns {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "EMAIL_NOTIFICATION_SEQUENCE";

	@jakarta.persistence.Id
	@GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "email_notification")
	@Convert(converter = EmailNotificationConverter.class)
	private EmailNotification emailNotification;

	@Column(name = "email_notification_status")
	@Enumerated(EnumType.STRING)
	private EmailNotificationStatus emailNotificationStatus;

	@Column(name = "email_notification_event_type")
	@Enumerated(EnumType.STRING)
	private EmailNotificationEventType emailNotificationEventType;

}
