package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.Practice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CustomDTOs {

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
