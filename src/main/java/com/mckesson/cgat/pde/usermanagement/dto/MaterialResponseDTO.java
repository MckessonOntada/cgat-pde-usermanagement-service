package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.SPTherapyConfigs;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.model.TherapyConfigs;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder
public class MaterialResponseDTO implements Serializable {

	private Therapy therapy;

	private TherapyConfigs materialConfig;

	private SPTherapyConfigs spTherapyConfig;

	public MaterialResponseDTO(Therapy therapy, TherapyConfigs materialConfig) {
		this.therapy = therapy;
		this.materialConfig = materialConfig;
	}

	public MaterialResponseDTO(Therapy therapy, SPTherapyConfigs spTherapyConfig) {
		this.therapy = therapy;
		this.spTherapyConfig = spTherapyConfig;
	}

	public MaterialResponseDTO(Therapy therapy, TherapyConfigs materialConfig, SPTherapyConfigs spTherapyConfig) {
		this.therapy = therapy;
		this.materialConfig = materialConfig;
		this.spTherapyConfig = spTherapyConfig;
	}

}
