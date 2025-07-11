package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkRequestDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.MaterialCrossWalkConfigRecordDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CsvFileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.MaterialCrossWalkAction;
import com.mckesson.cgat.pde.usermanagement.model.Crosswalk;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.MaterialCrossWalkConfigRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.MaterialCrossWalkService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

		// Parse the CSV file and perform the validation
		parseCrossWalkDataFile(crossWalkDataFile, crossWalkConfigsToAdd, crossWalkConfigsToDelete, failedCrossWalks);

		List<String> existingBnbNDCs = fetchBnbTherapies(crossWalkConfigsToAdd, crossWalkConfigsToDelete);
		List<String> existingSPIds = fetchSPTherapies(crossWalkConfigsToAdd, crossWalkConfigsToDelete);

		Map<String, Crosswalk> spIdToConfigMap = fetchExistingCrossWalkConfigsForSPs(existingSPIds);

		List<Crosswalk> entitiesToDelete = validateCrossWalkConstraintsAndGetConfigs(crossWalkConfigsToDelete,
				failedCrossWalks, existingBnbNDCs, existingSPIds, spIdToConfigMap);

		List<Crosswalk> entitiesToAdd = validateCrossWalkConstraintsAndGetConfigs(crossWalkConfigsToAdd,
				failedCrossWalks, existingBnbNDCs, existingSPIds, spIdToConfigMap);

		// Removing configurations and saving in batches
		removeConfigsFromDB(entitiesToDelete);

		saveOrUpdateInBatches(entitiesToAdd, BATCH_SIZE);

		// Update flags for materials
		updateCrossWalkFlagForMaterials(entitiesToDelete, entitiesToAdd);

		// Generate output CSV for failed cross walks
		createOutputCSV(outputStream, failedCrossWalks);

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
				log.error("The uploaded file is empty or contains no data.");
				throw new CsvFileNotValidException(List.of("invalid.csv.file.empty"));
			}

		}
		catch (IOException e) {
			log.error("Unable to read Cross walk Data CSV");
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.unstructured"));
		}
	}

	private void validateHeaders(Map<String, Integer> headerMap) {
		List<String> expectedHeaders = List.of("SAP NDC", "SP Product ID", "SP NDC", "Status");

		// Check for missing headers first
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
			.bnbNDC(recordData.get(0))
			.spProductId(recordData.get(1))
			.spNDC(recordData.get(2))
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
			String rowAction = recordData.get(3);
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
		List<String> distinctBnbMaterialNDC = Stream
			.concat(crossWalkConfigsToAdd.stream(), crossWalkConfigsToDelete.stream())
			.map(MaterialCrossWalkConfigRecordDTO::getBnbNDC)
			.distinct()
			.toList();
		List<Therapy> existingTherapies = therapyRepository.findByNdcNumberInAndChannelType(distinctBnbMaterialNDC,
				ChannelType.BUY_AND_BILL);

		return existingTherapies.stream().map(Therapy::getNdcNumber).toList();

	}

	private List<String> fetchSPTherapies(List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToAdd,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToDelete) {
		List<String> distinctSPId = Stream.concat(crossWalkConfigsToAdd.stream(), crossWalkConfigsToDelete.stream())
			.map(MaterialCrossWalkConfigRecordDTO::getSpProductId)
			.distinct()
			.toList();
		List<Therapy> existingTherapies = therapyRepository.findByMaterialNumberInAndChannelType(distinctSPId,
				ChannelType.SPECIALITY_PHARMA);

		return existingTherapies.stream().map(Therapy::getMaterialNumber).toList();

	}

	private Map<String, Crosswalk> fetchExistingCrossWalkConfigsForSPs(List<String> existingSPIds) {
		List<Crosswalk> existingCrossWalkConfigs = materialCrossWalkConfigRepository
			.findMaterialCrossWalkConfigBySpProductIdIn(existingSPIds);
		return existingCrossWalkConfigs.stream().collect(Collectors.toMap(Crosswalk::getSpProductId, config -> config));
	}

	private List<Crosswalk> validateCrossWalkConstraintsAndGetConfigs(
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigs,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks, List<String> existingBnbMaterialNDC,
			List<String> existingSPProductIds, Map<String, Crosswalk> spProductNDCToConfigMap) {
		List<Crosswalk> crossWalkConfigList = new ArrayList<>();

		Iterator<MaterialCrossWalkConfigRecordDTO> iterator = crossWalkConfigs.iterator();
		while (iterator.hasNext()) {

			MaterialCrossWalkConfigRecordDTO crossWalkConfig = iterator.next();
			String bnbMaterialNDC = crossWalkConfig.getBnbNDC();
			String spId = crossWalkConfig.getSpProductId();

			try {

				if (!existingBnbMaterialNDC.contains(bnbMaterialNDC)) {
					throw new IllegalArgumentException("Invalid SAP NDC");
				}

				if (!existingSPProductIds.contains(spId)) {
					throw new IllegalArgumentException("Invalid SP Product Id");
				}
				Crosswalk config = Crosswalk.builder()
					.bnbNdcNumber(bnbMaterialNDC)
					.spProductId(spId)
					.isActive(true)
					.build();

				Crosswalk existingConfig = spProductNDCToConfigMap.get(spId);
				if (existingConfig != null) {
					if (!existingConfig.getBnbNdcNumber().equals(bnbMaterialNDC)) {
						throw new IllegalArgumentException(
								"The SP product Id is already mapped to a Material, please review the existing mapping and retry.");
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
		Set<String> addedBnbMaterialNDCs = addedEntities.stream()
			.map(Crosswalk::getBnbNdcNumber)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		Set<String> deletedBnbMaterialNDCs = deletedEntities.stream()
			.map(Crosswalk::getBnbNdcNumber)
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
		if (!addedBnbMaterialNDCs.isEmpty()) {
			therapyRepository.updateIsCrossWalkedForNdcsAndChannelType(true, addedBnbMaterialNDCs,
					String.valueOf(ChannelType.BUY_AND_BILL));
		}

		// BnB deleted: Only mark false if material no longer exists in DB
		if (!deletedBnbMaterialNDCs.isEmpty()) {
			Set<String> stillExisting = new HashSet<>(
					materialCrossWalkConfigRepository.findExistingBnbNdcNumbers(deletedBnbMaterialNDCs));

			// Only keep the material numbers that no longer exist in DB
			Set<String> toBeMarkedFalse = deletedBnbMaterialNDCs.stream()
				.filter(bnb -> !stillExisting.contains(bnb))
				.collect(Collectors.toSet());

			if (!toBeMarkedFalse.isEmpty()) {
				therapyRepository.updateIsCrossWalkedForNdcsAndChannelType(false, toBeMarkedFalse,
						String.valueOf(ChannelType.BUY_AND_BILL));
			}
		}
	}

	private void createOutputCSV(OutputStream outputStream,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks) {
		try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("SAP NDC", "SP Product ID", "SP NDC", "Error Reason"))) {

			for (Map.Entry<MaterialCrossWalkConfigRecordDTO, String> e : failedCrossWalks.entrySet()) {
				MaterialCrossWalkConfigRecordDTO crossWalkConfigRecordDTO = e.getKey();
				csvPrinter.printRecord(crossWalkConfigRecordDTO.getBnbNDC(), crossWalkConfigRecordDTO.getSpProductId(),
						crossWalkConfigRecordDTO.getSpNDC(), e.getValue());
			}
		}
		catch (IOException e) {
			log.error("Unable to process output csv file, error message: {}", e.getMessage());
			throw new RuntimeException("Failed to generate output csv file");
		}
	}

	@Transactional
	@Override
	public int createCrosswalks(List<CrosswalkRequestDTO> crosswalks) throws ResourceNotFoundException {
		int totalCrosswalkedSpCount = 0;
		List<Therapy> allTherapiesToUpdate = new ArrayList<>();

		for (CrosswalkRequestDTO request : crosswalks) {
			String bnbNdc = request.getBnbNdc();
			List<String> spIds = request.getSpProductIds();

			log.info("Processing CrosswalkRequest for BNB NDC: {} with SP Product IDs: {}", bnbNdc, spIds);

			// 1. Validate BNB therapy
			// Assuming this method throws ResourceNotFoundException if not found or
			// invalid
			List<Therapy> bnbTherapies = validateBnbTherapy(bnbNdc);

			// 2. Fetch and validate SP therapies
			// Assuming this method throws ResourceNotFoundException if not found or
			// invalid
			Map<String, Therapy> spTherapyMap = validateSpTherapies(spIds);

			// 3. All inputs valid, create crosswalks for the current request
			List<Crosswalk> newCrosswalks = spTherapyMap.values()
				.stream()
				.map(spTherapy -> Crosswalk.builder()
					.bnbNdcNumber(bnbNdc)
					.spProductId(spTherapy.getMaterialNumber())
					.isActive(true)
					.build())
				.toList();

			materialCrossWalkConfigRepository.saveAll(newCrosswalks);
			log.debug("Created {} new crosswalk entities for BNB NDC: {}", newCrosswalks.size(), bnbNdc);

			// 4. Set SP therapies as crosswalked for the current request
			int currentRequestSpCrosswalkedCount = 0;
			for (Therapy spTherapy : spTherapyMap.values()) {
				if (!spTherapy.getIsCrosswalked()) {
					spTherapy.setIsCrosswalked(true);
					allTherapiesToUpdate.add(spTherapy);
				}
				currentRequestSpCrosswalkedCount++;
			}

			// 5. Set BNB therapy as crosswalked for the current request
			for (Therapy therapy : bnbTherapies) {
				if (!therapy.getIsCrosswalked()) {
					therapy.setIsCrosswalked(true);
					allTherapiesToUpdate.add(therapy);
				}
			}

			totalCrosswalkedSpCount += currentRequestSpCrosswalkedCount;
			log.info(
					"Successfully processed CrosswalkRequest for BNB NDC: {}. SP Product IDs crosswalked in this request: {}",
					bnbNdc, currentRequestSpCrosswalkedCount);
		}

		// Save all collected therapies once after processing all crosswalks
		therapyRepository.saveAll(allTherapiesToUpdate);
		log.info(
				"Finished processing all CrosswalkRequests. Total unique therapies updated: {}. Total SP Product IDs crosswalked overall: {}",
				allTherapiesToUpdate.size(), totalCrosswalkedSpCount);

		return totalCrosswalkedSpCount;
	}

	private List<Therapy> validateBnbTherapy(String bnbNdc) throws ResourceNotFoundException {

		List<Therapy> bnbTherapy = therapyRepository.findByNdcNumberInAndChannelType(List.of(bnbNdc),
				ChannelType.BUY_AND_BILL);
		if (bnbTherapy.isEmpty()) {
			throw new ResourceNotFoundException("Invalid BNB NDC or channel type: " + bnbNdc);
		}
		return bnbTherapy;
	}

	private Map<String, Therapy> validateSpTherapies(List<String> spIds) throws ResourceNotFoundException {
		Map<String, Therapy> validSpTherapyMap = new HashMap<>();
		List<String> invalidSpIds = new ArrayList<>();
		List<String> alreadyCrosswalkedSpIds = new ArrayList<>();

		for (String spId : spIds) {
			Optional<Therapy> therapy = therapyRepository.findByMaterialNumberAndChannelType(spId,
					ChannelType.SPECIALITY_PHARMA);
			if (therapy.isPresent()) {
				Therapy spTherapy = therapy.get();
				if (Boolean.TRUE.equals(spTherapy.getIsCrosswalked())) {
					alreadyCrosswalkedSpIds.add(spId);
				}
				else {
					validSpTherapyMap.put(spId, spTherapy);
				}
			}
			else {
				invalidSpIds.add(spId);
			}
		}

		if (!invalidSpIds.isEmpty()) {
			throw new ResourceNotFoundException("Invalid SP Product IDs: " + String.join(", ", invalidSpIds));
		}

		if (!alreadyCrosswalkedSpIds.isEmpty()) {
			throw new IllegalArgumentException(
					"These SP Product IDs are already crosswalked: " + String.join(", ", alreadyCrosswalkedSpIds));
		}
		return validSpTherapyMap;
	}

	public Page<CrosswalkResponseDTO> findAll(Pageable paging, String query, List<String> status) {
		try {
			log.info(" Inside method - findAll, query: {} ", query);
			query = StringUtils.toRootLowerCase(query);
			String statusActive = CustomUtils.getStatus(status);

			return materialCrossWalkConfigRepository.findAllCrosswalks(paging, query, statusActive);
		}
		catch (Exception ex) {
			log.error("Exception occurred in fetching crosswalks", ex);
			throw ex;
		}
	}

	@Transactional
	@Override
	public void deLinkMaterialCrosswalk(Long crosswalkId) throws ResourceNotFoundException {

		try {
			log.info(" Inside method - deLinkMaterialCrosswalk, crosswalkId: {} ", crosswalkId);
			Crosswalk crosswalk = materialCrossWalkConfigRepository.findById(crosswalkId)
				.orElseThrow(() -> new ResourceNotFoundException("No Crosswalk found with id: " + crosswalkId));

			String bnbNdcNumber = crosswalk.getBnbNdcNumber();
			String spProductId = crosswalk.getSpProductId();

			therapyRepository.updateIsCrossWalkedForMaterialNumbersAndChannelType(false, Set.of(spProductId),
					ChannelType.SPECIALITY_PHARMA.name());

			if (materialCrossWalkConfigRepository.findByBnbNdcNumber(bnbNdcNumber).size() == 1)
				therapyRepository.updateIsCrossWalkedForNdcsAndChannelType(false, Set.of(bnbNdcNumber),
						ChannelType.BUY_AND_BILL.name());

			materialCrossWalkConfigRepository.deleteById(crosswalk.getId());
		}
		catch (Exception ex) {
			log.error("Exception occurred in deLinking crosswalk", ex);
			throw ex;
		}
	}

}
