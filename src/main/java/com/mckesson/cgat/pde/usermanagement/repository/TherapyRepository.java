package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.ShipToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.SoldToAccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;

@Repository
public interface TherapyRepository extends JpaRepository<Therapy, Long> {

	@Query(value = """
			SELECT m.id, m.material_description, cp.ndc, cp.udi
			FROM usermanagement.material m
			JOIN usermanagement.catalog_price cp ON cast(m.id as varchar) = cp.sap_material
			WHERE
			(
				(
					:query is null
					or LOWER(cp.ndc) like %:query%
					or LOWER(cp.dist_channel) like %:query%
					or LOWER(cp.sap_material) like %:query%
					or LOWER(cp.udi) like %:query%
				)
				AND
					cp.sold_to_customer IN :soldToAccountIds
				AND
					(
					cast(:salesOrg as varchar) is null
					or
					:salesOrg = cp.sales_org
					)
			)
			""", nativeQuery = true)
	public Page<TherapyCatalogResponseDTO> findAllTherapiesCatalogs(
			@Param("soldToAccountIds") List<String> soldToAccountIds, @Param("query") String query,
			@Param("salesOrg") String salesOrg, Pageable paging);

	@Query(value = """
			SELECT cp.sold_to_customer, cp.sold_to_customer_name
			FROM usermanagement.material m
			JOIN usermanagement.catalog_price cp ON cast(m.id as varchar) = cp.sap_material
			WHERE
			(
				(
					:query is null
					or LOWER(cp.sold_to_customer) like %:query%
					or LOWER(cp.sold_to_customer_name) like %:query%
				)
				AND
					m.id = :therapyId
				AND
					cp.sold_to_customer IN :soldToAccountIds
			)

			""", nativeQuery = true)
	public Page<SoldToAccountsResponseDTO> findAllSoldToAccounts(@Param("therapyId") Long therapyId,
			@Param("soldToAccountIds") List<String> soldToAccountIds, @Param("query") String query, Pageable paging);

	@Query(value = """
			SELECT cp.ship_to_customer, cp.ship_to_customer_name
			FROM usermanagement.material m
			JOIN usermanagement.catalog_price cp ON cast(m.id as varchar) = cp.sap_material
			WHERE
				cp.sold_to_customer = :soldToAccountId
				AND
				m.id = :therapyId
			""", nativeQuery = true)
	public Page<ShipToAccountsResponseDTO> findAllShipToAccounts(@Param("soldToAccountId") String soldToAccountId,
			@Param("therapyId") Long therapyId, Pageable paging);

}
