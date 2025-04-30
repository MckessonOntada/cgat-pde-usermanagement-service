package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.*;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CSVGenerationException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CsvFileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogPriceRepository;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogServiceRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.CatalogService;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.internal.util.CollectionsUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.*;

@Service
@Slf4j
public class CatalogServiceImpl implements CatalogService {

	private static final String CATALOG_CONFIGURATION_NOT_FOUND = "Catalog configuration not found with the provided catalog attributes";

	private static final String UNKNOWN = "unknown";

	private final CatalogServiceRepository catalogServiceRepository;

	private final AccountRepository accountRepository;

	private final CatalogPriceRepository catalogPriceRepository;

	private final TherapyRepository therapyRepository;

	private final Validator validator;

	private final QuestionnaireService questionnaireService;

	public CatalogServiceImpl(CatalogServiceRepository catalogServiceRepository, AccountRepository accountRepository,
			CatalogPriceRepository catalogPriceRepository, TherapyRepository therapyRepository, Validator validator,
			QuestionnaireService questionnaireService) {
		this.catalogServiceRepository = catalogServiceRepository;
		this.accountRepository = accountRepository;
		this.catalogPriceRepository = catalogPriceRepository;
		this.therapyRepository = therapyRepository;
		this.validator = validator;
		this.questionnaireService = questionnaireService;
	}

	@Override
	public List<CatalogConfiguration> saveCatalogConfiguration(CatalogConfigurationDTO configurationDTO) {
		log.info("Starting to save {} catalog configurations.",
				configurationDTO.getCatalogConfigurationAttributes().size());

		List<CatalogConfiguration> catalogConfigurations = configurationDTO.getCatalogConfigurationAttributes()
			.stream()
			.map(attr -> {
				CatalogConfigId id = CatalogConfigId.builder()
					.soldToAccount(attr.getSoldToCustomer())
					.shipToAccount(attr.getShipToCustomer())
					.materialId(attr.getSapMaterial())
					.variant(StringUtils.isNotEmpty(attr.getVariant()) ? attr.getVariant() : UNKNOWN)
					.build();

				return CatalogConfiguration.builder().id(id).steps(configurationDTO.getSteps()).build();
			})
			.toList();

		log.info("Saving {} catalog configurations to the database.", catalogConfigurations.size());
		return catalogServiceRepository.saveAll(catalogConfigurations);
	}

	@Override
	public CatalogDetailsResponseDTO findCatalogConfiguration(String materialId, String variant, String shipToAccount,
			String soldToAccount, ChannelType channelType) throws ResourceNotFoundException {
		logSearchDetails(materialId, variant, shipToAccount, soldToAccount);
		CatalogConfigId id = buildCatalogConfigId(materialId, variant, shipToAccount, soldToAccount);
		CatalogResponseDTO catalogResponseDTO = catalogServiceRepository.findCatalogDetailsById(id)
			.orElseThrow(() -> new ResourceNotFoundException(CATALOG_CONFIGURATION_NOT_FOUND));

		List<Questionnaire> questionnaires = questionnaireService.getAllConfiguredQuestionnairesForSpecificType(
				QuestionnaireConfigurationType.Catalog, materialId, soldToAccount, shipToAccount, variant, channelType);

		CatalogDetailsResponseDTO catalogDetailsResponseDTO = CatalogDetailsResponseDTO.builder()
			.questionnaires(questionnaires)
			.build();

		catalogDetailsResponseDTO.setCatalogConfiguration(catalogResponseDTO.getCatalogConfiguration());
		catalogDetailsResponseDTO.setCatalogPrice(catalogResponseDTO.getCatalogPrice());
		return catalogDetailsResponseDTO;
	}

	@Override
	@Transactional
	public void deleteCatalogConfiguration(String materialId, String variant, String shipToAccount,
			String soldToAccount) throws ResourceNotFoundException {
		CatalogConfigId id = buildCatalogConfigId(materialId, variant, shipToAccount, soldToAccount);

		int deletedCount = catalogServiceRepository.removeCatalogConfigurationById(id);

		if (deletedCount == 0) {
			throw new ResourceNotFoundException(CATALOG_CONFIGURATION_NOT_FOUND);
		}

		log.info("Configuration deleted for materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
				materialId, variant, shipToAccount, soldToAccount);
	}

	@Override
	public CatalogConfiguration findCatalogConf(String materialId, String variant, String shipToAccount,
			String soldToAccount) {
		logSearchDetails(materialId, variant, shipToAccount, soldToAccount);
		CatalogConfigId id = buildCatalogConfigId(materialId, variant, shipToAccount, soldToAccount);
		return catalogServiceRepository.findById(id).orElseGet(() -> {
			log.info(
					"Catalog configuration not found for materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
					materialId, variant, shipToAccount, soldToAccount);
			return new CatalogConfiguration();
		});
	}

	private void logSearchDetails(String materialId, String variant, String shipToAccount, String soldToAccount) {
		log.info(
				"Searching for CatalogConfiguration with materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
				materialId, variant, shipToAccount, soldToAccount);
	}

	private CatalogConfigId buildCatalogConfigId(String materialId, String variant, String shipToAccount,
			String soldToAccount) {
		return CatalogConfigId.builder()
			.materialId(materialId)
			.soldToAccount(soldToAccount)
			.shipToAccount(shipToAccount)
			.variant(StringUtils.isNotEmpty(variant) ? variant : UNKNOWN)
			.build();
	}

	@Override
	@Transactional
	public CSVProcessingResult uploadSPCatalog(MultipartFile catalogsData) {
		validateCSVFile(catalogsData);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(catalogsData.getInputStream()));
				CSVParser csvParser = new CSVParser(reader,
						CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
			validateCSVFileData(csvParser);

			List<String[]> errorRecords = new ArrayList<>();
			Set<String> accountNumbers = new HashSet<>();
			Set<String> productIds = new HashSet<>();
			Set<SPCatalogLinkDTO> catalogsDataList = parseAndValidateCSVData(csvParser, errorRecords, accountNumbers,
					productIds);

			Map<String, Account> accountMap = getAccountMap(accountNumbers);
			Map<String, Therapy> materialMap = getTherapyMap(productIds);

			List<CatalogPrice> catalogsToUpdate = new ArrayList<>();
			processCatalogActions(catalogsDataList, accountMap, materialMap, catalogsToUpdate, errorRecords);

			if (CollectionsUtils.hasItems(catalogsToUpdate)) {
				catalogPriceRepository.saveAll(catalogsToUpdate);
			}
			log.info("Parsed records: {} | Errors: {} | Records update/saved: {}", catalogsDataList.size(),
					errorRecords.size(), catalogsToUpdate.size());

			CSVProcessingResult result = new CSVProcessingResult();
			result.setErrors(errorRecords);
			result.setErrorFileContent(generateErrorCSV(errorRecords));
			return result;
		}
		catch (IOException | IllegalArgumentException e) {
			log.error("Error while reading/parsing CSV file: {}", e.getMessage(), e);
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.missing"));
		}
	}

	@Override
	public void linkSPCatalog(List<CatalogRequestDTO> catalogList) {
		Set<String> sapMaterials = new HashSet<>();
		Set<String> soldToCustomers = new HashSet<>();

		for (CatalogRequestDTO dto : catalogList) {
			sapMaterials.add(dto.getSapMaterial());
			soldToCustomers.add(dto.getSoldToCustomer());
		}

		Map<String, Account> accountMap = getAccountMap(soldToCustomers);
		Map<String, Therapy> materialMap = getTherapyMap(sapMaterials);

        List<CatalogPrice> catalogPrices = new ArrayList<>(catalogList.stream()
                .map(dto -> catalogPriceRepository.findBySapMaterialAndSoldToCustomerAndChannelType(dto.getSapMaterial(),
                        dto.getSoldToCustomer(), ChannelType.SPECIALITY_PHARMA))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(catalog -> catalog.setDeleted(false))
                .toList());

		catalogPrices.add()
	}

	private void validateCSVFile(MultipartFile file) {
		String contentType = file.getContentType();
		String originalFilename = file.getOriginalFilename();

		boolean isValidType = StringUtils.isNotEmpty(contentType)
				&& (contentType.equals("text/csv") || contentType.equals("application/vnd.ms-excel"));

		boolean isValidExtension = StringUtils.isNotEmpty(originalFilename)
				&& originalFilename.toLowerCase().endsWith(".csv");

		if (!isValidType || !isValidExtension) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.type"));
		}

		if (file.isEmpty()) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.empty"));
		}
	}

	private Set<SPCatalogLinkDTO> parseAndValidateCSVData(CSVParser csvParser, List<String[]> errorRecords,
			Set<String> accountNumbers, Set<String> productIds) {
		Set<SPCatalogLinkDTO> catalogsDataList = new HashSet<>();
		boolean isEmpty = true;
		for (CSVRecord catalogRecord : csvParser) {

			isEmpty = false;
			if (ObjectUtils.isEmpty(catalogRecord) || catalogRecord.stream().allMatch(String::isBlank))
				continue;

			String accountNumber = catalogRecord.get(HEADER_ACCOUNT_NUMBER).trim();
			String spProductId = catalogRecord.get(HEADER_PRODUCT_ID).trim();
			String status = catalogRecord.get(HEADER_STATUS).trim();

			SPCatalogLinkDTO catalog = new SPCatalogLinkDTO(accountNumber, spProductId, status);
			Set<ConstraintViolation<SPCatalogLinkDTO>> violations = validator.validate(catalog);

			if (CollectionsUtils.hasItems(violations)) {
				String errorMsg = violations.stream()
					.map(ConstraintViolation::getMessage)
					.collect(Collectors.joining("; "));
				errorRecords.add(new String[] { accountNumber, spProductId, status, errorMsg });
			}
			else {
				accountNumbers.add(accountNumber);
				productIds.add(spProductId);
				catalogsDataList.add(catalog);
			}
		}
		if (isEmpty) {
			log.error("The uploaded file is empty or contains no data.");
			throw new CsvFileNotValidException(List.of("invalid.csv.file.empty"));
		}

		return catalogsDataList;
	}

	private Map<String, Account> getAccountMap(Set<String> accountNumbers) {
		return accountRepository
			.findByAccountNumberInAndChannelTypeAndIsActive(accountNumbers, ChannelType.SPECIALITY_PHARMA, true)
			.stream()
			.collect(Collectors.toMap(Account::getAccountNumber, Function.identity()));
	}

	private Map<String, Therapy> getTherapyMap(Set<String> productIds) {
		return therapyRepository
			.findByMaterialNumberInAndChannelTypeAndItemStatus(productIds, ChannelType.SPECIALITY_PHARMA,
					THERAPY_ITEM_STATUS_ACTIVE)
			.stream()
			.collect(Collectors.toMap(Therapy::getMaterialNumber, Function.identity()));
	}

	private void processCatalogActions(Set<SPCatalogLinkDTO> catalogsDataList, Map<String, Account> accountMap,
			Map<String, Therapy> materialMap, List<CatalogPrice> catalogsToUpdate, List<String[]> errorRecords) {

		List<CatalogPrice> existingCatalogs = catalogsDataList.stream()
			.map(dto -> catalogPriceRepository.findBySapMaterialAndSoldToCustomerAndChannelType(dto.getProductId(),
					dto.getAccountNumber(), ChannelType.SPECIALITY_PHARMA))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toList();

		Map<Pair<String, String>, CatalogPrice> existingCatalogMap = existingCatalogs.stream()
			.collect(Collectors.toMap(c -> Pair.of(c.getSoldToCustomer(), c.getSapMaterial()), Function.identity()));

		for (SPCatalogLinkDTO dto : catalogsDataList) {
			processCatalog(dto, accountMap, materialMap, existingCatalogMap, catalogsToUpdate, errorRecords);
		}
	}

	private void processCatalog(SPCatalogLinkDTO dto, Map<String, Account> accountMap, Map<String, Therapy> materialMap,
			Map<Pair<String, String>, CatalogPrice> existingCatalogMap, List<CatalogPrice> catalogsToUpdate,
			List<String[]> errorRecords) {

		String accountNumber = dto.getAccountNumber();
		String productId = dto.getProductId();
		String status = dto.getStatus();

		Account account = accountMap.get(accountNumber);
		Therapy therapy = materialMap.get(productId);
		Pair<String, String> key = Pair.of(accountNumber, productId);
		CatalogPrice existing = existingCatalogMap.get(key);

		if (CATALOG_ACTION_STATUS_UNLINK.equalsIgnoreCase(status)) {
			handleUnlink(accountNumber, productId, existing, catalogsToUpdate, errorRecords);
			return;
		}

		if (account == null || therapy == null) {
			handleMissingReferences(accountNumber, productId, status, account, therapy, errorRecords);
			return;
		}

		if (CATALOG_ACTION_STATUS_LINK.equalsIgnoreCase(status)) {
			handleLink(account, therapy, existing, catalogsToUpdate);
		}
	}

	private void handleUnlink(String accountNumber, String productId, CatalogPrice existing,
			List<CatalogPrice> catalogsToUpdate, List<String[]> errorRecords) {

		if (ObjectUtils.isNotEmpty(existing)) {
			if (Boolean.FALSE.equals(existing.isDeleted())) {
				existing.setDeleted(true);
				catalogsToUpdate.add(existing);
			}
		}
		else {
			errorRecords.add(new String[] { accountNumber, productId, CATALOG_ACTION_STATUS_UNLINK,
					"No matching catalog record found" });
		}
	}

	private void handleMissingReferences(String accountNumber, String productId, String status, Account account,
			Therapy therapy, List<String[]> errorRecords) {

		StringJoiner errorMsg = new StringJoiner(" & ");
		if (account == null)
			errorMsg.add("Account not found");
		if (therapy == null)
			errorMsg.add("Material not found");

		errorRecords.add(new String[] { accountNumber, productId, status, errorMsg.toString() });
	}

	private void handleLink(Account account, Therapy therapy, CatalogPrice existing,
			List<CatalogPrice> catalogsToUpdate) {

		if (ObjectUtils.isNotEmpty(existing)) {
			if (Boolean.TRUE.equals(existing.isDeleted())) {
				existing.setDeleted(false);
				catalogsToUpdate.add(existing);
			}
		}
		else {
			catalogsToUpdate.add(buildCatalogPrice(account, therapy));
		}
	}

	private CatalogPrice buildCatalogPrice(Account account, Therapy therapy) {
		return CatalogPrice.builder()
			.channelType(ChannelType.SPECIALITY_PHARMA)
			.ndc(therapy.getNdcNumber())
			.sapMaterial(therapy.getMaterialNumber())
			.materialDescriptionInLanguageEn(therapy.getMaterialDescription())
			.soldToCustomer(account.getSapCustomer())
			.soldToCustomerName(account.getAccountName())
			.shipToCustomer(account.getSapCustomer())
			.shipToCustomerName(account.getAccountName())
			.salesOrg(account.getSalesOrg())
			.distChannel(account.getDistributionChannel())
			.division(account.getDivision())
			.materialIdentifierForCgat("N")
			.sellUnit("NA")
			.netPrice(BigDecimal.ZERO)
			.pricingDate(LocalDate.now())
			.wacPrice(BigDecimal.ZERO)
			.priceUnit("NA")
			.currency("NA")
			.inventoryType("1")
			.deleted(false)
			.tenantId(Math.toIntExact(account.getPracticeId()))
			.build();
	}

	private void validateCSVFileData(CSVParser csvParser) {
		if (!csvParser.getHeaderMap().keySet().containsAll(REQUIRED_HEADERS)) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.missing"));
		}

		boolean isValidHeaderStructure = Stream
			.of(Map.entry(HEADER_ACCOUNT_NUMBER, 1), Map.entry(HEADER_PRODUCT_ID, 2), Map.entry(HEADER_STATUS, 5))
			.allMatch(entry -> {
				Integer actualIndex = csvParser.getHeaderMap().get(entry.getKey());
				return entry.getValue().equals(actualIndex);
			});

		if (!isValidHeaderStructure) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.unstructured"));
		}
	}

	private byte[] generateErrorCSV(List<String[]> errorRecords) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.builder()
							.setHeader(HEADER_ACCOUNT_NUMBER, HEADER_PRODUCT_ID, HEADER_STATUS, HEADER_ERROR_MESSAGE)
							.build())) {

			for (String[] errors : errorRecords) {
				csvPrinter.printRecord((Object[]) errors);
			}

			csvPrinter.flush();
			return out.toByteArray();

		}
		catch (IOException e) {
			throw new CSVGenerationException("Failed to generate error CSV", e);
		}
	}

}
