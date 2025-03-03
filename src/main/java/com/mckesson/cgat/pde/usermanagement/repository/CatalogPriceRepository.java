package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse;
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
			    cp.sap_material = :therapyId
			""", nativeQuery = true)
	Page<CatalogPrice> findAllCatalogPricesLinkedToShipToAccountsForTherapy(
			@Param("soldToAccountId") String soldToAccountId, @Param("shipToAccountId") String shipToAccountId,
			@Param("therapyId") String therapyId, Pageable paging);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse(cp, m.productLevelPricing)
				 FROM CatalogPrice cp
				 JOIN Therapy m ON m.materialNumber = cp.sapMaterial
				 WHERE
					cp.soldToCustomer = :soldToAccountId
				 AND
					 cp.shipToCustomer = :shipToAccountId
				AND
					cp.sapMaterial = :therapyId
				AND
					(
					 :variantType IS NULL
					 OR cp.variant = :variantType
					)
			""")
	Optional<CatalogPriceResponse> findFirstByPriceOfVariantLinkedToShipToAccountsForTherapy(
			@Param("soldToAccountId") String soldToAccountId, @Param("shipToAccountId") String shipToAccountId,
			@Param("therapyId") String therapyId, @Param("variantType") String variantType);

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
					)
					AND cp.sold_to_customer IN :accountIds
					AND ac.partner_function = :partnerFunction
				)
			""", nativeQuery = true)
	Page<CatalogPrice> findAllGlobalCatalogs(@Param("accountIds") List<String> accountIds,
			@Param("partnerFunction") String partnerFunction, @Param("query") String query, Pageable paging);

}
