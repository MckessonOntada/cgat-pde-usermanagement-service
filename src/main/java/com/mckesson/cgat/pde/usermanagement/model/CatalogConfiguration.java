package com.mckesson.cgat.pde.usermanagement.model;

import com.mckesson.cgat.pde.usermanagement.dto.StepDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "catalog_configuration", schema = "catalog",
		indexes = { @Index(name = "idx_material_variant_shipto_soldto",
				columnList = "materialId, variant, shipToAccount, soldToAccount") },
		uniqueConstraints = { @UniqueConstraint(name = "Configuration is already present for this combination",
				columnNames = { "materialId", "variant", "shipToAccount", "soldToAccount" }) })

public class CatalogConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String materialId;

	private String variant;

	@Column(nullable = false)
	private String shipToAccount;

	@Column(nullable = false)
	private String soldToAccount;

	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private List<StepDTO> steps;

}
