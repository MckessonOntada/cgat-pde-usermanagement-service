package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.SPTherapyConfigs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPTherapyConfigRepository extends JpaRepository<SPTherapyConfigs, Long> {

	List<SPTherapyConfigs> findBySpProductIdIn(List<String> spProductIds);

	void deleteBySpProductId(String spProductId);

	SPTherapyConfigs findBySpProductId(String spProductId);

}
