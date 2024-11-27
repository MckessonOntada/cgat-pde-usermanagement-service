package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
				)
				AND
				tenant_id =:practiceId
				AND
				(
					(:status = 'ALL' and (is_active = 1 or is_active = 0))
					OR ('ACTIVE' = :status and is_active = 1 )
					OR ('INACTIVE' = :status and is_active = 0 )
				)
				and partner_function = :partnerFunction
			)""")
	Page<Account> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging,
			@Param("practiceId") Long practiceId, String partnerFunction);

	Set<Account> findByPracticeId(Long practiceId);

	@Query(nativeQuery = true, value = """
			select *
			FROM usermanagement.account ua
			JOIN usermanagement.user_account_mapping uam ON ua.id = uam.account_id
			WHERE
			(
				tenant_id =:practiceId
				AND
				user_id =:userId
				AND
				(
					(:status = 'ALL' and (is_active = 1 or is_active = 0))
					OR ('ACTIVE' = :status and is_active = 1 )
					OR ('INACTIVE' = :status and is_active = 0 )
				)
			    and partner_function = :partnerFunction
			)""")
	Page<Account> findAccountsByUser(@Param("userId") Long userId, @Param("status") String status, Pageable pageable,
			@Param("practiceId") Long practiceId, String partnerFunction);

}