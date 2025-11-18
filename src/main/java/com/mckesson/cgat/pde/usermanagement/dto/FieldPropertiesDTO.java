package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;

@Data
public class FieldPropertiesDTO {

	private boolean displayOnForm;

	private boolean mandatoryField;

	private boolean showHelpIcon;

	private String displayName;

	private String errorMessage;

	private String helpInfoMessage;

	private FieldFormatDTO fieldFormat;

}