package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.DeLinkQuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuestionnaireService {

	Questionnaire createQuestionnaire(Questionnaire questionnaire);

	Questionnaire updateQuestionnaire(Long questionnaireId, Questionnaire questionnaire)
			throws ResourceNotFoundException;

	void configureQuestionnaire(QuestionnaireConfigurationType type,
			QuestionnaireConfigurationDTO questionnaireConfigurationDTO)
			throws ResourceNotFoundException, BadRequestException;

	void deLinkConfiguredQuestionnaire(DeLinkQuestionnaireConfigurationDTO dto, QuestionnaireConfigurationType type)
			throws ResourceNotFoundException;

	List<Questionnaire> getAllConfiguredQuestionnaires(String sapMaterial, String soldToCustomer, String shipToCustomer,
			String variant, ChannelType channelType, String materialOrNdcNumber);

	Page<Questionnaire> findAll(Pageable paging, String query, List<String> status);

	void deactivateQuestionnaires(List<Long> questionnaireId) throws ResourceNotFoundException;

	Questionnaire fetchQuestionnaireById(Long questionnaireId) throws ResourceNotFoundException;

	List<Questionnaire> getAllConfiguredQuestionnairesForSpecificType(QuestionnaireConfigurationType type,
			String sapMaterial, String soldToCustomer, String shipToCustomer, String variant, ChannelType channelType,
			String materialOrNdcNumber);

}
