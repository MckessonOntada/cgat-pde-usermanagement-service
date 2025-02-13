package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.DistributionChannel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgramChannelResponseDTO {

	private Set<String> programs;

	private Set<String> channels;

}
