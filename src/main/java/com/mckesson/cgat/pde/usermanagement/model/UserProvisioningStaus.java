package com.mckesson.cgat.pde.usermanagement.model;

import java.util.HashMap;
import java.util.Map;

public enum UserProvisioningStaus {

	PENDING("PENDING"), REJECTED("REJECTED"), APPROVED("APPROVED");

	private String userRequestType;

	private static Map<String, UserProvisioningStaus> valueToEnumMap = new HashMap<>();

	static {
		for (UserProvisioningStaus e : values()) {
			valueToEnumMap.put(e.userRequestType, e);
		}
	}

	private UserProvisioningStaus(String userRequestType) {
		this.userRequestType = userRequestType;
	}

	public static UserProvisioningStaus valueFor(String label) {
		return valueToEnumMap.get(label);
	}

	public String getUserRequestType() {
		return userRequestType;
	}

}
