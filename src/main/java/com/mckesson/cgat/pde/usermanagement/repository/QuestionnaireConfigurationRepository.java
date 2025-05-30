package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
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
	@Query("DELETE FROM QuestionnaireConfiguration q WHERE q.questionnaireId.id = :questionnaireId AND q.entityId = :entityId AND q.configuredType = :configuredType AND q.channelType = :channelType")
	void deleteByQuestionnaireIdAndEntityIdAndConfiguredTypeAndChannelType(
			@Param("questionnaireId") Long questionnaireId, @Param("entityId") String entityId,
			@Param("configuredType") QuestionnaireConfigurationType configuredType, ChannelType channelType);

	@Query(nativeQuery = true,
			value = """
					    SELECT qc.*
					    FROM catalog.questionnaire_configuration qc
					    JOIN catalog.questionnaire q ON qc.questionnaire_id = q.id
					    WHERE q.is_active = 1
					      AND (
					            (qc.configured_type = 'Material' AND qc.entity_id = :materialEntityId
					                AND (
					                    (:isCrosswalked = false AND qc.channel_type = :channelType)
					                    OR (:isCrosswalked = true AND qc.channel_type = 'BUY_AND_BILL')
					                )
					            )
					         OR (qc.configured_type = 'Account' AND qc.entity_id = :soldToCustomer AND qc.channel_type = :channelType)
					         OR (qc.configured_type = 'Catalog' AND qc.entity_id = :catalogId AND qc.channel_type = :channelType)
					          )
					""")
	List<QuestionnaireConfiguration> getAllConfiguredQuestionnaires(String materialEntityId, String soldToCustomer,
			String catalogId, String channelType, boolean isCrosswalked);

	@Query(nativeQuery = true, value = """
			      SELECT qc.*
			      FROM catalog.questionnaire_configuration qc
			      JOIN catalog.questionnaire q ON qc.questionnaire_id = q.id
			      WHERE q.is_active = 1
				  AND qc.configured_type = :configuredType
			      AND qc.entity_id = :entityId
			   	  AND qc.channel_type = :channelType;
			""")
	List<QuestionnaireConfiguration> getAllConfiguredQuestionnairesForSpecificType(String entityId,
			String configuredType, String channelType);

	List<QuestionnaireConfiguration> findByEntityIdInAndConfiguredTypeAndChannelType(List<String> entityIds,
			QuestionnaireConfigurationType type, ChannelType channelType);

}
