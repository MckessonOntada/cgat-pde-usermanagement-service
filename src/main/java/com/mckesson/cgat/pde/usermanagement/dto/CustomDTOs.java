package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
