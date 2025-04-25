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
		return therapyRepository.findAllTherapies(userId, StringUtils.toRootLowerCase(query), salesOrg, paging,
				customerIdentifier, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, String therapyId, String query, Pageable paging,
			String channel, String program) {
		return therapyRepository.findAllSoldToAccounts(userId, therapyId, StringUtils.toRootLowerCase(query), paging,
				channel, program, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, String therapyId, String query, Pageable paging,
			List<String> channels, String program) {
		if ("340B".equals(program)) {
			program = "340";
		}
		return therapyRepository.findAllSoldToAccounts(userId, therapyId, StringUtils.toRootLowerCase(query), paging,
				channels, program, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
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
	public Page<CatalogPriceResponseDto> findAllVariantsLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String therapyId, Pageable paging) {
		try {
			log.info(" Inside method - findAllVariantsLinkedToShipToAccountsForTherapy..");
			log.info("soldToAccountId: {} , shipToAccountId: {} and therapyId: {}", soldToAccountId, shipToAccountId,
					therapyId);
			Page<CatalogPrice> allVariants = catalogPriceRepository
				.findAllCatalogPricesLinkedToShipToAccountsForTherapy(soldToAccountId, shipToAccountId, therapyId,
						paging);
			List<CatalogPriceResponseDto> allTherapyVariants = catalogPriceMapper
				.toResponseDtoVariantsList(allVariants.getContent());
			return new PageImpl<>(allTherapyVariants, paging, allVariants.getTotalElements());
		}
		catch (Exception ex) {
			log.error(
					"Exception occurred in fetching variants linked to soldToAccountId, shipToAccountId and therapyId",
					ex);
			throw ex;
		}
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

	public ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, String therapyId) {

		List<ProgramChannelProjectionDTO> programAndChannels = therapyRepository.findAllProgramAndChannels(userId,
				therapyId, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());

		Set<String> uniquePrograms = null != programAndChannels
				? (programAndChannels.stream().map(programChannelProjectionDTO -> {
					if ("340".equals(programChannelProjectionDTO.getProgram())) {
						return "340B";
					}
					return programChannelProjectionDTO.getProgram();
				}).filter(Objects::nonNull).collect(Collectors.toSet())) : Collections.emptySet();

		Set<String> uniqueChannels = programAndChannels != null
				? programAndChannels.stream().map(ProgramChannelProjectionDTO::getChannel).map(channel -> {
					if ("8545".equals(channel))
						return "MPB";
					if ("1010".equals(channel))
						return "Speciality";
					if ("1100".equals(channel) || channel.startsWith("2"))
						return "3PL";
					return null; // Ignore other entries
				})
					.filter(Objects::nonNull) // Remove ignored entries
					.collect(Collectors.toSet())
				: Collections.emptySet();

		if (uniqueChannels.size() > 1) {
			// Add an option All
			uniqueChannels.add("All");
		}

		return ProgramChannelResponseDTO.builder().channels(uniqueChannels).programs(uniquePrograms).build();
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

}
