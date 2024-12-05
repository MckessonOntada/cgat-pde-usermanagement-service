package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class TherapyCatalogResponseDTO {

	private String therapyId;

	private String therapyDescription;

	private String ndc;

	private String udi;

}
