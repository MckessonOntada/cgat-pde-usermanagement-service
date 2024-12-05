package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
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
		Page<Object[]> allTherapies = therapyRepository.findAllTherapies(userId, query, salesOrg, paging);
		List<TherapyCatalogResponseDTO> therapies = allTherapies.getContent()
			.stream()
			.map(obj -> new TherapyCatalogResponseDTO(String.valueOf(obj[0]), String.valueOf(obj[1]),
					String.valueOf(obj[2]), String.valueOf(obj[3])))
			.toList();
		return new PageImpl<>(therapies, paging, allTherapies.getTotalElements());
	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, Long therapyId, String query, Pageable paging) {
		Page<Object[]> allSoldToAccounts = therapyRepository.findAllSoldToAccounts(userId, therapyId.toString(), query,
				paging);
		List<AccountsResponseDTO> soldToAccounts = allSoldToAccounts.getContent()
			.stream()
			.map(obj -> new AccountsResponseDTO(String.valueOf(obj[0]), String.valueOf(obj[1]), String.valueOf(obj[2]),
					String.valueOf(obj[3]), String.valueOf(obj[4]), String.valueOf(obj[5]), String.valueOf(obj[6]),
					String.valueOf(obj[7]), String.valueOf(obj[8]), String.valueOf(obj[9]), String.valueOf(obj[10]),
					String.valueOf(obj[11])))
			.toList();
		return new PageImpl<>(soldToAccounts, paging, allSoldToAccounts.getTotalElements());
	}

	public Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, Long therapyId, Pageable paging) {
		Page<Object[]> allShipToAccounts = therapyRepository.findAllShipToAccounts(soldToAccountId,
				therapyId.toString(), paging);
		List<AccountsResponseDTO> shipToAccounts = allShipToAccounts.getContent()
			.stream()
			.map(obj -> new AccountsResponseDTO(String.valueOf(obj[0]), String.valueOf(obj[1]), String.valueOf(obj[2]),
					String.valueOf(obj[3]), String.valueOf(obj[4]), String.valueOf(obj[5]), String.valueOf(obj[6]),
					String.valueOf(obj[7]), String.valueOf(obj[8]), String.valueOf(obj[9]), String.valueOf(obj[10]),
					String.valueOf(obj[11])))
			.toList();
		return new PageImpl<>(shipToAccounts, paging, allShipToAccounts.getTotalElements());
	}

	public Therapy findById(Long id) throws ResourceNotFoundException {
		return therapyRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + id));
	}

}
