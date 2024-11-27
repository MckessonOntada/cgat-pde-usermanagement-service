package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;
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
					or LOWER(cp.udi) like %:query%
				)
				AND
					uam.user_id = :userId
				AND
					ac.partner_function = 'SH'
				AND
					(
					cast(:salesOrg as varchar) is null
					or
					cp.sales_org = :salesOrg
					)
			)
			""", nativeQuery = true)
	public Page<TherapyCatalogResponseDTO> findAllTherapies(@Param("userId") Long userId, @Param("query") String query,
			@Param("salesOrg") String salesOrg, Pageable paging);

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
					m.material_number = :therapyId
				AND
					ac.partner_function = 'SH'
				AND
					uam.user_id = :userId
			)

			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllSoldToAccounts(@Param("userId") Long userId,
			@Param("therapyId") String therapyId, @Param("query") String query, Pageable paging);

	@Query(value = """
			SELECT DISTINCT ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2,
					 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code
			FROM usermanagement.account ac
			WHERE ac.sap_customer IN	(
					SELECT cp.ship_to_customer
					FROM catalog.material m
					JOIN catalog.catalog_price cp ON m.material_number = cp.material_identifier_for_cgat
					WHERE
						cp.sold_to_customer = :soldToAccountId
						AND
						m.material_number = :therapyId	)
			AND
				ac.partner_function = 'SP'
			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllShipToAccounts(@Param("soldToAccountId") String soldToAccountId,
			@Param("therapyId") String therapyId, Pageable paging);

}
