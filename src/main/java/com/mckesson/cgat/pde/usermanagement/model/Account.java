package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.NumericBooleanConverter;

import com.google.auto.value.AutoValue.Builder;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@UuidGenerator
	private String id;

	private String name;
	private String dcId;
	private String dcName;
	
	@Convert(converter = NumericBooleanConverter.class)
    private Boolean isActive;

}
