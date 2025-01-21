package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.NumericBooleanConverter;
import org.hibernate.type.SqlTypes;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
public class Questionnaire implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "questionnaire_id_seq")
	@SequenceGenerator(name = "questionnaire_id_seq", schema = "catalog", sequenceName = "questionnaire_id_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private Set<QuestionDTO> questions;

	@Column(name = "configured_at")
	private Set<QuestionnaireConfigurationType> configuredAt;

	@Convert(converter = NumericBooleanConverter.class)
	private boolean isActive = true;

}
