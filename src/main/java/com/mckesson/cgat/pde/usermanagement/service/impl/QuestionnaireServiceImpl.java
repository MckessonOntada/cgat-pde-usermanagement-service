package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireRepository;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

	QuestionnaireRepository questionnaireRepository;

	public QuestionnaireServiceImpl(QuestionnaireRepository questionnaireRepository) {
		this.questionnaireRepository = questionnaireRepository;
	}

	public Questionnaire createQuestionnaire(QuestionnaireDTO questionnaireDTO) {
		log.info(" Inside method - configureQuestionnaire..");
		log.info("questionnaireDTO: {}", questionnaireDTO);

		Questionnaire questionnaire = Questionnaire.builder()
			.name(questionnaireDTO.getName())
			.questions(questionnaireDTO.getQuestions())
			.isActive(true)
			.build();
		return questionnaireRepository.save(questionnaire);
	}

	public List<Questionnaire> getAllQuestionnaires() {
		log.info(" Inside method - getAllQuestionnaires..");
		return questionnaireRepository.findAll();
	}

	public Questionnaire updateQuestionnaire(QuestionnaireDTO questionnaireDTO) throws ResourceNotFoundException {
		log.info(" Inside method - updateQuestionnaire..");
		log.info("questionnaireDTO: {}", questionnaireDTO);
		Questionnaire questionnaire = Questionnaire.builder()
			.name(questionnaireDTO.getName())
			.questions(questionnaireDTO.getQuestions())
			.build();
		if (questionnaireDTO.getId() != null) {
			questionnaireRepository.findById(questionnaireDTO.getId())
				.orElseThrow(
						() -> new ResourceNotFoundException("Invalid Questionnaire id: " + questionnaireDTO.getId()));
			questionnaire.setId(questionnaireDTO.getId());
			return questionnaireRepository.save(questionnaire);
		}
		throw new ResourceNotFoundException("Questionnaire not found with id: " + questionnaireDTO.getId());
	}

}
