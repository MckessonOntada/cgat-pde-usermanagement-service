package com.mckesson.cgat.pde.usermanagement.config;

import com.mckesson.jbat.auth.module.security.GenericFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Configuration
public class FilterConfiguration {

	private int FILTER_ORDER = 1;

	@Value("${filter.enabled:true}")
	private boolean enableLoginFilter;

	public FilterConfiguration(ObjectMapper objectMapper) {
		SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider().setFailOnUnknownId(true);
		simpleFilterProvider.addFilter("PermissionFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "label",
				"shortLabel", "roleType", "rolePermissionMappings"));
		objectMapper.setFilterProvider(simpleFilterProvider);
	}

	@Bean
	public FilterRegistrationBean<GenericFilter> registration(GenericFilter genericFilter) {
		FilterRegistrationBean<GenericFilter> registration = new FilterRegistrationBean<>(genericFilter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean<GeneralFilter> generalFilterFilterRegistrationBean(
			GeneralFilter generalFilter) {
		FilterRegistrationBean<GeneralFilter> registrationBean = new FilterRegistrationBean<>(generalFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(FILTER_ORDER);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<UserManagementFilter> userManagementFilterFilterRegistrationBean(
			UserManagementFilter userManagementFilter) {
		FilterRegistrationBean<UserManagementFilter> registrationBean = new FilterRegistrationBean<>(
				userManagementFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(++FILTER_ORDER);
		registrationBean.setEnabled(enableLoginFilter);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<TenantFilter> tenantFilterFilterRegistrationBean(TenantFilter tenantFilter) {
		FilterRegistrationBean<TenantFilter> registrationBean = new FilterRegistrationBean<>(tenantFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(++FILTER_ORDER);
		return registrationBean;
	}

}