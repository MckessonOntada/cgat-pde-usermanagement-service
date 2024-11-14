package com.mckesson.cgat.pde.usermanagement.utils;

public class OrganizationContext {

	private static final ThreadLocal<Long> currentOrgId = new ThreadLocal<>();

	public static void setCurrentOrgId(Long tenantId) {
		currentOrgId.set(tenantId);
	}

	public static Long getCurrentOrgId() {
		return currentOrgId.get();
	}

	public static void clear() {
		currentOrgId.remove();
	}

}
