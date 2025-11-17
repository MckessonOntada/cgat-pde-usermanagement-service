package com.mckesson.cgat.pde.usermanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mckesson.cgat.pde.usermanagement.dto.VariantsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
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
import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponseDto;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.cgat.pde.usermanagement.util.TestUtils;

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

	@BeforeEach
	public void beforeEach() {
		pageNo = "0";
		pageSize = "20";
		paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		practiceId = "2";
		userId = 2L;
		TestUtils.setupPrincipal();
	}

	@Test
	@DisplayName("Test : FindAll Therapies")
	public void findAllTherapiesTest() throws Exception {

		TherapyCatalogResponseDTO therapy1 = new TherapyCatalogResponseDTO() {
			@Override
			public String getTradeName() {
				return "";
			}

			@Override
			public String getId() {
				return "";
			}

			@Override
			public String getTherapyId() {
				return "";
			}

			@Override
			public String getTherapyDescription() {
				return "";
			}

			@Override
			public String getNdc() {
				return "";
			}

			@Override
			public String getStrength() {
				return "";
			}

			@Override
			public String getDosage() {
				return "";
			}

			@Override
			public String getChannelType() {
				return "";
			}

			@Override
			public String getManufacturerName() {
				return "";
			}

			@Override
			public String getGenericName() {
				return "";
			}

		};

		when(therapyService.findAllTherapies(anyLong(), anyString(), anyList(), any(Pageable.class), anyList(), false))
			.thenReturn(new PageImpl<>(List.of(therapy1), paging, 1L));

		mockMvc
			.perform(get("/practices/{practiceId}/users/current/therapies", practiceId).param("pageNo", pageNo)
				.param("pageSize", pageSize)
				.param("query", "")
				.param("salesOrg", "")
				.param("customerIdentifier", "")
				.param("sortBy", "material_description")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("Test : FindAll SoldToAccounts")
	public void findAllSoldToAccountsTest() throws Exception {

		Long therapyId = 12L;

		when(therapyService.findAllSoldToAccounts(anyLong(), anyString(), anyString(), any(Pageable.class),
				any(ChannelType.class), anyString(), ChannelType.BUY_AND_BILL))
			.thenReturn(getAccounts());

		mockMvc.perform(
				get("/practices/{practiceId}/users/current/therapies/{therapyId}/soldtoaccounts", practiceId, therapyId)
					.param("pageNo", pageNo)
					.param("pageSize", pageSize)
					.param("query", "")
					.param("salesOrg", "")
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("Test : FindAll ShipToAccounts")
	public void findAllShipToAccountsTest() throws Exception {

		Long therapyId = 12L;
		String soldToAccountId = "4";

		when(therapyService.findAllShipToAccounts(anyString(), anyString(), any(PageRequest.class), anyString(),
				any(ChannelType.class)))
			.thenReturn(getAccounts());

		mockMvc.perform(get(
				"/practices/{practiceId}/users/current/therapies/{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccounts",
				practiceId, therapyId, soldToAccountId)
			.param("pageNo", pageNo)
			.param("pageSize", pageSize)
			.param("query", "")
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@Test
	@DisplayName("Test : Find TherapyById")
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
			.perform(get("/practices/{practiceId}/users/current/therapies/{therapyId}", practiceId, userId, therapyId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}

	private Page<AccountsResponseDTO> getAccounts() {
		AccountsResponseDTO account1 = new AccountsResponseDTO() {

			@Override
			public String getZipCode() {
				return "";
			}

			@Override
			public String getTelephone() {
				return "";
			}

			@Override
			public String getStreet3() {
				return "";
			}

			@Override
			public String getStreet2() {
				return "";
			}

			@Override
			public String getStreet() {
				return "";
			}

			@Override
			public String getState() {
				return "";
			}

			@Override
			public String getSalesOrg() {
				return "";
			}

			@Override
			public Boolean getIsDefault() {
				return false;
			}

			@Override
			public String getId() {
				return "";
			}

			@Override
			public String getHouseNumber() {
				return "";
			}

			@Override
			public String getDivision() {
				return "";
			}

			@Override
			public String getDistrict() {
				return "";
			}

			@Override
			public String getDistChannel() {
				return "20";
			}

			@Override
			public String getCountry() {
				return "";
			}

			@Override
			public String getCompanyCode() {
				return "";
			}

			@Override
			public String getCity() {
				return "";
			}

			@Override
			public String getCgatIdentifier() {
				return "";
			}

			@Override
			public String getAccountNumber() {
				return "";
			}

			@Override
			public String getAccountName() {
				return "";
			}
		};
		return new PageImpl<>(List.of(account1), paging, 1L);
	}

	@DisplayName("Test : Find ALl Variants linked to SoldTo, ShipTO and TherapyId")
	@Test
	public void findAllVariantsLinkedToShipToAccountsForTherapyTest() throws Exception {
		Long therapyId = 12L;
		String soldToAccount = "4";
		String shipToAccount = "103";
		Page<VariantsResponseDTO> variantsList = getVariantsList();

		when(therapyService.findAllVariantsLinkedToShipToAccountsForNDC(anyString(), anyString(), anyString(),
				any(PageRequest.class), ChannelType.BUY_AND_BILL))
			.thenReturn(variantsList);

		mockMvc.perform(get(
				"/practices/{practiceId}/users/current/therapies/{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/variants",
				practiceId, therapyId, soldToAccount, shipToAccount)
			.param("pageNo", pageNo)
			.param("pageSize", pageSize)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@DisplayName("Test : Find Price of Variant if variant is present linked to SoldTo, ShipTO and TherapyId")
	@Test
	public void findPriceOfVariantLinkedToShipToAccountsForTherapyTestIfVariantisPresent() throws Exception {
		Long therapyId = 12L;
		String soldToAccount = "4";
		String shipToAccount = "103";
		String variantType = "Type B";
		CatalogPriceResponseDto catalogPriceResponseDtos = getCatalogPrice();

		when(therapyService.findPriceOfVariantLinkedToShipToAccountsForTherapy(anyString(), anyString(), anyString(),
				anyString()))
			.thenReturn(catalogPriceResponseDtos);

		mockMvc.perform(get(
				"/practices/{practiceId}/users/current/therapies/{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/catalog",
				practiceId, therapyId, soldToAccount, shipToAccount)
			.param("variantType", variantType)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@DisplayName("Test : Find Price of Variant if variant is not present linked to SoldTo, ShipTO and TherapyId")
	@Test
	public void findPriceOfVariantLinkedToShipToAccountsForTherapyTestIfVariantisNotPresent() throws Exception {
		Long therapyId = 12L;
		String soldToAccount = "4";
		String shipToAccount = "103";
		String variantType = null;
		CatalogPriceResponseDto catalogPriceResponseDtos = getCatalogPrice();
		catalogPriceResponseDtos.setVariant(null);
		catalogPriceResponseDtos.setVariantDescription(null);

		when(therapyService.findPriceOfVariantLinkedToShipToAccountsForTherapy(anyString(), anyString(), anyString(),
				anyString()))
			.thenReturn(catalogPriceResponseDtos);

		mockMvc.perform(get(
				"/practices/{practiceId}/users/current/therapies/{therapyId}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/catalog",
				practiceId, userId, therapyId, soldToAccount, shipToAccount)
			.param("variantType", "")
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	private CatalogPriceResponseDto getCatalogPrice() {
		return CatalogPriceResponseDto.builder()
			.ndc("ndc1")
			.variant("Type B")
			.variantDescription("One to One")
			.netPrice(BigDecimal.valueOf(100.20))
			.contractPrice(BigDecimal.valueOf(100.20))
			.pricingDate(LocalDate.EPOCH)
			.netPrice(BigDecimal.TEN)
			.wacPrice(BigDecimal.TEN)
			.price340b(BigDecimal.TEN)
			.fixedPrice("10")
			.contractPrice(BigDecimal.TEN)
			.regularPrice(BigDecimal.valueOf(120.50))
			.udi("udi1")
			.build();
	}

	private Page<CatalogPriceResponseDto> getCatalogPriceList() {
		List<CatalogPriceResponseDto> catalogs = new ArrayList<>();
		CatalogPriceResponseDto catalogPrice1 = CatalogPriceResponseDto.builder()
			.ndc("ndc1")
			.variant("Type B")
			.variantDescription("One to One")
			.netPrice(BigDecimal.valueOf(100.20))
			.contractPrice(BigDecimal.valueOf(100.20))
			.pricingDate(LocalDate.EPOCH)
			.netPrice(BigDecimal.TEN)
			.wacPrice(BigDecimal.TEN)
			.price340b(BigDecimal.TEN)
			.fixedPrice("10")
			.contractPrice(BigDecimal.TEN)
			.regularPrice(BigDecimal.valueOf(120.50))
			.udi("udi1")
			.build();
		CatalogPriceResponseDto catalogPrice2 = CatalogPriceResponseDto.builder()
			.ndc("ndc2")
			.variant("Type C")
			.variantDescription("One to Many")
			.udi("udi2")
			.build();
		CatalogPriceResponseDto catalogPrice3 = CatalogPriceResponseDto.builder()
			.ndc("ndc3")
			.variant("Type D")
			.variantDescription("Many to Many")
			.udi("udi3")
			.build();
		CatalogPriceResponseDto catalogPrice4 = CatalogPriceResponseDto.builder()
			.ndc("ndc4")
			.variant("Type E")
			.variantDescription("Many to One")
			.udi("udi4")
			.build();

		catalogs.add(catalogPrice1);
		catalogs.add(catalogPrice2);
		catalogs.add(catalogPrice3);
		catalogs.add(catalogPrice4);
		return new PageImpl<>(catalogs, paging, catalogs.size());
	}

	private Page<VariantsResponseDTO> getVariantsList() {
		List<VariantsResponseDTO> variantsResponseDTOList = new ArrayList<>();
		VariantsResponseDTO variantsResponseDTO1 = VariantsResponseDTO.builder()
			.variant("Type B")
			.variantDescription("One to One")
			.materialNumber("material_number1")
			.build();

		VariantsResponseDTO variantsResponseDTO2 = VariantsResponseDTO.builder()
			.variant("Type B")
			.variantDescription("One to One")
			.materialNumber("material_number2")
			.build();

		variantsResponseDTOList.add(variantsResponseDTO1);
		variantsResponseDTOList.add(variantsResponseDTO2);

		return new PageImpl<>(variantsResponseDTOList, paging, variantsResponseDTOList.size());
	}

	/*
	 * private Page<TherapyCatalogResponseDTO> getTherapies() {
	 * List<TherapyCatalogResponseDTO> therapies = new ArrayList<>();
	 * TherapyCatalogResponseDTO therapy1 = TherapyCatalogResponseDTO.builder()
	 * .ndc("ndc1") .therapyDescription("tdesc1") .therapyId("1") .udi("udi1") .build();
	 * TherapyCatalogResponseDTO therapy2 = TherapyCatalogResponseDTO.builder()
	 * .ndc("ndc2") .therapyDescription("tdesc2") .therapyId("2") .udi("udi2") .build();
	 * TherapyCatalogResponseDTO therapy3 = TherapyCatalogResponseDTO.builder()
	 * .ndc("ndc3") .therapyDescription("tdesc3") .therapyId("3") .udi("udi3") .build();
	 * TherapyCatalogResponseDTO therapy4 = TherapyCatalogResponseDTO.builder()
	 * .ndc("ndc4") .therapyDescription("tdesc4") .therapyId("4") .udi("udi4") .build();
	 *
	 * therapies.add(therapy1); therapies.add(therapy2); therapies.add(therapy3);
	 * therapies.add(therapy4); return new PageImpl<>(therapies, paging,
	 * therapies.size()); }
	 *
	 * private Page<AccountsResponseDTO> getSoldToAccounts() { List<AccountsResponseDTO>
	 * soldToAccounts = new ArrayList<>(); AccountsResponseDTO account1 =
	 * AccountsResponseDTO.builder() .accountName("accountname1") .accountNumber("12345")
	 * .city("city1") .country("india") .district("LaxmiNagar") .houseNumber("12")
	 * .state("Delhi") .street("11") .telephone("9899989900") .zipCode("110031") .build();
	 * AccountsResponseDTO account2 = AccountsResponseDTO.builder()
	 * .accountName("accountname2") .accountNumber("12346") .city("city2")
	 * .country("india") .district("gk") .houseNumber("12") .state("Delhi") .street("11")
	 * .telephone("9899989901") .zipCode("110031") .build(); AccountsResponseDTO account3
	 * = AccountsResponseDTO.builder() .accountName("accountname3")
	 * .accountNumber("12347") .city("city3") .country("india") .district("shahdra")
	 * .houseNumber("12") .state("Delhi") .street("11") .telephone("9899989902")
	 * .zipCode("110031") .build();
	 *
	 * soldToAccounts.add(account1); soldToAccounts.add(account2);
	 * soldToAccounts.add(account3); return new PageImpl<>(soldToAccounts, paging,
	 * soldToAccounts.size()); }
	 *
	 * private Page<AccountsResponseDTO> getShipToAccounts() { List<AccountsResponseDTO>
	 * shipToAccounts = new ArrayList<>(); AccountsResponseDTO account1 =
	 * AccountsResponseDTO.builder() .accountName("accountname1") .accountNumber("12345")
	 * .city("city1") .country("india") .district("shahdra") .houseNumber("12")
	 * .state("Delhi") .street("11") .telephone("9899989900") .zipCode("110031") .build();
	 * AccountsResponseDTO account2 = AccountsResponseDTO.builder()
	 * .accountName("accountname2") .accountNumber("12346") .city("city1")
	 * .country("india") .district("shahdra") .houseNumber("12") .state("Delhi")
	 * .street("11") .telephone("9899989901") .zipCode("110031") .build();
	 *
	 * shipToAccounts.add(account1); shipToAccounts.add(account2); return new
	 * PageImpl<>(shipToAccounts, paging, shipToAccounts.size()); }
	 */

}
