package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
			ac1.telephone, ac1.city, ac1.district, ac1.state, ac1.country, ac1.zip_code, false as isDefault, ac1.sales_org, ac1.company_code
			FROM usermanagement.account ac1
			JOIN usermanagement.account ac2 ON ac1.sap_customer = ac2.sap_customer
			JOIN usermanagement.user_account_mapping uam ON ac2.id = uam.account_id
			WHERE(
					(
						:query IS NULL
					 	OR LOWER(ac1.name_1) LIKE %:query%
					 	OR LOWER(ac1.sap_customer) LIKE %:query%
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

}