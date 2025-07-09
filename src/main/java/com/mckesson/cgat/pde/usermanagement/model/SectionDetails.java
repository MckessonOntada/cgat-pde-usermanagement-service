package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionDetails {

	@NotBlank
	private String sectionName;

	@NotEmpty
	private List<Object> questions;

}
