package com.mckesson.cgat.pde.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;
import java.util.Set;

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

	@Size(min = 2, max = 50, groups = BasicInfo.class)
	@Pattern(regexp = "^[a-zA-Z ]+$", message = "Role Name must only contain alphabets.", groups = BasicInfo.class)
	@NotEmpty(message = "Role Name cannot be null or empty", groups = BasicInfo.class)
	private String name;

	@Size(max = 100, groups = BasicInfo.class)
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

	@OneToOne(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private CommunicationPreferences communicationPreferences;

}
