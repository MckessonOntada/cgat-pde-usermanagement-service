package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Where;
import org.hibernate.type.NumericBooleanConverter;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "role_permission_mpng")
@Setter
@Getter
@Where(clause = "IS_ACTIVE = 1")
@JsonIgnoreProperties({ "id"})
public class RolePermissionMapping extends UUIDBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@JoinColumn(name = "role_id", referencedColumnName = "id")
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JdbcType(VarcharJdbcType.class)
	@JsonIgnore
	private Role role;

	@JoinColumn(name = "permission_id", referencedColumnName = "id")
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JdbcType(VarcharJdbcType.class)
	private Permission permission;

	@Convert(converter = NumericBooleanConverter.class)
	@Column(name = "IS_ACTIVE")
	@JsonIgnore
	private Boolean isActive;
}
