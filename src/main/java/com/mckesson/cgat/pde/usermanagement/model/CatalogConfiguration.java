package com.mckesson.cgat.pde.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
		indexes = { @Index(name = "idx_catalog_configuration_composite_key",
				columnList = "material_id, variant, ship_to_account, sold_to_account") })
public class CatalogConfiguration {

	@EmbeddedId
	@Column(nullable = false)
	@JsonUnwrapped
	private CatalogConfigId id;

	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private List<StepDTO> steps;

}
