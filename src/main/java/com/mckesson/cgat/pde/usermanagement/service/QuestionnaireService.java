package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;

@Service
public interface QuestionnaireService {

	Questionnaire createQuestionnaire(QuestionnaireDTO questionnaire);

	List<Questionnaire> getAllQuestionnaires();

	Questionnaire updateQuestionnaire(QuestionnaireDTO questionnaire);

	void deleteQuestionnaire(Long id);
}
