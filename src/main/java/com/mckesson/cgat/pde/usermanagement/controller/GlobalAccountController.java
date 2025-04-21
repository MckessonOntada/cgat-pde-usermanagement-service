package com.mckesson.cgat.pde.usermanagement.controller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.List;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@Tag(name = "Accounts", description = "Controller for getting all unlinked accounts.")
@RequestMapping(path = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalAccountController {

	private final AccountService accountService;

	private static final String SORT_ORDER_ASC = "asc";

	public GlobalAccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping
	@Operation(summary = "Fetch all unlinked Accounts.")
	public ResponseEntity<Page<Account>> findAllAvailableAccounts(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name_1") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> salesOrg,
			@RequestParam(required = false) List<String> distChannels) throws ResourceNotFoundException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		Page<Account> accounts = accountService.findAll(paging, query, null, null, null, AccountPartnerFunction.SOLD_TO,
				salesOrg, distChannels, ChannelType.BUY_AND_BILL);

		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}

	@RequirePermissions({ Permission.VIEW_CATALOG, Permission.MANAGE_CATALOG })
	@GetMapping("/for-organizations")
	@Operation(summary = "Fetch all Accounts based on filter provided with pagination capabilities.")
	Page<Account> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "sap_partner") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) List<String> salesOrg,
			@RequestParam(required = false) List<String> distChannels,
			@RequestParam AccountPartnerFunction partnerFunction,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType,
			@RequestParam List<Long> practiceIdList) throws UnauthorizedAccessException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return accountService.findAllByOrganization(paging, query, status, practiceIdList, partnerFunction, salesOrg,
				distChannels, channelType);
	}

	@GetMapping(value = "/download", produces = "text/csv")
	@RequirePermissions({ Permission.MANAGE_SP_CATALOG })
	@Operation(summary = "Download csv file contains all speciality pharma active accounts")
	public ResponseEntity<StreamingResponseBody> downloadSPAccountsCsvFile() {
		StreamingResponseBody stream = outputStream -> {
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
				writer.write("Account Number,Account Name");
				writer.newLine();

				for (Account account : accountService.getSPAccounts()) {
					writer.write(String.format("\"%s\",\"%s\"", account.getAccountNumber(), account.getAccountName()));
					writer.newLine();
				}

				writer.flush();
			}
		};

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "SPAccounts.csv")
			.contentType(MediaType.parseMediaType("text/csv"))
			.body(stream);
	}

}
