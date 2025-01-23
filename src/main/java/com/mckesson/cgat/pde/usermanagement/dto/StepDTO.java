package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class StepDTO implements Serializable {

	private String stepName;

	private String description;

}
