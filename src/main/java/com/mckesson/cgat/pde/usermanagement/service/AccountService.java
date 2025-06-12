package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.dto.AccountDTO;
import com.mckesson.cgat.pde.usermanagement.dto.AccountDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.SpecialtyPharmaAccountDto;
import com.mckesson.cgat.pde.usermanagement.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface AccountService {

	Account getAccount(Long accountId) throws ResourceNotFoundException;

	void delinkAccountFromPractice(Long practiceId, Long accountId) throws ResourceNotFoundException;

	Page<Account> findAll(Pageable paging, String query, List<String> status, Long userId, Long practiceId,
			AccountPartnerFunction partnerFunction, List<String> salesOrg, List<String> distChannels,
			ChannelType channelType) throws ResourceNotFoundException;

	Account createAccount(AccountDTO account);

	Account updateAccount(Long accountId, AccountDTO accountDto) throws ResourceNotFoundException;

	void linkAccountsToPractice(Long practiceId, Set<Long> accountIds)
			throws ResourceNotFoundException, UnauthorizedAccessException;

	Page<AccountsResponseDTO> findAllPayToAccounts(Long userId, String query, Pageable paging);

	AccountDetailsResponseDTO getAccountDetails(Long accountId) throws ResourceNotFoundException;

	Account linkAccountToProviders(Long accountId, List<Long> providerIds) throws ResourceNotFoundException;

	void delinkAccountFromProvider(Long accountId, List<Long> providerIds) throws ResourceNotFoundException;

	Page<Provider> findNonLinkedProvidersByAccountId(String query, Long accountId, Pageable paging, Long practiceId)
			throws ResourceNotFoundException;

	List<Account> getSPAccounts();

	Page<Account> findAllByOrganization(Pageable paging, String query, List<String> status, List<Long> practiceIdList,
			AccountPartnerFunction partnerFunction, List<String> salesOrg, List<String> distChannels,
			ChannelType channelType);

	Page<Account> findAccountsByProviderId(String query, Long practiceId, Long providerId, Pageable paging)
			throws ResourceNotFoundException;

	Page<Account> findUnlinkedAccountsOfProviderIds(String query, Long practiceId, List<Long> providerIds,
			Pageable paging) throws ResourceNotFoundException;

	Page<SpecialtyPharmaAccountDto> getSpecialtyPharmaAccountsBySapMaterial(String sapMaterial, Pageable pageable,
			String query, String salesOrgs);

	void unlinkSpecialtyPharmaAccount(String sapMaterial, String accountNumber) throws ResourceNotFoundException;

}
