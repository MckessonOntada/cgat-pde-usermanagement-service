package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Therapy;

@Repository
public interface TherapyRepository extends JpaRepository<Therapy, Long> {

	@Query(value = """
			SELECT DISTINCT m.id as therapyId, m.material_description as therapyDescription, m.ndc_number as ndc, cp.udi
			FROM catalog.material m
			JOIN catalog.catalog_price cp ON m.material_number = cp.material_identifier_for_cgat
			JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
			JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
			WHERE
			(
				(
					:query is null
					or LOWER(cp.ndc) like %:query%
					or LOWER(cp.dist_channel) like %:query%
					or LOWER(cp.material_identifier_for_cgat) like %:query%
					or LOWER(m.generic_name) like %:query%
				)
				AND
					uam.user_id = :userId
				AND
					ac.partner_function = :partnerFunction
				AND
					(
					COALESCE(:salesOrg) is null
					or
					cp.sales_org IN (:salesOrg)
					)
				AND
					(
					COALESCE(:customerIdentifier) is null
					or
					cp.sold_to_customer IN (:customerIdentifier)
					)
			)
			""", nativeQuery = true)
	public Page<Object[]> findAllTherapies(@Param("userId") Long userId, @Param("query") String query,
			@Param("salesOrg") List<String> salesOrg, Pageable paging,
			@Param("customerIdentifier") List<String> customerIdentifier,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
			SELECT DISTINCT ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2,
					 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code
			FROM catalog.material m
			JOIN catalog.catalog_price cp ON m.material_number = cp.material_identifier_for_cgat
			JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
			JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
			WHERE
			(
					(
						:query is null
						or LOWER(ac.sap_customer) like %:query%
						or LOWER(ac.name_1) like %:query%
					)
				AND
					(
					cast(:distributionChannel as varchar) is null
					or cp.dist_channel = :distributionChannel
					)
				AND
					m.material_number = :therapyId
				AND
					ac.partner_function = :partnerFunction
				AND
					uam.user_id = :userId
			)

			""",
			nativeQuery = true)
	public Page<Object[]> findAllSoldToAccounts(@Param("userId") Long userId, @Param("therapyId") String therapyId,
			@Param("query") String query, Pageable paging, @Param("distributionChannel") String distributionChannel,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
			SELECT DISTINCT ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2,
					 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code
			FROM catalog.material m
			JOIN catalog.catalog_price cp ON m.material_number = cp.material_identifier_for_cgat
			JOIN usermanagement.account ac ON ac.sap_customer = cp.ship_to_customer
			WHERE
			(
				(
					:query is null
					or LOWER(ac.sap_customer) like %:query%
					or LOWER(ac.name_1) like %:query%
				)
				AND
					cp.sold_to_customer = :soldToAccountId
				AND
					m.material_number = :therapyId
				AND
					ac.partner_function = :partnerFunction
			)
			""",
			nativeQuery = true)
	public Page<Object[]> findAllShipToAccounts(@Param("soldToAccountId") String soldToAccountId,
			@Param("therapyId") String therapyId, @Param("query") String query, Pageable paging,
			@Param("partnerFunction") String partnerFunction);

}
