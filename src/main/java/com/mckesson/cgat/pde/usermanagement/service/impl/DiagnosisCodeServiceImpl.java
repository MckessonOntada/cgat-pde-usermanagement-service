package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.DiagnosisCode;
import com.mckesson.cgat.pde.usermanagement.repository.DiagnosisCodeRepository;
import com.mckesson.cgat.pde.usermanagement.service.DiagnosisCodeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisCodeServiceImpl implements DiagnosisCodeService {

    private final DiagnosisCodeRepository repository;

    @Override
    public Page<DiagnosisCode> searchDiagnosisCodes(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        return repository.findByCodeContainingIgnoreCaseOrLongDescriptionContainingIgnoreCase(query, query, pageable);
    }

    @Override
    public void uploadCsv(MultipartFile file) throws Exception {
        List<DiagnosisCode> codes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                codes.add(DiagnosisCode.builder()
                        .code(record.get("CODE"))
                        .longDescription(record.get("LONG DESCRIPTION"))
                        .build());
            }
        }

        repository.saveAll(codes);
    }


}
