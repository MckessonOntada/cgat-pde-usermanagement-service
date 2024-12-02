package com.mckesson.cgat.pde.usermanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

	List<Role> findByPracticeId(Practice practiceId);

	@Query(value = """
			SELECT *
			FROM usermanagement.role
			WHERE
				(
				(
				    :query is null
					or LOWER(name) like %:query%
				)
				and
				practice_id =:practiceId
				and
					(
					  (
					   (cast(:from as date) is null and cast(:to as date) is null)
					    or
					   (updated_at between :from and :to)
					  )
					  and
					  (
							(:status = 'ALL' and (is_active = 1 or is_active = 0))
							or
							('ACTIVE' = :status and is_active = 1 )
							or
							('INACTIVE' = :status and is_active = 0 )
					  )
				     )
				  )

			""", nativeQuery = true)
	public Page<Role> findAllByQueryText(@Param("query") String query, @Param("status") String status,
			@Param("practiceId") Long practiceId, Pageable paging, @Param("from") LocalDate from,
			@Param("to") LocalDate to);

	Optional<Role> findByIdAndIsActiveTrue(Long roleId);

	Role findByName(String name);

}
