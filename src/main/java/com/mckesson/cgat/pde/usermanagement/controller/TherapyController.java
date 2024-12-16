package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Therapy", description = "Controller for Managing Therapy-catalogs operations and data.")
@RequestMapping(path = "/practices/{practiceId}/users/current/therapies", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class TherapyController {

	private final TherapyService therapyService;

	public TherapyController(TherapyService therapyService) {
		this.therapyService = therapyService;
	}

	@GetMapping
	@Operation(
			summary = "Find all therapies being offered via catalog for all accounts that the logged in user has access")
	public Page<TherapyCatalogResponseDTO> findAllThreapies(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> salesOrg,
			@RequestParam(required = false) List<String> customerIdentifier) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllTherapies(UserSecurityUtils.getLoggedInUser().getId(), query, salesOrg, paging,
				customerIdentifier);
	}

	@GetMapping("/{therapyId}/soldtoaccounts")
	@Operation(
			summary = "Find all soldto accounts from which the selected therapy is being offered, and the logged in user has access to")
	public Page<AccountsResponseDTO> findAllSoldToAccountsByTherapy(@PathVariable Long therapyId,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(required = false) String query,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(required = false) String distributionChannel) {

		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllSoldToAccounts(UserSecurityUtils.getLoggedInUser().getId(), therapyId, query,
				paging, distributionChannel);
	}

	@GetMapping("{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccounts")
	@Operation(
			summary = "Find all shipto accounts that is linked to the selected soldto account and the therapy also being offered from that shipto account")
	public Page<AccountsResponseDTO> findAllShipToAccountsForTherapy(@PathVariable String soldToAccountId,
			@PathVariable Long therapyId, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllShipToAccounts(soldToAccountId, therapyId, paging, query);
	}

	@GetMapping("/{therapyId}")
	@Operation(summary = "Find Therapy by id")
	public Therapy findbyId(@PathVariable Long therapyId) throws ResourceNotFoundException {
		return therapyService.findById(therapyId);
	}

}
