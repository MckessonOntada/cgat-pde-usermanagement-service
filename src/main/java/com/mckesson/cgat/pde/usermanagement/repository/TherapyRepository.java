package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelProjectionDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;

@Repository
public interface TherapyRepository extends JpaRepository<Therapy, Long> {

	@Query(value = """
			SELECT *
			FROM (
				SELECT DISTINCT m.id, m.material_number as therapyId, m.material_description as therapyDescription, m.ndc_number as ndc, cp.udi, m.strength, m.dosage, m.selling_unit_code,
				m.vendor_name, m.upc_code, m.dropship_indicator, m.item_status as status, m.base_unit_of_measure, m.cgat_identifier, m.sales_organization,
				m.cgat_type, m.product_level_pricing, cp.minimum_order_quantity
				FROM catalog.material m
				JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
				JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
				JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
				WHERE
				(
					(
						:query is null
						or LOWER(cp.ndc) like %:query%
						or LOWER(cp.dist_channel) like %:query%
						or LOWER(cp.sap_material) like %:query%
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
			) subquery
			""",
			nativeQuery = true)
	public Page<TherapyCatalogResponseDTO> findAllTherapies(@Param("userId") Long userId, @Param("query") String query,
			@Param("salesOrg") List<String> salesOrg, Pageable paging,
			@Param("customerIdentifier") List<String> customerIdentifier,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
			SELECT *
			FROM (
				SELECT DISTINCT ac.id, ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2,
						 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier
				FROM catalog.material m
				JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
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
						cast(:channel as varchar) is null
						or ac.distribution_channel = :channel
						)
					AND
						(
						cast(:program as varchar) is null
						or ac.cgat_identifier = :program
						)
					AND
						m.material_number = :therapyId
					AND
						ac.partner_function = :partnerFunction
					AND
						uam.user_id = :userId
					)
				) subquery

			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllSoldToAccounts(@Param("userId") Long userId,
			@Param("therapyId") String therapyId, @Param("query") String query, Pageable paging,
			@Param("channel") String channel, @Param("program") String program,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
			SELECT *
			FROM (
				SELECT DISTINCT ac.id, ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2,
						 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier
				FROM catalog.material m
				JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
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
						cp.sap_material = :therapyId
					AND
						ac.partner_function = :partnerFunction
					)
				) subquery
			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllShipToAccounts(@Param("soldToAccountId") String soldToAccountId,
			@Param("therapyId") String therapyId, @Param("query") String query, Pageable paging,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
			SELECT ac.cgat_identifier as program, ac.distribution_channel as channel
			FROM catalog.material m
			JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
			JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
			JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
			WHERE
			(
					uam.user_id = :userId
				AND
					cp.sap_material = :therapyId
				AND
					ac.partner_function = :partnerFunction
			)
			""", nativeQuery = true)
	public List<ProgramChannelProjectionDTO> findAllProgramAndChannels(@Param("userId") Long userId,
			@Param("therapyId") String therapyId, @Param("partnerFunction") String partnerFunction);

	@Query(value = """
			SELECT DISTINCT ac.sap_customer as accountNumber, ac.name_1 as accountName
			FROM usermanagement.account ac
			JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
			JOIN catalog.catalog_price cp ON ac.sap_customer = cp.sold_to_customer
			WHERE
			(
				(
					:query is null
					or LOWER(ac.sap_customer) like %:query%
					or LOWER(ac.name_1) like %:query%
				)
				AND
					uam.user_id = :userId
				AND
					ac.partner_function = :partnerFunction
			)
			""", nativeQuery = true)
	public Page<AccountsResponseDTO> findAllCustomerIdentifiers(@Param("userId") Long userId,
			@Param("query") String query, Pageable paging, @Param("partnerFunction") String partnerFunction);

}
