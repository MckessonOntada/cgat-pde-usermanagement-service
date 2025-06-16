package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.dto.SpecialtyPharmaAccountDto;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

	@Query(nativeQuery = true, value = """
			SELECT *
			FROM usermanagement.account a
			WHERE
			(
				(
					:query IS NULL
				 	OR LOWER(a.name_1) LIKE %:query%
				 	OR LOWER(a.sap_partner) LIKE %:query%
				 	OR LOWER(a.full_address) LIKE %:query%
				)
				AND
				(
					(CAST(:practiceId AS varchar) is NULL and (tenant_id is null))
					OR (tenant_id =:practiceId )
				)
				AND
				(
					(:status = 'ALL' and (is_active = 1 or is_active = 0))
					OR ('ACTIVE' = :status and is_active = 1 )
					OR ('INACTIVE' = :status and is_active = 0 )
				)
				AND
				(
					COALESCE(:salesOrg) is null
					or
					sales_org IN (:salesOrg)
				)
				AND
				(
					COALESCE(:distChannels) is null
					or
					distribution_channel IN (:distChannels)
				)
				and partner_function = :partnerFunction
				and
				(
					(:channelType = 'ALL')
					OR ('BUY_AND_BILL' = :channelType and channel_type = 'BUY_AND_BILL' )
					OR ('SPECIALITY_PHARMA' = :channelType and channel_type = 'SPECIALITY_PHARMA' )
				)
			)""")
	Page<Account> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging,
			@Param("practiceId") Long practiceId, String partnerFunction, @Param("salesOrg") List<String> salesOrg,
			@Param("distChannels") List<String> distChannels, @Param("channelType") String channelType);

	Set<Account> findByPracticeId(Long practiceId);

	@Query(nativeQuery = true, value = """
			select *
			FROM usermanagement.account ua
			JOIN usermanagement.user_account_mapping uam ON ua.id = uam.account_id
			WHERE
			(
				(
					:query IS NULL
				 	OR LOWER(ua.name_1) LIKE %:query%
				 	OR LOWER(ua.sap_partner) LIKE %:query%
				)
				AND
				(
					(CAST(:practiceId AS varchar) is NULL and (tenant_id is null))
					OR (ua.tenant_id =:practiceId )
				)
				AND
					uam.user_id =:userId
				AND
				(
					(:status = 'ALL' and (ua.is_active = 1 or ua.is_active = 0))
					OR ('ACTIVE' = :status and ua.is_active = 1 )
					OR ('INACTIVE' = :status and ua.is_active = 0 )
				)
				AND
				(
					COALESCE(:salesOrg) is null
					or
					ua.sales_org IN (:salesOrg)
				)
				AND
				(
					COALESCE(:distChannels) is null
					or
					ua.distribution_channel IN (:distChannels)
				)
			    AND ua.partner_function = :partnerFunction
			    AND
				(
					(:channelType = 'ALL')
					OR ('BUY_AND_BILL' = :channelType and channel_type = 'BUY_AND_BILL' )
					OR ('SPECIALITY_PHARMA' = :channelType and channel_type = 'SPECIALITY_PHARMA' )
				)
			)""")
	Page<Account> findAccountsByUser(@Param("userId") Long userId, @Param("query") String query,
			@Param("status") String status, Pageable pageable, @Param("practiceId") Long practiceId,
			String partnerFunction, @Param("salesOrg") List<String> salesOrg,
			@Param("distChannels") List<String> distChannels, @Param("channelType") String channelType);

	@Query("select a FROM Account a WHERE a.accountNumber = :accountNumber AND a.partnerFunction = :partnerFunction")
	Set<Account> findByAccountNumber(String accountNumber, AccountPartnerFunction partnerFunction);

	@Query(value = """
			SELECT DISTINCT ON (ac1.sap_partner) ac1.id, ac1.sap_partner as AccountNumber, ac1.name_1 as AccountName, ac1.house_number, ac1.street, ac1.street_2 as street2, ac1.street_3 as street3,
			ac1.telephone, ac1.city, ac1.district, ac1.state, ac1.country, ac1.zip_code, false as isDefault, ac1.sales_org, ac1.company_code, ac1.channel_Type
			FROM usermanagement.account ac1
			JOIN usermanagement.account ac2 ON ac1.sap_customer = ac2.sap_customer
			JOIN usermanagement.user_account_mapping uam ON ac2.id = uam.account_id
			WHERE(
					(
						:query IS NULL
					 	OR LOWER(ac1.name_1) LIKE %:query%
					 	OR LOWER(ac1.sap_partner) LIKE %:query%
					)
					AND
					uam.user_id = :userId
					AND
					ac1.partner_function = :partnerFunction
				)
			""",
			nativeQuery = true)
	public Page<AccountsResponseDTO> findAllPayToAccounts(@Param("userId") Long userId, @Param("query") String query,
			Pageable paging, @Param("partnerFunction") String partnerFunction);

	@Query(nativeQuery = true, value = """
			select ua.id
			FROM usermanagement.account ua
			JOIN usermanagement.user_account_mapping uam ON ua.id = uam.account_id
			WHERE
			(
					ua.tenant_id =:practiceId
				AND
					uam.user_id =:userId
			    AND
			    	ua.partner_function = :partnerFunction
			)""")
	List<Long> findAccountIdsByUser(@Param("userId") Long userId, @Param("practiceId") Long practiceId,
			String partnerFunction);

	List<Account> findAllByAccountNumberInAndPartnerFunction(List<String> accountNumberList,
			AccountPartnerFunction accountPartnerFunction);

	List<Account> findByAccountNumberInAndChannelTypeAndIsActive(Set<String> accountNumbers, ChannelType channelType,
			boolean isActive);

	List<Account> findAllByChannelTypeAndIsActive(ChannelType channelType, boolean status);

	@Query(nativeQuery = true, value = """
			SELECT *
			FROM usermanagement.account a
			WHERE
			(
				(
					:query IS NULL
				 	OR LOWER(a.name_1) LIKE %:query%
				 	OR LOWER(a.sap_partner) LIKE %:query%
				 	OR LOWER(a.full_address) LIKE %:query%
				)
				AND
				(
			      COALESCE(:practiceIdList) IS NULL
			      OR tenant_id IN (:practiceIdList)
			  	)
				AND
				(
					(:status = 'ALL' and (is_active = 1 or is_active = 0))
					OR ('ACTIVE' = :status and is_active = 1 )
					OR ('INACTIVE' = :status and is_active = 0 )
				)
				AND
				(
					COALESCE(:salesOrg) is null
					or
					sales_org IN (:salesOrg)
				)
				AND
				(
					COALESCE(:distChannels) is null
					or
					distribution_channel IN (:distChannels)
				)
				and partner_function = :partnerFunction
				and
				(
					(:channelType = 'ALL')
					OR ('BUY_AND_BILL' = :channelType and channel_type = 'BUY_AND_BILL' )
					OR ('SPECIALITY_PHARMA' = :channelType and channel_type = 'SPECIALITY_PHARMA' )
				)
			)""")
	Page<Account> findAllOrganizations(String query, String status, Pageable paging, List<Long> practiceIdList,
			String partnerFunction, List<String> salesOrg, List<String> distChannels, String channelType);

	@Query(nativeQuery = true, value = """
			select *
			FROM usermanagement.account ua
			JOIN usermanagement.user_account_mapping uam ON ua.id = uam.account_id
			WHERE
			(
					ua.tenant_id =:practiceId
				AND
					uam.user_id =:userId
			    AND
			    	ua.partner_function = :partnerFunction
			)""")
	List<Account> findAccountsByUser(@Param("userId") Long userId, @Param("practiceId") Long practiceId,
			String partnerFunction);

	@Modifying
	@Query(value = "DELETE FROM usermanagement.user_account_mapping WHERE account_id = :accountId", nativeQuery = true)
	void delinkUsersFromAccount(Long accountId);

	@Query(nativeQuery = true, value = """
			select *
			FROM usermanagement.account ua
			JOIN usermanagement.account_provider_mapping apm ON ua.id = apm.account_id
			WHERE
			(
				(
					:query IS NULL
				 	OR LOWER(ua.name_1) LIKE %:query%
				 	OR LOWER(ua.sap_partner) LIKE %:query%
				 	OR LOWER(ua.full_address) LIKE %:query%
				)
				AND
			     	ua.tenant_id =:practiceId
				AND
					apm.provider_id =:providerId
			    AND
			    	ua.channel_type = :channelType
			)""")
	Page<Account> findAccountsByProviderId(@Param("query") String query, @Param("practiceId") Long practiceId,
			@Param("providerId") Long providerId, @Param("channelType") String channelType, Pageable paging);

	@Query(nativeQuery = true, value = """
				SELECT ua.*
				FROM usermanagement.account ua
				LEFT JOIN usermanagement.account_provider_mapping apm
				  ON ua.id = apm.account_id AND apm.provider_id IN (:providerIds)
				WHERE
				  (
					:query IS NULL
				 	OR LOWER(ua.name_1) LIKE %:query%
				 	OR LOWER(ua.sap_partner) LIKE %:query%
				 	OR LOWER(ua.full_address) LIKE %:query%
				  )
				  AND ua.tenant_id = :practiceId
				  AND ua.channel_type = :channelType
				GROUP BY ua.id
				HAVING COUNT(DISTINCT apm.provider_id) < :providerCount
			""")
	Page<Account> findUnlinkedAccountsOfProviderIds(@Param("query") String query, @Param("practiceId") Long practiceId,
			@Param("providerIds") List<Long> providerIds, @Param("channelType") String channelType, Pageable paging,
			@Param("providerCount") int providerCount);

	@Query(nativeQuery = true, value = """
			SELECT DISTINCT
			    cp.sold_to_customer as accountNumber,
			    cp.sold_to_customer_name as accountName,
			    cp.sales_org as salesOrg,
			    cp.sap_material as sapMaterial,
			    a.house_number as houseNumber,
			    a.street as street,
			    a.street_2 as street2,
			    a.street_3 as street3,
			    a.city as city,
			    a.district as district,
			    a.state as state,
			    a.zip_code as zipCode,
			    a.country as country,
			    a.telephone as telephone,
			    a.email as email,
			    a.fax as fax,
			    a.is_active as isActive
			FROM catalog.catalog_price cp
			INNER JOIN usermanagement.account a
			    ON cp.sold_to_customer = a.sap_customer
			    AND a.channel_type = 'SPECIALITY_PHARMA'
			WHERE cp.channel_type = 'SPECIALITY_PHARMA'
			  AND cp.sap_material = :sapMaterial
			  AND cp.deleted = false
			  AND (
			      :query IS NULL
			      OR LOWER(cp.sold_to_customer_name) LIKE LOWER(CONCAT('%', :query, '%'))
			      OR LOWER(cp.sold_to_customer) LIKE LOWER(CONCAT('%', :query, '%'))
			  )
			  AND (
			      :salesOrgs IS NULL
			      OR cp.sales_org = :salesOrgs
			  )
			""")
	Page<SpecialtyPharmaAccountDto> findSpecialtyPharmaAccountsBySapMaterial(@Param("sapMaterial") String sapMaterial,
			@Param("query") String query, @Param("salesOrgs") String salesOrgs, Pageable pageable);

	@Modifying
	@Query(nativeQuery = true, value = """
			UPDATE catalog.catalog_price
			SET deleted = true, updated_at = CURRENT_TIMESTAMP
			WHERE sap_material = :sapMaterial
			AND sold_to_customer = :accountNumber
			AND channel_type = 'SPECIALITY_PHARMA'
			""")
	int unlinkSpecialtyPharmaAccount(@Param("sapMaterial") String sapMaterial,
			@Param("accountNumber") String accountNumber);

	List<Account> findByAccountNameInAndChannelTypeAndPracticeId(Set<String> accountNames, ChannelType channelType,
			int practiceId);

}