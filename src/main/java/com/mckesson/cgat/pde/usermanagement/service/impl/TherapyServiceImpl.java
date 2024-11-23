package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.ShipToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.SoldToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;

@Service
public class TherapyServiceImpl implements TherapyService {

	private final TherapyRepository therapyRepository;

	private final AccountRepository accountRepository;

	public TherapyServiceImpl(TherapyRepository therapyRepository, AccountRepository accountRepository) {
		this.therapyRepository = therapyRepository;
		this.accountRepository = accountRepository;
	}

	public Page<TherapyCatalogResponseDTO> getAllTherapies(Long userId, Pageable paging) {
		Page<Account> allSoldToAccountsOfUser = accountRepository.findAccountsByUser(userId, "ALL", paging,
				OrganizationContext.getCurrentOrgId(), AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());

		List<String> soldtoAccountIds = allSoldToAccountsOfUser.getContent()
			.stream()
			.map(a -> a.getId().toString())
			.toList();
		return therapyRepository.getAllTherapiesCatalogs(soldtoAccountIds, paging);
	}

	public Page<SoldToAccountsResponseDTO> getAllSoldToAccounts(Long userId, Long therapyId, Pageable paging) {
		Page<Account> allSoldToAccountsOfUser = accountRepository.findAccountsByUser(userId, "ALL", paging,
				OrganizationContext.getCurrentOrgId(), AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
		List<String> soldtoAccountIds = allSoldToAccountsOfUser.getContent()
			.stream()
			.map(a -> a.getId().toString())
			.toList();
		return therapyRepository.getSoldToAccounts(therapyId, soldtoAccountIds, paging);
		// allSoldToAccountsOfUser.getContent().stream()
		// .filter(a ->
		// soldToAccounts.getContent().contains(String.valueOf(a.getId()))).toList();
		// return allSoldToAccountsOfUser;
	}

	public Page<ShipToAccountsResponseDTO> getAllShipToAccounts(String soldToAccountId, Long therapyId,
			Pageable paging) {
		return therapyRepository.getAllShipToAccounts(soldToAccountId, therapyId, paging);
	}

}
