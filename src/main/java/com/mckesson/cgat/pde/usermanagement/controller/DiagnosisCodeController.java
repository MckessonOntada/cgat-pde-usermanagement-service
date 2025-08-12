package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.model.DiagnosisCode;
import com.mckesson.cgat.pde.usermanagement.service.DiagnosisCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Diagnosis Codes", description = "Controller for Managing Diagnosis Code operations and data.")
@RequestMapping(path = "/practices/{practiceId}/diagnosis-codes", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class DiagnosisCodeController {

    private final DiagnosisCodeService diagnosisCodeService;

    public DiagnosisCodeController(DiagnosisCodeService diagnosisCodeService) {
        this.diagnosisCodeService = diagnosisCodeService;
    }


    @GetMapping
    @Operation(summary = "Search diagnosis codes with pagination and sorting")
    public Page<DiagnosisCode> searchDiagnosisCodes(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "code") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query) {

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return diagnosisCodeService.searchDiagnosisCodes(query, pageable);
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload diagnosis codes CSV")
    public ResponseEntity<String> uploadDiagnosisCodes(@RequestParam("file") MultipartFile file) {
        try {
            diagnosisCodeService.uploadCsv(file);
            return ResponseEntity.ok("Diagnosis codes uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
        }
    }

}
