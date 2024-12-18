package com.mckesson.cgat.pde.usermanagement.model;

import java.util.HashMap;
import java.util.Map;

public enum DistributionChannel {

	PL("20"), SD("40"), ALL("80");

	private String distributionChannelValue;

	private static Map<String, DistributionChannel> valueToEnumMap = new HashMap<>();

	private DistributionChannel(String distributionChannel) {
		this.distributionChannelValue = distributionChannel;
	}

	static {
		for (DistributionChannel e : values()) {
			valueToEnumMap.put(e.distributionChannelValue, e);
		}
	}

	public static DistributionChannel valueFor(String label) {
		return valueToEnumMap.get(label);
	}

	public String getDistributionChannelValue() {
		return distributionChannelValue;
	}

}
