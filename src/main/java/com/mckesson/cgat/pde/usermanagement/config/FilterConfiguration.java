package com.mckesson.cgat.pde.usermanagement.config;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Configuration
public class FilterConfiguration {

	public FilterConfiguration(ObjectMapper objectMapper) {
		SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider().setFailOnUnknownId(true);
		simpleFilterProvider.addFilter("PermissionFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "label",
				"shortLabel", "roleType", "rolePermissionMappings"));
		objectMapper.setFilterProvider(simpleFilterProvider);
	}

}