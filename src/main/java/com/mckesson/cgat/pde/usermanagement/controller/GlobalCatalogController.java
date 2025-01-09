package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponseDto;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Catalogs", description = "Controller for getting all catalogs.")
@RequestMapping(path = "/catalogs", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalCatalogController {

	private final TherapyService therapyService;

	public GlobalCatalogController(TherapyService therapyService) {
		this.therapyService = therapyService;
	}

	@GetMapping
	@Operation(summary = "Fetch all catalogs for provided soldto accounts lists.")
	public ResponseEntity<Page<CatalogPriceResponseDto>> findAllCatalogs(
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(required = false) String query,
			@NotEmpty(message = "Account Numbers can not be empty") @RequestParam List<String> accountNumbers) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return new ResponseEntity<>(therapyService.findAllGlobalCatalogs(accountNumbers, query, paging), HttpStatus.OK);
	}

}
