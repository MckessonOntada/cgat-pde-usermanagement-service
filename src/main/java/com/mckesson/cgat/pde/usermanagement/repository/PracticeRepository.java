package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Practice;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {

	@Query(value = """
			SELECT *
			FROM usermanagement.Practice
			WHERE
				(
				:query is null
				or LOWER(name) like %:query%
				or LOWER(city) like %:query%
				or LOWER(state) like %:query%
				and
					(
					(:status = 'ALL' and (is_active = 1 or is_active = 0))
					or ('ACTIVE' = :status and is_active = 1 )
					or ('INACTIVE' = :status and is_active = 0 )
					)
				)
			 """, nativeQuery = true)
	public Page<Practice> findAllByQueryText(@Param("query") String query, @Param("status") String status,
			Pageable paging);

}
