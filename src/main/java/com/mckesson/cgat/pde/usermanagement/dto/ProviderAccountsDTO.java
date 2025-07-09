package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderAccountsDTO {

	@NotEmpty(message = "Provider Ids cannot be empty")
	List<Long> providerIds;

	@NotEmpty(message = "Account Ids cannot be empty")
	List<Long> accountIds;

}
