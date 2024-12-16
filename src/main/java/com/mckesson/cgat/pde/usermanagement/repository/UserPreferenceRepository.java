package com.mckesson.cgat.pde.usermanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.UserPreference;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

	@Query(value = """
			SELECT DISTINCT ac.id, ac.sap_customer as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2,
					 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, ac.sales_org, ac.company_code
			FROM usermanagement.account ac
			JOIN usermanagement.user_preference up ON ac.sap_customer = up.value
			WHERE
				up.user_id = :userId
			AND
				up.type = :type
			""",
			nativeQuery = true)
	public List<Object[]> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

}
