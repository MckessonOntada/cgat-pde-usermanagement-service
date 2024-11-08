package com.mckesson.cgat.pde.usermanagement.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

	@InjectMocks
	private AccountServiceImpl accountService;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private PracticeRepository practiceRepository;

	private Account account;

	@BeforeEach
	public void setUp() {
		account = Account.builder()
			.id(1L)
			.practiceId(100L)
			.name("account")
			.dcId("123")
			.dcName("dcname")
			.user(null)
			.isActive(true)
			.build();

	}

	@DisplayName("CreateAccount")
	@Test
	void createAccountTest() {
		when(accountRepository.save(account)).thenReturn(account);
		assertEquals(account, accountService.createAccount(account));
		verify(accountRepository, times(1)).save(account);

	}

	@DisplayName("GetAccountById")
	@Test
	void getAccountByIdTest() throws ResourceNotFoundException {
		when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
		assertNotNull(accountRepository.findById(account.getId()));
		assertEquals(account, accountService.getAccount(account.getId()));

	}

	@DisplayName("UpdateAccount")
	@Test
	void updateAccountTest() throws ResourceNotFoundException {

		Account oldAccount = Account.builder()
			.id(1L)
			.practiceId(100L)
			.name("abc")
			.dcId("123")
			.dcName("dcname")
			.user(null)
			.isActive(true)
			.build();
		Account newAccount = Account.builder()
			.id(2L)
			.practiceId(100L)
			.name("abcd")
			.dcId("1243")
			.dcName("dcname2")
			.user(null)
			.isActive(true)
			.build();
		when(accountRepository.findById(oldAccount.getId())).thenReturn(Optional.of(oldAccount));
		when(accountRepository.save(newAccount)).thenReturn(newAccount);

		Account result = accountService.updateAccount(oldAccount.getId(), newAccount);

		assertNotNull(result);
		assertEquals(newAccount.getName(), result.getName());
		verify(accountRepository).findById(oldAccount.getId());
		verify(accountRepository).save(newAccount);
	}

	@DisplayName("DeleteAccount")
	@Test
	void deleteAccountTest() throws ResourceNotFoundException {
		when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
		accountService.deleteAccount(account.getId());
		assertFalse(account.getIsActive());
		verify(accountRepository, times(1)).save(account);

	}

	@DisplayName("FindAllUser")
	@Test
	void findAllWithPageableTest() throws ResourceNotFoundException {
		Page<Account> accountPage = getAccountList();
		Pageable paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		when(accountRepository.findAllByQueryText(null, "ALL", paging, 1)).thenReturn(accountPage);
		Page<Account> result = accountService.findAll(paging, null, List.of("ALL"), null, "1");
		assertNotNull(result);
		assertEquals(accountPage, result);
		verify(accountRepository, times(1)).findAllByQueryText(null, "ALL", paging, 1);
	}

	@DisplayName("UserNotFoundException : GetUser")
	@Test
	void getAccountById_userNotFoundException() {
		Long accountId = 123L;
		when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> accountService.getAccount(accountId));

	}

	@DisplayName("linkAccountsToPractice ")
	@Test
	void linkAccountsToPracticeTest() throws Exception {
		Long practiceId = 1L;
		Set<Long> accountIds = new HashSet<>(Arrays.asList(1L, 2L));

		Practice practice = Practice.builder()
			.id(practiceId)
			.name("practice_name")
			.logoUrl("logo_url")
			.contactPerson(Name.builder().firstName("ram").middleName(" ").lastName("kumar").suffix("mr").build())
			.address(Address.builder()
				.address1("address1")
				.address2("address1")
				.city("noida")
				.state("up")
				.zip("201301")
				.build())
			.contact(Contact.builder().email("abc@gmail.com").phone("1234567890").fax("").build())
			.isActive(true)
			.build();

		Account account1 = Account.builder()
			.id(1L)
			.practiceId(1L)
			.name("account1")
			.dcId("123")
			.dcName("dcname1")
			.user(null)
			.isActive(true)
			.build();

		Account account2 = Account.builder()
			.id(2L)
			.practiceId(1L)
			.name("account2")
			.dcId("1243")
			.dcName("dcname2")
			.user(null)
			.isActive(true)
			.build();

		List<Account> accounts = Arrays.asList(account1, account2);

		when(practiceRepository.findById(practiceId)).thenReturn(Optional.of(practice));
		when(accountRepository.findAllById(accountIds)).thenReturn(accounts);
		when(accountRepository.findByPracticeId(practiceId)).thenReturn(new HashSet<>(accounts));

		when(accountRepository.saveAll(Mockito.anySet())).thenReturn(accounts);
		accountService.linkAccountsToPractice(practiceId, accountIds);

		verify(accountRepository, times(1)).saveAll(Mockito.anySet());
	}

	@DisplayName("PracticeNotFoundException ")
	@Test
	void practiceNotFoundExceptionTest() {
		Long practiceId = 1L;
		Set<Long> accountIds = new HashSet<>(Arrays.asList(101L, 102L));
		when(practiceRepository.findById(practiceId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			accountService.linkAccountsToPractice(practiceId, accountIds);
		});
	}

	private Page<Account> getAccountList() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name";
		String sortOrder = "asc";

		Account account1 = Account.builder()
			.id(1L)
			.practiceId(100L)
			.name("account1")
			.dcId("123")
			.dcName("dcname1")
			.user(null)
			.isActive(true)
			.build();

		Account account2 = Account.builder()
			.id(2L)
			.practiceId(200L)
			.name("account2")
			.dcId("1243")
			.dcName("dcname2")
			.user(null)
			.isActive(true)
			.build();

		Account account3 = Account.builder()
			.id(1L)
			.practiceId(100L)
			.name("account3")
			.dcId("12345")
			.dcName("dcname3")
			.user(null)
			.isActive(true)
			.build();

		Account account4 = Account.builder()
			.id(2L)
			.practiceId(200L)
			.name("account4")
			.dcId("12434")
			.dcName("dcname4")
			.user(null)
			.isActive(true)
			.build();

		Account account5 = Account.builder()
			.id(1L)
			.practiceId(100L)
			.name("account5")
			.dcId("12345")
			.dcName("dcname5")
			.user(null)
			.isActive(true)
			.build();

		Account account6 = Account.builder()
			.id(2L)
			.practiceId(200L)
			.name("account6")
			.dcId("12435")
			.dcName("dcname6")
			.user(null)
			.isActive(true)
			.build();

		List<Account> accounts = Arrays.asList(account1, account2, account3, account4, account5, account6);

		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(accounts, pageable, accounts.size());

	}

}
