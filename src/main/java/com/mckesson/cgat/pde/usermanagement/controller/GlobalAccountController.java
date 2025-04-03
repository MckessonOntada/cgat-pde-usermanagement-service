package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

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
			@RequestParam(required = false) List<String> distChannels,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) throws ResourceNotFoundException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		Page<Account> accounts = accountService.findAll(paging, query, null, null, null, AccountPartnerFunction.SOLD_TO,
				salesOrg, distChannels, channelType);

		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}

}
