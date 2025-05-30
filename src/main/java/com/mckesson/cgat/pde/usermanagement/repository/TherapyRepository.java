package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogPriceResponse;
import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelProjectionDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;

@Repository
public interface TherapyRepository extends JpaRepository<Therapy, Long> {

	@Query(value = """
			         WITH COMBINED AS  (
			             SELECT m.id, m.material_number as material_number, m.material_description as material_description,
			                    m.ndc_number as ndc, m.strength, m.dosage, m.channel_Type, m.trade_name, m.manufacturer_name, m.generic_name
			             FROM catalog.material m
			             JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material AND m.channel_type = cp.channel_type
			             JOIN usermanagement.account ac  ON ac.sap_customer = cp.sold_to_customer
			             JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
			             WHERE
			                 NOT (m.channel_type = 'SPECIALITY_PHARMA' AND m.is_crosswalked = true)
			                 AND uam.user_id = :userId
			                 AND ac.partner_function = :partnerFunction
			                 AND (COALESCE(:salesOrg) IS NULL OR cp.sales_org IN (:salesOrg))
			                 AND (COALESCE(:customerIdentifier) IS NULL OR cp.sold_to_customer IN (:customerIdentifier))
			                 AND cp.deleted = false
							 AND
								m.item_status = 'A'
			             	AND ac.is_active = 1
			             union distinct

			             select  m.id, m.material_number as material_number, m.material_description as material_description,
			                    m.ndc_number as ndc, m.strength, m.dosage, m.channel_Type,  m.trade_name, m.manufacturer_name, m.generic_name
			             FROM catalog.material m
			             JOIN catalog.crosswalk cw
			                 ON cw.bnb_material_number = m.material_number
			             JOIN catalog.catalog_price cp
			                 ON cp.sap_material = cw.sp_product_id
			             JOIN usermanagement.account ac
			                 ON ac.sap_customer = cp.sold_to_customer
			             JOIN usermanagement.user_account_mapping uam
			                 ON ac.id = uam.account_id
			             WHERE
			                 uam.user_id = :userId
			                 AND ac.partner_function = :partnerFunction
			                 AND cp.deleted = false
			                 AND
								m.item_status = 'A'
							AND
								cw.is_active = 1
							AND ac.is_active = 1
			         )
			         SELECT
						com.trade_name as tradeName, com.channel_type AS channelType, STRING_AGG(DISTINCT com.id::text, ', ') AS id,
						STRING_AGG(DISTINCT com.material_number, ', ') AS therapyId,
						STRING_AGG(DISTINCT com.material_description, ', ') AS therapyDescription,
						STRING_AGG(DISTINCT com.ndc, ', ') AS ndc,
						STRING_AGG(DISTINCT com.strength, ', ') AS strength,
						STRING_AGG(DISTINCT com.dosage, ', ') AS dosage,
						STRING_AGG(DISTINCT com.manufacturer_name, ', ') AS manufacturerName,
						STRING_AGG(DISTINCT com.generic_name, ', ') AS genericName
			         from COMBINED com
					 where (
						cast(:query as varchar) is null
						OR LOWER(com.material_number) LIKE %:query%
						OR LOWER(com.ndc) LIKE %:query%
						OR LOWER(com.material_description) LIKE %:query%
						OR LOWER(com.strength) LIKE %:query%
						OR LOWER(com.dosage) LIKE %:query%
					)
			         GROUP BY com.trade_name, com.channel_type
			""",
			countQuery = """
							SELECT COUNT(*) FROM (
					         WITH COMBINED AS (
							 SELECT m.id, m.material_number as material_number, m.material_description as material_description,
					                  m.ndc_number as ndc, m.strength, m.dosage, m.channel_Type,  m.trade_name, m.manufacturer_name, m.generic_name
					         FROM catalog.material m
					         JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material AND m.channel_type = cp.channel_type
					         JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
					         JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
					         WHERE
					             NOT (m.channel_type = 'SPECIALITY_PHARMA' AND m.is_crosswalked = true)
					             AND uam.user_id = :userId
					             AND ac.partner_function = :partnerFunction
					           AND (COALESCE(:salesOrg) IS NULL OR cp.sales_org IN (:salesOrg))
					 		AND (COALESCE(:customerIdentifier) IS NULL OR cp.sold_to_customer IN (:customerIdentifier))
					             AND cp.deleted = false
					        AND
								m.item_status = 'A'
						 	AND ac.is_active = 1

					         UNION distinct

							 SELECT m.id, m.material_number as material_number, m.material_description as material_description,
					                  m.ndc_number as ndc, m.strength, m.dosage, m.channel_Type,  m.trade_name, m.manufacturer_name, m.generic_name
					         FROM catalog.material m
					         JOIN catalog.crosswalk cw ON cw.bnb_material_number = m.material_number
					         JOIN catalog.catalog_price cp ON cp.sap_material = cw.sp_product_id
					         JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
					         JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
					         WHERE
					             uam.user_id = :userId
					             AND ac.partner_function = :partnerFunction
					             AND cp.deleted = false
								 AND
									m.item_status = 'A'
								AND
									cw.is_active = 1
					         	AND ac.is_active = 1
					     )
						SELECT
							com.trade_name as tradeName, com.channel_type AS channelType, STRING_AGG(DISTINCT com.id::text, ', ') AS id,
							STRING_AGG(DISTINCT com.material_number, ', ') AS therapyId,
							STRING_AGG(DISTINCT com.material_description, ', ') AS therapyDescription,
							STRING_AGG(DISTINCT com.ndc, ', ') AS ndc,
							STRING_AGG(DISTINCT com.strength, ', ') AS strength,
							STRING_AGG(DISTINCT com.dosage, ', ') AS dosage,
							STRING_AGG(DISTINCT com.manufacturer_name, ', ') AS manufacturerName,
							STRING_AGG(DISTINCT com.generic_name, ', ') AS genericName
							FROM COMBINED com
					         where (
								cast(:query as varchar) is null
								OR LOWER(com.trade_name) LIKE %:query%
								OR LOWER(com.ndc) LIKE %:query%
							)
							GROUP BY com.trade_name, com.channel_type
					     ) AS total_count
					""",
			nativeQuery = true)
	Page<TherapyCatalogResponseDTO> findAllTherapies(@Param("userId") Long userId, @Param("query") String query,
			@Param("salesOrg") List<String> salesOrg, @Param("customerIdentifier") List<String> customerIdentifier,
			@Param("partnerFunction") String partnerFunction, Pageable paging);

	@Query(value = """
				SELECT ac.id, ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2 as street2,
						 ac.street_3 as street3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier, ac.channel_Type
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
						cast(:program as varchar) is null
						or ac.cgat_identifier = :program
						)
					AND
						m.ndc_number = :ndcNumber
					AND
						ac.partner_function = :partnerFunction
					AND
						uam.user_id = :userId
					AND
						cp.deleted = false
				    AND ac.channel_type=:channel
				    AND m.channel_type=:channel
					)
					GROUP BY ac.id, ac.sap_customer, ac.name_1, ac.house_number, ac.street, ac.street_2,
						 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel, ac.cgat_identifier, ac.channel_Type
			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllSoldToAccounts(@Param("userId") Long userId,
			@Param("ndcNumber") String ndcNumber, @Param("query") String query, Pageable paging,
			@Param("channel") String channel, @Param("program") String program,
			@Param("partnerFunction") String partnerFunction);

	// Need to fix/test after column rename, under JOIN catalog.material m on m.ndc_number
	// = cw.bnb_material_number, after deepaks merge
	@Query(value = """
			SELECT ac.id, ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2 as street2,
						 ac.street_3 as street3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier, ac.channel_type
				FROM catalog.crosswalk cw
				JOIN catalog.material m on m.ndc_number = cw.bnb_material_number
				JOIN catalog.catalog_price cp ON cp.sap_material = cw.sp_product_id
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
						m.ndc_number = :ndcNumber
					AND
						ac.partner_function = :partnerFunction
					AND
						uam.user_id = :userId
					AND
						cp.deleted = false
				    AND
						m.item_status = 'A'
				    AND
						cw.is_active = 1
					)
					GROUP BY ac.id, ac.sap_customer, ac.name_1, ac.house_number, ac.street, ac.street_2,
						 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel, ac.cgat_identifier, ac.channel_Type
			""",
			nativeQuery = true)
	Page<AccountsResponseDTO> findAllSoldToAccountsForSP(@Param("userId") Long userId,
			@Param("ndcNumber") String ndcNumber, @Param("query") String query, Pageable paging,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
				SELECT ac.id, ac.sap_partner as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2 as street2,
						 ac.street_3 as street3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier, ac.channel_Type
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
						m.ndc_number = :ndcNumber
					AND
						ac.partner_function = :partnerFunction
					AND
					    ac.sap_customer= :soldToAccountId
					AND
						cp.deleted = false
					)
					GROUP BY ac.id, ac.sap_customer, ac.name_1, ac.house_number, ac.street, ac.street_2,
						 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel, ac.cgat_identifier, ac.channel_Type
			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllShipToAccounts(@Param("soldToAccountId") String soldToAccountId,
			@Param("ndcNumber") String ndcNumber, @Param("query") String query, Pageable paging,
			@Param("partnerFunction") String partnerFunction);

	@Query(value = """
			SELECT ac.cgat_identifier as program, m.is_crosswalked as crosswalked
			FROM catalog.material m
			JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
			JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
			JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
			WHERE
			(
					uam.user_id = :userId
				AND
					m.ndc_number = :ndcNumber
				AND
					ac.partner_function = :partnerFunction
				AND
					cp.deleted = false
			    AND m.channel_type = :channelType
			)
			""", nativeQuery = true)
	List<ProgramChannelProjectionDTO> findAllPrograms(@Param("userId") Long userId,
			@Param("ndcNumber") String ndcNumber, @Param("partnerFunction") String partnerFunction, String channelType);

	// to do : change the column name of cw.bnb_material_number
	@Query(value = """
				select cw.sp_product_id
			 	FROM catalog.crosswalk cw
			 	JOIN catalog.material m on m.ndc_number = cw.bnb_material_number
				JOIN catalog.catalog_price cp ON cp.sap_material = cw.sp_product_id
				JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
				JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
				WHERE
				(
						uam.user_id = :userId
					AND
						m.ndc_number = :ndcNumber
					AND
						ac.partner_function = :partnerFunction
					AND
						cp.deleted = false
					AND
						m.item_status = 'A'
				    AND
						cw.is_active = 1
				)
			""", nativeQuery = true)
	List<String> findAllCrossWalkedSPsForUser(@Param("userId") Long userId, @Param("ndcNumber") String ndcNumber,
			@Param("partnerFunction") String partnerFunction);

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
			AND (
				    (
				      :ignoreCrosswalkedSP = true
				      AND
				      (
					    (m.channel_type = 'SPECIALITY_PHARMA' AND m.is_crosswalked = false)
					    OR m.channel_type = 'BUY_AND_BILL'
					  )
				    )
				 OR
				   (
				      :ignoreCrosswalkedSP = false
				      AND
				      (
				        :channelTypes = 'ALL'
				        OR m.channel_type = :channelTypes
				      )
				    )
				)
			""", nativeQuery = true)
	Page<Therapy> findAllGlobalTherapies(@Param("query") String query, String statusActive, String channelTypes,
			Pageable paging, @Param("ignoreCrosswalkedSP") boolean ignoreCrosswalkedSP);

	List<Therapy> findByMaterialNumberInAndChannelTypeAndItemStatus(Set<String> productIds, ChannelType channelType,
			String itemStatus);

	Optional<Therapy> findByMaterialNumberAndChannelType(String materialNumber, ChannelType channelType);

	List<Therapy> findByNdcNumberAndChannelType(String ndcNumber, ChannelType channelType);

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

	@Query(value = """
			    SELECT m.*
			    FROM catalog.material m
			    JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
			    JOIN catalog.crosswalk cw ON cp.sap_material = cw.sp_product_id
				JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
			    JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
			    WHERE m.channel_type = 'SPECIALITY_PHARMA'
			      AND m.item_status = 'A'
			      AND cp.channel_type = 'SPECIALITY_PHARMA'
			      AND cp.deleted = false
			      AND cw.bnb_material_number = :materialNumber
			      AND cw.is_active = 1
			      AND uam.user_id = :userId
			""", nativeQuery = true)
	List<Therapy> findCrosswalkedTherapies(String materialNumber, long userId);

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
			AND m.is_crosswalked = :crosswalked
			""", nativeQuery = true)
	Page<Therapy> findNonCrosswalkedSPGlobalTherapies(@Param("query") String query, String statusActive,
			String channelTypes, @Param("crosswalked") Boolean crosswalked, Pageable paging);

	@Query(value = """
				SELECT distinct m.*
				  FROM catalog.material m
				  JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
				  JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
				  JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
				  WHERE m.channel_type = :channelType
				  AND m.item_status = 'A'
				  AND cp.channel_type = :channelType
				  AND cp.deleted = false
				  AND m.trade_name = :tradeName
				  AND uam.user_id = :userId
			""", nativeQuery = true)
	List<Therapy> findByTradeNameAndChannelType(@Param("tradeName") String tradeName,
			@Param("channelType") String channelType, @Param("userId") Long userId);

	@Query("""
				     SELECT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
					 FROM CatalogPrice cp
					 JOIN Therapy m ON m.materialNumber = cp.sapMaterial
					 left JOIN TherapyConfig tc ON m.materialNumber = tc.materialNumber
					 JOIN Account ac ON ac.accountNumber = cp.soldToCustomer
					 WHERE cp.soldToCustomer = :soldToAccount
					 AND cp.shipToCustomer = :shipToAccount
					 AND (:program IS NULL OR ac.cgatIdentifier = :program)
					 AND m.ndcNumber = :ndcNumber
				     AND cp.deleted = false
					 AND m.channelType = :channelType
					 AND cp.channelType = :channelType
					 AND m.item_status = 'A'
			""")
	Optional<MaterialResponseDTO> findTherapyAndConfigBySoldToAccountAndProgramAndNdc(
			@Param("soldToAccount") String soldToAccount, @Param("shipToAccount") String shipToAccount,
			@Param("program") String program, @Param("ndcNumber") String ndcNumber,
			@Param("channelType") ChannelType channelType);

}
