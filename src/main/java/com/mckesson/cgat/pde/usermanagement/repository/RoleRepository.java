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
	List<Role> findByPracticeId(String practiceId);

	@Query(nativeQuery = true, value = "SELECT distinct u" + " FROM role u  "
			+ "Where (:query is null or LOWER(u.name)  like %:query% or LOWER(u.description) like %:query% ) and (:status = 'ALL' or (('ACTIVE' = :status and u.isActive = true ) or ('isActive' = :status and u.isActive = false )))")
	public Page<Role> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging);

}
