package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	@Query(nativeQuery = true, value = "select * from usermanagement.role where practice_id =:practiceId")
	List<Role> findByPracticeId(Long practiceId);

	@Query(nativeQuery = true, value = "SELECT * "
			+ "FROM usermanagement.role "
			+ "Where (:query is null or LOWER(name)  like %:query% or LOWER(description) like %:query% )")
	public Page<Role> findAllByQueryText(@Param("query") String query, Pageable paging);

}
