package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;
import java.util.Optional;

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
					(
					:query is null
					or LOWER(name) like %:query%
					)
				and
					(
					(:status = 'ALL' and (is_active = 1 or is_active = 0))
					or ('ACTIVE' = :status and is_active = 1 )
					or ('INACTIVE' = :status and is_active = 0 )
					)
				and
					(
						COALESCE(:location) is null
						or
						state IN (:location)
					)
				)
			""", nativeQuery = true)
	public Page<Practice> findAllByQueryText(@Param("query") String query, @Param("status") String status,
			Pageable paging, @Param("location") List<String> location);

	@Query("SELECT p.isActive FROM Practice p WHERE p.id = :practiceId")
	Optional<Boolean> findIsActiveById(Long practiceId);

	Optional<String> findNameById(Long aLong);

}
