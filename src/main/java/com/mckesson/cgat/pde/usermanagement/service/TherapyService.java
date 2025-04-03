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
			String channel, String program);

	Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, String therapyId, String query, Pageable paging,
			List<String> channel, String program);

	Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, String therapyId, Pageable paging,
			String query);

	Therapy findById(Long id) throws ResourceNotFoundException;

	ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, String therapyId);

	Page<AccountsResponseDTO> findAllCustomerIdentifiers(Long userId, String query, Pageable paging);

	Page<CatalogPriceResponseDto> findAllVariantsLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String therapyId, Pageable paging);

	CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String variantType, String therapyId);

	Page<Therapy> findAllGlobalTherapies(String query, Pageable paging);

	Page<CatalogPrice> findAllGlobalCatalogs(List<String> accountIds, String query, Pageable paging);

	Therapy findByMaterialNumber(String materialNumber) throws ResourceNotFoundException;

	void updateTherapyConfigs(List<String> materialNumbers, boolean isPatientInformationRequired,
			boolean isCoiIdRequired, ChannelType channelType);

	MaterialDetailsResponseDTO findTherapyDetailsByMaterialNumber(String materialNumber, ChannelType channelType)
			throws ResourceNotFoundException;

	void deleteConfigByMaterialNumber(String materialNumber) throws ResourceNotFoundException;

	MaterialResponseDTO findTherapyDetailsById(Long id) throws ResourceNotFoundException;

}
