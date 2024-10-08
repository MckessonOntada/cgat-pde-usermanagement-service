package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.annotations.Where;
import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Permission extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private String name;

	private String label;

	@Convert(converter = NumericBooleanConverter.class)
	@JsonIgnore
	private Boolean isActive;

}
