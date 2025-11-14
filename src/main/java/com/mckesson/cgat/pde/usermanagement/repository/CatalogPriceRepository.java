package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse;
import com.mckesson.cgat.pde.usermanagement.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse(cp, m.productLevelPricing, m.genericName, m.manufacturerName, m.dosage, cp.id as catalog_id)
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
			   SELECT new com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse(cp, m.productLevelPricing, m.genericName, m.manufacturerName, m.dosage, cp.id as catalog_id)
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
			              (:practiceIdListSize = 0 AND ac.practiceId IS NOT NULL)
			           OR (:practiceIdListSize > 0 AND ac.practiceId IN :practiceIdList)
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

	@Modifying
	@Query(nativeQuery = true, value = """
			  SELECT *
			  	FROM catalog.material AS m
			    JOIN catalog.catalog_price AS cp
			        ON m.material_number = cp.sap_material
			    JOIN usermanagement.account AS a
			        ON cp.sold_to_customer = a.sap_customer
			    JOIN usermanagement.user_account_mapping AS uam
			        ON a.id = uam.account_id
			    WHERE uam.user_id = :userId
			        AND a.id = :accountId
			        AND NOT EXISTS (
			            SELECT 1
			            FROM usermanagement.user_account_mapping AS uam2
			            JOIN usermanagement.account AS a2
			            ON uam2.account_id = a2.id
			            JOIN catalog.catalog_price AS cp2
			            ON a2.sap_customer = cp2.sold_to_customer
			            JOIN catalog.material AS m2
			            ON m2.material_number = cp2.sap_material
			            WHERE uam2.user_id = uam.user_id
			            AND m2.id = m.id
			            AND a2.id <> :accountId
			        );
			""")
	List<Therapy> findExclusiveTherapyByAccount(@Param("accountId") Long accountId, @Param("userId") Long userId);

	@Modifying
	@Query(nativeQuery = true, value = """
			DELETE FROM catalog.user_material_exclusion AS ume
			WHERE ume.user_id IN (
			    SELECT uam.user_id
			    FROM usermanagement.user_account_mapping AS uam
			    WHERE uam.account_id = :accountId
			)
			AND ume.material_id IN (
			    SELECT m.id
			    FROM catalog.material AS m
			    JOIN catalog.catalog_price AS cp
			      ON m.material_number = cp.sap_material
			    JOIN usermanagement.account AS a
			      ON cp.sold_to_customer = a.sap_customer
			    WHERE a.id = :accountId
			)
			AND NOT EXISTS (
			    SELECT 1
			    FROM usermanagement.user_account_mapping AS uam2
			    JOIN usermanagement.account AS a2
			      ON uam2.account_id = a2.id
			    JOIN catalog.catalog_price AS cp2
			      ON a2.sap_customer = cp2.sold_to_customer
			    JOIN catalog.material AS m2
			      ON m2.material_number = cp2.sap_material
			    WHERE uam2.user_id = ume.user_id
			      AND m2.id = ume.material_id
			      AND a2.id <> :accountId
			)
			""")
	void updateUserMaterialExclusion(@Param("accountId") Long accountId);

}
