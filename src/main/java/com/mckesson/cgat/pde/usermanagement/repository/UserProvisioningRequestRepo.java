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
						(
						:query IS NULL
				        OR LOWER(email) LIKE CONCAT('%', :query, '%')
				        OR LOWER(full_name) LIKE CONCAT('%', :query, '%')
						)
					AND
						practice_id =:practiceId
					AND
						(
						 	cast(:requestStatus as varchar) IS NULL
							OR status =:requestStatus
						)

					AND
						(
				 			(cast(:from as date) is null and cast(:to as date) is null)
					 		OR
					 		(created_at between :from and :to)
					 	)
					)

			""")

	Page<UserProvisioningRequest> findAllByQueryText(@Param("query") String query,
			@Param("requestStatus") String requestStatus, Pageable paging, @Param("practiceId") long practiceId,
			@Param("from") LocalDate from, @Param("to") LocalDate to);

	List<UserProvisioningRequest> findAllByPracticeId(Long id);

}
