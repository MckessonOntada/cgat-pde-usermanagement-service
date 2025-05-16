package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.ProviderAccountsDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProviderDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CsvFileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Provider;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.service.ProviderService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@Tag(name = "Providers", description = "Controller for Managing provider-related operations and data.")
@RequestMapping(path = "/practices/{practiceId}/providers", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ProviderController {

	private final ProviderService providerService;

	public ProviderController(ProviderService providerService) {
		this.providerService = providerService;
	}

	@RequirePermissions({ Permission.UPLOAD_PROVIDER })
	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	@Operation(summary = "Upload bulk providers")
	public ResponseEntity<byte[]> uploadProviders(@RequestPart("providerData") MultipartFile providerData,
			@NotNull(message = "Practice Id is Required") @PathVariable Long practiceId)
			throws FileNotValidException, UnauthorizedAccessException {
		if (providerData == null
				|| !StringUtils.getFilenameExtension(providerData.getOriginalFilename()).equalsIgnoreCase("csv")) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.type"));
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Boolean uploadStatus = providerService.uploadProviders(providerData, practiceId, outputStream);
		HttpStatus status = uploadStatus ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;

		return ResponseEntity.status(status)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FailedProviders.csv")
			.contentType(MediaType.parseMediaType("text/csv"))
			.body(outputStream.toByteArray());
	}

	@RequirePermissions({ Permission.ADD_PROVIDER })
	@PostMapping
	@Operation(summary = "Create new provider")
	public ResponseEntity<Provider> createProvider(@Validated(BasicInfo.class) @RequestBody Provider provider,
			@NotNull(message = "Practice Id is Required") @PathVariable Long practiceId)
			throws UnauthorizedAccessException {
		provider.setPracticeId(practiceId);
		return new ResponseEntity<>(providerService.createProvider(provider), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_PROVIDERS_SCREEN, Permission.ADD_SP_ACCOUNT, Permission.ADD_ORDERS,
			Permission.EDIT_ORDERS })
	@GetMapping
	@Operation(summary = "Fetch all Providers based on filter provided with pagination capabilities.")
	public Page<Provider> findAllProviders(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "first_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable @NotNull(message = "Practice ID cannot be null") Long practiceId,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) Long accountId) {

		Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		return providerService.findAllProviders(paging, query, status, accountId, practiceId);
	}

	@RequirePermissions({ Permission.EDIT_PROVIDER, Permission.ADD_PROVIDER })
	@PatchMapping("/{providerId}")
	@Operation(summary = "Update existing provider details by Id")
	public Provider updateProviderDetails(@PathVariable Long providerId,
			@Validated(BasicInfo.class) @RequestBody ProviderDTO provider)
			throws ResourceNotFoundException, UnauthorizedAccessException {

		return providerService.updateProviderDetailsById(providerId, provider);
	}

	@RequirePermissions({ Permission.EDIT_PROVIDER })
	@PatchMapping("/{providerId}/deactivate")
	@Operation(summary = "Deactivate provider byId")
	public void deactivateProvider(@PathVariable Long providerId) throws ResourceNotFoundException {
		providerService.deactivateProvider(providerId);
	}

	@RequirePermissions({ Permission.EDIT_PROVIDER })
	@PatchMapping("/{providerId}/activate")
	@Operation(summary = "Activate provider by Id")
	public void activateProvider(@PathVariable Long providerId) throws ResourceNotFoundException {
		providerService.activateProvider(providerId);
	}

	@RequirePermissions({ Permission.VIEW_PROVIDER, Permission.ADD_ORDERS, Permission.EDIT_ORDERS })
	@GetMapping("/{providerId}")
	@Operation(summary = "Get provider details by Id ")
	public ResponseEntity<Provider> getProvider(@PathVariable Long providerId) throws ResourceNotFoundException {
		return new ResponseEntity<>(providerService.getProvider(providerId), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_PROVIDERS_SCREEN })
	@PatchMapping("/link-accounts")
	@Operation(summary = "Link multiple provider with multiple sp account.")
	public ResponseEntity<Void> linkProviderToAccounts(@Valid @RequestBody ProviderAccountsDTO providersAccounts)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		providerService.linkProvidersToAccounts(providersAccounts);
		return ResponseEntity.ok().build();
	}

}
