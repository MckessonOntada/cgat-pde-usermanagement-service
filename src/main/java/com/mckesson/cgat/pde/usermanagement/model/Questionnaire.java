package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.NumericBooleanConverter;
import org.hibernate.type.SqlTypes;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "questionnaire", schema = "catalog")
public class Questionnaire extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "questionnaire_id_seq")
	@SequenceGenerator(name = "questionnaire_id_seq", schema = "catalog", sequenceName = "questionnaire_id_seq",
			allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private List<QuestionDTO> questions;

	@Column(name = "configured_at")
	@Enumerated(EnumType.STRING)
	private Set<QuestionnaireConfigurationType> configuredAt;

	@Convert(converter = NumericBooleanConverter.class)
	private boolean isActive = true;

}
