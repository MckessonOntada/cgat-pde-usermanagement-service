package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

}
