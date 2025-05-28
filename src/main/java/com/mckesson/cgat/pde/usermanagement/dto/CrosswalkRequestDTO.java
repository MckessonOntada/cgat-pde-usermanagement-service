package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class CrosswalkRequestDTO {

	private String bnbMaterialNdc;

	private List<String> spProductNdcs;

}
