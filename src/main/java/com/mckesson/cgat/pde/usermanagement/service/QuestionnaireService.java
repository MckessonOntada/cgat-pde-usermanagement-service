package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuestionnaireService {

	Questionnaire createQuestionnaire(Questionnaire questionnaire);

	List<Questionnaire> getAllQuestionnaires();

	Questionnaire updateQuestionnaire(Long questionnaireId, Questionnaire questionnaire)
			throws ResourceNotFoundException;

	void configureQuestionnaire(QuestionnaireConfigurationType type,
			QuestionnaireConfigurationDTO questionnaireConfigurationDTO);

}
