package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.model.TherapyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TherapyConfigRepository extends JpaRepository<TherapyConfig, String> {

}
