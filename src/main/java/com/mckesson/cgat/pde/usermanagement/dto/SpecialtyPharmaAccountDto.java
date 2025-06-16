package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyPharmaAccountDto {

	private String accountNumber;

	private String accountName;

	private String salesOrg;

	private String houseNumber;

	private String street;

	private String street2;

	private String street3;

	private String city;

	private String district;

	private String state;

	private String zipCode;

	private String country;

	private String telephone;

	private String email;

	private String fax;

}
