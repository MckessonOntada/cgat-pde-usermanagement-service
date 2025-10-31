package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.DiagnosisCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantsResponseDTO {

	private String variant;

	private String variantDescription;

	private String materialNumber;

	private Long therapyId;

	private Set<DiagnosisCode> diagnosisCodes = new HashSet<>();

}
