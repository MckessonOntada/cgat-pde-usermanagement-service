package com.mckesson.cgat.pde.usermanagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "permission")
@Setter
@Getter
@Where(clause = "IS_ACTIVE = 1")
@NoArgsConstructor
public class Permission extends UUIDBaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;

	@Column(name="LABEL")
	private String label;

	@Convert(converter = NumericBooleanConverter.class)
	@Column(name="IS_ACTIVE")
	@JsonIgnore
	private Boolean isActive;

	@Column(name="CATEGORY")
	private String category;

}
