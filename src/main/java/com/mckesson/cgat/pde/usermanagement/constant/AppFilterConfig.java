package com.mckesson.cgat.pde.usermanagement.constant;

import org.springframework.context.annotation.Configuration;

import com.mckesson.jbat.auth.module.security.GenericFilterConfigurations;

@Configuration
public class AppFilterConfig implements GenericFilterConfigurations {

	@Override
	public String[] getPatternsToExclude() {
		return new String[] { "/**" };
	}

}
