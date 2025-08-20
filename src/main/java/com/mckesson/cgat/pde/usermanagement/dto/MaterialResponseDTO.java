package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.SPTherapyConfig;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.model.TherapyConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder
public class MaterialResponseDTO implements Serializable {

	private Therapy therapy;

	private TherapyConfig materialConfig;

	private SPTherapyConfig spTherapyConfig;

	public MaterialResponseDTO(Therapy therapy, TherapyConfig materialConfig) {
		this.therapy = therapy;
		this.materialConfig = materialConfig;
	}

	public MaterialResponseDTO(Therapy therapy, SPTherapyConfig spTherapyConfig) {
		this.therapy = therapy;
		this.spTherapyConfig = spTherapyConfig;
	}

	public MaterialResponseDTO(Therapy therapy, TherapyConfig materialConfig, SPTherapyConfig spTherapyConfig) {
		this.therapy = therapy;
		this.materialConfig = materialConfig;
		this.spTherapyConfig = spTherapyConfig;
	}

}
