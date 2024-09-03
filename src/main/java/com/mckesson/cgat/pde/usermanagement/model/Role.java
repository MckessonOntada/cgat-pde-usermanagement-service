package com.mckesson.cgat.pde.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "role")
@Setter
@Getter
@Where(clause = "IS_ACTIVE = 1")
@JsonIgnoreProperties({ "createdDate", "createdBy", "updatedDate", "updatedBy" ,"isActive"})
@JsonFilter("PermissionFilter")
public class Role extends UUIDBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;

	@Column(name="LABEL")
	private String label;

	@Column(name="SHORT_LABEL")
	private String shortLabel;

	@Column(name="ROLE_TYPE")
	private String roleType;
	
	@Convert(converter = NumericBooleanConverter.class)
	@Column(name="IS_ACTIVE")
	private Boolean isActive;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "role")
	private List<RolePermissionMapping> rolePermissionMappings;

}
