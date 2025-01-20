package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CatalogService {

	List<CatalogConfiguration> saveCatalogConfiguration(CatalogConfigurationDTO configuration);

	CatalogConfiguration findCatalogConfigurationByFields(String materialId, String variant, String shipToAccount,
			String soldToAccount) throws ResourceNotFoundException;

}
