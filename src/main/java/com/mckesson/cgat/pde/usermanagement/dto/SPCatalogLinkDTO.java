package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SPCatalogLinkDTO {

	@NotBlank(message = "Account Number is required")
	@Size(max = 20, message = "Account Number must be a maximum of 20 characters")
	@Pattern(regexp = ACCOUNT_REGEX, message = "Account Number must be numeric")
	private String accountNumber;

	@NotBlank(message = "SP Product ID is required")
	@Size(max = 50, message = "SP Product ID must be a maximum of 50 characters")
	@Pattern(regexp = PRODUCT_ID_REGEX, message = "SP Product ID must be alphanumeric")
	private String productId;

	@NotBlank(message = "Status is required")
	@Size(max = 6, message = "Status must be a maximum of 6 characters")
	@Pattern(regexp = STATUS_REGEX, message = "Status must be 'Link' or 'Unlink'")
	private String status;

}
