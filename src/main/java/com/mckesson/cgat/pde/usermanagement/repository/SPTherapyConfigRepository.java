package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.SPTherapyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPTherapyConfigRepository extends JpaRepository<SPTherapyConfig, Long> {

	List<SPTherapyConfig> findBySpProductIdIn(List<String> spProductIds);

	void deleteBySpProductId(String spProductId);

}
