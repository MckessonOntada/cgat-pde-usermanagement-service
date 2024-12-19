package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponseDto;
import com.mckesson.cgat.pde.usermanagement.mapper.CatalogPriceMapper;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogPriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelProjectionDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.DistributionChannel;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
@Slf4j
public class TherapyServiceImpl implements TherapyService {

	private final TherapyRepository therapyRepository;

	private final CatalogPriceRepository catalogPriceRepository;

	private final CatalogPriceMapper catalogPriceMapper;

	public TherapyServiceImpl(TherapyRepository therapyRepository, CatalogPriceRepository catalogPriceRepository,
			CatalogPriceMapper catalogPriceMapper) {
		this.therapyRepository = therapyRepository;
		this.catalogPriceRepository = catalogPriceRepository;
		this.catalogPriceMapper = catalogPriceMapper;
	}

	public Page<TherapyCatalogResponseDTO> findAllTherapies(Long userId, String query, List<String> salesOrg,
			Pageable paging, List<String> customerIdentifier) {
		return therapyRepository.findAllTherapies(userId, query, salesOrg, paging, customerIdentifier,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllSoldToAccounts(Long userId, Long therapyId, String query, Pageable paging,
			DistributionChannel channel, String program) {
		return therapyRepository.findAllSoldToAccounts(userId, therapyId.toString(), query, paging,
				null != channel ? channel.getCode() : null, program,
				AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());
	}

	public Page<AccountsResponseDTO> findAllShipToAccounts(String soldToAccountId, Long therapyId, Pageable paging,
			String query) {
		return therapyRepository.findAllShipToAccounts(soldToAccountId, therapyId.toString(), query, paging,
				AccountPartnerFunction.SHIP_TO.getPartnerFunctionType());
	}

	public Therapy findById(Long id) throws ResourceNotFoundException {
		return therapyRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Therapy not found with id: " + id));
	}

	@Override
	public Page<CatalogPriceResponseDto> findAllVariantsLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, Long therapyId, Pageable paging) {
		try {
			log.info(" Inside method - findAllVariantsLinkedToShipToAccountsForTherapy..");
			log.info("soldToAccountId: {} , shipToAccountId: {} and therapyId: {}", soldToAccountId, shipToAccountId,
					therapyId);
			Page<CatalogPrice> allVariants = catalogPriceRepository
				.findAllCatalogPricesLinkedToShipToAccountsForTherapy(soldToAccountId, shipToAccountId,
						therapyId.toString(), paging);
			List<CatalogPriceResponseDto> allTherapyVariants = catalogPriceMapper
				.toResponseDtoVariantsList(allVariants.getContent());
			return new PageImpl<>(allTherapyVariants, paging, allVariants.getTotalElements());
		}
		catch (Exception ex) {
			log.error(
					"Exception occurred in fetching variants linked to soldToAccountId, shipToAccountId and therapyId");
			return new PageImpl<>(Collections.emptyList());
		}
	}

	@Override
	public CatalogPriceResponseDto findPriceOfVariantLinkedToShipToAccountsForTherapy(String soldToAccountId,
			String shipToAccountId, String variantType, Long therapyId) {
		try {
			log.info(" Inside method - findPriceOfVariantLinkedToShipToAccountsForTherapy..");
			log.info("soldToAccountId: {} , shipToAccountId: {} , variantType: {} and therapyId: {}", soldToAccountId,
					shipToAccountId, variantType, therapyId);
			CatalogPrice catalogPrice = catalogPriceRepository.findPriceOfVariantLinkedToShipToAccountsForTherapy(
					soldToAccountId, shipToAccountId, therapyId.toString(), variantType);
			return catalogPriceMapper.toResponseDto(catalogPrice);
		}
		catch (Exception e) {
			log.error(
					"Exception occurred in fetching Price of variant linked to soldToAccountId, shipToAccountId and therapyId");
			return null;
		}
	}

	public ProgramChannelResponseDTO findAllProgramAndChannels(Long userId, Long therapyId) {

		List<ProgramChannelProjectionDTO> programAndChannels = therapyRepository.findAllProgramAndChannels(userId,
				therapyId.toString(), AccountPartnerFunction.SOLD_TO.getPartnerFunctionType());

		Set<String> uniquePrograms = null != programAndChannels ? (programAndChannels.stream()
			.map(ProgramChannelProjectionDTO::getProgram)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet())) : Collections.emptySet();

		Set<DistributionChannel> uniqueChannels = null != programAndChannels ? (programAndChannels.stream()
			.map(ProgramChannelProjectionDTO::getChannel)
			.map(DistributionChannel::fromCode)
			.collect(Collectors.toSet())) : Collections.emptySet();

		return ProgramChannelResponseDTO.builder().channels(uniqueChannels).programs(uniquePrograms).build();
	}

}
