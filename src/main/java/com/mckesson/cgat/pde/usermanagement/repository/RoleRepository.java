package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	// @Query(nativeQuery = true, value = "select * from usermanagement.role where
	// practice_id =:practiceId")
	List<Role> findByPracticeId(Practice practiceId);

	@Query(value = """
			SELECT *
			FROM usermanagement.role
			WHERE
				(
				:query is null
				or LOWER(name) like %:query%
				and
				practice_id =:practiceId
				and
					(
					(:status = 'ALL' and (is_active = 1 or is_active = 0))
					or ('ACTIVE' = :status and is_active = 1 )
					or ('INACTIVE' = :status and is_active = 0 )
					)
				)
			""", nativeQuery = true)
	public Page<Role> findAllByQueryText(@Param("query") String query, @Param("status") String status,
			@Param("practiceId") Long practiceId, Pageable paging);

}
