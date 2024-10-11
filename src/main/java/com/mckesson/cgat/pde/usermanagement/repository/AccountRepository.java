package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mckesson.cgat.pde.usermanagement.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

	@Query(nativeQuery = true, value = "SELECT distinct a FROM Account a  WHERE  "
			+ "(:query is null or LOWER(a.name)  like %:query% or LOWER(a.dcid) like %:query% or"
			+ " LOWER(a.dcname) like %:query% and (:status = 'ALL' "
			+ "or (('ACTIVE' = :status and a.isActive = true ) or ('isActive' = :status and a.isActive = false )))")
	Page<Account> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging);

}