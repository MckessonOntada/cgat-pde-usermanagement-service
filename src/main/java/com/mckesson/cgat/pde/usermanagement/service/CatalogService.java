package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.CSVProcessingResult;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CatalogService {

	List<CatalogConfiguration> saveCatalogConfiguration(CatalogConfigurationDTO configuration);

	CatalogDetailsResponseDTO findCatalogConfiguration(String materialId, String variant, String shipToAccount,
			String soldToAccount, ChannelType channelType) throws ResourceNotFoundException;

	void deleteCatalogConfiguration(String materialId, String variant, String shipToAccount, String soldToAccount)
			throws ResourceNotFoundException;

	CatalogConfiguration findCatalogConf(@NotNull(message = "'materialId' cannot be null") String materialId,
			String variant,
			@NotNull(message = "'shipToAccount' cannot be null") @NotEmpty(
					message = "'shipToAccount' cannot be empty") String shipToAccount,
			@NotNull(message = "'soldToAccount' cannot be null") @NotEmpty(
					message = "'soldToAccount' cannot be empty") String soldToAccount);

	CSVProcessingResult uploadSPCatalog(MultipartFile catalogsData);

}
