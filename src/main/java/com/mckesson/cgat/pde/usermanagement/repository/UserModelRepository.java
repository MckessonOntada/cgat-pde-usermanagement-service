package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.UserModel;

@Repository
public interface UserModelRepository extends JpaRepository<UserModel, String> {

	@Query(nativeQuery = true, value = "SELECT distinct um FROM UserModel um  WHERE  (:query is null or LOWER(um.name)  like %:query% or LOWER(um.email) like %:query% or LOWER(um.city) like %:query% or LOWER(um.state) like %:query% or LOWER(um.firstname) like %:query% or LOWER(um.middlename) like %:query% or LOWER(um.lastname) like %:query% or LOWER(um.address1) like %:query% or LOWER(um.address2) like %:query%) and (:status = 'ALL' or (('ACTIVE' = :status and um.isActive = true ) or ('isActive' = :status and um.isActive = false )))")
	Page<UserModel> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging);


}
