package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialCrossWalkConfigRecordDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.MaterialCrossWalkAction;
import com.mckesson.cgat.pde.usermanagement.model.MaterialCrossWalkConfig;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.MaterialCrossWalkConfigRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.MaterialCrossWalkService;
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

	public MaterialCrossWalkServiceImpl(MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository,
			TherapyRepository therapyRepository) {
		this.materialCrossWalkConfigRepository = materialCrossWalkConfigRepository;
		this.therapyRepository = therapyRepository;
	}

	@Override
	@Transactional
	public Boolean uploadCrossWalks(MultipartFile crossWalkDataFile, ByteArrayOutputStream outputStream) {
		List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToAdd = new ArrayList<>();
		List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToDelete = new ArrayList<>();

		Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks = new LinkedHashMap<>();

		parseCrossWalkDataFile(crossWalkDataFile, crossWalkConfigsToAdd, crossWalkConfigsToDelete, failedCrossWalks);

		/*
		 * Steps: 1) Validation if provided Bnb material and sp material exists in db. 2)
		 * fetch existing cross walk configs from DB to prevent the linking of existing SP
		 * product id to different BnB material. 3) Remove configs from db for records
		 * status = Unlink. 4) Add configs in db in batch for records status = Link 5)
		 * Update crossWalk boolean for BnBMaterials and SpProductIds
		 *
		 */
		List<String> existingBnbMaterialNumbers = fetchBnbTherapies(crossWalkConfigsToAdd, crossWalkConfigsToDelete);
		List<String> existingSPProductIds = fetchSPTherapies(crossWalkConfigsToAdd, crossWalkConfigsToDelete);

		Map<String, MaterialCrossWalkConfig> spProductIdToConfigMap = fetchExistingCrossWalkConfigsForSPs(
				existingSPProductIds);

		List<MaterialCrossWalkConfig> entitiesToDelete = validateCrossWalkConstraintsAndGetConfigs(
				crossWalkConfigsToDelete, failedCrossWalks, existingBnbMaterialNumbers, existingSPProductIds,
				spProductIdToConfigMap);

		List<MaterialCrossWalkConfig> entitiesToAdd = validateCrossWalkConstraintsAndGetConfigs(crossWalkConfigsToAdd,
				failedCrossWalks, existingBnbMaterialNumbers, existingSPProductIds, spProductIdToConfigMap);

		removeConfigsFromDB(entitiesToDelete);

		saveOrUpdateInBatches(entitiesToAdd, BATCH_SIZE);

		updateCrossWalkFlagForMaterials(entitiesToDelete, entitiesToAdd);

		createOutputCSV(outputStream, failedCrossWalks);

		return failedCrossWalks.isEmpty();
	}

	private void parseCrossWalkDataFile(MultipartFile crossWalkDataFile,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToAdd,
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigsToDelete,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(crossWalkDataFile.getInputStream()));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			for (CSVRecord record : csvParser) {
				if (record.stream().allMatch(String::isBlank)) {
					break;
				}
				List<String> recordData = record.toList();

				createCrossWalkConfigFromRecord(recordData, crossWalkConfigsToAdd, crossWalkConfigsToDelete,
						failedCrossWalks);
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

	private Map<String, MaterialCrossWalkConfig> fetchExistingCrossWalkConfigsForSPs(
			List<String> existingSPProductIds) {
		List<MaterialCrossWalkConfig> existingCrossWalkConfigs = materialCrossWalkConfigRepository
			.findMaterialCrossWalkConfigBySpProductIdIn(existingSPProductIds);
		return existingCrossWalkConfigs.stream()
			.collect(Collectors.toMap(MaterialCrossWalkConfig::getSpProductId, config -> config));
	}

	private List<MaterialCrossWalkConfig> validateCrossWalkConstraintsAndGetConfigs(
			List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigs,
			Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks, List<String> existingBnbMaterialNumbers,
			List<String> existingSPProductIds, Map<String, MaterialCrossWalkConfig> spProductIdToConfigMap) {
		List<MaterialCrossWalkConfig> crossWalkConfigList = new ArrayList<>();

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
				MaterialCrossWalkConfig config = MaterialCrossWalkConfig.builder()
					.bnbMaterialNumber(bnbMaterialNumber)
					.spProductId(spProductId)
					.build();

				MaterialCrossWalkConfig existingConfig = spProductIdToConfigMap.get(spProductId);
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

	@Transactional
	private void removeConfigsFromDB(List<MaterialCrossWalkConfig> entitiesToDelete) {
		List<String> spProductIds = entitiesToDelete.stream()
			.map(MaterialCrossWalkConfig::getSpProductId)
			.distinct()
			.toList();
		if (!CollectionUtils.isEmpty(spProductIds)) {
			materialCrossWalkConfigRepository.deleteBySpProductIdIn(spProductIds);
		}

	}

	private void saveOrUpdateInBatches(List<MaterialCrossWalkConfig> configs, int batchSize) {
		for (int i = 0; i < configs.size(); i += batchSize) {
			int end = Math.min(i + batchSize, configs.size());
			List<MaterialCrossWalkConfig> batch = configs.subList(i, end);
			materialCrossWalkConfigRepository.saveAll(batch);
			materialCrossWalkConfigRepository.flush();
		}
	}

	private void updateCrossWalkFlagForMaterials(List<MaterialCrossWalkConfig> deletedEntities,
			List<MaterialCrossWalkConfig> addedEntities) {
		Set<String> spProductIdsToUpdate = new HashSet<>();
		Set<String> bnbMaterialNumbersToUpdate = new HashSet<>();

		// Collect SP Product IDs
		spProductIdsToUpdate
			.addAll(deletedEntities.stream().map(MaterialCrossWalkConfig::getSpProductId).collect(Collectors.toSet()));
		spProductIdsToUpdate
			.addAll(addedEntities.stream().map(MaterialCrossWalkConfig::getSpProductId).collect(Collectors.toSet()));

		// Collect BnB Material Numbers
		bnbMaterialNumbersToUpdate.addAll(deletedEntities.stream()
			.map(MaterialCrossWalkConfig::getBnbMaterialNumber)
			.collect(Collectors.toSet()));
		bnbMaterialNumbersToUpdate.addAll(
				addedEntities.stream().map(MaterialCrossWalkConfig::getBnbMaterialNumber).collect(Collectors.toSet()));

		// Fetch existing SP Product IDs and BnB Material Numbers in batch
		Set<String> existingSpProductIds = new HashSet<>(
				materialCrossWalkConfigRepository.findExistingSpProductIds(spProductIdsToUpdate));

		Set<String> existingBnbMaterialNumbers = new HashSet<>(
				materialCrossWalkConfigRepository.findExistingBnbMaterialNumbers(bnbMaterialNumbersToUpdate));

		// Group SP Product IDs by 'exists' flag
		Map<Boolean, Set<String>> spProductGroups = spProductIdsToUpdate.stream()
			.collect(Collectors.groupingBy(existingSpProductIds::contains, Collectors.toSet()));

		// Group BnB Material Numbers by 'exists' flag
		Map<Boolean, Set<String>> bnbMaterialGroups = bnbMaterialNumbersToUpdate.stream()
			.collect(Collectors.groupingBy(existingBnbMaterialNumbers::contains, Collectors.toSet()));

		// Bulk update SP Product IDs
		spProductGroups.forEach((exists, ids) -> {
			if (!ids.isEmpty()) {
				therapyRepository.updateIsCrossWalkedForMaterialNumbersAndChannelType(exists, ids,
						String.valueOf(ChannelType.SPECIALITY_PHARMA));
			}
		});

		// Bulk update BnB Material Numbers
		bnbMaterialGroups.forEach((exists, ids) -> {
			if (!ids.isEmpty()) {
				therapyRepository.updateIsCrossWalkedForMaterialNumbersAndChannelType(exists, ids,
						String.valueOf(ChannelType.BUY_AND_BILL));
			}
		});
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
