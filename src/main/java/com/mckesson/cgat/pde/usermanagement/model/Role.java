package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Role extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_sequence")
	@SequenceGenerator(name = "role_sequence", sequenceName = "role_sequence", allocationSize = 1)
	private Long id;

	@NotEmpty(message = "Role Name cannot be Empty")
	@Column(unique = true)
	private String name;

	private String description;

	@ManyToOne
	@JoinColumn(name = "practice_id")
	private Practice practiceId;

	@Convert(converter = NumericBooleanConverter.class)
	@JsonIgnore
	@JsonProperty("isActive")
	private Boolean isActive = false;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_permissions_mapping", joinColumns = @JoinColumn(name = "role_id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private Set<Permission> permissions;

}
