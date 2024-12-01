package com.mckesson.cgat.pde.usermanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import com.mckesson.cgat.pde.usermanagement.model.Practice;
import lombok.*;


public class CustomDTOs {

	@AllArgsConstructor
	@Data
	public static class TherapyCatalogResponseDTO {

		private String therapyId;

		private String therapyDescription;

		private String ndc;

		private String udi;

	}

	@AllArgsConstructor
	@Data
	public static class AccountsResponseDTO {

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

	@AllArgsConstructor
	@Data
	@NoArgsConstructor
	@Getter
	@Setter
	public static class GlobalPracticeResponseDto {

		private Long id;

		private String name;

		public static GlobalPracticeResponseDto convert(Practice practice) {
			GlobalPracticeResponseDto dto = new GlobalPracticeResponseDto();
			dto.setId(practice.getId());
			dto.setName(practice.getName());
			return dto;
		}

	}

}
