package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Map;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Therapy", description = "Controller for Managing Therapy-catalogs operations and data.")
@RequestMapping(path = "/practices/{practiceId}/users/current/therapies", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Validated
public class TherapyController {

	private final TherapyService therapyService;

	// private static final Map<String, List<String>> CHANNEL_MAPPING = Map.of("MPB",
	// List.of("20", "30", "40", "50", "60"), "Speciality", List.of("10"), "3PL",
	// List.of("10"), "All",
	// List.of("10", "20", "30", "40", "50", "60"));

	public TherapyController(TherapyService therapyService) {
		this.therapyService = therapyService;
	}

	@GetMapping
	@Operation(
			summary = "Find all therapies being offered via catalog for all accounts that the logged in user has access")
	public Page<TherapyCatalogResponseDTO> findAllThreapies(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> salesOrg,
			@RequestParam(required = false) List<String> customerIdentifier,
			@RequestParam(defaultValue = "material_description") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder) {
		Sort sort = CustomUtils.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return therapyService.findAllTherapies(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), query,
				salesOrg, paging, customerIdentifier);
	}

	@GetMapping("/{therapyId}/soldtoaccounts")
	@Operation(
			summary = "Find all soldto accounts from which the selected therapy is being offered, and the logged in user has access to")
	public Page<AccountsResponseDTO> findAllSoldToAccountsByTherapy(@PathVariable String therapyId,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(required = false) String query,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String program,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType therapyChannelType) {

		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllSoldToAccounts(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()),
				therapyId, query, paging, channelType, program, therapyChannelType);

	}

	@GetMapping("{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccounts")
	@Operation(
			summary = "Find all shipto accounts that is linked to the selected soldto account and the therapy also being offered from that shipto account")
	public Page<AccountsResponseDTO> findAllShipToAccountsForTherapy(@PathVariable String soldToAccountId,
			@PathVariable String therapyId, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllShipToAccounts(soldToAccountId, therapyId, paging, query, channelType);
	}

	@GetMapping("/{therapyId}")
	@Operation(summary = "Find Therapy by id")
	public Therapy findbyId(@PathVariable Long therapyId) throws ResourceNotFoundException {
		return therapyService.findById(therapyId);
	}

	@GetMapping("/therapyDetails/{therapyId}")
	@Operation(summary = "Find Therapy Details with crosswalk by id")
	public TherapyResponseDTO findTherapyWithCrosswalkById(@PathVariable Long therapyId)
			throws ResourceNotFoundException {
		return therapyService.findTherapyWithCrosswalkById(therapyId);
	}

	@GetMapping("{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/variants")
	@Operation(
			summary = "Find all variants of therapy that is linked to the selected soldto account,shipto account and the therapy being offered from that shipto account")
	public Page<VariantsResponseDTO> findAllVariantsLinkedToShipToAccountsForTherapy(
			@PathVariable @NotNull(message = "'soldToAccountId' cannot be null") @NotEmpty(
					message = "'soldToAccountId' cannot be empty") String soldToAccountId,
			@PathVariable @NotNull(message = "'shipToAccountId' cannot be null") @NotEmpty(
					message = "'shipToAccountId' cannot be empty") String shipToAccountId,
			@PathVariable @NotNull(message = "'therapyId' cannot be null") String therapyId,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllVariantsLinkedToShipToAccountsForTherapy(soldToAccountId, shipToAccountId,
				therapyId, paging, channelType);
	}

	@GetMapping("{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/catalog")
	@Operation(
			summary = "Find price of the variant of therapy that is linked to the selected soldto account,shipto account and the therapy being offered from that shipto account")
	public CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForTherapy(
			@PathVariable @NotNull(message = "'soldToAccountId' cannot be null") @NotEmpty(
					message = "'soldToAccountId' cannot be empty") String soldToAccountId,
			@PathVariable @NotNull(message = "'shipToAccountId' cannot be null") @NotEmpty(
					message = "'shipToAccountId' cannot be empty") String shipToAccountId,
			@RequestParam(required = false) String variantType,
			@PathVariable @NotNull(message = "'therapyId' cannot be null") String therapyId) {
		return therapyService.findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccountId, shipToAccountId,
				variantType, therapyId);
	}

	@GetMapping("/{therapyId}/programs-channels")
	@Operation(
			summary = "Find all programs and channels from which the selected therapy is being offered, and the logged in user has access to")
	public ResponseEntity<ProgramChannelResponseDTO> findAllProgramAndChannels(@PathVariable String therapyId,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) {
		return new ResponseEntity<>(therapyService.findAllProgramAndChannels(
				Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), therapyId, channelType), HttpStatus.OK);
	}

	@GetMapping("/customeridentifiers")
	@Operation(
			summary = "Find all Customer Identifiers or soldto accounts from which therapies are being offered, and the logged in user has access to")
	public ResponseEntity<Page<AccountsResponseDTO>> findAllCustomerIdentifiers(
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(required = false) String query) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return new ResponseEntity<>(therapyService.findAllCustomerIdentifiers(
				Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), query, paging), HttpStatus.OK);
	}

}
