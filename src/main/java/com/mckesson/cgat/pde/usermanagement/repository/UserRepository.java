package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query(nativeQuery = true, value = """
			SELECT *
			FROM usermanagement.user
			WHERE
			(
				(   :query IS NULL
					OR LOWER(city) LIKE %:query%
					OR LOWER(state) LIKE %:query%
					OR LOWER(first_name) LIKE %:query%
				)
				AND practice_id =:practiceId
				AND (
					(:status = 'ALL' AND (is_active = 1 OR is_active = 0))
					OR ('ACTIVE' = :status AND is_active = 1)
					OR ('INACTIVE' = :status AND is_active = 0)
					)
			 )
			   """)
	Page<User> findAllByQueryText(@Param("status") String status, @Param("query") String query, Pageable paging,
			@Param("practiceId") long practiceId);

}
