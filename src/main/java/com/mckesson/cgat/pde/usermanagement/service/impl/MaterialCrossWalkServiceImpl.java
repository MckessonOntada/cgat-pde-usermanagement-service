package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialCrossWalkConfigRecordDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.SystemFailureException;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.MaterialCrossWalkAction;
import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.MaterialCrossWalkConfigRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.MaterialCrossWalkService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class MaterialCrossWalkServiceImpl implements MaterialCrossWalkService {

	private final MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository;

	private final TherapyRepository therapyRepository;

	private final int BATCH_SIZE = 100;

	private final Validator validator;

	public MaterialCrossWalkServiceImpl(MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository,
			TherapyRepository therapyRepository, Validator validator) {
		this.materialCrossWalkConfigRepository = materialCrossWalkConfigRepository;
		this.therapyRepository = therapyRepository;
		this.validator = validator;
	}

	@Override
	@Transactional
	public Boolean uploadCrossWalks(MultipartFile crossWalkDataFile, ByteArrayOutputStream outputStream) {
		List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToAdd = new ArrayList<>();
		List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToDelete = new ArrayList<>();

		Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks = new LinkedHashMap<>();

		try {
			// Parse the CSV file and perform the validation
			parseCrossWalkDataFile(crossWalkDataFile, crossWalkConfigsToAdd, crossWalkConfigsToDelete,
					failedCrossWalks);

			List<String> existingBnbMaterialNumbers = fetchBnbTherapies(crossWalkConfigsToAdd,
					crossWalkConfigsToDelete);
			List<String> existingSPProductIds = fetchSPTherapies(crossWalkConfigsToAdd, crossWalkConfigsToDelete);

			Map<String, Crosswalk> spProductIdToConfigMap = fetchExistingCrossWalkConfigsForSPs(existingSPProductIds);

			List<Crosswalk> entitiesToDelete = validateCrossWalkConstraintsAndGetConfigs(crossWalkConfigsToDelete,
					failedCrossWalks, existingBnbMaterialNumbers, existingSPProductIds, spProductIdToConfigMap);

			List<Crosswalk> entitiesToAdd = validateCrossWalkConstraintsAndGetConfigs(crossWalkConfigsToAdd,
					failedCrossWalks, existingBnbMaterialNumbers, existingSPProductIds, spProductIdToConfigMap);

			// Removing configurations and saving in batches
			removeConfigsFromDB(entitiesToDelete);

			saveOrUpdateInBatches(entitiesToAdd, BATCH_SIZE);

			// Update flags for materials
			updateCrossWalkFlagForMaterials(entitiesToDelete, entitiesToAdd);

			// Generate output CSV for failed cross walks
			createOutputCSV(outputStream, failedCrossWalks);
		}
		catch (FileNotValidException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("System failure occurred during upload: {}", e.getMessage(), e);
			throw new SystemFailureException("An unexpected error occurred during the upload. Please try again later.");
		}

		return failedCrossWalks.isEmpty();
	}

	private void parseCrossWalkDataFile(MultipartFile crossWalkDataFile,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToAdd,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToDelete,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(crossWalkDataFile.getInputStream()));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			Map<String, Integer> headerMap = csvParser.getHeaderMap();
			validateHeaders(headerMap);

			boolean isEmpty = true;
			for (CSVRecord record : csvParser) {
				if (record.stream().anyMatch(value -> !value.isBlank())) {
					isEmpty = false;
					List<String> recordData = record.toList();
					createCrossWalkConfigFromRecord(recordData, crossWalkConfigsToAdd, crossWalkConfigsToDelete,
							failedCrossWalks);
				}
			}
			if (isEmpty) {
				throw new FileNotValidException("The uploaded file is empty or contains no data.");
			}

		}
		catch (IOException e) {
			log.error("Unable to read Cross walk Data CSV");
			throw new FileNotValidException("Unable to read User Data CSV");
		}
		catch (IndexOutOfBoundsException e) {
			log.error("Invalid Cross walk Data file, some columns are missing");
			throw new FileNotValidException("Invalid Cross walk Data file, some columns are missing");
		}
	}

	private void validateHeaders(Map<String, Integer> headerMap) {
		List<String> expectedHeaders = List.of("Buy & Bill Material ID", "Buy & Bill Material Description",
				"Buy & Bill NDC", "SP Product ID", "SP Product Name", "SP NDC", "Status");
		for (String expected : expectedHeaders) {
			if (!headerMap.containsKey(expected)) {
				throw new FileNotValidException("The uploaded file is invalid. Please upload a valid file");
			}
		}
	}

	private void createCrossWalkConfigFromRecord(List<String> recordData,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToAdd,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToDelete,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks) {
		/*
		 * Steps: 1) Create cross walk config from the record. 2) Add validation on the
		 * status column of csv to accept only 2 types. 3) If status == Link, check for
		 * duplicate rows of SP. 4) If status == Unlink, simply add in the unlink list and
		 * remove from DB.
		 */
		MaterialCrossWalkConfigRecordDTO crossWalkConfig = MaterialCrossWalkConfigRecordDTO.builder()
			.bnbMaterialNumber(recordData.get(0))
			.bnbDescription(recordData.get(1))
			.bnbNDC(recordData.get(2))
			.spProductId(recordData.get(3))
			.spProductName(recordData.get(4))
			.spNDC(recordData.get(5))
			.build();

		Set<ConstraintViolation<MaterialCrossWalkConfigRecordDTO>> violations = validator.validate(crossWalkConfig);
		if (!violations.isEmpty()) {
			String errorMessage = violations.stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.joining("; "));
			failedCrossWalks.put(crossWalkConfig, errorMessage);
			return;
		}

		try {
			String rowAction = recordData.get(6);
			MaterialCrossWalkAction crossWalkAction = MaterialCrossWalkAction.fromString(rowAction);
			if (MaterialCrossWalkAction.LINK.equals(crossWalkAction)) {
				validateForDuplicateSPRows(crossWalkConfig.getSpProductId(), crossWalkConfigsToAdd);
				crossWalkConfigsToAdd.add(crossWalkConfig);
			}
			else {
				crossWalkConfigsToDelete.add(crossWalkConfig);
			}
		}
		catch (IllegalArgumentException e) {
			log.error("Error occurred while reading row data: {} ", e.getMessage(), e);
			failedCrossWalks.put(crossWalkConfig, e.getMessage());
		}
		catch (Exception e) {
			failedCrossWalks.put(crossWalkConfig, e.getMessage());
		}
	}

	private void validateForDuplicateSPRows(String spProductId,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigs) {
		boolean exists = crossWalkConfigs.stream().anyMatch(record -> spProductId.equals(record.getSpProductId()));
		if (exists) {
			throw new IllegalArgumentException("Duplicate Entry");
		}
	}

	private List<String> fetchBnbTherapies(List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToAdd,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToDelete) {
		List<String> distinctBnbMaterialNumbers = Stream
			.concat(crossWalkConfigsToAdd.stream(), crossWalkConfigsToDelete.stream())
			.map(MaterialCrossWalkConfigRecordDTO::getBnbMaterialNumber)
			.distinct()
			.toList();
		List<Therapy> existingTherapies = therapyRepository
			.findByMaterialNumberInAndChannelType(distinctBnbMaterialNumbers, ChannelType.BUY_AND_BILL);

		return existingTherapies.stream().map(Therapy::getMaterialNumber).toList();

	}

	private List<String> fetchSPTherapies(List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToAdd,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToDelete) {
		List<String> distinctSPProductId = Stream
			.concat(crossWalkConfigsToAdd.stream(), crossWalkConfigsToDelete.stream())
			.map(MaterialCrossWalkConfigRecordDTO::getSpProductId)
			.distinct()
			.toList();
		List<Therapy> existingTherapies = therapyRepository.findByMaterialNumberInAndChannelType(distinctSPProductId,
				ChannelType.SPECIALITY_PHARMA);

		return existingTherapies.stream().map(Therapy::getMaterialNumber).toList();

	}

	private Map<String, Crosswalk> fetchExistingCrossWalkConfigsForSPs(List<String> existingSPProductIds) {
		List<Crosswalk> existingCrossWalkConfigs = materialCrossWalkConfigRepository
			.findMaterialCrossWalkConfigBySpProductIdIn(existingSPProductIds);
		return existingCrossWalkConfigs.stream().collect(Collectors.toMap(Crosswalk::getSpProductId, config -> config));
	}

	private List<Crosswalk> validateCrossWalkConstraintsAndGetConfigs(
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigs,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks, List<String> existingBnbMaterialNumbers,
			List<String> existingSPProductIds, Map<String, Crosswalk> spProductIdToConfigMap) {
		List<Crosswalk> crossWalkConfigList = new ArrayList<>();

		Iterator<MaterialCrossWalkConfigRecordDTO> iterator = crossWalkConfigs.iterator();
		while (iterator.hasNext()) {

			MaterialCrossWalkConfigRecordDTO crossWalkConfig = iterator.next();
			String bnbMaterialNumber = crossWalkConfig.getBnbMaterialNumber();
			String spProductId = crossWalkConfig.getSpProductId();

			try {

				if (!existingBnbMaterialNumbers.contains(bnbMaterialNumber)) {
					throw new IllegalArgumentException("Invalid Buy & Bill Material ID");
				}

				if (!existingSPProductIds.contains(spProductId)) {
					throw new IllegalArgumentException("Invalid SP Product ID");
				}
				Crosswalk config = Crosswalk.builder()
					.bnbMaterialNumber(bnbMaterialNumber)
					.spProductId(spProductId)
					.build();

				Crosswalk existingConfig = spProductIdToConfigMap.get(spProductId);
				if (existingConfig != null) {
					if (!existingConfig.getBnbMaterialNumber().equals(bnbMaterialNumber)) {
						throw new IllegalArgumentException(
								"The SP product ID is already mapped to a Material, please review the existing mapping and retry.");
					}
					else {
						config.setId(existingConfig.getId());
					}
				}
				crossWalkConfigList.add(config);
			}
			catch (IllegalArgumentException e) {
				failedCrossWalks.put(crossWalkConfig, e.getMessage());
				iterator.remove();
			}

		}

		return crossWalkConfigList;
	}

	private void removeConfigsFromDB(List<Crosswalk> entitiesToDelete) {
		List<String> spProductIds = entitiesToDelete.stream().map(Crosswalk::getSpProductId).distinct().toList();
		if (!CollectionUtils.isEmpty(spProductIds)) {
			materialCrossWalkConfigRepository.deleteBySpProductIdIn(spProductIds);
		}

	}

	private void saveOrUpdateInBatches(List<Crosswalk> configs, int batchSize) {
		for (int i = 0; i < configs.size(); i += batchSize) {
			int end = Math.min(i + batchSize, configs.size());
			List<Crosswalk> batch = configs.subList(i, end);
			materialCrossWalkConfigRepository.saveAll(batch);
			materialCrossWalkConfigRepository.flush();
		}
	}

	private void updateCrossWalkFlagForMaterials(List<Crosswalk> deletedEntities, List<Crosswalk> addedEntities) {
		// --- Collect SP ---
		Set<String> addedSpProductIds = addedEntities.stream()
			.map(Crosswalk::getSpProductId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		Set<String> deletedSpProductIds = deletedEntities.stream()
			.map(Crosswalk::getSpProductId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		// --- Collect BnB ---
		Set<String> addedBnbMaterialNumbers = addedEntities.stream()
			.map(Crosswalk::getBnbMaterialNumber)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		Set<String> deletedBnbMaterialNumbers = deletedEntities.stream()
			.map(Crosswalk::getBnbMaterialNumber)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		// Mark SP: true for added, false for deleted
		if (!addedSpProductIds.isEmpty()) {
			therapyRepository.updateIsCrossWalkedForMaterialNumbersAndChannelType(true, addedSpProductIds,
					String.valueOf(ChannelType.SPECIALITY_PHARMA));
		}

		if (!deletedSpProductIds.isEmpty()) {
			therapyRepository.updateIsCrossWalkedForMaterialNumbersAndChannelType(false, deletedSpProductIds,
					String.valueOf(ChannelType.SPECIALITY_PHARMA));
		}

		// Mark BnB: true for added
		if (!addedBnbMaterialNumbers.isEmpty()) {
			therapyRepository.updateIsCrossWalkedForMaterialNumbersAndChannelType(true, addedBnbMaterialNumbers,
					String.valueOf(ChannelType.BUY_AND_BILL));
		}

		// BnB deleted: Only mark false if material no longer exists in DB
		if (!deletedBnbMaterialNumbers.isEmpty()) {
			Set<String> stillExisting = new HashSet<>(
					materialCrossWalkConfigRepository.findExistingBnbMaterialNumbers(deletedBnbMaterialNumbers));

			// Only keep the material numbers that no longer exist in DB
			Set<String> toBeMarkedFalse = deletedBnbMaterialNumbers.stream()
				.filter(bnb -> !stillExisting.contains(bnb))
				.collect(Collectors.toSet());

			if (!toBeMarkedFalse.isEmpty()) {
				therapyRepository.updateIsCrossWalkedForMaterialNumbersAndChannelType(false, toBeMarkedFalse,
						String.valueOf(ChannelType.BUY_AND_BILL));
			}
		}
	}

	private void createOutputCSV(OutputStream outputStream,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks) {
		try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("Buy & Bill Material ID", "Buy & Bill Description",
								"Buy & Bill NDC", "SP Product ID", "SP Product Name", "SP NDC", "Message"))) {

			for (Map.Entry<MaterialCrossWalkConfigRecordDTO, String> e : failedCrossWalks.entrySet()) {
				MaterialCrossWalkConfigRecordDTO crossWalkConfigRecordDTO = e.getKey();
				csvPrinter.printRecord(crossWalkConfigRecordDTO.getBnbMaterialNumber(),
						crossWalkConfigRecordDTO.getBnbDescription(), crossWalkConfigRecordDTO.getBnbNDC(),
						crossWalkConfigRecordDTO.getSpProductId(), crossWalkConfigRecordDTO.getSpProductName(),
						crossWalkConfigRecordDTO.getSpNDC(), e.getValue());
			}
		}
		catch (IOException e) {
			log.error("Unable to process output csv file, error message: {}", e.getMessage());
			throw new RuntimeException("Failed to generate output csv file");
		}
	}

}
