package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Provider;
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
				validateUniqueConstraints(provider);
				providerRepository.save(provider);
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
		}
		// Create output csv
		createOutputCSV(outputStream, failedProviders);

		return failedProviders.isEmpty();
	}

	private void validateProvider(Provider provider) {
		// Trigger validation with the BasicInfo group
		Set<ConstraintViolation<Provider>> violations = validator.validate(provider, BasicInfo.class);

		if (!violations.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("Validation errors: ");
			for (ConstraintViolation<Provider> violation : violations) {
				errorMessage.append(violation.getPropertyPath())
					.append(" ")
					.append(violation.getMessage())
					.append("; ");
			}
			throw new IllegalArgumentException(errorMessage.toString());
		}
	}

	private void parseProviderDataCSV(MultipartFile providerDataFile, List<Provider> providers, Long practiceId,
			Map<Provider, String> failedProvider) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(providerDataFile.getInputStream()));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			for (CSVRecord record : csvParser) {

				if (record.stream().allMatch(String::isBlank)) {
					break;
				}

				List<String> providerData = record.toList();
				Provider provider = createProviderFromCSVRecord(providerData, practiceId);
				try {
					providers.add(provider);
				}
				catch (IllegalArgumentException e) {
					failedProvider.put(provider, e.getMessage());
				}

			}
		}
		catch (IOException e) {
			log.error("Unable to read provider Data CSV");
			throw new FileNotValidException("Unable to read provider Data CSV");
		}
		catch (IndexOutOfBoundsException e) {
			log.error("Invalid provider Data file, some columns are missing");
			throw new FileNotValidException("Invalid provider Data file, some columns are missing");
		}
	}

	private Provider createProviderFromCSVRecord(List<String> providerData, Long practiceId) {

		return Provider.builder()
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
			.isActive(true)
			.build();
	}

	private void createOutputCSV(OutputStream outputStream, Map<Provider, String> failedProvider) {
		try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("First Name", "Middle Name", "Last Name", "DEA Number",
								"NPI Number", "Address 1", "Address 2", "City", "Zip", "State", "Phone Number",
								"Message"))) {

			for (Map.Entry<Provider, String> e : failedProvider.entrySet()) {
				Provider provider = e.getKey();
				csvPrinter.printRecord(provider.getName().getFirstName(), provider.getName().getMiddleName(),
						provider.getName().getLastName(), provider.getDea(), provider.getNpi(),
						provider.getAddress().getAddress1(), provider.getAddress().getAddress2(),
						provider.getAddress().getCity(), provider.getAddress().getZip(),
						provider.getAddress().getState(), provider.getPhone(), e.getValue());
			}
		}
		catch (IOException e) {
			log.error("Unable to process output csv file, error message: {}", e.getMessage());
			throw new RuntimeException("Failed to generate output csv file");
		}
	}

	@Override
	public Provider createProvider(Provider provider) {
		validateProvider(provider);
		validateUniqueConstraints(provider);
		provider.setIsActive(true);
		return providerRepository.save(provider);

	}

	@Override
	public Page<Provider> findAllProviders(Pageable paging, String query, List<String> status) {
		log.debug("Searching providers based on filter query:{}, status: {}", query, status);
		query = StringUtils.toRootLowerCase(query);

		String statusFilter = CustomUtils.object_status.ALL.name();
		if (!CollectionUtils.isEmpty(status)) {
			if (status.contains(CustomUtils.object_status.ACTIVE.name())) {
				statusFilter = CustomUtils.object_status.ACTIVE.name();
			}
			if (status.contains(CustomUtils.object_status.INACTIVE.name())) {
				statusFilter = statusFilter.equals(CustomUtils.object_status.ACTIVE.name())
						? CustomUtils.object_status.ALL.name() : CustomUtils.object_status.INACTIVE.name();
			}
		}

		return providerRepository.findAllByQueryText(query, statusFilter, paging);
	}

	@Override
	public Provider updateProverDetailsById(Long providerId, Provider model) throws ResourceNotFoundException {
		Provider provider = getProvider(providerId);
		provider.setAddress(model.getAddress());
		provider.setPhone(model.getPhone());
		provider.setDea(model.getDea());
		provider.setNpi(model.getNpi());
		provider.setName(model.getName());
		validateUniqueConstraints(provider);
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
		if (providerRepository.existsByPracticeIdAndDea(provider.getPracticeId(), provider.getDea())) {
			throw new IllegalArgumentException("Provider with DEA " + provider.getDea() + " already exists in practice "
					+ provider.getPracticeId());
		}
		if (providerRepository.existsByPracticeIdAndNpi(provider.getPracticeId(), provider.getNpi())) {
			throw new IllegalArgumentException("Provider with NPI " + provider.getNpi() + " already exists in practice "
					+ provider.getPracticeId());
		}
	}

}
