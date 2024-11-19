package com.mckesson.cgat.pde.usermanagement.config;

import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIDResolver implements
		CurrentTenantIdentifierResolver<Long>/* , HibernatePropertiesCustomizer */ {

	/*
	 * @Override public void customize(Map<String, Object> hibernateProperties) {
	 * hibernateProperties.put(MultiTenancySettings. MULTI_TENANT_IDENTIFIER_RESOLVER,
	 * this); }
	 */

	@Override
	public @UnknownKeyFor @NonNull @Initialized Long resolveCurrentTenantIdentifier() {
		return OrganizationContext.getCurrentOrgId();
	}

	@Override
	public @UnknownKeyFor @NonNull @Initialized boolean validateExistingCurrentSessions() {
		return false;
	}

}
