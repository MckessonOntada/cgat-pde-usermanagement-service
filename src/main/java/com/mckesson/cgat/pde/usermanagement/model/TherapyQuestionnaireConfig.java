package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapyQuestionnaireConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "ther_questionnaire_id_seq")
	@SequenceGenerator(name = "ther_questionnaire_id_seq", schema = "catalog", sequenceName = "ther_questionnaire_id_seq", allocationSize = 1)
	private Long id;

	@Column(name = "questionnaire_id")
	private Long questionnaireId;

	@Column(name = "therapy_id")
	private String therapyId;

}
