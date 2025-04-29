package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantsResponseDTO {

	private String variant;

	private String variantDescription;

	private String materialNumber;

	private Long therapyId;

}
