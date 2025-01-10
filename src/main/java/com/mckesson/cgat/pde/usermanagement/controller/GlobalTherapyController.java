package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Therapy", description = "Controller for Managing Therapy-catalogs operations and data.")
@RequestMapping(path = "/therapy", consumes = "application/json",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Validated
public class GlobalTherapyController {
    private final TherapyService therapyService;

    public GlobalTherapyController(TherapyService therapyService) {
        this.therapyService = therapyService;
    }

    @GetMapping("/{materialNumber}")
    @Operation(summary = "Find Therapy by Material Number")
    public Therapy findByMaterialNumber(@PathVariable String materialNumber) throws ResourceNotFoundException {
        log.debug("Fetching therapy with materialNumber: {}", materialNumber);
        return therapyService.findByMaterialNumber(materialNumber);
    }
}
