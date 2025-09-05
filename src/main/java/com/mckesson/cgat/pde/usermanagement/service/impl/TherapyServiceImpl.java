package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.*;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.mapper.CatalogPriceMapper;
import com.mckesson.cgat.pde.usermanagement.mapper.TradeCatalogResponseMapper;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.repository.*;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.PracticeUtils;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.internal.util.CollectionsUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.*;

@Service
@Slf4j
public class TherapyServiceImpl implements TherapyService {

	private final TherapyRepository therapyRepository;

	private final CatalogPriceRepository catalogPriceRepository;

	private final CatalogPriceMapper catalogPriceMapper;

	private final TherapyConfigRepository therapyConfigRepository;

	private final QuestionnaireService questionnaireService;

	private final ShippingLocationRepository shippingLocationRepository;

	private final MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository;

	private final SPTherapyConfigRepository spTherapyConfigRepository;

	private final PracticeUtils practiceUtils;

	private final String MANUFACTURER_LOGO_BASE_URL = "logos/manufacturer";

	private final String MANUFACTURER_LOGO_EXTENSION = "jpg";

	public TherapyServiceImpl(TherapyRepository therapyRepository, CatalogPriceRepository catalogPriceRepository,
			CatalogPriceMapper catalogPriceMapper, TherapyConfigRepository therapyConfigRepository,
			QuestionnaireService questionnaireService, ShippingLocationRepository shippingLocationRepository,
			MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository,
			SPTherapyConfigRepository spTherapyConfigRepository, PracticeUtils practiceUtils) {
		this.therapyRepository = therapyRepository;
		this.catalogPriceRepository = catalogPriceRepository;
		this.catalogPriceMapper = catalogPriceMapper;
		this.therapyConfigRepository = therapyConfigRepository;
		this.questionnaireService = questionnaireService;
		this.shippingLocationRepository = shippingLocationRepository;
		this.materialCrossWalkConfigRepository = materialCrossWalkConfigRepository;
		this.spTherapyConfigRepository = spTherapyConfigRepository;
		this.practiceUtils = practiceUtils;
	}

	public Page<TradeCatalogResponseDTO> findAllTherapies(Long userId, String query, List<String> salesOrg,
			Pageable paging, List<String> customerIdentifier) {
		Page<TherapyCatalogResponseDTO> projections = therapyRepository.findAllTherapies(userId,
				StringUtils.toRootLowerCase(query), salesOrg, customerIdentifier,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(), paging);

		Page<TradeCatalogResponseDTO> map = projections.map(TradeCatalogResponseMapper::mapToTradeCatalogResponse);
		return map;

	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, String ndcNumber, String query, Pageable paging,
			ChannelType channelType, String program, ChannelType ndcChannelType) {

		boolean isCrossWalkedTherapySelected = !ndcChannelType.equals(channelType);

		if (ChannelType.SPECIALITY_PHARMA.equals(channelType) && isCrossWalkedTherapySelected) {
			return therapyRepository.findAllSoldToAccountsForSP(userId, ndcNumber, StringUtils.toRootLowerCase(query),
					paging, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
		}

		if ("340B".equals(program)) {
			program = "340";
		}

		return therapyRepository.findAllSoldToAccounts(userId, ndcNumber, StringUtils.toRootLowerCase(query), paging,
				channelType.name(), program, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, String ndcNumber, Pageable paging,
			String query, ChannelType channelType) {
		String loweredCaseQuery = StringUtils.toRootLowerCase(query);
		if (channelType.equals(ChannelType.SPECIALITY_PHARMA)) {
			Page<ShippingLocation> shippingLocations = shippingLocationRepository.findAllByQueryText(loweredCaseQuery,
					paging, OrganizationContext.getCurrentOrgId(), Long.valueOf(soldToAccountId));
			List<AccountsResponseDTO> mappedShipTo = shippingLocations.getContent()
				.stream()
				.map(this::mapToAccountsResponse)
				.collect(Collectors.toList());
			return new PageImpl<>(mappedShipTo, paging, mappedShipTo.size());
		}
		return therapyRepository.findAllShipToAccounts(soldToAccountId, ndcNumber, loweredCaseQuery, paging,
				AccountPartnerFunction.SHIP_TO.getPartnerFunctionType());
	}

	private AccountsResponseDTOImpl mapToAccountsResponse(ShippingLocation location) {
		return AccountsResponseDTOImpl.builder()
			.id(String.valueOf(location.getId()))
			.accountNumber(String.valueOf(location.getId()))
			.accountName(location.getName())
			.city(location.getAddress().getCity())
			.street(location.getAddress().getAddress1())
			.street2(location.getAddress().getAddress2())
			.state(location.getAddress().getState())
			.zipCode(location.getAddress().getZip())
			.build();
	}

	public Therapy findById(Long id) throws ResourceNotFoundException {
		return therapyRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + id));
	}

	@Override
	public Page<VariantsResponseDTO> findAllVariantsLinkedToShipToAccountsForNDC(String soldToAccountId,
			String shipToAccountId, String ndcNumber, Pageable paging, ChannelType channelType) {
		try {
			log.info(" Inside method - findAllVariantsLinkedToShipToAccountsForNDC..");
			log.info("soldToAccountId: {} , shipToAccountId: {} and ndcNumber: {}", soldToAccountId, shipToAccountId,
					ndcNumber);
			List<VariantsResponseDTO> variantsResponse;

			Page<CatalogPrice> allVariants;
			if (ChannelType.BUY_AND_BILL.equals(channelType)) {
				allVariants = catalogPriceRepository.findAllCatalogPricesLinkedToShipToAccountsForNDC(soldToAccountId,
						shipToAccountId, ndcNumber, paging);
				variantsResponse = toResponseDtoVariantsFromCatalogs(allVariants.getContent());
			}
			else {
				List<Therapy> crosswalkedTherapies = therapyRepository.findCrosswalkedTherapies(ndcNumber,
						Long.parseLong(UserSecurityUtils.getLoggedInUser().getId()));
				variantsResponse = toResponseDtoVariantsListFromTherapies(crosswalkedTherapies);

			}
			return new PageImpl<>(variantsResponse, paging, variantsResponse.size());
		}
		catch (Exception ex) {
			log.error(
					"Exception occurred in fetching variants linked to soldToAccountId, shipToAccountId and therapyId",
					ex);
			throw ex;
		}
	}

	private List<VariantsResponseDTO> toResponseDtoVariantsListFromTherapies(List<Therapy> therapies) {
		if (therapies == null) {
			return null;
		}

		return therapies.stream()
			.map(therapy -> VariantsResponseDTO.builder()
				.variant(therapy.getMaterialDescription())
				.variantDescription(therapy.getMaterialDescription())
				.materialNumber(therapy.getMaterialNumber())
				.therapyId(therapy.getId())
				.build())
			.toList();
	}

	private List<VariantsResponseDTO> toResponseDtoVariantsFromCatalogs(List<CatalogPrice> catalogPriceList) {
		if (catalogPriceList == null) {
			return null;
		}

		return catalogPriceList.stream()
			.map(catalogPrice -> VariantsResponseDTO.builder()
				.variant(catalogPrice.getVariant())
				.variantDescription(catalogPrice.getVariantDescription())
				.materialNumber(catalogPrice.getSapMaterial())
				.therapyId(null)
				.build())
			.toList();
	}

	@Override
	public CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForNdc(String soldToAccountId,
			String shipToAccountId, String variantType, String ndcNumber) {
		try {
			log.info(" Inside method - findPriceOfVariantLinkedToShipToAccountsForNdc..");
			log.info("soldToAccountId: {} , shipToAccountId: {} , variantType: {} and ndcNumber: {}", soldToAccountId,
					shipToAccountId, variantType, ndcNumber);
			Page<CatalogPriceResponse> catalogPriceResponsePage = catalogPriceRepository
				.findPriceOfVariantLinkedToShipToAccountsForNdc(soldToAccountId, shipToAccountId, ndcNumber,
						variantType, PageRequest.of(0, 1));
			CatalogPriceResponse catalogPrice = catalogPriceResponsePage.getContent().stream().findFirst().orElse(null);
			CatalogPriceResponseDto responseDto = null;
			if (catalogPrice != null) {
				responseDto = catalogPriceMapper.toResponseDto(catalogPrice.getCatalogPrice());
				if (responseDto != null) {
					responseDto.setProductLevelPricing(Objects.equals(catalogPrice.getProductLevelPricing(), "Y"));

				}
			}

			return responseDto;
		}
		catch (Exception e) {
			log.error(
					"Exception occurred in fetching Price of variant linked to soldToAccountId, shipToAccountId and therapyId and exception details",
					e);
			throw e;
		}
	}

	@Override
	public Therapy findByMaterialNumber(String materialNumber, ChannelType channelType)
			throws ResourceNotFoundException {
		return therapyRepository.findByMaterialNumberAndChannelType(materialNumber, channelType)
			.orElseThrow(() -> new ResourceNotFoundException(
					"Therapy not found with materialNumber: " + materialNumber + " and channelType: " + channelType));
	}

	@Override
	public List<Therapy> findByNdcNumber(String ndcNumber, ChannelType channelType) throws ResourceNotFoundException {
		List<Therapy> therapies = therapyRepository.findByNdcNumberAndChannelType(ndcNumber, channelType);
		if (therapies == null || therapies.isEmpty())
			throw new ResourceNotFoundException(
					"Therapy not found with Ndc Number: " + ndcNumber + " and channelType: " + channelType);
		return therapies;
	}

	public ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, String ndcNumber, ChannelType channelType)
			throws ResourceNotFoundException {

		if (ChannelType.SPECIALITY_PHARMA.equals(channelType)) {
			return ProgramChannelResponseDTO.builder()
				.channels(Collections.singleton(ChannelType.SPECIALITY_PHARMA.name()))
				.programs(new HashSet<>())
				.build();
		}

		List<ProgramChannelProjectionDTO> allPrograms = therapyRepository.findAllPrograms(userId, ndcNumber,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(), ChannelType.BUY_AND_BILL.name());

		Set<String> uniquePrograms = null != allPrograms ? (allPrograms.stream().map(programChannelProjectionDTO -> {
			if ("340".equals(programChannelProjectionDTO.getProgram())) {
				return "340B";
			}
			return programChannelProjectionDTO.getProgram();
		}).filter(Objects::nonNull).collect(Collectors.toSet())) : Collections.emptySet();

		boolean isCrosswalkMaterial;
		Set<String> uniqueChannels = new HashSet<>();

		if (!CollectionUtils.isEmpty(allPrograms)) {
			isCrosswalkMaterial = allPrograms.stream().anyMatch(ProgramChannelProjectionDTO::isCrosswalked);
			uniqueChannels.add(ChannelType.BUY_AND_BILL.name());
		}
		else {
			List<Therapy> therapies = findByNdcNumber(ndcNumber, channelType);
			isCrosswalkMaterial = therapies.stream().anyMatch(Therapy::getIsCrosswalked);
		}

		List<String> crossWalkedSPs = new ArrayList<>();

		if (isCrosswalkMaterial) {
			crossWalkedSPs = fetchCrossWalkedSPsForUserAndNdc(userId, ndcNumber);
		}

		if (!CollectionUtils.isEmpty(crossWalkedSPs)) {
			uniqueChannels.add(ChannelType.SPECIALITY_PHARMA.name());
		}
		return ProgramChannelResponseDTO.builder().channels(uniqueChannels).programs(uniquePrograms).build();
	}

	private List<String> fetchCrossWalkedSPsForUserAndNdc(Long userId, String ndcNumber) {
		return therapyRepository.findAllCrossWalkedSPsForUser(userId, ndcNumber,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllCustomerIdentifiers(Long userId, String query, Pageable paging) {
		return therapyRepository.findAllCustomerIdentifiers(userId, query, paging,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<TradeCatalogResponseDTO> findAllGlobalTherapies(String query, Pageable paging,
			List<ChannelType> channelTypes, List<String> status, boolean ignoreCrosswalkedSP) {
		try {
			log.info(" Inside method - findAllGlobalTherapies");
			log.info("query: {}", StringUtils.toRootLowerCase(query));
			Page<TherapyCatalogResponseDTO> projections = therapyRepository.findAllGlobalTherapies(
					StringUtils.toRootLowerCase(query), getConsolidatedStatus(status),
					getConsolidatedChannelType(channelTypes), paging, ignoreCrosswalkedSP);

			Page<TradeCatalogResponseDTO> map = projections.map(TradeCatalogResponseMapper::mapToTradeCatalogResponse);
			return map;
		}
		catch (Exception e) {
			log.error("Exception occurred in findAllGlobalTherapies and exception details", e);
			throw e;
		}

	}

	public Page<Therapy> findAllGlobalSPTherapies(String query, Pageable paging, Status status, String manufacturer) {
		try {
			log.info(" Inside method - findAllGlobalSPTherapies");
			log.info("query: {}", StringUtils.toRootLowerCase(query));
			return therapyRepository.findAllGlobalSPTherapies(StringUtils.toRootLowerCase(query), status.name(), paging,
					StringUtils.isNotEmpty(manufacturer) ? manufacturer : "ALL");
		}
		catch (Exception e) {
			log.error("Exception occurred in findAllGlobalSPTherapies and exception details", e);
			throw e;
		}

	}

	public Page<CatalogPriceResponse> findAllGlobalCatalogs(List<String> accountIds, String query, Pageable paging,
			ChannelType channelType, List<Integer> practiceIdList) {
		try {
			log.info(" Inside method - findAllGlobalCatalogs");
			log.info("accountIds : {}, query: {}", accountIds, StringUtils.toRootLowerCase(query));
			int practiceIdListSize = 0;
			int accountIdListSize = 0;
			if (CollectionsUtils.hasItems(practiceIdList)) {
				practiceIdListSize = practiceIdList.size();
			}
			if (CollectionsUtils.hasItems(accountIds)) {
				accountIdListSize = accountIds.size();
			}
			return catalogPriceRepository.findAllGlobalCatalogs(
					accountIdListSize == 0 ? Collections.emptyList() : accountIds, AccountPartnerFunction.SOLD_TO,
					StringUtils.toRootLowerCase(query), paging, channelType,
					practiceIdListSize == 0 ? Collections.emptyList() : practiceIdList, practiceIdListSize,
					accountIdListSize);
		}
		catch (Exception e) {
			log.error("Exception occurred in findAllGlobalCatalogs and exception details", e);
			throw e;
		}

	}

	@Override
	@Transactional
	public void updateTherapyConfigs(TherapyConfigRequestDTO request) {
		List<String> ndcNumbers = request.getNdcNumbers();
		ChannelType channelType = request.getChannelType();
		boolean patientInfoRequired = request.getIsPatientInformationRequired();
		boolean coiIdRequired = request.getIsCoiIdRequired();
		boolean mrnRequired = request.getIsMRNRequired();
		boolean doaRequired = request.getIsDOARequired();
		boolean poRequired = request.getIsPORequired();
		boolean pidRequired = request.getIsPIDRequired();
		boolean dateOfApheresisRequired = request.getIsDateOfApheresisRequired();
		Map<String, TherapyConfig> existingConfigMap = therapyConfigRepository
			.findByNdcNumberInAndChannelType(ndcNumbers, channelType)
			.stream()
			.collect(Collectors.toMap(TherapyConfig::getNdcNumber, Function.identity()));

		List<TherapyConfig> updatedConfigs = ndcNumbers.stream().map(ndc -> {
			TherapyConfig config = existingConfigMap.getOrDefault(ndc,
					TherapyConfig.builder().ndcNumber(ndc).channelType(channelType).build());

			config.setIsPatientInformationRequired(patientInfoRequired);
			config.setIsCoiIdRequired(coiIdRequired);
			config.setIsMRNRequired(mrnRequired);
			config.setIsDOARequired(doaRequired);
			config.setIsPORequired(poRequired);
			config.setIsPIDRequired(pidRequired);
			config.setIsDateOfApheresisRequired(dateOfApheresisRequired);
			return config;
		}).toList();

		therapyConfigRepository.saveAll(updatedConfigs);
	}

	@Override
	public List<MaterialDetailsResponseDTO> findTherapyDetailsWithConfigByTradeName(String tradeName,
			ChannelType channelType) throws ResourceNotFoundException {

		List<MaterialResponseDTO> responseDTOList = new ArrayList<>();
		if (channelType.equals(ChannelType.SPECIALITY_PHARMA)) {
			responseDTOList = therapyConfigRepository.findSPDetailsByTradeName(tradeName,
					ChannelType.SPECIALITY_PHARMA);
		}
		else {
			responseDTOList = therapyConfigRepository.findDetailesByTradeName(tradeName, ChannelType.BUY_AND_BILL);
		}

		if (CollectionUtils.isEmpty(responseDTOList)) {
			throw new ResourceNotFoundException("Therapy details not found with tradeName: " + tradeName);
		}

		return responseDTOList.stream().map(this::buildMaterialDetailsResponse).toList();
	}

	@Override
	public MaterialDetailsResponseDTO findSPTherapyDetailsByMaterialNumber(String materialNumber,
			ChannelType channelType) throws ResourceNotFoundException {

		Crosswalk crosswalkDetails = null;
		MaterialResponseDTO responseDTO;

		if (channelType.equals(ChannelType.SPECIALITY_PHARMA)) {
			responseDTO = therapyConfigRepository
				.findSPDetailsByMaterialNumber(materialNumber, ChannelType.SPECIALITY_PHARMA)
				.orElseThrow(() -> new ResourceNotFoundException(
						"SP Therapy config not found with materialNumber: " + materialNumber));
		}
		else {
			responseDTO = therapyConfigRepository.findDetailsByMaterialNumber(materialNumber, ChannelType.BUY_AND_BILL)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Therapy config not found with materialNumber: " + materialNumber));
		}

		if (channelType.equals(ChannelType.SPECIALITY_PHARMA)
				&& Boolean.TRUE.equals(responseDTO.getTherapy().getIsCrosswalked())) {
			crosswalkDetails = materialCrossWalkConfigRepository.findBySpProductIdAndIsActive(materialNumber, true);
		}

		List<Questionnaire> questionnaires = questionnaireService.getAllConfiguredQuestionnairesForSpecificType(
				QuestionnaireConfigurationType.Material, "", "", "", "",
				ObjectUtils.isEmpty(crosswalkDetails) ? channelType : ChannelType.BUY_AND_BILL,
				ObjectUtils.isEmpty(crosswalkDetails) ? materialNumber : crosswalkDetails.getBnbNdcNumber());

		return MaterialDetailsResponseDTO.builder()
			.therapy(responseDTO.getTherapy())
			.materialConfig(responseDTO.getMaterialConfig())
			.spTherapyConfig(responseDTO.getSpTherapyConfig())
			.questionnaires(questionnaires)
			.build();
	}

	private MaterialDetailsResponseDTO buildMaterialDetailsResponse(MaterialResponseDTO responseDTO) {
		Therapy therapy = responseDTO.getTherapy();
		String productId = therapy.getMaterialNumber();
		ChannelType channelType = therapy.getChannelType();
		boolean isSpeciality = ChannelType.SPECIALITY_PHARMA.equals(channelType);
		boolean isCrosswalked = Boolean.TRUE.equals(therapy.getIsCrosswalked());

		Crosswalk crosswalk = (isSpeciality && isCrosswalked)
				? materialCrossWalkConfigRepository.findBySpProductIdAndIsActive(productId, true) : null;

		String materialOrNdc = getMaterialOrNdcNumber(crosswalk, therapy, isSpeciality, productId);
		ChannelType questionnaireChannel = (crosswalk != null) ? ChannelType.BUY_AND_BILL : channelType;

		List<Questionnaire> questionnaires = questionnaireService.getAllConfiguredQuestionnairesForSpecificType(
				QuestionnaireConfigurationType.Material, "", "", "", "", questionnaireChannel, materialOrNdc);

		return MaterialDetailsResponseDTO.builder()
			.therapy(therapy)
			.materialConfig(responseDTO.getMaterialConfig())
			.questionnaires(questionnaires)
			.spTherapyConfig(responseDTO.getSpTherapyConfig())
			.build();
	}

	private String getMaterialOrNdcNumber(Crosswalk crosswalk, Therapy therapy, boolean isSpeciality,
			String productId) {
		if (crosswalk != null)
			return crosswalk.getBnbNdcNumber();
		return isSpeciality ? productId : therapy.getNdcNumber();
	}

	@Override
	@Transactional
	public void deleteConfigByNdcNumber(String ndcNumber, ChannelType channelType) throws ResourceNotFoundException {
		int deletedCount = therapyConfigRepository.removeTherapyConfigByNdcNumberAndChannelType(ndcNumber, channelType);

		if (deletedCount == 0) {
			throw new ResourceNotFoundException(
					String.format("Therapy config not found with ndcNumber: %s", ndcNumber));
		}
	}

	@Override
	public MaterialResponseDTO findTherapyDetailsByNdc(String ndcNumber, ChannelType channelType)
			throws ResourceNotFoundException {
		return therapyConfigRepository.findTherapyDetailsByNdc(ndcNumber, channelType)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with ndc: " + ndcNumber));
	}

	@Override
	public List<MaterialResponseDTO> findTherapyDetailsByNdcAndSoldToAndProgram(String ndcNumber, String soldToAccount,
			String shipToAccount, String program, ChannelType ndcChannelType, ChannelType channelType)
			throws ResourceNotFoundException {

		String mappedProgram = "340B".equals(program) ? "340" : program;
		boolean isCrossWalkedTherapySelected = !ndcChannelType.equals(channelType);
		List<MaterialResponseDTO> therapies;
		if (ChannelType.SPECIALITY_PHARMA.equals(channelType)) {

			if (isCrossWalkedTherapySelected) {
				therapies = therapyRepository.findCrossWalkedSPTherapyAndConfigBySoldToAccountAndProgramAndNdc(
						soldToAccount, shipToAccount, ndcNumber, AccountPartnerFunction.SOLD_TO);
			}
			else {
				therapies = therapyRepository.findSPTherapyAndConfigBySoldToAccountAndProgramAndNdc(soldToAccount,
						shipToAccount, ndcNumber, AccountPartnerFunction.SOLD_TO);
			}

			if (therapies.isEmpty()) {
				throw new ResourceNotFoundException(String
					.format("SP Therapy not found with ndc: %s, soldToAccount: %s", ndcNumber, soldToAccount));
			}
			return therapies;
		}

		therapies = therapyRepository.findTherapyAndConfigBySoldToAccountAndProgramAndNdc(soldToAccount, shipToAccount,
				channelType.equals(channelType.SPECIALITY_PHARMA) ? null : mappedProgram, ndcNumber, channelType,
				AccountPartnerFunction.SOLD_TO);
		if (therapies.isEmpty()) {
			throw new ResourceNotFoundException(
					String.format("Therapy not found with ndc: %s, soldToAccount: %s, program: %s", ndcNumber,
							soldToAccount, program));
		}
		return therapies;
	}

	@Override
	@Transactional
	public void deLinkSPCatalog(List<CatalogDeLinkRequestDTO> catalogList) {
		List<CatalogPrice> deletedCatalog = catalogList.stream()
			.map(dto -> catalogPriceRepository.findBySapMaterialAndSoldToCustomerAndChannelType(dto.getSapMaterial(),
					dto.getSoldToCustomer(), ChannelType.SPECIALITY_PHARMA))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.peek(catalog -> catalog.setDeleted(true))
			.toList();

		catalogPriceRepository.saveAll(deletedCatalog);
	}

	@Override
	public List<Therapy> getTherapiesByChannelType(ChannelType channelType) {
		return therapyRepository.findAllByChannelTypeAndItemStatus(channelType, THERAPY_ITEM_STATUS_ACTIVE);
	}

	@Override
	public TherapyResponseDTO findTherapyWithCrosswalkById(Long therapyId) throws ResourceNotFoundException {
		Therapy therapy = therapyRepository.findById(therapyId)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + therapyId));
		if (Boolean.TRUE.equals(therapy.getIsCrosswalked())) {
			List<Therapy> crosswalkedTherapies = therapyRepository.findCrosswalkedTherapies(therapy.getMaterialNumber(),
					Long.parseLong(UserSecurityUtils.getLoggedInUser().getId()));
			return new TherapyResponseDTO(therapy, crosswalkedTherapies, therapy.getChannelType());
		}
		else {
			return new TherapyResponseDTO(therapy, Collections.emptyList(), therapy.getChannelType());
		}
	}

	@Override
	public List<Therapy> findTherapyDetailsByTradeName(String tradeName, ChannelType channelType) {
		return therapyRepository.findByTradeNameAndChannelType(tradeName, channelType.name(),
				Long.parseLong(UserSecurityUtils.getLoggedInUser().getId()));
	}

	public Page<TradeCatalogResponseDTO> findNonCrosswalkedSPGlobalTherapies(String query, Pageable paging,
			List<ChannelType> channelTypes, List<String> status, Boolean crosswalked) {
		try {
			log.info(" Inside method - findNonCrosswalkedSPGlobalTherapies");
			log.info("query: {}", StringUtils.toRootLowerCase(query));

			Page<TherapyCatalogResponseDTO> projections = therapyRepository.findNonCrosswalkedSPGlobalTherapies(
					StringUtils.toRootLowerCase(query), getConsolidatedStatus(status),
					getConsolidatedChannelType(channelTypes), crosswalked, paging);

			Page<TradeCatalogResponseDTO> map = projections.map(TradeCatalogResponseMapper::mapToTradeCatalogResponse);
			return map;
		}
		catch (Exception e) {
			log.error("Exception occurred in findNonCrosswalkedSPGlobalTherapies and exception details", e);
			throw e;
		}

	}

	@Override
	public MaterialResponseDTO findTherapyDetailsById(Long id) throws ResourceNotFoundException {
		return therapyConfigRepository.findTherapyDetailsById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + id));
	}

	@Override
	public List<String> getUniqueManufacturersByStatus(ChannelType channelType, Status itemStatus) {
		return therapyRepository.findDistinctManufacturerNames(channelType, itemStatus.name());
	}

	public List<Therapy> findTherapyDetailsByNdcNumber(String ndcNumber, ChannelType channelType)
			throws ResourceNotFoundException {

		List<Therapy> responseDTOList = therapyConfigRepository.findDetailesByNdcNumber(ndcNumber, channelType);

		if (CollectionUtils.isEmpty(responseDTOList)) {
			throw new ResourceNotFoundException("Therapy details not found with NDC number: " + ndcNumber);
		}

		return responseDTOList;
	}

	@Override
	public void updateSPTherapyConfigs(SPTherapyConfigRequestDTO request) {
		List<SPTherapyConfig> existingConfigs = spTherapyConfigRepository
			.findBySpProductIdIn(request.getSpProductIds());

		Map<String, SPTherapyConfig> existingConfigMap = existingConfigs.stream()
			.collect(Collectors.toMap(SPTherapyConfig::getSpProductId, Function.identity()));

		List<SPTherapyConfig> updatedConfigs = request.getSpProductIds().stream().map(spProductId -> {
			SPTherapyConfig config = existingConfigMap.getOrDefault(spProductId,
					SPTherapyConfig.builder().spProductId(spProductId).build());

			applySPCommonFields(config, request);
			return config;
		}).toList();

		spTherapyConfigRepository.saveAll(updatedConfigs);
	}

	private void applySPCommonFields(SPTherapyConfig config, SPTherapyConfigRequestDTO request) {
		Boolean prescriptionRequired = request.getIsPrescriptionDetailsRequired();

		if (Boolean.TRUE.equals(prescriptionRequired)) {
			config.setIsPrescriptionDetailsRequired(true);
			config.setQuantity(request.getQuantity());
			config.setDaysSupply(request.getDaysSupply());
			config.setRefills(request.getRefills());
			config.setInstructions(request.getInstructions());
			config.setConsentPolicy(request.getConsentPolicy());
		}
		else if (Boolean.FALSE.equals(prescriptionRequired)) {
			config.setIsPrescriptionDetailsRequired(false);
			config.setQuantity(null);
			config.setDaysSupply(null);
			config.setRefills(null);
			config.setInstructions(null);
			config.setConsentPolicy(null);
		}
		else {
			config.setIsCoiIdRequired(request.getIsCoiIdRequired());
			config.setIsMRNRequired(request.getIsMrnRequired());
			config.setIsPatientIdentifierRequired(request.getIsPatientIdentifierRequired());
		}
	}

	@Override
	@Transactional
	public void removeSPTherapyConfig(String spProductId) throws ResourceNotFoundException {
		SPTherapyConfig configToUpdate = spTherapyConfigRepository.findBySpProductId(spProductId);

		if (configToUpdate == null) {
			throw new ResourceNotFoundException("SPTherapyConfig not found for spProductId: " + spProductId);
		}
		configToUpdate.setIsCoiIdRequired(null);
		configToUpdate.setIsPatientIdentifierRequired(null);
		configToUpdate.setIsMRNRequired(null);

		spTherapyConfigRepository.save(configToUpdate);
	}

	@Override
	public TherapyLogoResponseDto getManufacturerLogo(Long therapyId) throws ResourceNotFoundException {
		Therapy therapy = findById(therapyId);
		String manufacturerName = therapy.getManufacturerName();
		if (manufacturerName == null) {
			throw new ResourceNotFoundException("Manufacturer is not present in the therapy: " + therapyId);
		}
		String firstName = manufacturerName.contains(" ") ? manufacturerName.substring(0, manufacturerName.indexOf(" "))
				: manufacturerName;

		MediaType mediaType = practiceUtils.getContentType(MANUFACTURER_LOGO_EXTENSION);
		if (mediaType == null) {
			throw new FileNotValidException("Unsupported file type: " + MANUFACTURER_LOGO_EXTENSION);
		}
		String logoUri = String.format("%s/%s.%s", MANUFACTURER_LOGO_BASE_URL, firstName, MANUFACTURER_LOGO_EXTENSION);
		byte[] logo = practiceUtils.fetchLogo(logoUri);
		return TherapyLogoResponseDto.builder().logo(logo).logoExtension(mediaType).build();
	}

}
