package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class AccountsResponseDTO {

	private String id;

	private String accountNumber;

	private String accountName;

	private String houseNumber;

	private String street;

	private String street2;

	private String street3;

	private String telephone;

	private String city;

	private String district;

	private String state;

	private String country;

	private String zipCode;

}
