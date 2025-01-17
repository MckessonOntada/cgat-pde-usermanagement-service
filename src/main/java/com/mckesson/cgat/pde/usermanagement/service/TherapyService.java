package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponseDto;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import com.mckesson.cgat.pde.usermanagement.model.DistributionChannel;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface TherapyService {

	Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, String query, List<String> salesOrg, Pageable paging,
			List<String> customerIdentifier);

	Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, Long therapyId, String query, Pageable paging,
			DistributionChannel channel, String program);

	Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, Long therapyId, Pageable paging,
			String query);

	Therapy findById(Long id) throws ResourceNotFoundException;

	ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, Long therapyId);

	Page<AccountsResponseDTO> findAllCustomerIdentifiers(Long userId, String query, Pageable paging);

	Page<CatalogPriceResponseDto> findAllVariantsLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, Long therapyId, Pageable paging);

	CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String variantType, Long therapyId);

	Page<Therapy> findAllGlobalTherapies(String query, Pageable paging);

	Page<CatalogPrice> findAllGlobalCatalogs(List<String> accountIds, String query, Pageable paging);

	Therapy findByMaterialNumber(String materialNumber) throws ResourceNotFoundException;

}
