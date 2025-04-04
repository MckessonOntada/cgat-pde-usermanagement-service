package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.ShippingLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShippingLocationRepository extends JpaRepository<ShippingLocation, Long> {

	@Query(nativeQuery = true, value = """
			    SELECT *
			    FROM usermanagement.shipping_location a
			    WHERE
			    (
			        (
			            :query IS NULL
			            OR LOWER(CAST(a.id AS VARCHAR)) LIKE %:query%
			            OR LOWER(a.name) LIKE %:query%
			            OR LOWER(a.address1) LIKE %:query%
			            OR LOWER(a.address2) LIKE %:query%
			            OR LOWER(a.city) LIKE %:query%
			            OR LOWER(a.state) LIKE %:query%
			            OR LOWER(CAST(a.zip AS VARCHAR)) LIKE %:query%
			        )
			        AND
			        a.practice_id = :practiceId
			        AND
			        a.account_id = :accountId
			        AND
			        a.is_active = 1
			    )
			""")
	Page<ShippingLocation> findAllByQueryText(@Param("query") String query, Pageable paging,
			@Param("practiceId") Long practiceId, @Param("accountId") Long accountId);

}
