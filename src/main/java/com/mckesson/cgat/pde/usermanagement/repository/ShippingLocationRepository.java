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
			            OR LOWER(CAST(a.shipping_account_number AS VARCHAR)) LIKE %:query%
			            OR LOWER(a.shipping_location_name) LIKE %:query%
			            OR LOWER(a.address_1) LIKE %:query%  -- Fixed column name
			            OR LOWER(a.address_2) LIKE %:query%  -- Fixed column name
			            OR LOWER(a.city) LIKE %:query%
			            OR LOWER(a.state) LIKE %:query%
			            OR LOWER(CAST(a.zip AS VARCHAR)) LIKE %:query%  -- Fixed column name (zip instead of zip_code)
			        )
			        AND
			        (
			            (CAST(:practiceId AS varchar) IS NULL AND a.tenant_id IS NULL)
			            OR (a.tenant_id = :practiceId)
			        )
			        AND
			        (
			            (CAST(:accountId AS varchar) IS NULL AND a.account_id IS NULL)
			            OR (a.account_id = :accountId)
			        )
			        AND a.is_active = 1
			    )
			""")
	Page<ShippingLocation> findAllByQueryText(@Param("query") String query, Pageable paging,
			@Param("practiceId") Long practiceId, @Param("accountId") Long accountId);

}
