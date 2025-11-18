package com.mckesson.cgat.pde.usermanagement.model;

import com.mckesson.cgat.pde.usermanagement.dto.TherapyConfigDTO;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "therapy_config", schema = "catalog")
public class TherapyConfigs implements Serializable {

	@Id
	@GeneratedValue(generator = "therapy_config_id_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "therapy_config_id_seq", sequenceName = "catalog.therapy_config_id_seq",
			allocationSize = 1)
	private Long id;

	@Size(max = 18)
	@Column(name = "ndc_number", nullable = false, unique = true)
	private String ndcNumber;

	@Column(name = "config", columnDefinition = "jsonb")
	@Type(JsonType.class)
	private TherapyConfigDTO config;

	@Enumerated(EnumType.STRING)
	private ChannelType channelType;

	@Column(name = "min_quantity", nullable = false, columnDefinition = "SMALLINT DEFAULT 1")
	private Integer minQuantity = 1;

	@Column(name = "max_quantity", nullable = false, columnDefinition = "SMALLINT DEFAULT 99")
	private Integer maxQuantity = 99;

}
