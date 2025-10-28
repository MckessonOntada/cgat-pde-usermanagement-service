package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.*;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.SPTherapyConfigs;
import com.mckesson.cgat.pde.usermanagement.model.Status;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TherapyService {

	Page<TradeCatalogResponseDTO> findAllTherapies(Long userId, String query, List<String> salesOrg, Pageable paging,
			List<String> customerIdentifier);

	Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, String ndcNumber, String query, Pageable paging,
			ChannelType channel, String program, ChannelType ndcChannelType);

	Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, String ndcNumber, Pageable paging,
			String query, ChannelType channelType);

	Therapy findById(Long id) throws ResourceNotFoundException;

	ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, String ndcNumber, ChannelType channelType)
			throws ResourceNotFoundException;

	Page<AccountsResponseDTO> findAllCustomerIdentifiers(Long userId, String query, Pageable paging);

	Page<VariantsResponseDTO> findAllVariantsLinkedToShipToAccountsForNDC(String soldToAccountId,
			String shipToAccountId, String ndcNumber, Pageable paging, ChannelType channelType);

	CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForNdc(String soldToAccountId,
			String shipToAccountId, String variantType, String ndcNumber);

	Page<TradeCatalogResponseDTO> findAllGlobalTherapies(String query, Pageable paging, List<ChannelType> channelTypes,
			List<String> status, boolean ignoreCrosswalkedSP);

	Page<Therapy> findAllGlobalSPTherapies(String query, Pageable paging, Status status, String manufacturer);

	Page<TradeCatalogResponseDTO> findNonCrosswalkedSPGlobalTherapies(String query, Pageable paging,
			List<ChannelType> channelTypes, List<String> status, Boolean crosswalked);

	Page<CatalogPriceResponse> findAllGlobalCatalogs(List<String> accountIds, String query, Pageable paging,
			ChannelType channelType, List<Integer> practiceIdList);

	Therapy findByMaterialNumber(String materialNumber, ChannelType channelType) throws ResourceNotFoundException;

	List<Therapy> findByNdcNumber(String ndcNumber, ChannelType channelType) throws ResourceNotFoundException;

	void updateTherapyConfigs(TherapyConfigRequestDTO request);

	List<MaterialDetailsResponseDTO> findTherapyDetailsWithConfigByTradeName(String tradeName, ChannelType channelType)
			throws ResourceNotFoundException;

	void deleteConfigByNdcNumber(String ndcNumber, ChannelType channelType) throws ResourceNotFoundException;

	MaterialResponseDTO findTherapyDetailsByNdc(String ndcNumber, ChannelType channelType)
			throws ResourceNotFoundException;

	List<MaterialResponseDTO> findTherapyDetailsByNdcAndSoldToAndProgram(String ndcNumber, String soldToAccount,
			String shipToAccount, String program, ChannelType ndcChannelType, ChannelType channelType)
			throws ResourceNotFoundException;

	List<Therapy> getTherapiesByChannelType(ChannelType channelType);

	void deLinkSPCatalog(List<CatalogDeLinkRequestDTO> catalogList);

	TherapyResponseDTO findTherapyWithCrosswalkById(Long therapyId) throws ResourceNotFoundException;

	List<Therapy> findTherapyDetailsByTradeName(String tradeName, ChannelType channelType);

	MaterialResponseDTO findTherapyDetailsById(Long id) throws ResourceNotFoundException;

	MaterialDetailsResponseDTO findSPTherapyDetailsByMaterialNumber(String materialNumber, ChannelType channelType)
			throws ResourceNotFoundException;

	List<String> getUniqueManufacturersByStatus(ChannelType channelType, Status itemStatus);

	List<Therapy> findTherapyDetailsByNdcNumber(String ndcNumber, ChannelType channelType)
			throws ResourceNotFoundException;

	void updateSPTherapyConfigs(SPTherapyConfigRequestDTO request);

	void removeSPTherapyConfig(String spProductId) throws ResourceNotFoundException;

	TherapyLogoResponseDto getManufacturerLogo(Long therapyId) throws ResourceNotFoundException;

	SPTherapyConfigs updateSPTherapyConfigDiagnosisCodes(String spProductId, List<Long> diagnosisCodeIDs)
			throws ResourceNotFoundException;

	void delinkSPProductFromDiagnosisCodes(String spProductId, List<Long> diagnosisCodeIDs)
			throws ResourceNotFoundException;

}
