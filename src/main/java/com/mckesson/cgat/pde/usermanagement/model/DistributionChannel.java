package com.mckesson.cgat.pde.usermanagement.model;

public enum DistributionChannel {

	PL("20"), SD("40"), ALL("80");

	private final String code;

	DistributionChannel(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	/**
	 * Get enum constant by code.
	 * @param code The code to search for.
	 * @return The matching DistributionChannelEnum, or null if no match is found.
	 */
	public static DistributionChannel fromCode(String code) {
		for (DistributionChannel channel : values()) {
			if (channel.code.equals(code)) {
				return channel;
			}
		}
		throw new IllegalArgumentException("No enum constant for code: " + code);
	}

	@Override
	public String toString() {
		return this.name() + "(" + code + ")";
	}

}
