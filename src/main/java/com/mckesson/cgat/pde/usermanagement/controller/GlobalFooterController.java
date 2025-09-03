package com.mckesson.cgat.pde.usermanagement.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Tag(name = "Global Footer", description = "Provides the global footer configuration for the application UI.")
@RequestMapping(path = "/global-config", produces = MediaType.APPLICATION_JSON_VALUE)
public class GlobalFooterController {

	private final JdbcTemplate jdbcTemplate;

	public GlobalFooterController(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@GetMapping
	@Operation(summary = "Get the global footer configuration (cached)",
			description = "Retrieves the global footer configuration from the database. The response is cached for 30 minutes.")
	public ResponseEntity<String> getFooterConfig() {
		Map<String, Object> result = jdbcTemplate
			.queryForMap("SELECT config_json::text FROM usermanagement.footer_config WHERE id = 1");
		String footerJson = (String) result.get("config_json");

		return ResponseEntity.ok()
			.cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
			.contentType(MediaType.APPLICATION_JSON) // Ensure proper content type
			.body(footerJson);
	}

}