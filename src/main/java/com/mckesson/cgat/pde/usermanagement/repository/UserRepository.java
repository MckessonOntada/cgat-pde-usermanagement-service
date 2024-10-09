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

//	@Query(nativeQuery = true, value = "SELECT distinct u FROM USERS u "
//			+ " WHERE  (:query is null or LOWER(u.name)  like %:query% or LOWER(u.email)"
//			+ " like %:query% or LOWER(u.city) like %:query% or LOWER(u.state) like %:query% "
//			+ "or LOWER(u.firstname) like %:query% or LOWER(u.middlename) like %:query% or"
//			+ " LOWER(u.lastname) like %:query% or LOWER(u.address1) like %:query% or"
//			+ " LOWER(u.address2) like %:query%) and (:status = 'ALL' or (('ACTIVE' = :status"
//			+ " and u.isActive = true ) or ('isActive' = :status and u.isActive = false )))")
//	Page<Users> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging);

	Users findByEmail(String email);

//	Optional<List<User>> findUsersByIsSyncedWithChartAbstraction(int i);

//	void updateIsSyncWithChartAbstraction(List<UUID> collect, int i);

}
