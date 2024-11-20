package com.mckesson.cgat.pde.usermanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningStaus;

@Repository
public interface UserProvisioningRequestRepo extends JpaRepository<UserProvisioningRequest, Long> {

	@Query(nativeQuery = true, value = """
			SELECT *
				FROM usermanagement.user_provisioning_request
				WHERE
					(
						(   :query IS NULL
						    OR LOWER(first_name) LIKE %:query%
							OR LOWER(middle_name) LIKE %:query%
							OR LOWER(last_name) LIKE %:query%
							OR LOWER(email) LIKE %:query%
						 )

					AND
						(practice_id =:practiceId)
					AND
						(
						 	(:status is null AND status = 'PENDING')
							or status =:status
						)

					AND
						(
				 			(cast(:from as date) is null and cast(:to as date) is null)
					 		OR
					 		(created_at between :from and :to)
					 	)
					)

			""")

	Page<UserProvisioningRequest> findAllByQueryText(@Param("query") String query, @Param("status") String status,
			Pageable paging, @Param("practiceId") long practiceId, @Param("from") LocalDate from,
			@Param("to") LocalDate to);

	List<UserProvisioningRequest> findAllByPracticeId(Long id);

}
