package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

	boolean existsByPracticeIdAndDea(Long practiceId, String dea);

	boolean existsByPracticeIdAndNpi(Long practiceId, String npi);

	@Query(value = """
			SELECT DISTINCT p.*
			FROM usermanagement.provider p
			LEFT JOIN usermanagement.account_provider_mapping ap ON p.id = ap.provider_id
			WHERE
			    (
			        :query IS NULL OR
			        LOWER(p.full_name) LIKE CONCAT('%', :query, '%') OR
			        LOWER(p.full_address) LIKE CONCAT('%', :query, '%') OR
			        LOWER(p.dea) LIKE CONCAT('%', :query, '%') OR
			        p.npi LIKE CONCAT('%', :query, '%')
			    )
			    AND
			    (
			        (:status = 'ALL' AND (is_active = 1 OR is_active = 0)) OR
			        ('ACTIVE' = :status AND is_active = 1) OR
			        ('INACTIVE' = :status AND is_active = 0)
			    )
			    AND
			    (
			        p.practice_id = :practiceId
			        AND
			        (
			            :accountId IS NULL OR ap.account_id = :accountId
			        )
			    )""", nativeQuery = true)
	Page<Provider> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging,
			@Param("accountId") Long accountId, @Param("practiceId") Long practiceId);

	Provider findByPracticeIdAndNpi(Long practiceId, String npi);

	@Query(value = """
			SELECT p.*
			     FROM usermanagement.provider p
			     WHERE p.id NOT IN (
			         SELECT ap.provider_id
			         FROM usermanagement.account_provider_mapping ap
			         WHERE ap.account_id = :accountId
			     )
			     AND p.is_active = 1
			     AND p.practice_id = :practiceId
			     AND (
			     	 :query IS NULL OR
			         LOWER(p.full_name) LIKE CONCAT('%', :query, '%') OR
			         LOWER(p.full_address) LIKE CONCAT('%', :query, '%') OR
			         LOWER(p.dea) LIKE CONCAT('%', :query, '%') OR
			         p.npi LIKE CONCAT('%', :query, '%')
			     )
			""", nativeQuery = true)
	Page<Provider> findNonLinkedProvidersByAccountId(@Param("query") String query, @Param("accountId") Long accountId,
			Pageable paging, @Param("practiceId") Long practiceId);

	boolean existsByEmail(String email);

}
