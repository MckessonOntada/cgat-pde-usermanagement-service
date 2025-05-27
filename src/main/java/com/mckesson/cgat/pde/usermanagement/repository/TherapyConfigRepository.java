package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.TherapyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TherapyConfigRepository extends JpaRepository<TherapyConfig, Long> {

	List<TherapyConfig> findByMaterialNumberInAndChannelType(@Param("materialNumbers") List<String> materialNumbers,
			ChannelType channelType);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
			    FROM Therapy m
			    LEFT JOIN TherapyConfig tc
			    ON m.materialNumber = tc.materialNumber
			    AND m.channelType = tc.channelType
			    WHERE m.ndcNumber = :ndcNumber
			    AND m.channelType = :channelType
			""")
	Optional<MaterialResponseDTO> findDetailedByNdcNumber(@Param("ndcNumber") String ndcNumber,
			ChannelType channelType);

	int removeTherapyConfigByMaterialNumberAndChannelType(String materialNumber, ChannelType channelType);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
			    FROM Therapy m
			    LEFT JOIN TherapyConfig tc
			    ON m.materialNumber = tc.materialNumber
			    WHERE m.id = :id
			""")
	Optional<MaterialResponseDTO> findTherapyDetailsById(@Param("id") Long id);

}
