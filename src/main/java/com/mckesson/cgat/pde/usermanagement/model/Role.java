package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;
import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonIgnore
//	@TenantId
	private Practice practiceId;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "role_permissions_mapping", joinColumns = @JoinColumn(name = "role_id"))
	@Column(name = "permission", nullable = false)
	@Enumerated(EnumType.STRING)
	private Set<Permission> permissions;

	@Convert(converter = NumericBooleanConverter.class)
	@JsonIgnore
	@JsonProperty("isActive")
	private Boolean isActive = true;

}
