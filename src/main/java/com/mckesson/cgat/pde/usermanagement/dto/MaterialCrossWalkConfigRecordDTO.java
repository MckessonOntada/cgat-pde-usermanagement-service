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

	@NotBlank(message = "Buy & Bill Material ID is mandatory, please provide Buy & Bill Material ID and try again.")
	@Size(max = 50, message = "Buy & Bill Material ID must be at most 50 characters long")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Buy & Bill Material ID must be alphanumeric")
	private String bnbMaterialNumber;

	@Size(max = 100, message = "Buy & Bill Material Description must be at most 100 characters long")
	private String bnbDescription;

	@Size(max = 11, message = "Buy & Bill Material NDC must be at most 11 characters long")
	@Pattern(regexp = "^[0-9]{11}$", message = "Buy & Bill Material NDC must be numeric and 11 digits")
	private String bnbNDC;

	@NotBlank(message = "SP Product ID is mandatory, please provide SP Product ID and try again.")
	@Size(max = 50, message = "SP Product ID must be at most 50 characters long")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "SP Product ID must be alphanumeric")
	private String spProductId;

	@Size(max = 100, message = "SP Product Name must be at most 100 characters long")
	private String spProductName;

	@Size(max = 11, message = "SP Product NDC must be at most 11 characters long")
	@Pattern(regexp = "^[0-9]{11}$", message = "SP Product NDC must be numeric and 11 digits")
	private String spNDC;

}
