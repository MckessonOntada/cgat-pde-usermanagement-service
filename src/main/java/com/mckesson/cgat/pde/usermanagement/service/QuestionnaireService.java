package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface QuestionnaireService {

	Questionnaire createQuestionnaire(QuestionnaireDTO questionnaire);

	List<Questionnaire> getAllQuestionnaires();

	Questionnaire updateQuestionnaire(QuestionnaireDTO questionnaire) throws ResourceNotFoundException;

	void deleteQuestionnaire(Long id);

}
