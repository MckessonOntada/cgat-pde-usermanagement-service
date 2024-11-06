package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionMappingRepository extends JpaRepository<Object, Long> {

    @Query(value = """
            SELECT permission
            FROM usermanagement.role_permissions_mapping
            WHERE role_id = :roleId
            """, nativeQuery = true)
    List<String> findPermissionsByRoleId(@Param("roleId") String roleId);

    // Additional custom queries can be added here as needed
}
