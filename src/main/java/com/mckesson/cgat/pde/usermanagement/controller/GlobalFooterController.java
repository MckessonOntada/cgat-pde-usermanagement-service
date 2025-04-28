package com.mckesson.cgat.pde.usermanagement.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.concurrent.TimeUnit;

@RestController
@Tag(name = "Global Footer", description = "Controller for global footer configuration")
@RequestMapping(path = "/global-footer", produces = MediaType.APPLICATION_JSON_VALUE)
public class GlobalFooterController {

	private final JdbcTemplate jdbcTemplate;

	public GlobalFooterController(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@GetMapping
	@Operation(summary = "Get footer configuration (latest version)")
	public ResponseEntity<String> getFooterConfig() {
		String footerJson = jdbcTemplate.queryForObject(
				"SELECT config_json FROM usermanagement.footer_config ORDER BY version DESC LIMIT 1", String.class);

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(footerJson);
	}

}