package com.mckesson.cgat.pde.usermanagement.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.DistributionChannel;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@WebMvcTest(AccountController.class)
@ContextConfiguration(classes = { AccountController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	@InjectMocks
	private AccountController accountController;

	private Account account;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		account = Account.builder()
			.id(1L)
			.practiceId(100L)
			.accountName("abc")
			.salesOrg("123")
			.distributionChannel("20")
			.division("dv")
			.user(null)
			.isActive(true)
			.partnerFunction(AccountPartnerFunction.SHIP_TO)
			.build();
	}

	@DisplayName("CreateAccountTest")
	@Test
	void createAccountTest() throws Exception {
		String objectString = objectMapper.writeValueAsString(account);
		when(accountService.createAccount(any(Account.class))).thenReturn(account);
		MvcResult result = mockMvc
			.perform(post("/practices/{practiceId}/accounts", account.getPracticeId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectString))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();

		assertNotNull(result);

	}

	@DisplayName("ValidationError :CreateAccountTest")
	@Test
	void createAccount_ValidationErrorTest() throws Exception {
		account = Account.builder()
			.id(1L)
			.practiceId(100L)
			.accountName("")
			.salesOrg("123")
			.distributionChannel("20")
			.division("dv")
			.user(null)
			.isActive(true)
			.build();

		ResultActions result = mockMvc
			.perform(post("/practices/{practiceId}/accounts", account.getPracticeId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(account)))
			.andExpect(status().isBadRequest());
		assertNotNull(result);
	}

	@DisplayName("UpdateAccountTest")
	@Test
	void updateAccountTest() throws Exception {

		Account requestedAccount = Account.builder()
			.id(null)
			.practiceId(100L)
			.accountName("abc")
			.salesOrg("123")
			.distributionChannel("20")
			.division("dv")
			.user(null)
			.isActive(true)
			.build();

		Account updatedAccount = Account.builder()
			.id(1L)
			.practiceId(100L)
			.accountName("abcd")
			.salesOrg("1234")
			.distributionChannel("20")
			.division("dv")
			.user(null)
			.isActive(true)
			.build();

		when(accountService.updateAccount(account.getId(), requestedAccount)).thenReturn(updatedAccount);
		ResultActions result = mockMvc
			.perform(put("/practices/{practiceId}/accounts/{accountId}", account.getPracticeId(), account.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestedAccount)))
			.andExpect(status().isOk());
		assertNotNull(result);
	}

	@DisplayName("GetAccountTest")
	@Test
	void getAccountTest() throws Exception {
		when(accountService.getAccount(account.getId())).thenReturn(account);
		ResultActions result = mockMvc
			.perform(get("/practices/{practiceId}/accounts/{accountId}", account.getPracticeId(), account.getId())
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andDo(print())
			.andExpect(status().isOk());
		assertNotNull(result);

	}

	@DisplayName("AccountNotFoundException :Updateaccount")
	@Test
	void accountNotFoundTest() throws Exception {

		Account updatedAccount = Account.builder()
			.id(1L)
			.practiceId(100L)
			.accountName("abcd")
			.salesOrg("1234")
			.distributionChannel("20")
			.division("dv")
			.user(null)
			.isActive(true)
			.build();
		when(accountService.updateAccount(account.getId(), updatedAccount))
			.thenThrow(new ResourceNotFoundException("Account not found"));

		ResultActions result = mockMvc
			.perform(put("/{accountId}", account.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedAccount)))
			.andExpect(status().isNotFound());
		assertNotNull(result);
	}

	@DisplayName("DeleteAccountTest")
	@Test
	void deleteaAccountTest() throws Exception {
		doNothing().when(accountService).delinkAccountFromPractice(account.getPracticeId(), account.getId());
		ResultActions result = mockMvc
			.perform(delete("/practices/{practiceId}/accounts/{accountId}", account.getPracticeId(), account.getId())
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk());
		assertNotNull(result);

	}

	@DisplayName("FindAllTest")
	@Test
	void findAllTest() throws Exception {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name_1";
		String sortOrder = "asc";
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);
		List<Account> accounts = getAccountList();
		PageImpl<Account> accountPage = new PageImpl<>(accounts, pageable, accounts.size());
		when(accountService.findAll(any(PageRequest.class), Mockito.anyString(), Mockito.anyList(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(), anyList(), anyList()))
			.thenReturn(accountPage);

		ResultActions result = mockMvc
			.perform(get("/practices/{practiceId}/accounts", account.getPracticeId()).param("pageNo", "0")
				.param("pageSize", "20")
				.param("sortBy", "name_1")
				.param("sortOrder", "asc")
				.param("salesOrg", "")
				.param("distChannels", "")
				.param("partnerFunction", "SOLD_TO")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		assertNotNull(result);
		verify(accountService).findAll(pageable, null, null, null, 100L, AccountPartnerFunction.SOLD_TO,
				new ArrayList<>(), new ArrayList<>());
	}

	@DisplayName("FindAllTestForDesc")
	@Test
	void findAllTestForDesc() throws Exception {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name_1";
		String sortOrder = "desc";
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);
		List<Account> accounts = getAccountList();
		PageImpl<Account> accountPage = new PageImpl<>(accounts, pageable, accounts.size());
		when(accountService.findAll(any(PageRequest.class), Mockito.anyString(), Mockito.anyList(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(), anyList(), anyList()))
			.thenReturn(accountPage);

		ResultActions result = mockMvc
			.perform(get("/practices/{practiceId}/accounts", account.getPracticeId()).param("pageNo", "0")
				.param("pageSize", "20")
				.param("sortBy", "name_1")
				.param("sortOrder", "desc")
				.param("salesOrg", "")
				.param("distChannels", "")
				.param("partnerFunction", "SOLD_TO")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		assertNotNull(result);
		verify(accountService).findAll(pageable, null, null, null, 100L, AccountPartnerFunction.SOLD_TO,
				new ArrayList<>(), new ArrayList<>());
	}

	@DisplayName("LinkAccountToPracticeTest")
	@Test
	void linkAccountsToPracticeTest() throws Exception {

		Set<Long> accountIds = new HashSet<>(Arrays.asList(1L, 2L));
		Long practiceId = 1L;
		doNothing().when(accountService).linkAccountsToPractice(practiceId, accountIds);

		ResultActions result = mockMvc
			.perform(patch("/practices/{practiceId}/accounts/{accountId}", practiceId, account.getId())
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(accountIds)));
		assertNotNull(result);
	}

	private List<Account> getAccountList() {

		Account account1 = Account.builder()
			.id(121L)
			.practiceId(100L)
			.accountName("abc")
			.salesOrg("123")
			.distributionChannel("20")
			.division("d1")
			.user(null)
			.isActive(true)
			.partnerFunction(AccountPartnerFunction.SHIP_TO)
			.build();

		Account account2 = Account.builder()
			.id(122L)
			.practiceId(101L)
			.accountName("abcd")
			.salesOrg("1234")
			.distributionChannel("20")
			.division("d2")
			.user(null)
			.isActive(true)
			.partnerFunction(AccountPartnerFunction.SHIP_TO)
			.build();

		Account account3 = Account.builder()
			.id(121L)
			.practiceId(102L)
			.accountName("abcde")
			.salesOrg("12345")
			.distributionChannel("20")
			.division("d3")
			.user(null)
			.isActive(true)
			.partnerFunction(AccountPartnerFunction.SHIP_TO)
			.build();

		Account account4 = Account.builder()
			.id(121L)
			.practiceId(103L)
			.accountName("abcdef")
			.salesOrg("123456")
			.distributionChannel("20")
			.division("d4")
			.user(null)
			.isActive(true)
			.partnerFunction(AccountPartnerFunction.SHIP_TO)
			.build();

		Account account5 = Account.builder()
			.id(121L)
			.practiceId(104L)
			.accountName("abcdefg")
			.salesOrg("1234567")
			.distributionChannel("20")
			.division("d5")
			.user(null)
			.isActive(true)
			.partnerFunction(AccountPartnerFunction.SHIP_TO)
			.build();
		return Arrays.asList(account1, account2, account3, account4, account5);

	}

}
