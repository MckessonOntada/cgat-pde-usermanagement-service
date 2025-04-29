package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.ProviderDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CsvFileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.ResourceConflictException;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Provider;
import com.mckesson.cgat.pde.usermanagement.model.ProviderStatus;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.repository.ProviderRepository;
import com.mckesson.cgat.pde.usermanagement.service.ProviderService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ProviderServiceImpl implements ProviderService {

	private final ProviderRepository providerRepository;

	@Autowired
	Validator validator;

	public ProviderServiceImpl(ProviderRepository providerRepository) {
		this.providerRepository = providerRepository;
	}

	@Override
	public Boolean uploadProviders(MultipartFile providerDataFile, Long practiceId, OutputStream outputStream)
			throws FileNotValidException {
		List<Provider> providers = new ArrayList<>();
		// Map to store failed provider
		Map<Provider, String> failedProviders = new LinkedHashMap<>();

		// Parse CSV and Store Provider Data
		parseProviderDataCSV(providerDataFile, providers, practiceId, failedProviders);

		for (Provider provider : providers) {
			try {

				validateProvider(provider);
				Provider existingProvider = providerRepository.findByPracticeIdAndNpi(practiceId, provider.getNpi());

				if (existingProvider != null) {
					updateExistingProvider(existingProvider, provider);
					providerRepository.save(existingProvider);
					log.info("Updated provider with NPI: {} for Practice ID: {}", provider.getNpi(), practiceId);
				}
				else {
					provider.setPracticeId(practiceId);
					validateUniqueConstraints(provider);
					providerRepository.save(provider);
					log.info("Saved new provider with NPI: {} for Practice ID: {}", provider.getNpi(), practiceId);
				}
			}
			catch (IllegalArgumentException e) {
				log.error("Validation failed for provider {}", provider.getName().getFirstName());
				log.error(e.getMessage());
				failedProviders.put(provider, e.getMessage());
			}
			catch (DataIntegrityViolationException e) {
				log.error("Data Integrity violation for provider {}", provider.getName().getFirstName());
				log.error(e.getMessage());
				failedProviders.put(provider, e.getCause().getCause().getMessage());
			}
			catch (Exception e) {
				log.error("Exception occured in validation of provider", e);
				failedProviders.put(provider, e.getCause().getCause().getMessage());
			}
		}
		// Create output csv
		createOutputCSV(outputStream, failedProviders);

		return failedProviders.isEmpty();
	}

	private void updateExistingProvider(Provider existingProvider, Provider newProvider) {
		if (newProvider.getName() != null) {
			existingProvider.getName().setFirstName(newProvider.getName().getFirstName());
			existingProvider.getName().setMiddleName(newProvider.getName().getMiddleName());
			existingProvider.getName().setLastName(newProvider.getName().getLastName());
		}
		existingProvider.setDea(newProvider.getDea());
		if (newProvider.getAddress() != null) {
			existingProvider.getAddress().setAddress1(newProvider.getAddress().getAddress1());
			existingProvider.getAddress().setAddress2(newProvider.getAddress().getAddress2());
			existingProvider.getAddress().setCity(newProvider.getAddress().getCity());
			existingProvider.getAddress().setZip(newProvider.getAddress().getZip());
			existingProvider.getAddress().setState(newProvider.getAddress().getState());
		}
		existingProvider.setPhone(newProvider.getPhone());
		existingProvider.setIsActive(newProvider.getIsActive());
	}

	private void validateProvider(Provider provider) {
		// Trigger validation with the BasicInfo group
		Set<ConstraintViolation<Provider>> violations = validator.validate(provider, BasicInfo.class);

		if (!violations.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("Validation errors: ");
			for (ConstraintViolation<Provider> violation : violations) {
				errorMessage.append(violation.getMessage()).append("; ");
			}
			throw new IllegalArgumentException(errorMessage.toString());
		}
	}

	private void parseProviderDataCSV(MultipartFile providerDataFile, List<Provider> providers, Long practiceId,
			Map<Provider, String> failedProvider) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(providerDataFile.getInputStream()));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			Map<String, Integer> headerMap = csvParser.getHeaderMap();
			validateProviderHeaders(headerMap);

			boolean isEmpty = true;
			for (CSVRecord record : csvParser) {
				if (record.stream().anyMatch(value -> !value.isBlank())) {
					isEmpty = false;
					List<String> providerData = record.toList();
					Provider provider = createProviderFromCSVRecord(providerData, practiceId, failedProvider);
					if (provider != null) { // <-- Only add valid providers
						providers.add(provider);
					}
				}
			}
			if (isEmpty) {
				log.error("The uploaded file is empty or contains no data.");
				throw new CsvFileNotValidException(List.of("invalid.csv.file.empty"));
			}
		}
		catch (IOException e) {
			log.error("Unable to read Provider Data CSV", e);
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.unstructured"));
		}
	}

	private void validateProviderHeaders(Map<String, Integer> headerMap) {
		List<String> expectedHeaders = List.of("First Name", "Middle Name", "Last Name", "DEA Number", "NPI Number",
				"Address 1", "Address 2", "City", "Zip", "State", "Phone Number", "Status");

		// Check for missing headers
		List<String> missingHeaders = expectedHeaders.stream()
			.filter(header -> !headerMap.containsKey(header))
			.toList();

		if (!missingHeaders.isEmpty()) {
			log.error("Invalid Cross walk Data file, some columns are missing: {}", missingHeaders);
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.missing"));
		}

		// Validate header sequence
		List<String> actualHeadersInOrder = headerMap.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.toList();

		if (!expectedHeaders.equals(actualHeadersInOrder)) {
			log.error("CSV headers are invalid or not in expected order.");
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.unstructured"));
		}
	}

	private Provider createProviderFromCSVRecord(List<String> providerData, Long practiceId,
			Map<Provider, String> failedProvider) {
		String statusValue = providerData.get(11).trim().toUpperCase();
		ProviderStatus status = null;

		try {
			status = ProviderStatus.fromString(statusValue);
		}
		catch (IllegalArgumentException e) {
			log.error("Invalid Status value: '{}'. Expected ACTIVE or INACTIVE.", statusValue);
		}

		Provider provider = Provider.builder()
			.practiceId(practiceId)
			.name(Name.builder()
				.firstName(providerData.get(0))
				.middleName(providerData.get(1))
				.lastName(providerData.get(2))
				.build())
			.dea(providerData.get(3))
			.npi(providerData.get(4))
			.address(Address.builder()
				.address1(providerData.get(5))
				.address2(providerData.get(6))
				.city(providerData.get(7))
				.zip(providerData.get(8))
				.state(providerData.get(9))
				.build())
			.phone(providerData.get(10))
			.build();

		// If status is invalid, record error and return null (but dummy provider is used
		// for output)
		if (status == null) {
			failedProvider.put(provider, "Invalid Status Value: " + statusValue + ". Expected ACTIVE or INACTIVE.");
			return null;
		}

		// Valid provider
		provider.setIsActive(status == ProviderStatus.ACTIVE);
		return provider;
	}

	private void createOutputCSV(OutputStream outputStream, Map<Provider, String> failedProvider) {
		try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("First Name", "Middle Name", "Last Name", "DEA Number",
								"NPI Number", "Address 1", "Address 2", "City", "Zip", "State", "Phone Number",
								"Status", "Error Reason"))) {

			for (Map.Entry<Provider, String> e : failedProvider.entrySet()) {
				Provider provider = e.getKey();
				if (provider == null) {
					continue;
				}

				// Fix: leave status empty if unknown
				String status = "";
				if (provider.getIsActive() != null) {
					status = provider.getIsActive() ? "ACTIVE" : "INACTIVE";
				}

				csvPrinter.printRecord(provider.getName() != null ? provider.getName().getFirstName() : "",
						provider.getName() != null ? provider.getName().getMiddleName() : "",
						provider.getName() != null ? provider.getName().getLastName() : "",
						provider.getDea() != null ? provider.getDea() : "",
						provider.getNpi() != null ? provider.getNpi() : "",
						provider.getAddress() != null ? provider.getAddress().getAddress1() : "",
						provider.getAddress() != null ? provider.getAddress().getAddress2() : "",
						provider.getAddress() != null ? provider.getAddress().getCity() : "",
						provider.getAddress() != null ? provider.getAddress().getZip() : "",
						provider.getAddress() != null ? provider.getAddress().getState() : "",
						provider.getPhone() != null ? provider.getPhone() : "", status,
						e.getValue() != null ? e.getValue() : "");
			}
		}
		catch (IOException e) {
			log.error("Unable to process output csv file, error message: ", e);
			throw new RuntimeException("Failed to generate output csv file", e);
		}
	}

	@Override
	public Provider createProvider(Provider provider) {
		validateProvider(provider);
		validateUniqueConstraintsForAddProvider(provider);
		provider.setIsActive(true);
		return providerRepository.save(provider);

	}

	@Override
	public Page<Provider> findAllProviders(Pageable paging, String query, List<String> status, Long accountId,
			Long practiceId) {
		log.debug("Searching providers based on filter query:{}, status: {}", query, status);
		query = StringUtils.toRootLowerCase(query);

		String statusFilter = CustomUtils.object_status.ALL.name();
		if (!CollectionUtils.isEmpty(status)) {
			if (status.contains(CustomUtils.object_status.ACTIVE.name())) {
				statusFilter = CustomUtils.object_status.ACTIVE.name();
			}
			else if (status.contains(CustomUtils.object_status.INACTIVE.name())) {
				statusFilter = CustomUtils.object_status.INACTIVE.name();
			}
		}

		return providerRepository.findAllByQueryText(query, statusFilter, paging, accountId, practiceId);
	}

	@Override
	public Provider updateProviderDetailsById(Long providerId, ProviderDTO model) throws ResourceNotFoundException {
		Provider provider = getProvider(providerId);
		provider.setAddress(model.getAddress());
		provider.setPhone(model.getPhone());
		provider.setDea(model.getDea());
		provider.setNpi(model.getNpi());
		provider.setName(model.getName());
		validateProvider(provider);
		return providerRepository.save(provider);
	}

	@Override
	public Provider getProvider(Long providerId) throws ResourceNotFoundException {
		return providerRepository.findById(providerId)
			.orElseThrow(() -> new ResourceNotFoundException("Provider not found with id:" + providerId.toString()));
	}

	@Override
	public void deactivateProvider(Long providerId) throws ResourceNotFoundException {
		Provider provider = providerRepository.findById(providerId)
			.orElseThrow(() -> new ResourceNotFoundException(providerId.toString()));
		provider.setIsActive(false);
		providerRepository.save(provider);
	}

	@Override
	public void activateProvider(Long providerId) throws ResourceNotFoundException {
		Provider provider = providerRepository.findById(providerId)
			.orElseThrow(() -> new ResourceNotFoundException(providerId.toString()));
		provider.setIsActive(true);
		providerRepository.save(provider);
	}

	private void validateUniqueConstraints(Provider provider) {
		StringBuilder errorMessage = new StringBuilder();

		if (providerRepository.existsByPracticeIdAndDea(provider.getPracticeId(), provider.getDea())) {
			errorMessage.append("Provider with DEA ")
				.append(provider.getDea())
				.append(" already exists in practice ")
				.append(provider.getPracticeId())
				.append(". ");
		}

		if (providerRepository.existsByPracticeIdAndNpi(provider.getPracticeId(), provider.getNpi())) {
			errorMessage.append("Provider with NPI ")
				.append(provider.getNpi())
				.append(" already exists in practice ")
				.append(provider.getPracticeId())
				.append(". ");
		}

		if (!errorMessage.isEmpty()) {
			throw new IllegalArgumentException(errorMessage.toString().trim());
		}
	}

	private void validateUniqueConstraintsForAddProvider(Provider provider) {
		List<String> errorCodes = new ArrayList<>();

		if (providerRepository.existsByPracticeIdAndDea(provider.getPracticeId(), provider.getDea())) {
			errorCodes.add("provider.dea.conflict");
		}

		if (providerRepository.existsByPracticeIdAndNpi(provider.getPracticeId(), provider.getNpi())) {
			errorCodes.add("provider.npi.conflict");
		}

		if (!errorCodes.isEmpty()) {
			throw new ResourceConflictException(errorCodes);
		}
	}

}
