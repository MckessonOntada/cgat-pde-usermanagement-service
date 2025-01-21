package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

public enum QuestionnaireConfigurationType implements Serializable{

	A("Account"), C("Catalog"), M("Material");

	private final String type;

	QuestionnaireConfigurationType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	/**
	 * Get enum constant by type.
	 * 
	 * @param code The code to search for.
	 * @return The matching DistributionChannelEnum, or null if no match is found.
	 */
	public static QuestionnaireConfigurationType fromType(String type) {
		for (QuestionnaireConfigurationType configType : values()) {
			if (configType.type.equals(type)) {
				return configType;
			}
		}
		throw new IllegalArgumentException("No enum constant for config type: " + type);
	}

	@Override
	public String toString() {
		return this.name() + "(" + type + ")";
	}
}
