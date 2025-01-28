package com.mckesson.cgat.pde.usermanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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

import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import com.mckesson.cgat.pde.usermanagement.model.DistributionChannel;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

@WebMvcTest(GlobalCatalogController.class)
@ContextConfiguration(classes = { GlobalCatalogController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class GlobalCatalogControllerTest {

	@MockBean
	private TherapyService therapyService;

	@Autowired
	private MockMvc mockMvc;

	/*
	 * @DisplayName("Test: FindAll Global Catalogs")
	 *
	 * @Test void findAllCatalogsTest() throws Exception {
	 *
	 * String pageNo = "0"; String pageSize = "20"; String sortBy = "name"; String
	 * sortOrder = "asc"; String query = ""; List<String> accountNumbers = new
	 * ArrayList<>();
	 *
	 * Page<CatalogPrice> mockResponse = getCatalogs();
	 *
	 * when(therapyService.findAllGlobalCatalogs(anyList(), anyString(),
	 * any(PageRequest.class))) .thenReturn(mockResponse);
	 *
	 * mockMvc .perform(get("/catalogs").param("pageNo", pageNo) .param("pageSize",
	 * pageSize) .param("sortBy", sortBy) .param("sortOrder", sortOrder) .param("query",
	 * query) .param("accountNumbers", accountNumbers.toString())
	 * .contentType(MediaType.APPLICATION_JSON)) .andExpect(status().isOk()) .andReturn();
	 * }
	 */

	private Page<CatalogPrice> getCatalogs() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name";
		String sortOrder = "asc";
		List<CatalogPrice> catalogs = new ArrayList<>();
		CatalogPrice catalog1 = CatalogPrice.builder()
			.salesOrg("1100")
			.distChannel(DistributionChannel.ALL)
			.sapMaterial("12")
			.variant("v1")
			.variantDescription("version 1")
			.ndc("c1")
			.build();
		CatalogPrice catalog2 = CatalogPrice.builder()
			.salesOrg("1000")
			.distChannel(DistributionChannel.ALL)
			.sapMaterial("18")
			.variant("v2")
			.variantDescription("version 2")
			.ndc("c2")
			.build();
		CatalogPrice catalog3 = CatalogPrice.builder()
			.salesOrg("8545")
			.distChannel(DistributionChannel.ALL)
			.sapMaterial("22")
			.variant("v3")
			.variantDescription("version 3")
			.ndc("c3")
			.build();

		catalogs.add(catalog1);
		catalogs.add(catalog2);
		catalogs.add(catalog3);
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(catalogs, pageable, catalogs.size());
	}

}
