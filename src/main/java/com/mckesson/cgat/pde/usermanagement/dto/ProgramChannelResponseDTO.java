package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramChannelResponseDTO {

	List<String> programs = new ArrayList<>();

	List<String> channels = new ArrayList<>();

}
