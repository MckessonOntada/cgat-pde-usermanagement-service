package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = """
				SELECT *
				FROM usermanagement.user
				WHERE
					(
					:query is null
					OR LOWER(email) LIKE %:query%
					OR LOWER(first_name) LIKE %:query%
					OR LOWER(middle_name) LIKE %:query%
					OR LOWER(last_name) LIKE %:query%
					and
					practice_id =:practiceId
					and
						(
							(:status = 'ALL' and (is_active = 1 or is_active = 0))
							or ('ACTIVE' = :status and is_active = 1 )
							or ('INACTIVE' = :status and is_active = 0 )
						)
					)
			""", nativeQuery = true)
	Page<User> findAllByQueryText(@Param("status") String status, @Param("query") String query, Pageable paging,
			@Param("practiceId") long practiceId);

	@Query(value = """
				SELECT *
				FROM usermanagement.user
				WHERE
					(
					:query is null
					OR LOWER(email) LIKE %:query%
					OR LOWER(first_name) LIKE %:query%
					OR LOWER(middle_name) LIKE %:query%
					OR LOWER(last_name) LIKE %:query%
					and
					practice_id =:practiceId
					and
						created_at >= :from and created_at < :to
					and
						(
							(:status = 'ALL' and (is_active = 1 or is_active = 0))
							or ('ACTIVE' = :status and is_active = 1 )
							or ('INACTIVE' = :status and is_active = 0 )
						)
					)
			""", nativeQuery = true)
	Page<User> findAllByQueryText(@Param("status") String status, @Param("query") String query, Pageable paging,
			@Param("practiceId") long practiceId, @Param("from") Date from, @Param("to") Date to);

	List<User> findByCreatedAt(Date createdAt);

}
