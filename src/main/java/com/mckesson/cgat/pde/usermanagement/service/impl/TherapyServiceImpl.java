package com.mckesson.cgat.pde.usermanagement.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public class TherapyServiceImpl implements TherapyService {

	private final TherapyRepository therapyRepository;

	public TherapyServiceImpl(TherapyRepository therapyRepository) {
		this.therapyRepository = therapyRepository;
	}

	public Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, String query, String salesOrg,
			Pageable paging) {
		return therapyRepository.findAllTherapies(userId, query, salesOrg, paging);
	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, Long therapyId, String query, Pageable paging) {
		return therapyRepository.findAllSoldToAccounts(userId, therapyId.toString(), query, paging);
	}

	public Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, Long therapyId, Pageable paging) {
		return therapyRepository.findAllShipToAccounts(soldToAccountId, therapyId.toString(), paging);
	}

	public Therapy findById(Long id) throws ResourceNotFoundException {
		Therapy therapy = therapyRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + id));
		return therapy;
	}

}
