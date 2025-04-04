package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.ProviderDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.Provider;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.io.OutputStream;
import java.util.List;

public interface ProviderService {

	Boolean uploadProviders(MultipartFile providerDataFile, Long practiceId, OutputStream outputStream)
			throws FileNotValidException;

	Provider createProvider(Provider provider);

	Page<Provider> findAllProviders(Pageable paging, String query, List<String> status);

	void deactivateProvider(Long providerId) throws ResourceNotFoundException;

	void activateProvider(Long providerId) throws ResourceNotFoundException;

	Provider updateProverDetailsById(Long providerId, ProviderDTO model) throws ResourceNotFoundException;

	Provider getProvider(Long providerId) throws ResourceNotFoundException;

}
