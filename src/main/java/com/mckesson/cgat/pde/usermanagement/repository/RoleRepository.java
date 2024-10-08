package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
