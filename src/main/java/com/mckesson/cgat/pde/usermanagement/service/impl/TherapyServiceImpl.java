package com.mckesson.cgat.pde.usermanagement.service.impl;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.THERAPY_ITEM_STATUS_ACTIVE;
import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.getConsolidatedChannelType;
import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.getConsolidatedStatus;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mckesson.cgat.pde.usermanagement.dto.*;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.mapper.CatalogPriceMapper;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.cgat.pde.usermanagement.model.ShippingLocation;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.model.TherapyConfig;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogPriceRepository;
import com.mckesson.cgat.pde.usermanagement.repository.ShippingLocationRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyConfigRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.CollectionUtils;
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

	public TherapyServiceImpl(TherapyRepository therapyRepository, CatalogPriceRepository catalogPriceRepository,
			CatalogPriceMapper catalogPriceMapper, TherapyConfigRepository therapyConfigRepository,
			QuestionnaireService questionnaireService, ShippingLocationRepository shippingLocationRepository) {
		this.therapyRepository = therapyRepository;
		this.catalogPriceRepository = catalogPriceRepository;
		this.catalogPriceMapper = catalogPriceMapper;
		this.therapyConfigRepository = therapyConfigRepository;
		this.questionnaireService = questionnaireService;
		this.shippingLocationRepository = shippingLocationRepository;
	}

	public Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, String query, List<String> salesOrg,
			Pageable paging, List<String> customerIdentifier) {
		return therapyRepository.findAllTherapies(userId, StringUtils.toRootLowerCase(query), salesOrg,
				customerIdentifier, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(), paging);

	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, String therapyId, String query, Pageable paging,
			ChannelType channelType, String program) {

		if (ChannelType.SPECIALITY_PHARMA.equals(channelType)) {
			return therapyRepository.findAllSoldToAccountsForSP(userId, therapyId, StringUtils.toRootLowerCase(query),
					paging, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
		}

		if ("340B".equals(program)) {
			program = "340";
		}

		return therapyRepository.findAllSoldToAccounts(userId, therapyId, StringUtils.toRootLowerCase(query), paging,
				channelType.name(), program, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, String therapyId, Pageable paging,
			String query, ChannelType channelType) {

		if (channelType.equals(ChannelType.SPECIALITY_PHARMA)) {
			Page<ShippingLocation> shippingLocations = shippingLocationRepository.findAllByQueryText(query, paging,
					OrganizationContext.getCurrentOrgId(), Long.valueOf(soldToAccountId));
			List<AccountsResponseDTO> mappedShipTo = shippingLocations.getContent()
				.stream()
				.map(this::mapToAccountsResponse)
				.collect(Collectors.toList());
			return new PageImpl<>(mappedShipTo, paging, mappedShipTo.size());
		}
		return therapyRepository.findAllShipToAccounts(soldToAccountId, therapyId, StringUtils.toRootLowerCase(query),
				paging, AccountPartnerFunction.SHIP_TO.getPartnerFunctionType());
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
	public Page<VariantsResponseDTO> findAllVariantsLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String therapyId, Pageable paging, ChannelType channelType) {
		try {
			log.info(" Inside method - findAllVariantsLinkedToShipToAccountsForTherapy..");
			log.info("soldToAccountId: {} , shipToAccountId: {} and therapyId: {}", soldToAccountId, shipToAccountId,
					therapyId);
			List<VariantsResponseDTO> variantsResponse;

			Page<CatalogPrice> allVariants;
			if (ChannelType.BUY_AND_BILL.equals(channelType)) {
				allVariants = catalogPriceRepository.findAllCatalogPricesLinkedToShipToAccountsForTherapy(
						soldToAccountId, shipToAccountId, therapyId, paging);
				variantsResponse = toResponseDtoVariantsFromCatalogs(allVariants.getContent());
			}
			else {
				List<Therapy> crosswalkedTherapies = therapyRepository.findCrosswalkedTherapies(therapyId,
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
	public CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String variantType, String therapyId) {
		try {
			log.info(" Inside method - findPriceOfVariantLinkedToShipToAccountsForTherapy..");
			log.info("soldToAccountId: {} , shipToAccountId: {} , variantType: {} and therapyId: {}", soldToAccountId,
					shipToAccountId, variantType, therapyId);
			Page<CatalogPriceResponse> catalogPriceResponsePage = catalogPriceRepository
				.findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccountId, shipToAccountId, therapyId,
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

	public ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, String therapyId, ChannelType channelType) {

		if (ChannelType.SPECIALITY_PHARMA.equals(channelType)) {
			return ProgramChannelResponseDTO.builder()
				.channels(Collections.singleton(ChannelType.SPECIALITY_PHARMA.name()))
				.programs(new HashSet<>())
				.build();
		}

		List<ProgramChannelProjectionDTO> allPrograms = therapyRepository.findAllPrograms(userId, therapyId,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(), ChannelType.BUY_AND_BILL.name());

		Set<String> uniquePrograms = null != allPrograms ? (allPrograms.stream().map(programChannelProjectionDTO -> {
			if ("340".equals(programChannelProjectionDTO.getProgram())) {
				return "340B";
			}
			return programChannelProjectionDTO.getProgram();
		}).filter(Objects::nonNull).collect(Collectors.toSet())) : Collections.emptySet();

		boolean isCrosswalkMaterial = null != allPrograms
				&& allPrograms.stream().anyMatch(ProgramChannelProjectionDTO::isCrosswalked);
		List<String> crossWalkedSPs = new ArrayList<>();

		if (isCrosswalkMaterial) {
			crossWalkedSPs = fetchCrossWalkedSPsForUserAndTherapyId(userId, therapyId);
		}

		Set<String> uniqueChannels = new HashSet<>();
		uniqueChannels.add(ChannelType.BUY_AND_BILL.name());
		if (!CollectionUtils.isEmpty(crossWalkedSPs)) {
			uniqueChannels.add(ChannelType.SPECIALITY_PHARMA.name());
		}
		return ProgramChannelResponseDTO.builder().channels(uniqueChannels).programs(uniquePrograms).build();
	}

	private List<String> fetchCrossWalkedSPsForUserAndTherapyId(Long userId, String therapyId) {
		return therapyRepository.findAllCrossWalkedSPsForUser(userId, therapyId,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllCustomerIdentifiers(Long userId, String query, Pageable paging) {
		return therapyRepository.findAllCustomerIdentifiers(userId, query, paging,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<Therapy> findAllGlobalTherapies(String query, Pageable paging, List<ChannelType> channelTypes,
			List<String> status) {
		try {
			log.info(" Inside method - findAllGlobalTherapies");
			log.info("query: {}", StringUtils.toRootLowerCase(query));
			return therapyRepository.findAllGlobalTherapies(StringUtils.toRootLowerCase(query),
					getConsolidatedStatus(status), getConsolidatedChannelType(channelTypes), paging);
		}
		catch (Exception e) {
			log.error("Exception occurred in findAllGlobalTherapies and exception details", e);
			throw e;
		}

	}

	public Page<CatalogPrice> findAllGlobalCatalogs(List<String> accountIds, String query, Pageable paging,
			ChannelType channelType) {
		try {
			log.info(" Inside method - findAllGlobalCatalogs");
			log.info("accountIds : {}, query: {}", accountIds, StringUtils.toRootLowerCase(query));
			return catalogPriceRepository.findAllGlobalCatalogs(accountIds,
					AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(), StringUtils.toRootLowerCase(query), paging,
					channelType.name());
		}
		catch (Exception e) {
			log.error("Exception occurred in findAllGlobalCatalogs and exception details", e);
			throw e;
		}

	}

	@Override
	@Transactional
	public void updateTherapyConfigs(List<String> materialNumbers, boolean isPatientInformationRequired,
			boolean isCoiIdRequired, ChannelType channelType) {

		List<TherapyConfig> existingConfigs = therapyConfigRepository
			.findByMaterialNumberInAndChannelType(materialNumbers, channelType);

		Map<String, TherapyConfig> existingConfigMap = existingConfigs.stream()
			.collect(Collectors.toMap(TherapyConfig::getMaterialNumber, Function.identity()));

		List<TherapyConfig> updatedConfigs = materialNumbers.stream().map(materialNumber -> {
			TherapyConfig config = existingConfigMap.get(materialNumber);

			if (config != null) {
				config.setIsPatientInformationRequired(isPatientInformationRequired);
				config.setIsCoiIdRequired(isCoiIdRequired);
				return config;
			}
			else {
				return TherapyConfig.builder()
					.materialNumber(materialNumber)
					.isPatientInformationRequired(isPatientInformationRequired)
					.isCoiIdRequired(isCoiIdRequired)
					.channelType(channelType)
					.build();
			}
		}).collect(Collectors.toList());

		therapyConfigRepository.saveAll(updatedConfigs);
	}

	@Override
	public MaterialDetailsResponseDTO findTherapyDetailsByMaterialNumber(String materialNumber, ChannelType channelType)
			throws ResourceNotFoundException {
		MaterialResponseDTO responseDTO = therapyConfigRepository
			.findDetailedByMaterialNumber(materialNumber, channelType)
			.orElseThrow(() -> new ResourceNotFoundException(
					"Therapy config not found with materialNumber: " + materialNumber));

		List<Questionnaire> questionnaires = questionnaireService.getAllConfiguredQuestionnairesForSpecificType(
				QuestionnaireConfigurationType.Material, materialNumber, "", "", "", channelType);

		MaterialDetailsResponseDTO materialDetailsResponseDTO = MaterialDetailsResponseDTO.builder()
			.questionnaires(questionnaires)
			.build();
		materialDetailsResponseDTO.setTherapy(responseDTO.getTherapy());
		materialDetailsResponseDTO.setMaterialConfig(responseDTO.getMaterialConfig());
		return materialDetailsResponseDTO;
	}

	@Override
	@Transactional
	public void deleteConfigByMaterialNumber(String materialNumber, ChannelType channelType)
			throws ResourceNotFoundException {
		int deletedCount = therapyConfigRepository.removeTherapyConfigByMaterialNumberAndChannelType(materialNumber,
				channelType);

		if (deletedCount == 0) {
			throw new ResourceNotFoundException(
					String.format("Therapy config not found with materialNumber: %s", materialNumber));
		}
	}

	@Override
	public MaterialResponseDTO findTherapyDetailsById(Long id) throws ResourceNotFoundException {
		return therapyConfigRepository.findTherapyDetailsById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + id));
	}

	@Override
	@Transactional
	public void deLinkSPCatalog(List<CatalogRequestDTO> catalogList) {
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

}
