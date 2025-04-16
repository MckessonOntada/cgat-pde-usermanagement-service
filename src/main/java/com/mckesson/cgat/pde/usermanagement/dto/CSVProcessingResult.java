package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class CSVProcessingResult {

	private List<String[]> errors;

	private byte[] errorFileContent;

}
