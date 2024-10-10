package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

	@Query(nativeQuery = true, value = "SELECT * FROM usermanagement.users WHERE"
			+ " first_name ILIKE '%:query%'AND middle_name ILIKE '%:query%' AND last_name ILIKE '%:query%' AND city ILIKE '%:query%' AND "
			+ "email ILIKE '%:query%'AND state ILIKE '%:query%' AND address1 ILIKE '%:query%' AND is_active = 0 OR is_active = 1")
	Page<Users> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging);

	Users findByEmail(String email);

}
