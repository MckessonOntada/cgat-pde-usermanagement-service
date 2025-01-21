package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Questionnaire", description = "Controller for Managing Questionnaires operations and data.")
@RequestMapping(path = "/questionnaires", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionnaireController {

	private QuestionnaireService questionnaireService;

	public QuestionnaireController(QuestionnaireService questionnaireService) {
		this.questionnaireService = questionnaireService;
	}

	@PostMapping
	@Operation(summary = "Create Questionnaire")
	public ResponseEntity<Questionnaire> createQuestionnaire(@RequestBody QuestionnaireDTO questionnaire) {
		return new ResponseEntity<>(questionnaireService.createQuestionnaire(questionnaire), HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "Get All Questionnaire")
	public ResponseEntity<List<Questionnaire>> getAllQuestionnaires() {
		return new ResponseEntity<>(questionnaireService.getAllQuestionnaires(), HttpStatus.OK);
	}

	@PutMapping
	@Operation(summary = "Update Questionnaire")
	public ResponseEntity<Questionnaire> updateQuestionnaire(@RequestBody QuestionnaireDTO questionnaire) {
		return new ResponseEntity<>(questionnaireService.updateQuestionnaire(questionnaire), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete Questionnaire")
	public ResponseEntity<Questionnaire> deleteQuestionnaire(@RequestBody Long id) {
		questionnaireService.deleteQuestionnaire(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
