package com.mckesson.cgat.pde.usermanagement.model;

import java.util.HashMap;
import java.util.Map;

public enum AccountPartnerFunction {

	SOLD_TO("SH"), SHIP_TO("SP"), PAYER("PY"), BILL_TO("BP");

	private String partnerFunctionType;

	private static Map<String, AccountPartnerFunction> valueToEnumMap = new HashMap<>();

	private AccountPartnerFunction(String partnerFunctionType) {
		this.partnerFunctionType = partnerFunctionType;
	}

	static {
		for (AccountPartnerFunction e : values()) {
			valueToEnumMap.put(e.partnerFunctionType, e);
		}
	}

	public static AccountPartnerFunction valueFor(String label) {
		return valueToEnumMap.get(label);
	}

	public String getPartnerFunctionType() {
		return partnerFunctionType;
	}

}
