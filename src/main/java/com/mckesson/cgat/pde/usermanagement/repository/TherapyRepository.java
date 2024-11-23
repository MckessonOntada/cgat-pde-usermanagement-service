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
				cp.sold_to_customer IN :soldToAccountIds
			""", nativeQuery = true)
	public Page<TherapyCatalogResponseDTO> getAllTherapiesCatalogs(
			@Param("soldToAccountIds") List<String> soldToAccountIds, Pageable paging);

	@Query(value = """
			SELECT cp.sold_to_customer, cp.sold_to_customer_name
			FROM usermanagement.material m
			JOIN usermanagement.catalog_price cp ON cast(m.id as varchar) = cp.sap_material
			WHERE
				m.id = :therapyId
				AND
				cp.sold_to_customer IN :soldToAccountIds
			""", nativeQuery = true)
	public Page<SoldToAccountsResponseDTO> getSoldToAccounts(@Param("therapyId") Long therapyId,
			@Param("soldToAccountIds") List<String> soldToAccountIds, Pageable paging);

	@Query(value = """
			SELECT cp.ship_to_customer, cp.ship_to_customer_name
			FROM usermanagement.material m
			JOIN usermanagement.catalog_price cp ON cast(m.id as varchar) = cp.sap_material
			WHERE
				cp.sold_to_customer = :soldToAccountId
				AND
				m.id = :therapyId
			""", nativeQuery = true)
	public Page<ShipToAccountsResponseDTO> getAllShipToAccounts(@Param("soldToAccountId") String soldToAccountId,
			@Param("therapyId") Long therapyId, Pageable paging);

}
