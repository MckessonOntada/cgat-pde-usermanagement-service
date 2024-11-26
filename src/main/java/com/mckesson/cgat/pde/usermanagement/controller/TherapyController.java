package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.ShipToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.SoldToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import com.mckesson.cgat.pde.usermanagement.utils.WebClientUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@Tag(name = "Therapy", description = "Controller for Managing Therapy-catalogs operations and data.")
@RequestMapping(path = "/practices/{practiceId}/users/{userId}/therapies", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class TherapyController {

	private final TherapyService therapyService;

	private final WebClient webClient;

	private final WebClientUtils webClientUtils;

	@Value("${usermanagementservice.base.url}")
	private String userServiceBaseUrl;

	private ObjectMapper objectMapper;

	public TherapyController(TherapyService therapyService, WebClient.Builder webClientBuilder,
			WebClientUtils webClientUtils, ObjectMapper objectMapper) {
		this.therapyService = therapyService;
		this.webClient = webClientBuilder.baseUrl("").build();
		this.webClientUtils = webClientUtils;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	@Operation(
			summary = "Find all therapies being offered via catalog for all accounts that the logged in user has access")
	public Page<TherapyCatalogResponseDTO> findAllThreapies(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(required = false) String query, @RequestParam(required = false) String salesOrg) {
		Pageable paging = PageRequest.of(pageNo, pageSize);

		String uri = userServiceBaseUrl + "/practices/" + OrganizationContext.getCurrentOrgId() + "/users/" + userId
				+ "/accounts-api";
		ResponseEntity<Flux<Object>> allObjects = webClientUtils.makeGetRequest(uri);
		List<String> soldtoAccountIds = new ArrayList<>();
		allObjects.getBody().collectList().block().stream().map(a -> {
			Account account = objectMapper.convertValue(a, Account.class);
			soldtoAccountIds.add(account.getId().toString());
			return soldtoAccountIds;
		}).toList();
		return therapyService.findAllTherapies(userId, soldtoAccountIds, query, salesOrg, paging);
	}

	@GetMapping("/{therapyId}")
	@Operation(
			summary = "Find all soldto accounts from which the selected therapy is being offered, and the logged in user has access to")
	public Page<SoldToAccountsResponseDTO> findAllSoldToAccountsByTherapy(@PathVariable Long userId,
			@PathVariable Long therapyId, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(required = false) String query, @RequestParam(defaultValue = "20") Integer pageSize) {

		Pageable paging = PageRequest.of(pageNo, pageSize);
		String uri = userServiceBaseUrl + "/practices/" + OrganizationContext.getCurrentOrgId() + "/users/" + userId
				+ "/accounts-api";
		ResponseEntity<Flux<Object>> allObjects = webClientUtils.makeGetRequest(uri);
		List<String> soldtoAccountIds = new ArrayList<>();
		allObjects.getBody().collectList().block().stream().map(a -> {
			Account account = objectMapper.convertValue(a, Account.class);
			soldtoAccountIds.add(account.getId().toString());
			return soldtoAccountIds;
		}).toList();
		return therapyService.findAllSoldToAccounts(userId, soldtoAccountIds, therapyId, query, paging);
	}

	@GetMapping("/{therapyId}/{soldToAccountId}")
	@Operation(
			summary = "Find all shipto accounts that is linked to the selected soldto account and the therapy also being offered from that shipto account")
	public Page<ShipToAccountsResponseDTO> findAllShipToAccountsForTherapy(@PathVariable String soldToAccountId,
			@PathVariable Long therapyId, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return therapyService.findAllShipToAccounts(soldToAccountId, therapyId, paging);
	}

	public ResponseEntity<Flux<Account>> getAccountsByUserId(Long userId) {
		ResponseEntity<Flux<Account>> response = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("userId", UserSecurityUtils.getLoggedInUser().getId());
			String apiUri = userServiceBaseUrl + "/practices/" + OrganizationContext.getCurrentOrgId() + "/users/"
					+ userId + "/accounts-api";
			response = webClient.get()
				.uri(apiUri)
				.headers(h -> h.addAll(headers))
				.retrieve()
				.toEntityFlux(Account.class)
				.block();
		}
		catch (Exception e) {
			log.error("Error connecting with usermanagerment service.", e);
			throw new RuntimeException("Error connecting with usermanagerment service.", e);
		}
		return response;
	}

}
