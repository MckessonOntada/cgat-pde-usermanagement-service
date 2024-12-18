package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
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

	public Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, String query, List<String> salesOrg,
			Pageable paging, List<String> customerIdentifier) {
		return therapyRepository.findAllTherapies(userId, query, salesOrg, paging, customerIdentifier,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, Long therapyId, String query, Pageable paging,
			String distributionChannel) {
		return therapyRepository.findAllSoldToAccounts(userId, therapyId.toString(), query, paging, distributionChannel,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, Long therapyId, Pageable paging,
			String query) {
		return therapyRepository.findAllShipToAccounts(soldToAccountId, therapyId.toString(), query, paging,
				AccountPartnerFunction.SHIP_TO.getPartnerFunctionType());
	}

	public Therapy findById(Long id) throws ResourceNotFoundException {
		return therapyRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + id));
	}

}
