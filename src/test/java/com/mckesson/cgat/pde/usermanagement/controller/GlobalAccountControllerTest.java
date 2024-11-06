package com.mckesson.cgat.pde.usermanagement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.mckesson.cgat.pde.usermanagement.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;

@WebMvcTest(GlobalAccountController.class)
@ContextConfiguration(classes = { GlobalAccountController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class GlobalAccountControllerTest {

	@MockBean
	private AccountService accountService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("Test: FindAll Available Accounts")
	@Test
	void findAllAvailableAccountsTest() throws Exception {

		Account account1 = Account.builder()
			.id(1L)
			.practiceId(null)
			.name("Account1")
			.dcId("DC1")
			.dcName("DataCenter1")
			.isActive(true)
			.build();

		List<Account> accounts = List.of(account1);
		String pageNo = "0";
		String pageSize = "20";
		String sortBy = "name";
		String sortOrder = "asc";
		String query = "";

		Pageable paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		Page<Account> mockResponse = new PageImpl<>(accounts, paging, accounts.size());

		when(accountService.findAll(paging, query, null, null)).thenReturn(mockResponse);

		mockMvc
			.perform(get("/accounts").param("pageNo", pageNo)
				.param("pageSize", pageSize)
				.param("sortBy", sortBy)
				.param("sortOrder", sortOrder)
				.param("query", query)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
	}

	@DisplayName("Test: FindAll Available Accounts Desc")
	@Test
	void findAllAvailableAccountsTestDesc() throws Exception {

		Account account1 = Account.builder()
				.id(1L)
				.practiceId(null)
				.name("Account1")
				.dcId("DC1")
				.dcName("DataCenter1")
				.isActive(true)
				.build();

		List<Account> accounts = List.of(account1);
		String pageNo = "0";
		String pageSize = "20";
		String sortBy = "name";
		String sortOrder = "desc";
		String query = "";

		Pageable paging = PageRequest.of(0, 20, Sort.by("name").descending());
		Page<Account> mockResponse = new PageImpl<>(accounts, paging, accounts.size());

		when(accountService.findAll(paging, query, null, null)).thenReturn(mockResponse);

		mockMvc
				.perform(get("/accounts").param("pageNo", pageNo)
						.param("pageSize", pageSize)
						.param("sortBy", sortBy)
						.param("sortOrder", sortOrder)
						.param("query", query)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
	}

}
