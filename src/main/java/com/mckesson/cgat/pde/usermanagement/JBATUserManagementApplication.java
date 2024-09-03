package com.mckesson.cgat.pde.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@ComponentScan(basePackages = "com.mckesson.jbat")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
@EnableJpaAuditing
public class JBATUserManagementApplication {
	
    public static void main(String[] args) {
        SpringApplication.run(JBATUserManagementApplication.class, args);
    }


}
