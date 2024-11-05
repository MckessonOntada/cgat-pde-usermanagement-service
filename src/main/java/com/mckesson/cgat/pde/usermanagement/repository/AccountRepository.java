package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.Set;

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
			(:query IS NULL
			OR LOWER(a.name) LIKE %:query%
			AND
			practice_id =:pacticeId
			AND
			(
			(:status = 'ALL' and (is_active = 1 or is_active = 0))
			OR ('ACTIVE' = :status and is_active = 1 )
			OR ('INACTIVE' = :status and is_active = 0 )
			))""")
	Page<Account> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging,
			@Param("pacticeId") long pacticeId);

	Set<Account> findByPracticeId(Long practiceId);

}