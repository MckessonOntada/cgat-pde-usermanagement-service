package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireRepository;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

	QuestionnaireRepository questionnaireRepository;

	public QuestionnaireServiceImpl(QuestionnaireRepository questionnaireRepository) {
		this.questionnaireRepository = questionnaireRepository;
	}

	@Override
	public Questionnaire createQuestionnaire(QuestionnaireDTO questionnaireDTO) {
		log.info(" Inside method - configureQuestionnaire..");
		log.info("questionnaireDTO: {}", questionnaireDTO);
		Questionnaire questionnaire = Questionnaire.builder().name(questionnaireDTO.getName())
				.configuredAt(questionnaireDTO.getConfiguredFor()).questions(questionnaireDTO.getQuestions()).build();
		return questionnaireRepository.save(questionnaire);
	}

	public List<Questionnaire> getAllQuestionnaires() {
		log.info(" Inside method - getAllQuestionnaires..");
		return questionnaireRepository.findAll();
	}

	public Questionnaire updateQuestionnaire(QuestionnaireDTO questionnaireDTO) {
		log.info(" Inside method - updateQuestionnaire..");
		log.info("questionnaireDTO: {}", questionnaireDTO);
		Questionnaire questionnaire = Questionnaire.builder().name(questionnaireDTO.getName())
				.configuredAt(questionnaireDTO.getConfiguredFor()).questions(questionnaireDTO.getQuestions()).build();
		if (questionnaireDTO.getId() != null)
			questionnaire.setId(questionnaireDTO.getId());
		return questionnaireRepository.save(questionnaire);
	}

	@Override
	public void deleteQuestionnaire(Long id) {
		log.info(" Inside method - deleteQuestionnaire..");
		questionnaireRepository.deleteById(id);
	}

}
