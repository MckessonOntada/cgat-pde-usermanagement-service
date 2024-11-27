package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.Practice;
import lombok.*;

public class CustomDTOs {

	@AllArgsConstructor
	@Data
	@NoArgsConstructor
	public static class PermissionDto {

		public String id;

		public String name;

		public String label;

		public boolean is_active;

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
