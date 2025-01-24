package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.DeLinkQuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Questionnaire", description = "Controller for Managing Questionnaires operations and data.")
@RequestMapping(path = "/questionnaires", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionnaireController {

	private final QuestionnaireService questionnaireService;

	public QuestionnaireController(QuestionnaireService questionnaireService) {
		this.questionnaireService = questionnaireService;
	}

	@PostMapping
	@Operation(summary = "Create Questionnaire")
	public ResponseEntity<Questionnaire> createQuestionnaire(@RequestBody Questionnaire questionnaire) {
		return new ResponseEntity<>(questionnaireService.createQuestionnaire(questionnaire), HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "Get All Questionnaire")
	public ResponseEntity<List<Questionnaire>> getAllQuestionnaires() {
		return new ResponseEntity<>(questionnaireService.getAllQuestionnaires(), HttpStatus.OK);
	}

	@PutMapping("/{questionnaireId}")
	@Operation(summary = "Update Questionnaire")
	public ResponseEntity<Questionnaire> updateQuestionnaire(@PathVariable Long questionnaireId,
			@RequestBody Questionnaire questionnaire) throws ResourceNotFoundException {
		return new ResponseEntity<>(questionnaireService.updateQuestionnaire(questionnaireId, questionnaire),
				HttpStatus.OK);
	}

	@PostMapping("/configure")
	@Operation(summary = "Configure Questionnaire with Master")
	public ResponseEntity<Void> configureQuestionnaireWithMaster(
			@Valid @RequestBody QuestionnaireConfigurationDTO questionnaireConfigurationDTO,
			@RequestParam QuestionnaireConfigurationType type) throws ResourceNotFoundException {
		questionnaireService.configureQuestionnaireWithMaster(type, questionnaireConfigurationDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/configure/deLink")
	@Operation(summary = "De Configure Questionnaire with Master")
	public ResponseEntity<Void> deLinkConfigureQuestionnaireWithMaster(
			@Valid @RequestBody DeLinkQuestionnaireConfigurationDTO dto,
			@RequestParam QuestionnaireConfigurationType type) throws ResourceNotFoundException {
		questionnaireService.deLinkConfigureQuestionnaireWithMaster(dto, type);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
