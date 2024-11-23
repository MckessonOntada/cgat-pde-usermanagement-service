package com.mckesson.cgat.pde.usermanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.ShipToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.SoldToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;

@Service
public interface TherapyService {

	Page<TherapyCatalogResponseDTO> getAllTherapies(Long userId, Pageable paging);

	Page<SoldToAccountsResponseDTO> getAllSoldToAccounts(Long userId, Long therapyId, Pageable paging);

	Page<ShipToAccountsResponseDTO> getAllShipToAccounts(String soldToAccountId, Long therapyId, Pageable paging);

}
