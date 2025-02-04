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

	@Value("${filter.enabled:true}")
	private boolean enableLoginFilter;

	@Value("${filter.enableDevLoginFilter:false}")
	private boolean enableDevLoginFilter;

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
	public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean(CorsFilter corsFilter) {
		FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>(corsFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<CGATBaseFilter> cgatBaseFilterFilterRegistrationBean(CGATBaseFilter CGATBaseFilter) {
		FilterRegistrationBean<CGATBaseFilter> registrationBean = new FilterRegistrationBean<>(CGATBaseFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<DevUserAuthenticationBaseFilter> devUserAuthenticationFilterFilterRegistrationBean(
			DevUserAuthenticationBaseFilter devUserAuthenticationBaseFilter) {
		FilterRegistrationBean<DevUserAuthenticationBaseFilter> registrationBean = new FilterRegistrationBean<>(
				devUserAuthenticationBaseFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(3);
		registrationBean.setEnabled(enableDevLoginFilter);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<UserAuthenticationBaseFilter> userAuthenticationFilterFilterRegistrationBean(
			UserAuthenticationBaseFilter userAuthenticationFilter) {
		FilterRegistrationBean<UserAuthenticationBaseFilter> registrationBean = new FilterRegistrationBean<>(
				userAuthenticationFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(4);
		registrationBean.setEnabled(!enableDevLoginFilter);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<TenantBaseFilter> tenantFilterFilterRegistrationBean(TenantBaseFilter tenantFilter) {
		FilterRegistrationBean<TenantBaseFilter> registrationBean = new FilterRegistrationBean<>(tenantFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(5);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<PracticeValidityFilter> practiceFilterRegistrationBean(
			PracticeValidityFilter practiceFilter) {
		FilterRegistrationBean<PracticeValidityFilter> registrationBean = new FilterRegistrationBean<>(practiceFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(6);
		return registrationBean;
	}

}