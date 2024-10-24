package com.mckesson.cgat.pde.usermanagement.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.service.impl.AccountServiceImpl;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.jbat.usermanagement.controller.AbstractTest;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest extends AbstractTest {

	@InjectMocks
	private AccountServiceImpl accountService;

	@Mock
	private AccountRepository accountRepository;

	private Account account;

	@BeforeEach
	public void setUp() {
		account = Account.builder().id(1L).practiceId(100L).name("abc").dcId("123").dcName("dcname").user(null)
				.isActive(true).build();

	}

	
	@Test
	void createAccountTest() {
		when(accountRepository.save(account)).thenReturn(account);
		assertEquals(account, accountService.createAccount(account));

	}

	@Test
	void getAccountByIdTest() throws ResourceNotFoundException {
		when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
		assertEquals(account, accountService.getAccount(account.getId()));

	}

	@Test
	void testUpdateAccountTest() throws ResourceNotFoundException {

		Account existingAccount = new Account(1L, 1L, "account1", "123", "abc", null, true);
		Account updateAccount = new Account(1L, 2L, "account2", "1234", "abcd", null, true);
		when(accountRepository.findById(existingAccount.getId())).thenReturn(Optional.of(existingAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(updateAccount);

		Account result = accountService.updateAccount(existingAccount.getId(), updateAccount);

		assertNotNull(result);
		assertEquals(updateAccount.getName(), result.getName());
		verify(accountRepository).findById(existingAccount.getId());
		verify(accountRepository).save(updateAccount);
	}

	@Test
	void deleteAccountTest() {
		accountRepository.delete(account);
		verify(accountRepository, times(1)).delete(account);

	}

	@Test
	void findAllWithPageableTest() {

		Account account1 = new Account(1L, 1L, "account1", "123", "abc", null, true);
		Account account2 = new Account(1L, 2L, "account2", "1234", "abcd", null, true);

		List<Account> accounts = Arrays.asList(account1, account2);

		Pageable pageable = PageRequest.of(1, 20, Sort.by("first_name").ascending());
		Page<Account> accountPage = new PageImpl<>(accounts, pageable, accounts.size());
		when(accountRepository.findAllByQueryText("", "ALL", pageable, 1)).thenReturn(accountPage);

		Page<Account> result = accountService.findAll(pageable, "", List.of("ALL"), "1");
		assertNotNull(result);
		assertEquals(accounts, result.getContent());

		verify(accountRepository, times(1)).findAllByQueryText("", "ALL", pageable, 1);
	}

}
