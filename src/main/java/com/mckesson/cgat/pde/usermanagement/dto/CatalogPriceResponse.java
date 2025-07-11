package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CatalogPriceResponse {

	private CatalogPrice catalogPrice;

	private String productLevelPricing;

	private String genericName;

	private String manufacturer;

	private String dosage;

}
