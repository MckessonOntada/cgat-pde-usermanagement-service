package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.cgat.pde.usermanagement.dto.ReferenceDataResponse;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.service.ReferenceDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Reference", description = "Controller for static reference data.")
@RequestMapping(path = "/reference", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ReferenceDataController {

    @Autowired
    private ReferenceDataService referenceDataService;

    @GetMapping
    @Operation(summary = "Fetch all static reference data.")
    ReferenceDataResponse allStaticReferenceData() {
        return referenceDataService.findAll();
    }
    
    @GetMapping("/role/{rolename}/permissions")
    @Operation(summary = "Fetch all permissions associated with each role")
    public ResponseEntity<Role> getAllRolesAndPermissions(@PathVariable String rolename) throws ResourceNotFoundException {
    	Role role= referenceDataService.getRoleByname(rolename);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
    
    
}
