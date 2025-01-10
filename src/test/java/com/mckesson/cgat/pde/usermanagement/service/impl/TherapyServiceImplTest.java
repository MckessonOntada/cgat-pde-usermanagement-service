package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.mapper.CatalogPriceMapper;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogPriceRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

	/*
	 * @DisplayName("Test : FindAll Therapies")
	 *
	 * @Test public void findAllTherapiesTest() { Long userId = 2L; String query = "";
	 *
	 * Mockito .when(therapyRepository.findAllTherapies(anyLong(), anyString(), anyList(),
	 * Mockito.any(Pageable.class), anyList(), anyString())) .thenReturn(getTherapies());
	 * therapyService.findAllTherapies(userId, query, new ArrayList<>(), paging, new
	 * ArrayList<>()); verify(therapyRepository, times(1)).findAllTherapies(userId, query,
	 * new ArrayList<>(), paging, new ArrayList<>(), "SH"); }
	 *
	 * @DisplayName("Test : FindAll SoldToAccounts")
	 *
	 * @Test public void findAllSoldToAccountsTest() { Long userId = 2L; String query =
	 * ""; String therapyId = "18";
	 *
	 * Mockito .when(therapyRepository.findAllSoldToAccounts(anyLong(), anyString(),
	 * anyString(), Mockito.any(Pageable.class), anyString(), anyString()))
	 * .thenReturn(getSoldToAccounts()); therapyService.findAllSoldToAccounts(userId,
	 * Long.valueOf(therapyId), query, paging, ""); verify(therapyRepository,
	 * times(1)).findAllSoldToAccounts(userId, therapyId, query, paging, "", "SH"); }
	 *
	 * @DisplayName("Test : FindAll ShipToAccounts")
	 *
	 * @Test public void findAllShipToAccountsTest() { String soldToAccount = "4"; String
	 * therapyId = "18";
	 *
	 * Mockito .when(therapyRepository.findAllShipToAccounts(anyString(), anyString(),
	 * anyString(), Mockito.any(Pageable.class), anyString()))
	 * .thenReturn(getShipToAccounts());
	 * therapyService.findAllShipToAccounts(soldToAccount, Long.valueOf(therapyId),
	 * paging, ""); verify(therapyRepository,
	 * times(1)).findAllShipToAccounts(soldToAccount, therapyId, "", paging, "SP"); }
	 *
	 * @DisplayName("Test : FindAll TherapyById")
	 *
	 * @Test public void findTherapyByIdTest() { Long therapyId = 2L; Therapy therapy =
	 * Therapy.builder() .materialNumber("123") .ndcNumber("r34")
	 * .materialDescription("paracetamol") .sellingUnitCode("p12")
	 * .baseUnitOfMeasure("gm") .manufacturerName("sunpharma") .salesOrg("SUN") .build();
	 * Mockito.when(therapyRepository.findById(therapyId)).thenReturn(Optional.of(therapy)
	 * ); therapyRepository.findById(therapyId); verify(therapyRepository,
	 * times(1)).findById(therapyId); }
	 *
	 * @DisplayName("Test Exception : FindAll TherapyById")
	 *
	 * @Test public void findTherapyByIdResourceNotFoundExceptionTest() { Long therapyId =
	 * 2L;
	 * Mockito.when(therapyRepository.findById(therapyId)).thenReturn(Optional.empty());
	 * assertThrows(ResourceNotFoundException.class, () ->
	 * therapyService.findById(therapyId)); }
	 */

	@DisplayName("Test : FindAll Variants linked to soldToAccount, ShipToAccounts and TherapyId")
	@Test
	public void findAllVariantsLinkedToShipToAccountsForTherapyTest() {
		String soldToAccount = "4";
		String shipToAccount = "103";
		String therapyId = "18";
		when(catalogPriceRepository.findAllCatalogPricesLinkedToShipToAccountsForTherapy(anyString(), anyString(),
					anyString(), Mockito.any(Pageable.class)))
			.thenReturn(getAllVariantsLinkedToShipToAccounts());
		therapyService.findAllVariantsLinkedToShipToAccountsForTherapy(soldToAccount, shipToAccount,
				Long.valueOf(therapyId), paging);
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
		when(catalogPriceRepository.findPriceOfVariantLinkedToShipToAccountsForTherapy(anyString(), anyString(),
					anyString(), anyString()))
			.thenReturn(getCatalogPrice());
		therapyService.findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccount, shipToAccount, variantType,
				Long.valueOf(therapyId));
		verify(catalogPriceRepository, times(1)).findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccount,
				shipToAccount, therapyId, variantType);
	}

	@DisplayName("Test : Find Price of therapy with no variant linked to soldToAccount, ShipToAccounts and TherapyId")
	@Test
	public void findPriceOfVariantNotLinkedToShipToAccountsForTherapyTest() {
		String soldToAccount = "4";
		String shipToAccount = "103";
		String therapyId = "18";
		String variantType = "";
		when(catalogPriceRepository.findPriceOfVariantLinkedToShipToAccountsForTherapy(anyString(), anyString(),
					anyString(), anyString()))
			.thenReturn(getCatalogPriceWithEmptyVariant());
		therapyService.findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccount, shipToAccount, variantType,
				Long.valueOf(therapyId));
		verify(catalogPriceRepository, times(1)).findPriceOfVariantLinkedToShipToAccountsForTherapy(soldToAccount,
				shipToAccount, therapyId, variantType);
	}

	@Test
	@DisplayName("Test : Find Material from catalogue using unique material number (Happy Path)")
	public void testFindByMaterialNumber_Success() throws ResourceNotFoundException {
		String materialNumber = "12345";
		Therapy mockTherapy = new Therapy();
		mockTherapy.setMaterialNumber(materialNumber);
		when(therapyRepository.findByMaterialNumber(materialNumber)).thenReturn(Optional.of(mockTherapy));
		Therapy result = therapyService.findByMaterialNumber(materialNumber);
		Assertions.assertEquals(materialNumber, result.getMaterialNumber());
		verify(therapyRepository, times(1)).findByMaterialNumber(materialNumber);
	}

	@Test
	@DisplayName("Test : Find Material from catalogue using unique material number (negative Path)")
	public void testFindByMaterialNumber_NotFound() {
		String materialNumber = "12345";
		when(therapyRepository.findByMaterialNumber(materialNumber)).thenReturn(Optional.empty());
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> therapyService.findByMaterialNumber(materialNumber));
		Assertions.assertEquals("Therapy not found with materialNumber: " + materialNumber, exception.getMessage());
		verify(therapyRepository, times(1)).findByMaterialNumber(materialNumber);
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

	private Page<Object[]> getTherapies() {
		List<Object[]> therapies = new ArrayList<>();
		Object[] therapy1 = { "1", "ndc1", "tdesc4", "1", "udi1", "ndc1", "tdesc4", "1", "udi1", "ndc1", "tdesc4", "1",
				"udi1", "ndc1", "tdesc4", "1", "udi1", "" };
		Object[] therapy2 = { "2", "ndc2", "tdesc4", "1", "udi1", "ndc1", "tdesc4", "1", "udi1", "ndc1", "tdesc4", "1",
				"udi1", "ndc1", "tdesc4", "1", "udi1", "" };
		Object[] therapy3 = { "3", "ndc3", "tdesc4", "1", "udi1", "ndc1", "tdesc4", "1", "udi1", "ndc1", "tdesc4", "1",
				"udi1", "ndc1", "tdesc4", "1", "udi1", "" };
		Object[] therapy4 = { "4", "ndc4", "tdesc4", "1", "udi1", "ndc1", "tdesc4", "1", "udi1", "ndc1", "tdesc4", "1",
				"udi1", "ndc1", "tdesc4", "1", "udi1", "" };
		therapies.add(therapy1);
		therapies.add(therapy2);
		therapies.add(therapy3);
		therapies.add(therapy4);
		return new PageImpl<>(therapies, paging, therapies.size());
	}

	private Page<Object[]> getSoldToAccounts() {
		List<Object[]> soldToAccounts = new ArrayList<>();
		Object[] soldToAccount1 = { "", "acnumber1", "4", "12", "1", "", "", "8989898900", "delhi", "dist1", "delhi",
				"india", "001", "", "" };
		Object[] soldToAccount2 = { "", "acnumber2", "7", "145", "6", "", "", "8989898901", "delhi", "dist3", "delhi",
				"india", "003", "", "" };
		Object[] soldToAccount3 = { "", "acnumber3", "9", "102", "11", "", "", "8989898902", "delhi", "dist2", "delhi",
				"india", "009", "", "" };
		soldToAccounts.add(soldToAccount1);
		soldToAccounts.add(soldToAccount2);
		soldToAccounts.add(soldToAccount3);
		return new PageImpl<>(soldToAccounts, paging, soldToAccounts.size());
	}

	private Page<Object[]> getShipToAccounts() {
		List<Object[]> shipToAccounts = new ArrayList<>();
		Object[] shipToAccount1 = { "", "acnumber1", "101", "12", "1", "", "", "8989898900", "delhi", "dist1", "delhi",
				"india", "001", "", "" };
		Object[] shipToAccount2 = { "", "acnumber2", "102", "145", "6", "", "", "8989898901", "delhi", "dist3", "delhi",
				"india", "003", "", "" };
		shipToAccounts.add(shipToAccount1);
		shipToAccounts.add(shipToAccount2);
		return new PageImpl<>(shipToAccounts, paging, shipToAccounts.size());
	}

}
