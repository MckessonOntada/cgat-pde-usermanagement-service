package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.model.TherapyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TherapyConfigRepository extends JpaRepository<TherapyConfig, Long> {

	List<TherapyConfig> findByNdcNumberInAndChannelType(@Param("ndcNumbers") List<String> ndcNumbers,
			ChannelType channelType);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
			    FROM Therapy m
			    LEFT JOIN TherapyConfig tc
			    ON m.ndcNumber = tc.ndcNumber
			    AND m.channelType = tc.channelType
			    WHERE m.tradeName = :tradeName
			    AND m.channelType = :channelType
			    AND m.itemStatus = 'A'
			""")
	List<MaterialResponseDTO> findDetailesByTradeName(@Param("tradeName") String tradeName, ChannelType channelType);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
			    FROM Therapy m
			    LEFT JOIN TherapyConfig tc
			    ON m.ndcNumber = tc.ndcNumber
			    AND m.channelType = tc.channelType
			    WHERE m.materialNumber = :materialNumber
			    AND m.channelType = :channelType
			""")
	Optional<MaterialResponseDTO> findDetailesByMaterialNumber(@Param("materialNumber") String materialNumber,
			ChannelType channelType);

	int removeTherapyConfigByNdcNumberAndChannelType(String ndcNumber, ChannelType channelType);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
			    FROM Therapy m
			    LEFT JOIN TherapyConfig tc
			    ON m.ndcNumber = tc.ndcNumber
			    WHERE m.ndcNumber = :ndcNumber
			    and m.channelType = :channelType
			    and m.itemStatus = 'A'
			""")
	Optional<MaterialResponseDTO> findTherapyDetailsByNdc(@Param("ndcNumber") String ndcNumber,
			@Param("channelType") ChannelType channelType);

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO(m,tc)
			    FROM Therapy m
			    LEFT JOIN TherapyConfig tc
			    ON m.ndcNumber = tc.ndcNumber
			    WHERE m.id = :id
			""")
	Optional<MaterialResponseDTO> findTherapyDetailsById(@Param("id") Long id);

	@Query("""
			    SELECT m
			    FROM Therapy m
			    WHERE m.ndcNumber = :ndcNumber
			    AND m.channelType = :channelType
			""")
	List<Therapy> findDetailesByNdcNumber(@Param("ndcNumber") String ndcNumber, ChannelType channelType);

}
