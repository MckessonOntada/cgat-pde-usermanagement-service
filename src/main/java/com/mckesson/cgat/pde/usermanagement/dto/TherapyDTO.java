package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TherapyDTO {

	private String id;

	private String materialNumber;

	private String materialDescription;

	private String ndc;

	private String strength;

	private String dosage;

	private String genericName;

	private String manufacturerName;

}
