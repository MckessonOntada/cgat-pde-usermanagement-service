package com.mckesson.cgat.pde.usermanagement.service.impl;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.ALL;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
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

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelProjectionDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.mapper.CatalogPriceMapper;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogPriceRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TherapyServiceImplTest {

	@InjectMocks
	private TherapyServiceImpl therapyService;

	@Mock
	private TherapyRepository therapyRepository;

	@Mock
	private CatalogPriceRepository catalogPriceRepository;

	@Mock
	private CatalogPriceMapper catalogPriceMapper;

	private Pageable paging;

	@BeforeEach
	public void beforeEach() {
		paging = PageRequest.of(0, 20, Sort.by("name").ascending());
	}

	@DisplayName("Test: FindAll Therapies")
	@Test
	public void findAllTherapiesTest() {
		Long userId = 2L;
		String query = "";
		TherapyCatalogResponseDTO therapy1 = Mockito.mock(TherapyCatalogResponseDTO.class);
		TherapyCatalogResponseDTO therapy2 = Mockito.mock(TherapyCatalogResponseDTO.class);
		TherapyCatalogResponseDTO therapy3 = Mockito.mock(TherapyCatalogResponseDTO.class);
		TherapyCatalogResponseDTO therapy4 = Mockito.mock(TherapyCatalogResponseDTO.class);
		TherapyCatalogResponseDTO therapy5 = Mockito.mock(TherapyCatalogResponseDTO.class);

		List<TherapyCatalogResponseDTO> mockResponse = List.of(therapy1, therapy2, therapy3, therapy4, therapy5);

		Mockito
			.when(therapyRepository.findAllTherapies(anyLong(), anyString(), anyList(), any(Pageable.class), anyList(),
					anyString()))
			.thenReturn(new PageImpl<>(mockResponse, paging, 1L));

		therapyService.findAllTherapies(userId, query, new ArrayList<>(), paging, new ArrayList<>());
		verify(therapyRepository, times(1)).findAllTherapies(userId, query, new ArrayList<>(), paging,
				new ArrayList<>(), "SP");
	}

	@DisplayName("Test: FindAll SoldToAccounts")
	@Test
	public void findAllSoldToAccountsTest() {
		Long userId = 2L;
		String query = "";
		String therapyId = "18";

		Mockito
			.when(therapyRepository.findAllSoldToAccounts(anyLong(), anyString(), anyString(), any(Pageable.class),
					anyString(), anyString(), anyString()))
			.thenReturn(getAccounts());
		therapyService.findAllSoldToAccounts(userId, therapyId, query, paging, ChannelType.BUY_AND_BILL, "");
		verify(therapyRepository, times(1)).findAllSoldToAccounts(userId, therapyId, query, paging, "80", "", "SP");
	}

	@DisplayName("Test: FindAll ShipToAccounts")
	@Test
	public void findAllShipToAccountsTest() {
		String soldToAccount = "4";
		String therapyId = "18";

		Mockito
			.when(therapyRepository.findAllShipToAccounts(anyString(), anyString(), anyString(), any(Pageable.class),
					anyString()))
			.thenReturn(getAccounts());
		therapyService.findAllShipToAccounts(soldToAccount, therapyId, paging, "");
		verify(therapyRepository, times(1)).findAllShipToAccounts(soldToAccount, therapyId, "", paging, "SH");
	}

	@DisplayName("Test: Find Therapy ById")
	@Test
	public void findTherapyByIdTest() {
		Long therapyId = 2L;
		Therapy therapy = Therapy.builder()
			.materialNumber("123")
			.ndcNumber("r34")
			.materialDescription("paracetamol")
			.sellingUnitCode("p12")
			.baseUnitOfMeasure("gm")
			.manufacturerName("sunpharma")
			.salesOrg("SUN")
			.build();
		Mockito.when(therapyRepository.findById(therapyId)).thenReturn(Optional.of(therapy));
		therapyRepository.findById(therapyId);
		verify(therapyRepository, times(1)).findById(therapyId);
	}

	@Test
	public void findTherapyByIdResourceNotFoundExceptionTest() {
		Long therapyId = 2L;
		Mockito.when(therapyRepository.findById(therapyId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> therapyService.findById(therapyId));
	}

	@DisplayName("Test: Find All Program And Channels")
	@Test
	public void findAllProgramAndChannelsTest() {
		String therapyId = "2";
		Long userId = 502L;
		ProgramChannelProjectionDTO programsandChannels = Mockito.mock(ProgramChannelProjectionDTO.class);

		Mockito
			.when(therapyRepository.findAllPrograms(anyLong(), anyString(), anyString(),
					ChannelType.BUY_AND_BILL.name()))
			.thenReturn(List.of(programsandChannels));
		therapyRepository.findAllPrograms(userId, therapyId, AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(),
				ChannelType.BUY_AND_BILL.name());
		verify(therapyRepository, times(1)).findAllPrograms(userId, therapyId,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(), ChannelType.BUY_AND_BILL.name());
	}

	private Page<AccountsResponseDTO> getAccounts() {
		AccountsResponseDTO account1 = Mockito.mock(AccountsResponseDTO.class);
		AccountsResponseDTO account2 = Mockito.mock(AccountsResponseDTO.class);
		AccountsResponseDTO account3 = Mockito.mock(AccountsResponseDTO.class);
		AccountsResponseDTO account4 = Mockito.mock(AccountsResponseDTO.class);
		AccountsResponseDTO account5 = Mockito.mock(AccountsResponseDTO.class);
		return new PageImpl<>(List.of(account1, account2, account3, account4, account5), paging, 5L);
	}

	@DisplayName("Test : FindAll Global Therapies")
	@Test
	public void findAllGlobalTherapiesTest() {
		String query = "";
		Mockito
			.when(therapyRepository.findAllGlobalTherapies(anyString(), anyString(), anyString(), any(Pageable.class)))
			.thenReturn(getAllGlobalTherapies());
		therapyService.findAllGlobalTherapies(query, paging, Collections.emptyList(), Collections.emptyList());
		verify(therapyRepository).findAllGlobalTherapies(query, ALL, ChannelType.BUY_AND_BILL.name(), paging);
	}

	@DisplayName("Test : FindAll Global Therapies")
	@Test
	public void findAllGlobalCatalogsTest() {
		String query = "";
		List<String> accounts = new ArrayList<>();
		Mockito
			.when(catalogPriceRepository.findAllGlobalCatalogs(anyList(), anyString(), anyString(),
					any(Pageable.class)))
			.thenReturn(getAllGlobalCatalogs());
		therapyService.findAllGlobalCatalogs(accounts, query, paging);
		verify(catalogPriceRepository).findAllGlobalCatalogs(accounts,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType(), query, paging);
	}

	@DisplayName("Test : FindAll Variants linked to soldToAccount, ShipToAccounts and TherapyId")
	@Test
	public void findAllVariantsLinkedToShipToAccountsForTherapyTest() {
		String soldToAccount = "4";
		String shipToAccount = "103";
		String therapyId = "18";
		Mockito
			.when(catalogPriceRepository.findAllCatalogPricesLinkedToShipToAccountsForTherapy(anyString(), anyString(),
					anyString(), any(Pageable.class)))
			.thenReturn(getAllVariantsLinkedToShipToAccounts());
		therapyService.findAllVariantsLinkedToShipToAccountsForTherapy(soldToAccount, shipToAccount, therapyId, paging);
		verify(catalogPriceRepository, times(1)).findAllCatalogPricesLinkedToShipToAccountsForTherapy(soldToAccount,
				shipToAccount, therapyId, paging);
	}

	@DisplayName("Test : Find Price of Variant linked to soldToAccount, ShipToAccounts and TherapyId")
	@Test
	public void findPriceOfVariantLinkedToShipToAccountsForTherapyTest() {
		String soldToAccount = "4";
		String shipToAccount = "103";
		String therapyId = "18";
		String variantType = "Type B";
		Mockito
			.when(catalogPriceRepository.findPriceOfVariantLinkedToShipToAccountsForTherapy(anyString(), anyString(),
					anyString(), anyString(), any(Pageable.class)))
			.thenReturn(getCatalogPriceResponse());
		therapyService.findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccount, shipToAccount, variantType,
				therapyId);
		verify(catalogPriceRepository, times(1)).findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccount,
				shipToAccount, therapyId, variantType, PageRequest.of(0, 1));
	}

	@DisplayName("Test : Find Price of therapy with no variant linked to soldToAccount, ShipToAccounts and TherapyId")
	@Test
	public void findPriceOfVariantNotLinkedToShipToAccountsForTherapyTest() {
		String soldToAccount = "4";
		String shipToAccount = "103";
		String therapyId = "18";
		String variantType = "";
		Mockito
			.when(catalogPriceRepository.findPriceOfVariantLinkedToShipToAccountsForTherapy(anyString(), anyString(),
					anyString(), anyString(), any(Pageable.class)))
			.thenReturn(getCatalogPriceResponseWithEmptyVariant());
		therapyService.findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccount, shipToAccount, variantType,
				therapyId);
		verify(catalogPriceRepository, times(1)).findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccount,
				shipToAccount, therapyId, variantType, PageRequest.of(0, 1));
	}

	private Page<CatalogPriceResponse> getCatalogPriceResponseWithEmptyVariant() {
		List<CatalogPriceResponse> responses = new ArrayList<>();
		CatalogPrice catalogPrice = getCatalogPriceWithEmptyVariant();
		CatalogPriceResponse response = new CatalogPriceResponse();
		response.setCatalogPrice(catalogPrice);
		response.setProductLevelPricing("Y");
		responses.add(response);
		return new PageImpl<>(responses, paging, responses.size());

	}

	private CatalogPrice getCatalogPriceWithEmptyVariant() {
		CatalogPrice catalogPrice = new CatalogPrice();
		catalogPrice.setNdc("ndc1");
		catalogPrice.setVariant(null);
		catalogPrice.setVariantDescription(null);
		catalogPrice.setNetPrice(BigDecimal.valueOf(100.20));
		catalogPrice.setContractPrice(BigDecimal.valueOf(100.20));
		catalogPrice.setPricingDate(LocalDate.EPOCH);
		catalogPrice.setNetPrice(BigDecimal.TEN);
		catalogPrice.setWacPrice(BigDecimal.TEN);
		catalogPrice.setPrice340b(BigDecimal.TEN);
		catalogPrice.setFixedPrice("10");
		catalogPrice.setContractPrice(BigDecimal.TEN);
		catalogPrice.setRegularPrice(BigDecimal.valueOf(120.50));
		catalogPrice.setUdi("udi1");
		return catalogPrice;
	}

	private Page<CatalogPrice> getAllVariantsLinkedToShipToAccounts() {
		List<CatalogPrice> catalogPrices = new ArrayList<>();
		CatalogPrice catalogPrice1 = new CatalogPrice();
		catalogPrice1.setContractPrice(BigDecimal.TEN);
		catalogPrice1.setVariant("Type B");
		catalogPrice1.setVariantDescription("One to One");
		CatalogPrice catalogPrice2 = new CatalogPrice();
		catalogPrice2.setContractPrice(BigDecimal.TEN);
		catalogPrice2.setVariant("Type B");
		catalogPrice2.setVariantDescription("One to One");
		catalogPrices.add(catalogPrice1);
		catalogPrices.add(catalogPrice2);
		return new PageImpl<>(catalogPrices, paging, catalogPrices.size());

	}

	private Page<CatalogPriceResponse> getCatalogPriceResponse() {
		List<CatalogPriceResponse> responses = new ArrayList<>();
		CatalogPrice catalogPrice = getCatalogPrice();
		CatalogPriceResponse response = new CatalogPriceResponse();
		response.setCatalogPrice(catalogPrice);
		response.setProductLevelPricing("Y");
		responses.add(response);
		return new PageImpl<>(responses, paging, responses.size());
	}

	private CatalogPrice getCatalogPrice() {
		CatalogPrice catalogPrice = new CatalogPrice();
		catalogPrice.setNdc("ndc1");
		catalogPrice.setVariant("Type B");
		catalogPrice.setVariantDescription("One to One");
		catalogPrice.setNetPrice(BigDecimal.valueOf(100.20));
		catalogPrice.setContractPrice(BigDecimal.valueOf(100.20));
		catalogPrice.setPricingDate(LocalDate.EPOCH);
		catalogPrice.setNetPrice(BigDecimal.TEN);
		catalogPrice.setWacPrice(BigDecimal.TEN);
		catalogPrice.setPrice340b(BigDecimal.TEN);
		catalogPrice.setFixedPrice("10");
		catalogPrice.setContractPrice(BigDecimal.TEN);
		catalogPrice.setRegularPrice(BigDecimal.valueOf(120.50));
		catalogPrice.setUdi("udi1");
		return catalogPrice;
	}

	private Page<Therapy> getAllGlobalTherapies() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name";
		String sortOrder = "asc";
		List<Therapy> therapies = new ArrayList<>();
		Therapy therapy1 = Therapy.builder()
			.materialNumber("12")
			.materialDescription("PCM")
			.ndcNumber("89")
			.strength("650mg")
			.dosage("SOS")
			.build();
		Therapy therapy2 = Therapy.builder()
			.materialNumber("18")
			.materialDescription("PANTOP")
			.ndcNumber("76")
			.strength("40mg")
			.dosage("BB")
			.build();

		therapies.add(therapy1);
		therapies.add(therapy2);
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(therapies, pageable, therapies.size());
	}

	private Page<CatalogPrice> getAllGlobalCatalogs() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name";
		String sortOrder = "asc";
		List<CatalogPrice> catalogs = new ArrayList<>();
		CatalogPrice catalog1 = CatalogPrice.builder()
			.salesOrg("1100")
			.distChannel("10")
			.sapMaterial("12")
			.variant("v1")
			.variantDescription("version 1")
			.ndc("c1")
			.build();
		CatalogPrice catalog2 = CatalogPrice.builder()
			.salesOrg("1000")
			.distChannel("40")
			.sapMaterial("18")
			.variant("v2")
			.variantDescription("version 2")
			.ndc("c2")
			.build();

		catalogs.add(catalog1);
		catalogs.add(catalog2);
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(catalogs, pageable, catalogs.size());
	}

}
