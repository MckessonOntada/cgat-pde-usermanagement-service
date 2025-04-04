package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "questionnaire_configuration", schema = "catalog")
public class QuestionnaireConfiguration extends AuditColumns implements Serializable {

	@Id
	@GeneratedValue(generator = "catalog.questionnaire_configuration_id_seq")
	@SequenceGenerator(name = "catalog.questionnaire_configuration_id_seq", schema = "catalog",
			sequenceName = "catalog.questionnaire_configuration_id_seq", allocationSize = 1)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "questionnaire_id", nullable = false)
	private Questionnaire questionnaireId;

	@Column(nullable = false, name = "configured_type")
	@Enumerated(EnumType.STRING)
	private QuestionnaireConfigurationType configuredType;

	@Column(nullable = false, name = "entity_id")
	private String entityId;

	@Enumerated(EnumType.STRING)
	private ChannelType channelType = ChannelType.BUY_AND_BILL;

}
