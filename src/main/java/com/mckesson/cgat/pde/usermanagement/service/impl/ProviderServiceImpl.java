package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Provider;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.repository.ProviderRepository;
import com.mckesson.cgat.pde.usermanagement.service.ProviderService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	public Boolean uploadUsers(MultipartFile userDataFile, Long practiceId, OutputStream outputStream)
			throws FileNotValidException {
		List<Provider> providers = new ArrayList<>();
		// Map to store failed provider
		Map<Provider, String> failedProviders = new LinkedHashMap<>();

		// Parse CSV and Store Provider Data
		parseProviderDataCSV(userDataFile, providers, practiceId, failedProviders);

		for (Provider provider : providers) {
			try {

				validateProvider(provider);
				if (providerRepository.existsByPracticeIdAndDea(provider.getPracticeId(), provider.getDea())) {
					throw new IllegalArgumentException("Provider with DEA " + provider.getDea()
							+ " already exists in practice " + provider.getPracticeId());
				}
				providerRepository.save(provider);
			}
			catch (IllegalArgumentException e) {
				log.error("Validation failed for User {}", provider.getName().getFirstName());
				log.error(e.getMessage());
				failedProviders.put(provider, e.getMessage());
			}
			catch (DataIntegrityViolationException e) {
				log.error("Data Integrity violation for User {}", provider.getName().getFirstName());
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
			log.error("Unable to read User Data CSV");
			throw new FileNotValidException("Unable to read User Data CSV");
		}
		catch (IndexOutOfBoundsException e) {
			log.error("Invalid User Data file, some columns are missing");
			throw new FileNotValidException("Invalid User Data file, some columns are missing");
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

}
