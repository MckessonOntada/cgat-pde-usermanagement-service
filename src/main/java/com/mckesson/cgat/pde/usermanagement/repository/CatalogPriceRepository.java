package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;

public interface CatalogPriceRepository extends JpaRepository<CatalogPrice, Long> {

	@Query(value = """
			 SELECT DISTINCT  cp.*
			 FROM catalog.catalog_price cp
			 JOIN catalog.material m ON m.material_number = cp.sap_material
			 WHERE
			 	cp.sold_to_customer = :soldToAccountId
			 AND
			     cp.ship_to_customer = :shipToAccountId
			AND
			    m.ndc_number = :ndcNumber
			  	AND
				cp.variant is not null
			AND
				cp.deleted = false
			""", nativeQuery = true)
	Page<CatalogPrice> findAllCatalogPricesLinkedToShipToAccountsForNDC(
			@Param("soldToAccountId") String soldToAccountId, @Param("shipToAccountId") String shipToAccountId,
			@Param("ndcNumber") String ndcNumber, Pageable paging);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse(cp, m.productLevelPricing, m.genericName, m.manufacturerName, m.dosage)
				 FROM CatalogPrice cp
				 JOIN Therapy m ON m.materialNumber = cp.sapMaterial
				 WHERE
					cp.soldToCustomer = :soldToAccountId
				 AND
					 cp.shipToCustomer = :shipToAccountId
				AND
					m.ndcNumber = :ndcNumber
			     AND
				  cp.deleted = false
				AND
					(
					 :variantType IS NULL
					 OR cp.variant = :variantType
					)
			""")
	Page<CatalogPriceResponse> findPriceOfVariantLinkedToShipToAccountsForNdc(
			@Param("soldToAccountId") String soldToAccountId, @Param("shipToAccountId") String shipToAccountId,
			@Param("ndcNumber") String ndcNumber, @Param("variantType") String variantType, Pageable pageable);

	@Query(value = """
			   SELECT new com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse(cp, m.productLevelPricing, m.genericName, m.manufacturerName, m.dosage)
			FROM CatalogPrice cp
			JOIN Account ac on cp.soldToCustomer = ac.sapCustomer
			JOIN Therapy m on m.materialNumber = cp.sapMaterial
			WHERE
				(
					(
						:query is null
						or LOWER(cp.ndc) like %:query%
						or LOWER(cp.udi) like %:query%
						or LOWER(cp.distChannel) like %:query%
						or LOWER(cp.sapMaterial) like %:query%
						or LOWER(cp.soldToCustomer) like %:query%
						or LOWER(cp.materialDescriptionInLanguageEn) like %:query%
						or LOWER(m.genericName) like %:query%
						or LOWER(m.manufacturerName) like %:query%
					)
			AND (
			          (:accountsListSize > 0 AND cp.soldToCustomer IN :accountIds)
			       OR (:accountsListSize = 0 AND (
			              (:practiceIdListSize = 0 AND cp.tenantId IS NOT NULL)
			           OR (:practiceIdListSize > 0 AND cp.tenantId IN :practiceIdList)
			          ))
			      )
			        AND ac.partnerFunction = :partnerFunction
			        AND ac.isActive = 1
					AND cp.deleted = false
			        	AND (:channelType = 'ALL' OR cp.channelType = :channelType)
			       )
			""")
	Page<CatalogPriceResponse> findAllGlobalCatalogs(@Param("accountIds") List<String> accountIds,
			@Param("partnerFunction") AccountPartnerFunction partnerFunction, @Param("query") String query,
			Pageable paging, @Param("channelType") ChannelType channelType, List<Integer> practiceIdList,
			int practiceIdListSize, int accountsListSize);

	Optional<CatalogPrice> findBySapMaterialAndSoldToCustomerAndChannelType(String sapMaterial, String soldToCustomer,
			ChannelType channelType);

}
