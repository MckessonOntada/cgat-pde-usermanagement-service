package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.dto.TherapyResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelProjectionDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TherapyRepository extends JpaRepository<Therapy, Long> {

	@Query(value = """
				SELECT m.id, m.material_number as therapyId, m.material_description as therapyDescription, m.ndc_number as ndc, cp.udi, m.strength, m.dosage, m.selling_unit_code,
				m.vendor_name, m.upc_code, m.item_category_group, m.item_status as status, m.base_unit_of_measure, m.cgat_therapy_type, m.sales_organization,
				 m.product_level_pricing, cp.minimum_order_quantity
				FROM catalog.material m
				JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
				JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
				JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
				WHERE
				(
					(
						:query is null
						or LOWER(m.ndc_number) like %:query%
						or LOWER(cp.dist_channel) like %:query%
						or LOWER(cp.sap_material) like %:query%
						or LOWER(m.material_description) like %:query%
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
					AND cp.deleted = false
				)
				GROUP BY m.id, m.material_number, m.material_description, m.ndc_number, cp.udi, m.strength, m.dosage, m.selling_unit_code,
				m.vendor_name, m.upc_code, m.item_category_group, m.base_unit_of_measure, m.cgat_therapy_type, m.sales_organization,
				 m.product_level_pricing, cp.minimum_order_quantity
			""",
			nativeQuery = true)
	public Page<TherapyCatalogResponseDTO> findAllTherapies(@Param("userId") Long userId, @Param("query") String query,
			@Param("salesOrg") List<String> salesOrg, Pageable paging,
			@Param("customerIdentifier") List<String> customerIdentifier,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
				SELECT ac.id, ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2 as street2,
						 ac.street_3 as street3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier
				FROM catalog.material m
				JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
				JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
				JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
				WHERE
				(
						(
							:query is null
							or LOWER(ac.sap_partner) like %:query%
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
					AND
						cp.deleted = false
					)
					GROUP BY ac.id, ac.sap_customer, ac.name_1, ac.house_number, ac.street, ac.street_2,
						 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel, ac.cgat_identifier
			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllSoldToAccounts(@Param("userId") Long userId,
			@Param("therapyId") String therapyId, @Param("query") String query, Pageable paging,
			@Param("channel") String channel, @Param("program") String program,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
				SELECT ac.id, ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2 as street2,
						 ac.street_3 as street3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier
				FROM catalog.material m
				JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
				JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
				JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
				WHERE
				(
						(
							:query is null
							or LOWER(ac.sap_partner) like %:query%
							or LOWER(ac.name_1) like %:query%
						)
					AND
						(COALESCE(:channels) IS NULL OR ac.distribution_channel IN (:channels))
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
					AND
						cp.deleted = false
					)
					GROUP BY ac.id, ac.sap_customer, ac.name_1, ac.house_number, ac.street, ac.street_2,
						 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel, ac.cgat_identifier
			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllSoldToAccounts(@Param("userId") Long userId,
			@Param("therapyId") String therapyId, @Param("query") String query, Pageable paging,
			@Param("channels") List<String> channels, @Param("program") String program,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
				SELECT ac.id, ac.sap_partner as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2 as street2,
						 ac.street_3 as street3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier
				FROM catalog.material m
				JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
				JOIN usermanagement.account ac ON ac.sap_partner = cp.ship_to_customer
				WHERE
				(
					(
						:query is null
						or LOWER(ac.sap_partner) like %:query%
						or LOWER(ac.name_1) like %:query%
					)
					AND
						cp.sold_to_customer = :soldToAccountId
					AND
						cp.sap_material = :therapyId
					AND
						ac.partner_function = :partnerFunction
					AND
					    ac.sap_customer= :soldToAccountId
					AND
						cp.deleted = false
					)
					GROUP BY ac.id, ac.sap_customer, ac.name_1, ac.house_number, ac.street, ac.street_2,
						 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel, ac.cgat_identifier
			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllShipToAccounts(@Param("soldToAccountId") String soldToAccountId,
			@Param("therapyId") String therapyId, @Param("query") String query, Pageable paging,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
			SELECT ac.cgat_identifier as program, ac.sales_org as channel
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
				AND
					cp.deleted = false
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

	@Query(value = """
			SELECT m.*
			FROM catalog.material m
			WHERE
			(
			    :query IS NULL
			    OR LOWER(m.material_number) LIKE %:query%
			    OR LOWER(m.ndc_number) LIKE %:query%
			    OR LOWER(m.material_description) LIKE %:query%
			    OR LOWER(m.generic_name) LIKE %:query%
			    OR LOWER(m.site_name) LIKE %:query%
			)
			AND
			(
			    :statusActive = 'ALL'
			    OR (:statusActive = 'ACTIVE' AND m.item_status = 'A')
			    OR (:statusActive = 'INACTIVE' AND m.item_status = 'V')
			)
			AND
			(
			    :channelTypes = 'ALL'
			    OR m.channel_type = :channelTypes
			)
			""", nativeQuery = true)
	Page<Therapy> findAllGlobalTherapies(@Param("query") String query, String statusActive, String channelTypes,
			Pageable paging);

	List<Therapy> findByMaterialNumberInAndChannelTypeAndItemStatus(Set<String> productIds, ChannelType channelType,
			String itemStatus);

	Optional<Therapy> findByMaterialNumberAndChannelType(String materialNumber, ChannelType channelType);

	List<Therapy> findByMaterialNumberInAndChannelType(List<String> materialNumbers, ChannelType channelType);

	@Modifying
	@Query(value = """
			UPDATE catalog.material
			SET is_crosswalked = :isCrossWalked
			WHERE material_number IN (:materialNumbers)
			AND channel_type = :channelType
			""", nativeQuery = true)
	void updateIsCrossWalkedForMaterialNumbersAndChannelType(@Param("isCrossWalked") boolean isCrossWalked,
			@Param("materialNumbers") Set<String> materialNumbers, @Param("channelType") String channelType);

	List<Therapy> findAllByChannelTypeAndItemStatus(ChannelType channelType, String itemStatus);

	@Query("""
    SELECT new com.mckesson.cgat.pde.usermanagement.dto.TherapyResponseDTO(t, t.channelType)
    FROM Therapy t
    WHERE t.id = :therapyId AND t.isCrosswalked = false
    """)
	Optional<TherapyResponseDTO> findTherapyWithCrosswalkById(Long therapyId);

}
