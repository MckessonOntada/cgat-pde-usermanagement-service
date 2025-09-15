package com.mckesson.cgat.pde.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import com.mckesson.jbat.common.util.service.AzureBlobContainerFileUploadService;

@ComponentScan(basePackages = "com.mckesson.cgat.pde")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
@Import(AzureBlobContainerFileUploadService.class)
@EnableJpaAuditing
public class CGATUserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CGATUserManagementApplication.class, args);

	}

}
