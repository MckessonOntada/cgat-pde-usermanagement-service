package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Role2;

@Repository
public interface RoleRepository2 extends JpaRepository<Role2, String> {

}
