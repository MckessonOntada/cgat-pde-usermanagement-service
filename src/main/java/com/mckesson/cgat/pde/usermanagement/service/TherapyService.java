package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.ShipToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.SoldToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;

@Service
public interface TherapyService {

	Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, List<String> allAccountIds, String query,
			String salesOrg, Pageable paging);

	Page<SoldToAccountsResponseDTO> findAllSoldToAccounts(Long userId, List<String> allAccountIds, Long therapyId,
			String query, Pageable paging);

	Page<ShipToAccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, Long therapyId, Pageable paging);

}
