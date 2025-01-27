package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuestionnaireConfigurationRepository extends JpaRepository<QuestionnaireConfiguration, Long> {

	@Query("SELECT COUNT(q.id) FROM QuestionnaireConfiguration q WHERE q.questionnaireId.id = :questionnaireId AND q.configuredType = :configuredType")
	long countByQuestionnaireIdAndConfiguredType(@Param("questionnaireId") Long questionnaireId,
			@Param("configuredType") QuestionnaireConfigurationType configuredType);

	@Modifying
	@Transactional
	@Query("DELETE FROM QuestionnaireConfiguration q WHERE q.questionnaireId.id = :questionnaireId AND q.entityId = :entityId AND q.configuredType = :configuredType")
	void deleteByQuestionnaireIdAndEntityIdAndConfiguredType(@Param("questionnaireId") Long questionnaireId,
			@Param("entityId") String entityId, @Param("configuredType") QuestionnaireConfigurationType configuredType);

	@Query(nativeQuery = true, value = """
			    SELECT * FROM catalog.questionnaire_configuration
			    WHERE
			        (configured_type = 'Material' AND entity_id = :sapMaterial)
			    OR
			        (configured_type = 'Account' AND entity_id = :soldToCustomer)
			    OR
			        (configured_type = 'Catalog' AND entity_id = :catalogId)
			""")
	List<QuestionnaireConfiguration> getAllConfiguredQuestionnaires(String sapMaterial, String soldToCustomer,
			String catalogId);

}
