package com.mckesson.cgat.pde.usermanagement.repository;

import java.time.LocalDate;
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
					(
					:query is null
					or LOWER(first_name) like %:query%
					or LOWER(middle_name) like %:query%
					or LOWER(last_name) like %:query%
					or LOWER(email) like %:query%
					)
				and
				practice_id =:practiceId
				and
					(
					  (
					   (cast(:from as date) is null and cast(:to as date) is null)
					    or
					   (created_at between :from and :to)
					  )
					  and
					  (
							(:status = 'ALL' and (is_active = 1 or is_active = 0))
							or
							('ACTIVE' = :status and is_active = 1 )
							or
							('INACTIVE' = :status and is_active = 0 )
					  )
				     )
				  )

			""", nativeQuery = true)
	Page<User> findAllByQueryText(@Param("status") String status, @Param("query") String query, Pageable paging,
			@Param("practiceId") long practiceId, @Param("from") LocalDate from, @Param("to") LocalDate to);

	List<User> findByCreatedAt(Date createdAt);

	User findByContactEmail(String email);

}
