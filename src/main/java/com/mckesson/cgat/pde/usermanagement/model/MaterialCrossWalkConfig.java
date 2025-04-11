package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "material_cross_walk_config", schema = "catalog")
public class MaterialCrossWalkConfig implements Serializable {

	@Id
	@GeneratedValue(generator = "material_cross_walk_config_id_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "material_cross_walk_config_id_seq",
			sequenceName = "catalog.material_cross_walk_config_id_seq", allocationSize = 1)
	private Long id;

	@Size(max = 50)
	@Column(name = "bnb_material_number", nullable = false)
	private String bnbMaterialNumber;

	@Size(max = 50)
	@Column(name = "sp_product_id", nullable = false)
	private String spProductId;

}
