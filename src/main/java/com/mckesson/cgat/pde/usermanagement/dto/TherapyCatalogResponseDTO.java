package com.mckesson.cgat.pde.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.determineDropshipIndicator;

public interface TherapyCatalogResponseDTO {

	String getId();

	String getTherapyId();

	String getTherapyDescription();

	String getNdc();

	String getStrength();

	String getDosage();

	String getChannelType();

}
