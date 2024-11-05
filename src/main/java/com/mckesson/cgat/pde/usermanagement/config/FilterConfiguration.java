package com.mckesson.cgat.pde.usermanagement.config;

import com.mckesson.jbat.auth.module.security.GenericFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Configuration
public class FilterConfiguration {

	private final int filterOrder = 100;

	public FilterConfiguration(ObjectMapper objectMapper) {
		SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider().setFailOnUnknownId(true);
		simpleFilterProvider.addFilter("PermissionFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "label",
				"shortLabel", "roleType", "rolePermissionMappings"));
		objectMapper.setFilterProvider(simpleFilterProvider);
	}

	@Bean()
	public FilterRegistrationBean<GenericFilter> registration(GenericFilter genericFilter) {
		FilterRegistrationBean<GenericFilter> registration = new FilterRegistrationBean<>(genericFilter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean<UserManagementFilter> userManagementFilterFilterRegistrationBean() {
		FilterRegistrationBean<UserManagementFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new UserManagementFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(filterOrder + 1);
		return registrationBean;
	}

}