package com.mckesson.cgat.pde.usermanagement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

@WebMvcTest(TherapyController.class)
@ContextConfiguration(classes = { TherapyController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class TherapyControllerTest {

	@MockBean
	TherapyService therapyService;

	@Autowired
	MockMvc mockMvc;

	private Pageable paging;

	private String practiceId;

	private Long userId;

	private String pageNo;

	private String pageSize;

	private String query = "";

	@BeforeEach
	public void beforeEach() {
		pageNo = "0";
		pageSize = "20";
		paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		practiceId = "2";
		userId = 2L;
	}

	@DisplayName("Test : FindAll Therapies")
	@Test
	public void findAllTherapiesTest() throws Exception {

		String salesOrg = "";

		Page<TherapyCatalogResponseDTO> therapies = getTherapies();

		when(therapyService.findAllTherapies(2L, query, salesOrg, paging)).thenReturn(therapies);

		mockMvc
			.perform(get("/practices/{practiceId}/users/{userId}/therapies", practiceId, userId).param("pageNo", pageNo)
				.param("pageSize", pageSize)
				.param("query", "")
				.param("salesOrg", "")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}

	@DisplayName("Test : FindAll SoldToAccounts")
	@Test
	public void findAllSoldToAccountsTest() throws Exception {

		Long therapyId = 12L;
		Page<AccountsResponseDTO> soldToAccounts = getSoldToAccounts();

		when(therapyService.findAllSoldToAccounts(userId, therapyId, query, paging)).thenReturn(soldToAccounts);

		mockMvc
			.perform(get("/practices/{practiceId}/users/{userId}/therapies/{therapyId}/soldtoaccounts", practiceId,
					userId, therapyId)
				.param("pageNo", pageNo)
				.param("pageSize", pageSize)
				.param("query", "")
				.param("salesOrg", "")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}

	@DisplayName("Test : FindAll ShipToAccounts")
	@Test
	public void findAllShipToAccountsTest() throws Exception {

		Long therapyId = 12L;
		String soldToAccount = "4";
		Page<AccountsResponseDTO> shipToAccounts = getShipToAccounts();

		when(therapyService.findAllShipToAccounts(soldToAccount, therapyId, paging)).thenReturn(shipToAccounts);

		mockMvc.perform(get(
				"/practices/{practiceId}/users/{userId}/therapies/{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccounts",
				practiceId, userId, therapyId, soldToAccount)
			.param("pageNo", pageNo)
			.param("pageSize", pageSize)
			.param("query", "")
			.param("salesOrg", "")
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@DisplayName("Test : Find TherapyById")
	@Test
	public void findTherapyByIdTest() throws Exception {
		Long therapyId = 12L;
		Therapy therapy = Therapy.builder()
			.materialNumber("123")
			.ndcNumber("r34")
			.materialDescription("paracetamol")
			.sellingUnitCode("p12")
			.baseUnitOfMeasure("gm")
			.manufacturerName("sunpharma")
			.salesOrg("SUN")
			.build();
		when(therapyService.findById(therapyId)).thenReturn(therapy);

		mockMvc
			.perform(get("/practices/{practiceId}/users/{userId}/therapies/{therapyId}", practiceId, userId, therapyId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}

	private Page<TherapyCatalogResponseDTO> getTherapies() {
		List<TherapyCatalogResponseDTO> therapies = new ArrayList<>();
		TherapyCatalogResponseDTO therapy1 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc1")
			.therapyDescription("tdesc1")
			.therapyId("1")
			.udi("udi1")
			.build();
		TherapyCatalogResponseDTO therapy2 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc2")
			.therapyDescription("tdesc2")
			.therapyId("2")
			.udi("udi2")
			.build();
		TherapyCatalogResponseDTO therapy3 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc3")
			.therapyDescription("tdesc3")
			.therapyId("3")
			.udi("udi3")
			.build();
		TherapyCatalogResponseDTO therapy4 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc4")
			.therapyDescription("tdesc4")
			.therapyId("4")
			.udi("udi4")
			.build();

		therapies.add(therapy1);
		therapies.add(therapy2);
		therapies.add(therapy3);
		therapies.add(therapy4);
		return new PageImpl<>(therapies, paging, therapies.size());
	}

	private Page<AccountsResponseDTO> getSoldToAccounts() {
		List<AccountsResponseDTO> soldToAccounts = new ArrayList<>();
		AccountsResponseDTO account1 = AccountsResponseDTO.builder()
			.accountName("accountname1")
			.accountNumber("12345")
			.city("city1")
			.country("india")
			.district("LaxmiNagar")
			.houseNumber("12")
			.state("Delhi")
			.street("11")
			.telephone("9899989900")
			.zipCode("110031")
			.build();
		AccountsResponseDTO account2 = AccountsResponseDTO.builder()
			.accountName("accountname2")
			.accountNumber("12346")
			.city("city2")
			.country("india")
			.district("gk")
			.houseNumber("12")
			.state("Delhi")
			.street("11")
			.telephone("9899989901")
			.zipCode("110031")
			.build();
		AccountsResponseDTO account3 = AccountsResponseDTO.builder()
			.accountName("accountname3")
			.accountNumber("12347")
			.city("city3")
			.country("india")
			.district("shahdra")
			.houseNumber("12")
			.state("Delhi")
			.street("11")
			.telephone("9899989902")
			.zipCode("110031")
			.build();

		soldToAccounts.add(account1);
		soldToAccounts.add(account2);
		soldToAccounts.add(account3);
		return new PageImpl<>(soldToAccounts, paging, soldToAccounts.size());
	}

	private Page<AccountsResponseDTO> getShipToAccounts() {
		List<AccountsResponseDTO> shipToAccounts = new ArrayList<>();
		AccountsResponseDTO account1 = AccountsResponseDTO.builder()
			.accountName("accountname1")
			.accountNumber("12345")
			.city("city1")
			.country("india")
			.district("shahdra")
			.houseNumber("12")
			.state("Delhi")
			.street("11")
			.telephone("9899989900")
			.zipCode("110031")
			.build();
		AccountsResponseDTO account2 = AccountsResponseDTO.builder()
			.accountName("accountname2")
			.accountNumber("12346")
			.city("city1")
			.country("india")
			.district("shahdra")
			.houseNumber("12")
			.state("Delhi")
			.street("11")
			.telephone("9899989901")
			.zipCode("110031")
			.build();

		shipToAccounts.add(account1);
		shipToAccounts.add(account2);
		return new PageImpl<>(shipToAccounts, paging, shipToAccounts.size());
	}

}
