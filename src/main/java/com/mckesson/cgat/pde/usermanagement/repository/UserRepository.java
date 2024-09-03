package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>  {

   @Query("SELECT distinct u FROM User u left outer join  u.roles  r WHERE  (:query is null or LOWER(u.displayName)  like %:query% or LOWER(u.email) like %:query%)  and (r.name in :roles) and (:status = 'ALL' or (('ACTIVE' = :status and u.isActive = true ) or ('INACTIVE' = :status and u.isActive = false )))")
   Page<User> findAllByQueryTextAndRoles(@Param("query") String query, @Param("roles") List<String> roles, @Param("status") String status, Pageable paging);

   @Query("SELECT distinct u FROM User u  WHERE  (:query is null or LOWER(u.displayName)  like %:query% or LOWER(u.email) like %:query%) and (u.roles is empty) and (:status = 'ALL' or (('ACTIVE' = :status and u.isActive = true) or ('INACTIVE' = :status and u.isActive = false )))")
   Page<User> findAllByQueryTextRoleUnAssigned(@Param("query") String query, @Param("status") String status, Pageable paging);

   @Query("SELECT distinct u FROM User u  WHERE  (:query is null or LOWER(u.displayName)  like %:query% or LOWER(u.email) like %:query%) and (:status = 'ALL' or (('ACTIVE' = :status and u.isActive = true ) or ('INACTIVE' = :status and u.isActive = false )))")
   Page<User> findAllByQueryText(@Param("query") String query, @Param("status") String status, Pageable paging);

   User findByEmail(String email);
   
   @Query("Select u,r FROM User u left outer join  u.roles r WHERE u.id = :id ")
   Optional<User> findById(@Param("id") UUID ID);

   List<User> findUsersByRoles_NameAndIdIn(String roleName, List<UUID> userIds);
   @Modifying
   @Transactional
   @Query("UPDATE User u SET u.isSyncWithChartAbstraction = :isSynced WHERE u.id IN :userIds")
   void updateIsSyncWithChartAbstraction(@Param("userIds") List<UUID> userIds, @Param("isSynced") Integer isSynced);

   @Query("SELECT u FROM User u WHERE u.isSyncWithChartAbstraction = :isSynced")
   Optional<List<User>> findUsersByIsSyncedWithChartAbstraction(@Param("isSynced") Integer isSynced);
}
