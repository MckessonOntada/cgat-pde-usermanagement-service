package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;

@Repository
public interface UserProvisioningRequestRepo extends JpaRepository<UserProvisioningRequest, Long> {

	@Query(nativeQuery = true, value = """
				SELECT *
				FROM usermanagement.user_provisioning_request
				WHERE
					(
						(   :query IS NULL
						    OR
							first_name LIKE %:query%
							OR 
							email LIKE %:query%
						 )
				AND practice_id =:practiceId
				AND status =:status

				 	 )
			""")
	Page<UserProvisioningRequest> findAllByQueryText(@Param("query") String query, @Param("status") String status,
			Pageable paging, @Param("practiceId") long practiceId);

}
