package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

	@Size(min = 2, max = 50)
	@Pattern(regexp = "^[a-zA-Z ]+$", message = "Role Name must only contain alphabets.")
	private String name;

	@Size(max = 100)
	private String description;

	@ManyToOne
	@JoinColumn(name = "practice_id")
	@JsonIgnore
	// @TenantId
	private Practice practiceId;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "role_permissions_mapping", joinColumns = @JoinColumn(name = "role_id"))
	@Column(name = "permission", nullable = false)
	@Enumerated(EnumType.STRING)
	private Set<Permission> permissions;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

}
