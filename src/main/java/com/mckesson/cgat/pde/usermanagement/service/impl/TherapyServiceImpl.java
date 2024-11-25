package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.ShipToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.SoldToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

@Service
public class TherapyServiceImpl implements TherapyService {

	private final TherapyRepository therapyRepository;

	public TherapyServiceImpl(TherapyRepository therapyRepository) {
		this.therapyRepository = therapyRepository;
	}

	public Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, List<String> allAccountIds, String query,
			String salesOrg, Pageable paging) {
		return therapyRepository.findAllTherapiesCatalogs(allAccountIds, query, salesOrg, paging);
	}

	public Page<SoldToAccountsResponseDTO> findAllSoldToAccounts(Long userId, List<String> allAccountIds,
			Long therapyId, String query, Pageable paging) {
		return therapyRepository.findAllSoldToAccounts(therapyId, allAccountIds, query, paging);
	}

	public Page<ShipToAccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, Long therapyId,
			Pageable paging) {
		return therapyRepository.findAllShipToAccounts(soldToAccountId, therapyId, paging);
	}

}
