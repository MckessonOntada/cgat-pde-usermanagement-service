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
			            OR CAST(a.id AS VARCHAR) ILIKE %:query%
			            OR a.name ILIKE %:query%
			            OR a.address_combined ILIKE %:query%
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
