package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Permission2;

@Repository
public interface PermissionRepository extends JpaRepository<Permission2, UUID> {

}
