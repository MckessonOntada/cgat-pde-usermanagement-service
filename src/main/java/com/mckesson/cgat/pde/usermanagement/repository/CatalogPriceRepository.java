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
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse(cp, m.productLevelPricing)
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
			SELECT DISTINCT cp.*
			FROM catalog.catalog_price cp
			JOIN usermanagement.account ac on cp.sold_to_customer = ac.sap_customer
			WHERE
				(
					(
						:query is null
						or LOWER(cp.ndc) like %:query%
						or LOWER(cp.udi) like %:query%
						or LOWER(cp.dist_channel) like %:query%
						or LOWER(cp.sap_material) like %:query%
						or LOWER(cp.sold_to_customer) like %:query%
						or LOWER(cp.material_description_in_language_en) like %:query%
					)
			AND (
			          (:accountsListSize > 0 AND cp.sold_to_customer IN :accountIds)
			       OR (:accountsListSize = 0 AND (
			              (:practiceIdListSize = 0 AND cp.tenant_id IS NOT NULL)
			           OR (:practiceIdListSize > 0 AND cp.tenant_id IN :practiceIdList)
			          ))
			      )
			        AND ac.partner_function = :partnerFunction
			        AND ac.is_active = 1
					AND cp.deleted = false
			        	AND (:channelType = 'ALL' OR cp.channel_type = :channelType)
			       )
			""", nativeQuery = true)
	Page<CatalogPrice> findAllGlobalCatalogs(@Param("accountIds") List<String> accountIds,
			@Param("partnerFunction") String partnerFunction, @Param("query") String query, Pageable paging,
			@Param("channelType") String channelType, List<Integer> practiceIdList, int practiceIdListSize,
			int accountsListSize);

	Optional<CatalogPrice> findBySapMaterialAndSoldToCustomerAndChannelType(String sapMaterial, String soldToCustomer,
			ChannelType channelType);

}
