package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.Provider;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
			         SELECT * FROM usermanagement.Provider
			WHERE (
			        (:query IS NULL OR LOWER(first_name) LIKE %:query% OR LOWER(address1) LIKE %:query% OR dea LIKE %:query% OR CAST(npi AS TEXT) LIKE %:query%)
			         AND (
			             (:status = 'ALL' AND (is_active = 1 OR is_active = 0))
			             OR ('ACTIVE' = :status AND is_active = 1)
			             OR ('INACTIVE' = :status AND is_active = 0)
			         ))""",
			nativeQuery = true)
	Page<Provider> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging);

}
