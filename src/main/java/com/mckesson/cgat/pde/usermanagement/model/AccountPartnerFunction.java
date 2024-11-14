package com.mckesson.cgat.pde.usermanagement.model;

import java.util.HashMap;
import java.util.Map;

public enum AccountPartnerFunction {

	SOLD_TO("SH"), SHIP_TO("SP"), PAYER("PY");

	private String value;

	private static Map<String, AccountPartnerFunction> valueToEnumMap = new HashMap<>();

	private AccountPartnerFunction(String value) {
		this.value = value;
	}

	static {
		for (AccountPartnerFunction e : values()) {
			valueToEnumMap.put(e.value, e);
		}
	}

	public static AccountPartnerFunction valueFor(String label) {
		return valueToEnumMap.get(label);
	}

	public String getValue() {
		return value;
	}
}
