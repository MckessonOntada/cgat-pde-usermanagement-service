package com.mckesson.cgat.pde.usermanagement.constant;


import com.mckesson.jbat.auth.module.security.GenericFilterConfigurations;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppFilterConfig implements GenericFilterConfigurations {


    @Override
    public String[] getPatternsToExclude() {
        return new String[]{
                "/public/*", "/inlineHooks/*"
        };
    }
}
