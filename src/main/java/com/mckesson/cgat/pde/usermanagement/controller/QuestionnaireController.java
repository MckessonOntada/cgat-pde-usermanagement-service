package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.DeLinkQuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.SORT_ORDER_ASC;

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
	@Operation(summary = "Fetch all Questionnaire based on filter provided with pagination capabilities.")
	Page<Questionnaire> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "template_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return questionnaireService.findAll(paging, query, status);

	}

	@PatchMapping("/{questionnaireId}")
	@Operation(summary = "Update Questionnaire")
	public ResponseEntity<Questionnaire> updateQuestionnaire(@PathVariable Long questionnaireId,
			@RequestBody Questionnaire questionnaire) throws ResourceNotFoundException {
		return new ResponseEntity<>(questionnaireService.updateQuestionnaire(questionnaireId, questionnaire),
				HttpStatus.OK);
	}

	@PostMapping("/configure")
	@Operation(summary = "Configure Questionnaire")
	public ResponseEntity<Void> configureQuestionnaire(
			@Valid @RequestBody QuestionnaireConfigurationDTO questionnaireConfigurationDTO,
			@RequestParam QuestionnaireConfigurationType type) throws ResourceNotFoundException, BadRequestException {
		questionnaireService.configureQuestionnaire(type, questionnaireConfigurationDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/configure/deLink")
	@Operation(summary = "De Configure Questionnaire")
	public ResponseEntity<Void> deLinkConfigureQuestionnaire(
			@Valid @RequestBody DeLinkQuestionnaireConfigurationDTO dto,
			@RequestParam QuestionnaireConfigurationType type) throws ResourceNotFoundException {
		questionnaireService.deLinkConfiguredQuestionnaire(dto, type);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/configured-questionnaire")
	@Operation(summary = "Fetch all configured questionnaire for the data")
	public ResponseEntity<List<Questionnaire>> getAllConfiguredQuestionnaires(@RequestParam String soldToCustomer,
			@RequestParam String shipToCustomer, @RequestParam String sapMaterial,
			@RequestParam(required = false) String variant, @RequestParam String materialOrNdcNumber,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) {
		return new ResponseEntity<>(questionnaireService.getAllConfiguredQuestionnaires(sapMaterial, soldToCustomer,
				shipToCustomer, variant, channelType, materialOrNdcNumber), HttpStatus.OK);
	}

	@PatchMapping("/deactivate")
	@Operation(summary = "Deactivate multiple questionnaires by IDs")
	public ResponseEntity<Void> deactivateQuestionnaires(@RequestBody List<Long> questionnaireIds)
			throws ResourceNotFoundException {
		questionnaireService.deactivateQuestionnaires(questionnaireIds);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{questionnaireId}")
	@Operation(summary = "Get questionnaire by Id")
	public ResponseEntity<Questionnaire> getQuestionnaireById(@PathVariable Long questionnaireId)
			throws ResourceNotFoundException {
		return new ResponseEntity<>(questionnaireService.fetchQuestionnaireById(questionnaireId), HttpStatus.OK);
	}

}
