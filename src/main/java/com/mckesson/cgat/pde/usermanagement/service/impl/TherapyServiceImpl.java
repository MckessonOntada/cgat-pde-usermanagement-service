package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyConfigRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponseDto;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelProjectionDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.mapper.CatalogPriceMapper;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogPriceRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TherapyServiceImpl implements TherapyService {

	private final TherapyRepository therapyRepository;

	private final CatalogPriceRepository catalogPriceRepository;

	private final CatalogPriceMapper catalogPriceMapper;

	private final TherapyConfigRepository therapyConfigRepository;

	public TherapyServiceImpl(TherapyRepository therapyRepository, CatalogPriceRepository catalogPriceRepository,
			CatalogPriceMapper catalogPriceMapper, TherapyConfigRepository therapyConfigRepository) {
		this.therapyRepository = therapyRepository;
		this.catalogPriceRepository = catalogPriceRepository;
		this.catalogPriceMapper = catalogPriceMapper;
		this.therapyConfigRepository = therapyConfigRepository;
	}

	public Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, String query, List<String> salesOrg,
			Pageable paging, List<String> customerIdentifier) {
		return therapyRepository.findAllTherapies(userId, StringUtils.toRootLowerCase(query), salesOrg, paging,
				customerIdentifier, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, Long therapyId, String query, Pageable paging,
			DistributionChannel channel, String program) {
		return therapyRepository.findAllSoldToAccounts(userId, therapyId.toString(), StringUtils.toRootLowerCase(query),
				paging, null != channel ? channel.getCode() : null, program,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, Long therapyId, Pageable paging,
			String query) {
		return therapyRepository.findAllShipToAccounts(soldToAccountId, therapyId.toString(),
				StringUtils.toRootLowerCase(query), paging, AccountPartnerFunction.SHIP_TO.getPartnerFunctionType());
	}

	public Therapy findById(Long id) throws ResourceNotFoundException {
		return therapyRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + id));
	}

	@Override
	public Page<CatalogPriceResponseDto> findAllVariantsLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, Long therapyId, Pageable paging) {
		try {
			log.info(" Inside method - findAllVariantsLinkedToShipToAccountsForTherapy..");
			log.info("soldToAccountId: {} , shipToAccountId: {} and therapyId: {}", soldToAccountId, shipToAccountId,
					therapyId);
			Page<CatalogPrice> allVariants = catalogPriceRepository
				.findAllCatalogPricesLinkedToShipToAccountsForTherapy(soldToAccountId, shipToAccountId,
						therapyId.toString(), paging);
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
			String shipToAccountId, String variantType, Long therapyId) {
		try {
			log.info(" Inside method - findPriceOfVariantLinkedToShipToAccountsForTherapy..");
			log.info("soldToAccountId: {} , shipToAccountId: {} , variantType: {} and therapyId: {}", soldToAccountId,
					shipToAccountId, variantType, therapyId);
			CatalogPrice catalogPrice = catalogPriceRepository.findPriceOfVariantLinkedToShipToAccountsForTherapy(
					soldToAccountId, shipToAccountId, therapyId.toString(), variantType);
			return catalogPriceMapper.toResponseDto(catalogPrice);
		}
		catch (Exception e) {
			log.error(
					"Exception occurred in fetching Price of variant linked to soldToAccountId, shipToAccountId and therapyId and exception details",
					e);
			throw e;
		}
	}

	@Override
	public Therapy findByMaterialNumber(String materialNumber) throws ResourceNotFoundException {
		return therapyRepository.findByMaterialNumber(materialNumber)
			.orElseThrow(
					() -> new ResourceNotFoundException("Therapy not found with materialNumber: " + materialNumber));
	}

	public ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, Long therapyId) {

		List<ProgramChannelProjectionDTO> programAndChannels = therapyRepository.findAllProgramAndChannels(userId,
				therapyId.toString(), AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());

		Set<String> uniquePrograms = null != programAndChannels ? (programAndChannels.stream()
			.map(ProgramChannelProjectionDTO::getProgram)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet())) : Collections.emptySet();

		Set<DistributionChannel> uniqueChannels = null != programAndChannels ? (programAndChannels.stream()
			.map(ProgramChannelProjectionDTO::getChannel)
			.map(DistributionChannel::fromCode)
			.collect(Collectors.toSet())) : Collections.emptySet();

		return ProgramChannelResponseDTO.builder().channels(uniqueChannels).programs(uniquePrograms).build();
	}

	public Page<AccountsResponseDTO> findAllCustomerIdentifiers(Long userId, String query, Pageable paging) {
		return therapyRepository.findAllCustomerIdentifiers(userId, query, paging,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<Therapy> findAllGlobalTherapies(String query, Pageable paging) {
		try {
			log.info(" Inside method - findAllGlobalTherapies");
			log.info("query: {}", StringUtils.toRootLowerCase(query));
			return therapyRepository.findAllGlobalTherapies(StringUtils.toRootLowerCase(query), paging);
		}
		catch (Exception e) {
			log.error("Exception occurred in findAllGlobalTherapies and exception details", e);
			throw e;
		}

	}

	public Page<CatalogPrice> findAllGlobalCatalogs(List<String> accountIds, String query, Pageable paging) {
		try {
			log.info(" Inside method - findAllGlobalCatalogs");
			log.info("accountIds : {}, query: {}", accountIds, StringUtils.toRootLowerCase(query));
			return catalogPriceRepository.findAllGlobalCatalogs(accountIds,
					AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(), StringUtils.toRootLowerCase(query),
					paging);
		}
		catch (Exception e) {
			log.error("Exception occurred in findAllGlobalCatalogs and exception details", e);
			throw e;
		}

	}

	@Override
	@Transactional
	public void updateTherapyConfigs(List<String> materialNumbers, boolean isPatientInformationRequired,
			boolean isCoiIdRequired) {

		List<TherapyConfig> existingConfigs = therapyConfigRepository.findByMaterialNumberIn(materialNumbers);

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
					.build();
			}
		}).collect(Collectors.toList());

		therapyConfigRepository.saveAll(updatedConfigs);
	}

	@Override
	public MaterialResponseDTO findTherapyDetailsByMaterialNumber(String materialNumber)
			throws ResourceNotFoundException {
		return therapyConfigRepository.findDetailedByMaterialNumber(materialNumber)
			.orElseThrow(() -> new ResourceNotFoundException(
					"Therapy config not found with materialNumber: " + materialNumber));
	}

}
