package com.mckesson.cgat.pde.usermanagement.utils;

import java.util.*;
import java.util.stream.Collectors;

public class SalesOrgUtils {

	private static final Map<String, List<String>> SALES_ORG_MAP = new HashMap<>();

	static {
		SALES_ORG_MAP.put("MPB", Collections.singletonList("8545"));
		SALES_ORG_MAP.put("3PL", Arrays.asList("1100", "2"));
		SALES_ORG_MAP.put("SD", Collections.singletonList("1000"));
		SALES_ORG_MAP.put("NA", Collections.singletonList("NA"));
	}

	public static String getNameByCode(String code) {
		if (code == null)
			return null;

		// Handle special case: codes starting with "2" can map to 3PL
		if (code.startsWith("2")) {
			return "3PL";
		}

		// Search for exact code match
		for (Map.Entry<String, List<String>> entry : SALES_ORG_MAP.entrySet()) {
			if (entry.getValue().contains(code)) {
				return entry.getKey();
			}
		}

		return "NA";
	}

	public static List<String> convertNamesToQueryCodes(List<String> names) {
		if (names == null || names.isEmpty()) {
			return new ArrayList<>();
		}

		List<String> result = new ArrayList<>();

		for (String name : names) {
			if (name == null)
				continue;

			if ("3PL".equals(name)) {
				result.add("1100");
			}
			else {
				List<String> codes = SALES_ORG_MAP.get(name);
				if (codes != null) {
					result.addAll(codes);
				}
			}
		}

		return result.stream().distinct().collect(Collectors.toList());
	}

}
