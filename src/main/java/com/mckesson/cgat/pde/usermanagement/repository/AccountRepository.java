package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Set;

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
				 	OR LOWER(a.sap_customer) LIKE %:query%
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
			)""")
	Page<Account> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging,
			@Param("practiceId") Long practiceId, String partnerFunction, @Param("salesOrg") List<String> salesOrg,
			@Param("distChannels") List<String> distChannels);

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
				 	OR LOWER(ua.sap_customer) LIKE %:query%
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
			)""")
	Page<Account> findAccountsByUser(@Param("userId") Long userId, @Param("query") String query,
			@Param("status") String status, Pageable pageable, @Param("practiceId") Long practiceId,
			String partnerFunction, @Param("salesOrg") List<String> salesOrg,
			@Param("distChannels") List<String> distChannels);

	@Query(nativeQuery = true, value = """
			select *
			FROM usermanagement.account ua
			WHERE
			(
					ua.sap_partner = :accountNumber
			    AND
			    	ua.partner_function = :partnerFunction
			)""")
	Account findByAccountNumber(String accountNumber, String partnerFunction);

	@Query(value = """
			SELECT DISTINCT ac1.id, ac1.sap_partner as AccountNumber, ac1.name_1 as AccountName, ac1.house_number, ac1.street, ac1.street_2, ac1.street_3,
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

	List<Account> findAllByAccountNumberIn(List<String> accountNumberList);

}