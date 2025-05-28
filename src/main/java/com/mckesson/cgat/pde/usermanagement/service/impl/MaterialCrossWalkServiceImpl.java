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
		List<String> existingSPNDCs = fetchSPTherapies(crossWalkConfigsToAdd, crossWalkConfigsToDelete);

		Map<String, Crosswalk> spNDCToConfigMap = fetchExistingCrossWalkConfigsForSPs(existingSPNDCs);

		List<Crosswalk> entitiesToDelete = validateCrossWalkConstraintsAndGetConfigs(crossWalkConfigsToDelete,
				failedCrossWalks, existingBnbNDCs, existingSPNDCs, spNDCToConfigMap);

		List<Crosswalk> entitiesToAdd = validateCrossWalkConstraintsAndGetConfigs(crossWalkConfigsToAdd,
				failedCrossWalks, existingBnbNDCs, existingSPNDCs, spNDCToConfigMap);

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
		List<String> expectedHeaders = List.of("Buy & Bill Material ID", "Buy & Bill Material Description",
				"Buy & Bill NDC", "SP Product ID", "SP Product Name", "SP NDC", "Status");

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
				validateForDuplicateSPRows(crossWalkConfig.getSpNDC(), crossWalkConfigsToAdd);
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

	private void validateForDuplicateSPRows(String spProductNdc,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigs) {
		boolean exists = crossWalkConfigs.stream().anyMatch(record -> spProductNdc.equals(record.getSpNDC()));
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
		List<String> distinctSPNDC = Stream.concat(crossWalkConfigsToAdd.stream(), crossWalkConfigsToDelete.stream())
			.map(MaterialCrossWalkConfigRecordDTO::getSpNDC)
			.distinct()
			.toList();
		List<Therapy> existingTherapies = therapyRepository.findByNdcNumberInAndChannelType(distinctSPNDC,
				ChannelType.SPECIALITY_PHARMA);

		return existingTherapies.stream().map(Therapy::getNdcNumber).toList();

	}

	private Map<String, Crosswalk> fetchExistingCrossWalkConfigsForSPs(List<String> existingSPNDCs) {
		List<Crosswalk> existingCrossWalkConfigs = materialCrossWalkConfigRepository
			.findMaterialCrossWalkConfigBySpNdcNumberIn(existingSPNDCs);
		return existingCrossWalkConfigs.stream().collect(Collectors.toMap(Crosswalk::getSpNdcNumber, config -> config));
	}

	private List<Crosswalk> validateCrossWalkConstraintsAndGetConfigs(
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigs,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks, List<String> existingBnbMaterialNDC,
			List<String> existingSPProductNDCs, Map<String, Crosswalk> spProductNDCToConfigMap) {
		List<Crosswalk> crossWalkConfigList = new ArrayList<>();

		Iterator<MaterialCrossWalkConfigRecordDTO> iterator = crossWalkConfigs.iterator();
		while (iterator.hasNext()) {

			MaterialCrossWalkConfigRecordDTO crossWalkConfig = iterator.next();
			String bnbMaterialNDC = crossWalkConfig.getBnbNDC();
			String spNDC = crossWalkConfig.getSpNDC();

			try {

				if (!existingBnbMaterialNDC.contains(bnbMaterialNDC)) {
					throw new IllegalArgumentException("Invalid Buy & Bill Material NDC");
				}

				if (!existingSPProductNDCs.contains(spNDC)) {
					throw new IllegalArgumentException("Invalid SP Product NDC");
				}
				Crosswalk config = Crosswalk.builder()
					.bnbNdcNumber(bnbMaterialNDC)
					.spNdcNumber(spNDC)
					.isActive(true)
					.build();

				Crosswalk existingConfig = spProductNDCToConfigMap.get(spNDC);
				if (existingConfig != null) {
					if (!existingConfig.getBnbNdcNumber().equals(bnbMaterialNDC)) {
						throw new IllegalArgumentException(
								"The SP product NDC is already mapped to a Material, please review the existing mapping and retry.");
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
		List<String> spProductNDCs = entitiesToDelete.stream().map(Crosswalk::getSpNdcNumber).distinct().toList();
		if (!CollectionUtils.isEmpty(spProductNDCs)) {
			materialCrossWalkConfigRepository.deleteBySpNdcNumberIn(spProductNDCs);
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
		Set<String> addedSpProductNDCs = addedEntities.stream()
			.map(Crosswalk::getSpNdcNumber)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		Set<String> deletedSpProductNDCs = deletedEntities.stream()
			.map(Crosswalk::getSpNdcNumber)
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
		if (!addedSpProductNDCs.isEmpty()) {
			therapyRepository.updateIsCrossWalkedForNdcsAndChannelType(true, addedSpProductNDCs,
					String.valueOf(ChannelType.SPECIALITY_PHARMA));
		}

		if (!deletedSpProductNDCs.isEmpty()) {
			therapyRepository.updateIsCrossWalkedForNdcsAndChannelType(false, deletedSpProductNDCs,
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
						CSVFormat.DEFAULT.withHeader("Buy & Bill Material ID", "Buy & Bill Description",
								"Buy & Bill NDC", "SP Product ID", "SP Product Name", "SP NDC", "Error Reason"))) {

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

	@Override
	@Transactional
	public int createCrosswalks(CrosswalkRequestDTO request) throws ResourceNotFoundException {
		String bnbNdc = request.getBnbMaterialNdc();
		List<String> spNdcs = request.getSpProductNdcs();

		// Validate BNB therapy
		Therapy bnbTherapy = validateBnbTherapy(bnbNdc);

		// Fetch and validate SP therapies
		Map<String, Therapy> spTherapyMap = validateSpTherapies(spNdcs);

		// All inputs valid, create crosswalks
		List<Crosswalk> newCrosswalks = spTherapyMap.values()
			.stream()
			.map(spTherapy -> Crosswalk.builder()
				.bnbNdcNumber(bnbNdc)
				.spNdcNumber(spTherapy.getNdcNumber())
				.isActive(true)
				.build())
			.toList();

		materialCrossWalkConfigRepository.saveAll(newCrosswalks);

		// Set SP therapies as crosswalked
		List<Therapy> therapiesToUpdate = new ArrayList<>();
		int crosswalkedSpCount = 0; // Keep track of crosswalked SP count
		for (Therapy spTherapy : spTherapyMap.values()) {
			spTherapy.setIsCrosswalked(true);
			therapiesToUpdate.add(spTherapy);
			crosswalkedSpCount++; // Increment count
		}
		// Set BNB therapy as crosswalked
		bnbTherapy.setIsCrosswalked(true);
		therapiesToUpdate.add(bnbTherapy);
		therapyRepository.saveAll(therapiesToUpdate);

		log.info("Crosswalked {} SP Product NDCs for BNB NDC: {}", crosswalkedSpCount, bnbNdc);
		return crosswalkedSpCount;

	}

	private Therapy validateBnbTherapy(String bnbNdc) throws ResourceNotFoundException {
		Optional<Therapy> bnbTherapy = therapyRepository.findByNdcNumberAndChannelType(bnbNdc,
				ChannelType.BUY_AND_BILL);
		if (bnbTherapy.isEmpty()) {
			throw new ResourceNotFoundException("Invalid BNB NDC or channel type: " + bnbNdc);
		}
		return bnbTherapy.get();
	}

	private Map<String, Therapy> validateSpTherapies(List<String> spNdcs) throws ResourceNotFoundException {
		Map<String, Therapy> validSpTherapyMap = new HashMap<>();
		List<String> invalidSpNdcs = new ArrayList<>();
		List<String> alreadyCrosswalkedSpNdcs = new ArrayList<>();

		for (String spNdc : spNdcs) {
			Optional<Therapy> therapy = therapyRepository.findByNdcNumberAndChannelType(spNdc,
					ChannelType.SPECIALITY_PHARMA);
			if (therapy.isPresent()) {
				Therapy spTherapy = therapy.get();
				if (Boolean.TRUE.equals(spTherapy.getIsCrosswalked())) {
					alreadyCrosswalkedSpNdcs.add(spNdc);
				}
				else {
					validSpTherapyMap.put(spNdc, spTherapy);
				}
			}
			else {
				invalidSpNdcs.add(spNdc);
			}
		}

		if (!invalidSpNdcs.isEmpty()) {
			throw new ResourceNotFoundException("Invalid SP Product NDCs: " + String.join(", ", invalidSpNdcs));
		}

		if (!alreadyCrosswalkedSpNdcs.isEmpty()) {
			throw new IllegalArgumentException(
					"These SP Product NDCs are already crosswalked: " + String.join(", ", alreadyCrosswalkedSpNdcs));
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
			String spNdcNumber = crosswalk.getSpNdcNumber();

			therapyRepository.updateIsCrossWalkedForNdcsAndChannelType(false, Set.of(spNdcNumber),
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
