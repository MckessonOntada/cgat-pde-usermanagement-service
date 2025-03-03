package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.UserPreference;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

	@Query(value = """
			SELECT DISTINCT ac.id, ac.sap_partner as accountNumber, ac.name_1 as accountName, ac.house_number, ac.street, ac.street_2,
					 ac.street_3, ac.telephone, ac.city, ac.district, ac.state, ac.country, ac.zip_code, true as isDefault, ac.sales_org, ac.company_code
			FROM usermanagement.account ac
			JOIN usermanagement.user_preference up ON ac.sap_partner = up.value
			WHERE
				up.user_id = :userId
			AND
				up.type = :type
			AND
				ac.partner_function = :partnerFunction
			""",
			nativeQuery = true)
	public AccountsResponseDTO findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type,
			@Param("partnerFunction") String partnerFunction);

}
