package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialCrossWalkConfigRecordDTO {

	@NotBlank(message = "SAP NDC is mandatory, please provide SAP NDC and try again.")
	@Size(max = 11, message = "SAP NDC must be at most 11 characters long")
	@Pattern(regexp = "^(|[0-9]{11})$", message = "SAP NDC must be numeric and exactly 11 digits")
	private String bnbNDC;

	@NotBlank(message = "SP Product ID is mandatory, please provide SP Product ID and try again.")
	@Size(max = 50, message = "SP Product ID must be at most 50 characters long")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "SP Product ID must be alphanumeric")
	private String spProductId;

	@NotBlank(message = "SP Product NDC is mandatory, please provide SP Product NDC and try again.")
	@Size(max = 11, message = "SP Product NDC must be at most 11 characters long")
	@Pattern(regexp = "^(|[0-9]{11})$", message = "SP Product NDC must be numeric and exactly 11 digits")
	private String spNDC;

}
