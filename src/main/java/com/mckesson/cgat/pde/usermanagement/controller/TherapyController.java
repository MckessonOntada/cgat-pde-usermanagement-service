package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Map;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.dto.*;
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
@Tag(name = "User-Context Therapies",
		description = "Provides therapy and catalog data from the perspective of the currently authenticated user.")
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

	private static final Map<String, String> SHIPPING_ACCOUNTS_SORTBY_MAPPING = Map.of("bnb_account_number",
			"ac.sap_partner", "sp_account_number", "id", "bnb_account_name", "ac.name_1", "sp_account_name", "name");

	public TherapyController(TherapyService therapyService) {
		this.therapyService = therapyService;
	}

	@GetMapping
	@Operation(summary = "Find all therapies available to the current user",
			description = "Retrieves a paginated list of all therapies that the currently authenticated user has access to.")
	public Page<TradeCatalogResponseDTO> findAllThreapies(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> salesOrg,
			@RequestParam(required = false) List<String> customerIdentifier,
			@RequestParam(defaultValue = "trade_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder) {
		Sort sort = CustomUtils.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return therapyService.findAllTherapies(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), query,
				salesOrg, paging, customerIdentifier, false);
	}

	@GetMapping("/ndc/{ndcNumber}/soldtoaccounts")
	@Operation(summary = "Find all sold-to accounts for a therapy available to the current user",
			description = "Retrieves a paginated list of all sold-to accounts that the currently authenticated user can use to order a specific therapy (identified by NDC).")
	public Page<AccountsResponseDTO> findAllSoldToAccountsByTherapy(@PathVariable String ndcNumber,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(required = false) String query,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String program,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType ndcChannelType) {

		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllSoldToAccounts(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()),
				ndcNumber, query, paging, channelType, program, ndcChannelType);

	}

	@GetMapping("/ndc/{ndcNumber}/soldtoaccount/{soldToAccountId}/shiptoaccounts")
	@Operation(summary = "Find all ship-to accounts for a therapy and sold-to account",
			description = "Retrieves a paginated list of all ship-to accounts that can be used for a specific therapy (identified by NDC) and sold-to account.")
	public Page<AccountsResponseDTO> findAllShipToAccountsForTherapy(@PathVariable String soldToAccountId,
			@PathVariable String ndcNumber, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@RequestParam(defaultValue = "sap_partner") String sortBy,
			@RequestParam(defaultValue = "desc") String sortOrder,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) {

		if (channelType == ChannelType.BUY_AND_BILL) {
			sortBy = SHIPPING_ACCOUNTS_SORTBY_MAPPING.getOrDefault(sortBy, "ac.sap_partner");
		}
		else {
			sortBy = SHIPPING_ACCOUNTS_SORTBY_MAPPING.getOrDefault(sortBy, "id");
		}

		Sort sort = CustomUtils.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return therapyService.findAllShipToAccounts(soldToAccountId, ndcNumber, paging, query, channelType);
	}

	@GetMapping("/{therapyId}")
	@Operation(summary = "Get a therapy by its ID",
			description = "Retrieves the details for a single therapy by its unique ID.")
	public Therapy findbyId(@PathVariable Long therapyId) throws ResourceNotFoundException {
		return therapyService.findById(therapyId);
	}

	@GetMapping("/therapyDetails/{therapyId}")
	@Operation(summary = "Get therapy details with crosswalk information by ID",
			description = "Retrieves the details for a single therapy, including its crosswalk information, by its unique ID.")
	public TherapyResponseDTO findTherapyWithCrosswalkById(@PathVariable Long therapyId)
			throws ResourceNotFoundException {
		return therapyService.findTherapyWithCrosswalkById(therapyId);
	}

	@GetMapping("/details/{tradeName}")
	@Operation(summary = "Get therapy details by trade name",
			description = "Retrieves a list of therapy details, identified by trade name and channel type.")
	public List<Therapy> findTherapyDetailsByTradeName(@PathVariable String tradeName,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) {
		return therapyService.findTherapyDetailsByTradeName(tradeName, channelType);
	}

	@GetMapping("{ndcNumber}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/variants")
	@Operation(summary = "Find all therapy variants for a specific NDC, sold-to, and ship-to account",
			description = "Retrieves a paginated list of all therapy variants available for a specific NDC, sold-to account, and ship-to account.")
	public Page<VariantsResponseDTO> findAllVariantsLinkedToShipToAccountsForNDC(
			@PathVariable @NotNull(message = "'soldToAccountId' cannot be null") @NotEmpty(
					message = "'soldToAccountId' cannot be empty") String soldToAccountId,
			@PathVariable @NotNull(message = "'shipToAccountId' cannot be null") @NotEmpty(
					message = "'shipToAccountId' cannot be empty") String shipToAccountId,
			@PathVariable @NotNull(message = "'ndcNumber' cannot be null") String ndcNumber,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllVariantsLinkedToShipToAccountsForNDC(soldToAccountId, shipToAccountId, ndcNumber,
				paging, channelType);
	}

	@GetMapping("/ndc/{ndcNumber}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/catalog")
	@Operation(summary = "Get the catalog price for a specific therapy variant",
			description = "Retrieves the catalog price for a specific therapy variant, based on the NDC, sold-to account, and ship-to account.")
	public CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForTherapy(
			@PathVariable @NotNull(message = "'soldToAccountId' cannot be null") @NotEmpty(
					message = "'soldToAccountId' cannot be empty") String soldToAccountId,
			@PathVariable @NotNull(message = "'shipToAccountId' cannot be null") @NotEmpty(
					message = "'shipToAccountId' cannot be empty") String shipToAccountId,
			@RequestParam(required = false) String variantType,
			@PathVariable @NotNull(message = "'Ndc number' cannot be null") String ndcNumber) {
		return therapyService.findPriceOfVariantLinkedToShipToAccountsForNdc(soldToAccountId, shipToAccountId,
				variantType, ndcNumber);
	}

	@GetMapping("/ndc/{ndcNumber}/programs-channels")
	@Operation(summary = "Find all programs and channels for a therapy available to the current user",
			description = "Retrieves a list of all programs and channels that the currently authenticated user can use to order a specific therapy (identified by NDC).")
	public ResponseEntity<ProgramChannelResponseDTO> findAllProgramAndChannels(@PathVariable String ndcNumber,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) throws ResourceNotFoundException {
		return new ResponseEntity<>(therapyService.findAllProgramAndChannels(
				Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), ndcNumber, channelType), HttpStatus.OK);
	}

	@GetMapping("/customeridentifiers")
	@Operation(summary = "Find all customer identifiers (sold-to accounts) available to the current user",
			description = "Retrieves a paginated list of all customer identifiers (sold-to accounts) that the currently authenticated user has access to.")
	public ResponseEntity<Page<AccountsResponseDTO>> findAllCustomerIdentifiers(
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(required = false) String query) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return new ResponseEntity<>(therapyService.findAllCustomerIdentifiers(
				Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), query, paging), HttpStatus.OK);
	}

}
