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
public class AccountQuestionnaireConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "acc_questionnaire_id_seq")
	@SequenceGenerator(name = "acc_questionnaire_id_seq", schema = "catalog", sequenceName = "acc_questionnaire_id_seq", allocationSize = 1)
	private Long id;

	@Column(name = "questionnaire_id")
	private Long questionnaireId;

	@Column(name = "account_number")
	private String accountNumber;
}
