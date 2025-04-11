package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrosswalkResponseDTO {

	private Long id;

	private String bnbMaterialNumber;

	private String bnbMaterialDescription;

	private String bnbNdc;

	private String spProductId;

	private String spProductName;

	private String spNdc;

}
