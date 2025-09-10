package com.mckesson.cgat.pde.usermanagement.model;

import com.mckesson.cgat.pde.usermanagement.notification.config.NotificationEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_event")
public class NotificationEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "NOTIFICATION_SEQUENCE";

	@Id
	@GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	@Column(name = "trigger_id")
	private Long triggerId;

	@Column(name = "notification_picked")
	@Convert(converter = NumericBooleanConverter.class)
	private boolean notificationPicked;

	@Column(name = "notification_event_type")
	@Enumerated(EnumType.STRING)
	private NotificationEventType notificationEventType;

	@Column(name = "created_at")
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt;

	@Column(name = "created_by")
	@CreatedBy
	private String createdBy;

}
