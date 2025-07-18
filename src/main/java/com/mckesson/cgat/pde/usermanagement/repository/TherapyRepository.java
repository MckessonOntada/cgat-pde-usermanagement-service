package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.ProgramChannelProjectionDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;

@Repository
public interface TherapyRepository extends JpaRepository<Therapy, Long> {

	@Query(value = """
			         WITH COMBINED AS  (
			             SELECT m.id, m.material_number as material_number, m.material_description as material_description,
			                    m.ndc_number as ndc, m.strength, m.dosage, m.channel_Type, m.trade_name, m.manufacturer_name, m.generic_name,m.product_form
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
			                    m.ndc_number as ndc, m.strength, m.dosage, m.channel_Type,  m.trade_name, m.manufacturer_name, m.generic_name,m.product_form
			             FROM catalog.material m
			             JOIN catalog.crosswalk cw
			                 ON cw.bnb_ndc_number = m.ndc_number
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
						com.trade_name as tradeName, com.channel_type AS channelType, STRING_AGG(com.id::text, ', ') AS id,
						STRING_AGG(com.material_number, ', ') AS therapyId,
						STRING_AGG(com.material_description, ', ') AS therapyDescription,
						STRING_AGG(com.ndc, ', ') AS ndc,
						STRING_AGG(com.strength, ', ') AS strength,
						STRING_AGG(com.dosage, ', ') AS dosage,
						STRING_AGG(com.manufacturer_name, ', ') AS manufacturerName,
						STRING_AGG(com.generic_name, ', ') AS genericName,
						STRING_AGG(com.product_form, ', ') AS productForm
			         from COMBINED com
					 where (
						cast(:query as varchar) is null
						OR LOWER(com.ndc) LIKE %:query%
						OR LOWER(com.trade_name) LIKE %:query%
						OR LOWER(com.strength) LIKE %:query%
						OR LOWER(com.dosage) LIKE %:query%
					)
			         GROUP BY com.trade_name, com.channel_type
			""",
			countQuery = """
							SELECT COUNT(*) FROM (
					         WITH COMBINED AS (
							 SELECT m.id, m.material_number as material_number, m.material_description as material_description,
					                  m.ndc_number as ndc, m.strength, m.dosage, m.channel_Type,  m.trade_name, m.manufacturer_name, m.generic_name, m.product_form
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
					                  m.ndc_number as ndc, m.strength, m.dosage, m.channel_Type,  m.trade_name, m.manufacturer_name, m.generic_name,m.product_form
					         FROM catalog.material m
					         JOIN catalog.crosswalk cw ON cw.bnb_ndc_number = m.ndc_number
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
							STRING_AGG(DISTINCT com.generic_name, ', ') AS genericName,
							STRING_AGG(DISTINCT com.product_form, ', ') AS productForm
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

	@Query(value = """
			SELECT ac.id, ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2 as street2,
						 ac.street_3 as street3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, false as isDefault,
						 ac.sales_org, ac.company_code, ac.division, ac.distribution_channel as distChannel, ac.cgat_identifier, ac.channel_type
				FROM catalog.crosswalk cw
				JOIN catalog.material m on m.ndc_number = cw.bnb_ndc_number
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

	@Query(value = """
				select cw.sp_product_id
			 	FROM catalog.crosswalk cw
			 	JOIN catalog.material m on m.ndc_number = cw.bnb_ndc_number
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
			       SELECT
			                      m.trade_name AS tradeName,
			                      m.channel_type AS channelType,
			                      STRING_AGG(COALESCE(m.id::text, ''), ',') AS id,
			                      STRING_AGG(COALESCE(m.material_number, ''), ',') AS therapyId,
			                      STRING_AGG(COALESCE(m.material_description, ''), ',') AS therapyDescription,
			                      STRING_AGG(COALESCE(m.ndc_number, ''), ',') AS ndc,
			                      STRING_AGG(COALESCE(m.strength, ''), ',') AS strength,
			                      STRING_AGG(COALESCE(m.dosage, ''), ',') AS dosage,
			                      STRING_AGG(COALESCE(m.manufacturer_name, ''), ',') AS manufacturerName,
			                      STRING_AGG(COALESCE(m.generic_name, ''), ',') AS genericName,
			                      STRING_AGG(COALESCE(m.product_form, ''), ',') AS productForm

			                  FROM
			                      catalog.material m
			                  WHERE
			                      (
			                          :query IS NULL
			                          OR LOWER(m.ndc_number) LIKE %:query%
			                          OR LOWER(m.generic_name) LIKE %:query%
			                          OR LOWER(m.trade_name) LIKE %:query%
			                          OR LOWER(m.dosage) LIKE %:query%
			                          OR LOWER(m.strength) LIKE %:query%
			                      )
			                      AND (
			                          :statusActive = 'ALL'
			                          OR (:statusActive = 'ACTIVE' AND m.item_status = 'A')
			                          OR (:statusActive = 'INACTIVE' AND m.item_status = 'V')
			                      )
			                      AND (
			                          (
			                              :ignoreCrosswalkedSP = TRUE
			                              AND (
			                                  (m.channel_type = 'SPECIALITY_PHARMA' AND m.is_crosswalked = FALSE)
			                                  OR m.channel_type = 'BUY_AND_BILL'
			                              )
			                          )
			                          OR (
			                              :ignoreCrosswalkedSP = FALSE
			                              AND (
			                                  :channelTypes = 'ALL'
			                                  OR m.channel_type = :channelTypes
			                              )
			                          )
			                      )
			                  GROUP BY
			                      m.trade_name,
			                      m.channel_type
			""", nativeQuery = true)
	Page<TherapyCatalogResponseDTO> findAllGlobalTherapies(@Param("query") String query, String statusActive,
			String channelTypes, Pageable paging, @Param("ignoreCrosswalkedSP") boolean ignoreCrosswalkedSP);

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
			    OR LOWER(m.trade_name) LIKE %:query%
			)
			AND
			(
			    :statusActive = 'ALL'
			    OR (:statusActive = 'ACTIVE' AND m.item_status = 'A')
			    OR (:statusActive = 'INACTIVE' AND m.item_status = 'V')
			)
			AND
			 (
			        :manufacturer = 'ALL'
			        OR m.manufacturer_name = :manufacturer
			    )
			AND
			    m.channel_type = 'SPECIALITY_PHARMA'
			""", nativeQuery = true)
	Page<Therapy> findAllGlobalSPTherapies(@Param("query") String query, String statusActive, Pageable paging,
			String manufacturer);

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
			    SELECT distinct m.*
			    FROM catalog.material m
			    JOIN catalog.catalog_price cp ON m.material_number = cp.sap_material
			    JOIN catalog.crosswalk cw ON cp.sap_material = cw.sp_product_id
				JOIN usermanagement.account ac ON ac.sap_customer = cp.sold_to_customer
			    JOIN usermanagement.user_account_mapping uam ON ac.id = uam.account_id
			    WHERE m.channel_type = 'SPECIALITY_PHARMA'
			      AND m.item_status = 'A'
			      AND cp.channel_type = 'SPECIALITY_PHARMA'
			      AND cp.deleted = false
			      AND cw.bnb_ndc_number = :ndcNumber
			      AND cw.is_active = 1
			      AND uam.user_id = :userId
			""", nativeQuery = true)
	List<Therapy> findCrosswalkedTherapies(String ndcNumber, long userId);

	@Query(value = """
			      SELECT
			                   m.trade_name AS tradeName,
			                   m.channel_type AS channelType,
			                   STRING_AGG(m.id::text, ', ') AS id,
			                   STRING_AGG(m.material_number, ', ') AS therapyId,
			                   STRING_AGG(m.material_description, ', ') AS therapyDescription,
			                   STRING_AGG(m.ndc_number, ', ') AS ndc,
			                   STRING_AGG(m.strength, ', ') AS strength,
			                   STRING_AGG(m.dosage, ', ') AS dosage,
			                   STRING_AGG(m.manufacturer_name, ', ') AS manufacturerName,
			                   STRING_AGG(m.generic_name, ', ') AS genericName,
			                   STRING_AGG(m.product_form, ', ') AS productForm
			               FROM
			                   catalog.material m
			               WHERE
			                   (
			                       :query IS NULL
			                       OR LOWER(m.ndc_number) LIKE %:query%
			                       OR LOWER(m.generic_name) LIKE %:query%
			                       OR LOWER(m.trade_name) LIKE %:query%
			                   )
			                   AND (
			                       :statusActive = 'ALL'
			                       OR (:statusActive = 'ACTIVE' AND m.item_status = 'A')
			                       OR (:statusActive = 'INACTIVE' AND m.item_status = 'V')
			                   )
			                   AND (
			                       :channelTypes = 'ALL'
			                       OR m.channel_type = :channelTypes
			                   )
			                   AND m.is_crosswalked = :crosswalked
			               GROUP BY
			                   m.trade_name,
			                   m.channel_type
			""", nativeQuery = true)
	Page<TherapyCatalogResponseDTO> findNonCrosswalkedSPGlobalTherapies(@Param("query") String query,
			String statusActive, String channelTypes, @Param("crosswalked") Boolean crosswalked, Pageable paging);

	List<Therapy> findByNdcNumberInAndChannelType(List<String> distinctBnbMaterialNDC, ChannelType channelType);

	@Modifying
	@Query(value = """
			UPDATE catalog.material
			SET is_crosswalked = :isCrossWalked
			WHERE ndc_number IN (:ndcNumbers)
			AND channel_type = :channelType
			""", nativeQuery = true)
	void updateIsCrossWalkedForNdcsAndChannelType(@Param("isCrossWalked") boolean isCrossWalked,
			@Param("ndcNumbers") Set<String> ndcNumbers, @Param("channelType") String channelType);

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
				     SELECT DISTINCT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
					 FROM CatalogPrice cp
					 JOIN Therapy m ON m.materialNumber = cp.sapMaterial
					 left JOIN TherapyConfig tc ON m.ndcNumber = tc.ndcNumber
					 JOIN Account ac ON ac.accountNumber = cp.soldToCustomer
					 WHERE cp.soldToCustomer = :soldToAccount
					 AND cp.shipToCustomer = :shipToAccount
					 AND ac.partnerFunction  = :partnerFunction
					 AND (:program IS NULL OR ac.cgatIdentifier = :program)
					 AND m.ndcNumber = :ndcNumber
				     AND cp.deleted = false
					 AND m.channelType = :channelType
					 AND cp.channelType = :channelType
					 AND m.itemStatus = 'A'
			""")
	List<MaterialResponseDTO> findTherapyAndConfigBySoldToAccountAndProgramAndNdc(
			@Param("soldToAccount") String soldToAccount, @Param("shipToAccount") String shipToAccount,
			@Param("program") String program, @Param("ndcNumber") String ndcNumber,
			@Param("channelType") ChannelType channelType,
			@Param("partnerFunction") AccountPartnerFunction partnerFunction);

	@Query("""
			     SELECT DISTINCT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,stc)
				 FROM CatalogPrice cp
				 JOIN Therapy m ON m.materialNumber = cp.sapMaterial
				 left JOIN SPTherapyConfig stc ON m.materialNumber = stc.spProductId
				 JOIN Account ac ON ac.accountNumber = cp.soldToCustomer
				 WHERE cp.soldToCustomer = :soldToAccount
				 AND cp.shipToCustomer = :shipToAccount
				 AND ac.partnerFunction  = :partnerFunction
			     AND cp.deleted = false
				 AND m.channelType = 'SPECIALITY_PHARMA'
				 AND cp.channelType = 'SPECIALITY_PHARMA'
				 AND m.itemStatus = 'A'
				 and m.ndcNumber = :ndcNumber
			""")
	List<MaterialResponseDTO> findSPTherapyAndConfigBySoldToAccountAndProgramAndNdc(
			@Param("soldToAccount") String soldToAccount, @Param("shipToAccount") String shipToAccount,
			@Param("ndcNumber") String ndcNumber, @Param("partnerFunction") AccountPartnerFunction partnerFunction);

	@Query("""
			     SELECT DISTINCT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,stc)
				 FROM CatalogPrice cp
				 JOIN Therapy m ON m.materialNumber = cp.sapMaterial
				 left JOIN SPTherapyConfig stc ON m.materialNumber = stc.spProductId
				 left JOIN Crosswalk cw on cw.spProductId  = cp.sapMaterial
				 JOIN Account ac ON ac.accountNumber = cp.soldToCustomer
				 WHERE cp.soldToCustomer = :soldToAccount
				 AND cp.shipToCustomer = :shipToAccount
				 AND ac.partnerFunction  = :partnerFunction
				 AND cw.bnbNdcNumber = :ndcNumber
			     AND cp.deleted = false
				 AND m.channelType = 'SPECIALITY_PHARMA'
				 AND cp.channelType = 'SPECIALITY_PHARMA'
				 AND m.itemStatus = 'A'
			""")
	List<MaterialResponseDTO> findCrossWalkedSPTherapyAndConfigBySoldToAccountAndProgramAndNdc(
			@Param("soldToAccount") String soldToAccount, @Param("shipToAccount") String shipToAccount,
			@Param("ndcNumber") String ndcNumber, @Param("partnerFunction") AccountPartnerFunction partnerFunction);

	@Query("""
			    SELECT DISTINCT m.manufacturerName
			    FROM Therapy m
			    WHERE m.channelType = :channelType
			      AND m.manufacturerName IS NOT NULL
			      AND (
			            :itemStatus = 'ALL'
			            OR (:itemStatus = 'ACTIVE' AND m.itemStatus = 'A')
			            OR (:itemStatus = 'INACTIVE' AND m.itemStatus = 'V')
			          )
			""")
	List<String> findDistinctManufacturerNames(@Param("channelType") ChannelType channelType,
			@Param("itemStatus") String itemStatus);

}
