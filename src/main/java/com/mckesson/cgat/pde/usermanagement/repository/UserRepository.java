package com.mckesson.cgat.pde.usermanagement.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
						:query IS NULL
				        OR LOWER(email) LIKE CONCAT('%', :query, '%')
				        OR LOWER(full_name) LIKE CONCAT('%', :query, '%')
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
					  and
					  (
						COALESCE(:roleIds) is null
						or
						role_id IN (:roleIds)
					  )
				     )
				  )

			""", nativeQuery = true)
	Page<User> findAllByQueryText(@Param("status") String status, @Param("query") String query, Pageable paging,
			@Param("practiceId") long practiceId, @Param("from") LocalDate from, @Param("to") LocalDate to,
			@Param("roleIds") List<Long> roleIds);

	List<User> findByCreatedAt(Date createdAt);

	@Modifying
	@Query(value = "Delete from usermanagement.user_account_mapping where user_id =:userId and account_id =:accountId",
			nativeQuery = true)
	void delinkUserFromAccount(@Param("userId") Long userId, @Param("accountId") Long accountId);

	@Modifying
	@Query(value = "Delete from usermanagement.user_preference where user_id =:userId and type =:preferenceType",
			nativeQuery = true)
	void resetDefaultPayToAccount(@Param("userId") Long userId, @Param("preferenceType") String preferenceType);

	Optional<User> findByContactEmailAndIsActiveTrue(String email);

	Optional<User> findByContactEmailAndIsActiveFalse(String email);

	Optional<User> findByContactEmail(String email);

	@Query(value = """
			SELECT upm.permission as permission, STRING_AGG(DISTINCT r.name, ',') as role_names
			FROM usermanagement.user u
			JOIN usermanagement.user_permission_mapping upm ON u.id = upm.user_id
			JOIN usermanagement.role r ON u.role_id = r.id
			WHERE u.practice_id = :practiceId
			  AND upm.permission IN (:permissions)
			  AND u.is_active = 1
			GROUP BY upm.permission
			""", nativeQuery = true)
	List<Object[]> findPermissionWithAggregatedRoleNamesByAccountAndPermissions(@Param("practiceId") Long practiceId,
			@Param("permissions") List<String> permissions);

}
