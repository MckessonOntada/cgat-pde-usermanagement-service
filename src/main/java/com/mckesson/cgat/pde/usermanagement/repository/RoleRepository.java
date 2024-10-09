package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	@Query(nativeQuery = true, value = "select * from usermanagement.role where practice_id =:practiceId")
	List<Role> findByPracticeId(String practiceId);

}
