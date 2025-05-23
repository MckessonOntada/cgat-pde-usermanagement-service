package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import com.mckesson.cgat.pde.usermanagement.dto.*;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface TherapyService {

	Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, String query, List<String> salesOrg, Pageable paging,
			List<String> customerIdentifier);

	Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, String therapyId, String query, Pageable paging,
			ChannelType channel, String program, ChannelType therapyChannelType);

	Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, String therapyId, Pageable paging,
			String query, ChannelType channelType);

	Therapy findById(Long id) throws ResourceNotFoundException;

	ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, String therapyId, ChannelType channelType)
			throws ResourceNotFoundException;

	Page<AccountsResponseDTO> findAllCustomerIdentifiers(Long userId, String query, Pageable paging);

	Page<VariantsResponseDTO> findAllVariantsLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String therapyId, Pageable paging, ChannelType channelType);

	CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String variantType, String therapyId);

	Page<Therapy> findAllGlobalTherapies(String query, Pageable paging, List<ChannelType> channelTypes,
			List<String> status, boolean ignoreCrosswalkedSP);

	Page<Therapy> findNonCrosswalkedSPGlobalTherapies(String query, Pageable paging, List<ChannelType> channelTypes,
			List<String> status, Boolean crosswalked);

	Page<CatalogPrice> findAllGlobalCatalogs(List<String> accountIds, String query, Pageable paging,
			ChannelType channelType, List<Integer> practiceIdList, boolean allCatalogs);

	Therapy findByMaterialNumber(String materialNumber, ChannelType channelType) throws ResourceNotFoundException;

	void updateTherapyConfigs(List<String> materialNumbers, boolean isPatientInformationRequired,
			boolean isCoiIdRequired, ChannelType channelType);

	MaterialDetailsResponseDTO findTherapyDetailsByMaterialNumber(String materialNumber, ChannelType channelType)
			throws ResourceNotFoundException;

	void deleteConfigByMaterialNumber(String materialNumber, ChannelType channelType) throws ResourceNotFoundException;

	MaterialResponseDTO findTherapyDetailsById(Long id) throws ResourceNotFoundException;

	List<Therapy> getTherapiesByChannelType(ChannelType channelType);

	void deLinkSPCatalog(List<CatalogDeLinkRequestDTO> catalogList);

	TherapyResponseDTO findTherapyWithCrosswalkById(Long therapyId) throws ResourceNotFoundException;

}
