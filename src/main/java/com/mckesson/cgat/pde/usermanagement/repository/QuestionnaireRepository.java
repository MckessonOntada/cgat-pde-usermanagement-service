package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

	@Query(value = """
			SELECT *
			FROM catalog.questionnaire
			WHERE
				(
				(
				    :query is null
					or LOWER(template_name) like %:query%
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
			""", nativeQuery = true)
	public Page<Questionnaire> findAllByQueryText(@Param("query") String query, @Param("status") String status,
			Pageable paging);

}
