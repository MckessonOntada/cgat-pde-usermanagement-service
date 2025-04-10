package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialCrossWalkConfigRecordDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.MaterialCrossWalkConfig;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.repository.MaterialCrossWalkConfigRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.MaterialCrossWalkService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Slf4j
@Service
public class MaterialCrossWalkServiceImpl implements MaterialCrossWalkService {

    private final MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository;

    private final TherapyRepository therapyRepository;

    @Autowired
    Validator validator;

    public MaterialCrossWalkServiceImpl(MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository, TherapyRepository therapyRepository) {
        this.materialCrossWalkConfigRepository = materialCrossWalkConfigRepository;
        this.therapyRepository = therapyRepository;
    }

    @Override
    public Boolean uploadCrossWalks(MultipartFile crossWalkDataFile, Long practiceId, ByteArrayOutputStream outputStream) {
        List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigs = new ArrayList<>();

        Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks = new LinkedHashMap<>();

        Set<String> updatedSPMaterialNumbers = new HashSet<>();
        Set<String> updatedBnBMaterialNumbers = new HashSet<>();

        parseCrossWalkDataFile(crossWalkDataFile, crossWalkConfigs);

        for(MaterialCrossWalkConfigRecordDTO crossWalkConfigRecordDTO : crossWalkConfigs) {
            String bnbMaterialNumber = crossWalkConfigRecordDTO.getBnbMaterialNumber();
            String spProductId = crossWalkConfigRecordDTO.getSpProductId();

            try {
                MaterialCrossWalkConfig crossWalkConfig = MaterialCrossWalkConfig.builder()
                                .bnbMaterialNumber(crossWalkConfigRecordDTO.getBnbMaterialNumber())
                                .spProductId(crossWalkConfigRecordDTO.getSpProductId())
                                .build();

                validateCrossWalkConfig(crossWalkConfig);

                validateMaterialNumber(bnbMaterialNumber, ChannelType.BUY_AND_BILL);
                validateMaterialNumber(spProductId, ChannelType.SPECIALITY_PHARMA);

                Optional<MaterialCrossWalkConfig> existingConfig = materialCrossWalkConfigRepository.findMaterialCrossWalkConfigByBnbMaterialNumberAndSpProductId(bnbMaterialNumber, spProductId);
                if (existingConfig.isPresent()) {
                    log.info("Cross walk already exists for the provided bnbMaterialNumber: {}, spProductId: {}. Ignoring it.", bnbMaterialNumber, spProductId);
                } else {
                    validateCrossWalkConstraints(bnbMaterialNumber, spProductId, updatedSPMaterialNumbers);
                    materialCrossWalkConfigRepository.save(crossWalkConfig);
                }
                updatedSPMaterialNumbers.add(spProductId);
                updatedBnBMaterialNumbers.add(bnbMaterialNumber);

            } catch (IllegalArgumentException e) {
                log.error("Validation failed for bnbMaterialNumber: {}, spMaterialNumber: {} {}",bnbMaterialNumber, spProductId, e.getMessage(), e);
                failedCrossWalks.put(crossWalkConfigRecordDTO, e.getMessage());
            }catch (ResourceNotFoundException e) {
                failedCrossWalks.put(crossWalkConfigRecordDTO, e.getMessage());
            }
        }

        if(!updatedBnBMaterialNumbers.isEmpty()) {
            //TODO: update is cross walked flag in bnb therapies
        }

        if(!updatedSPMaterialNumbers.isEmpty()) {
            //TODO: update is cross walked flag in sp therapies
        }

        createOutputCSV(outputStream, failedCrossWalks);

        return failedCrossWalks.isEmpty();
    }


    private void parseCrossWalkDataFile(MultipartFile crossWalkDataFile, List<MaterialCrossWalkConfigRecordDTO> crossWalkConfigs) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(crossWalkDataFile.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                if (record.stream().allMatch(String::isBlank)) {
                    break;
                }
                List<String> recordData = record.toList();

                MaterialCrossWalkConfigRecordDTO crossWalkConfig = MaterialCrossWalkConfigRecordDTO.builder()
                        .bnbMaterialNumber(recordData.get(0))
                        .bnbDescription(recordData.get(1))
                        .bnbNDC(recordData.get(2))
                        .spProductId(recordData.get(3))
                        .spProductName(recordData.get(4))
                        .spNDC(recordData.get(5))
                        .build();

                crossWalkConfigs.add(crossWalkConfig);
            }

        }catch (IOException e) {
            log.error("Unable to read Cross walk Data CSV");
            throw new FileNotValidException("Unable to read User Data CSV");
        }
        catch (IndexOutOfBoundsException e) {
            log.error("Invalid Cross walk Data file, some columns are missing");
            throw new FileNotValidException("Invalid Cross walk Data file, some columns are missing");
        }
    }

    private void validateMaterialNumber(String materialNumber, ChannelType channelType) throws ResourceNotFoundException {
        therapyRepository.findByMaterialNumberAndChannelType(materialNumber, channelType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Therapy not found with this number: %s, channelType: %s", materialNumber, channelType)));
    }


    private void validateCrossWalkConfig(MaterialCrossWalkConfig crossWalkConfig) {
        // Trigger validation with the BasicInfo group
        Set<ConstraintViolation<MaterialCrossWalkConfig>> violations = validator.validate(crossWalkConfig, BasicInfo.class);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors: ");
            for (ConstraintViolation<MaterialCrossWalkConfig> violation : violations) {
                errorMessage.append(violation.getPropertyPath())
                        .append(" ")
                        .append(violation.getMessage())
                        .append("; ");
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    private void validateCrossWalkConstraints(String bnbMaterialNumber, String spMaterialNumber, Set<String> updatedSPCrossWalks) {
        Optional<MaterialCrossWalkConfig> config = materialCrossWalkConfigRepository.findMaterialCrossWalkConfigBySpProductIdAndIsActiveTrue(spMaterialNumber);
        if (config.isPresent()) {
            MaterialCrossWalkConfig crossWalkConfig = config.get();
            if(!bnbMaterialNumber.equals(crossWalkConfig.getBnbMaterialNumber())) {
                throw new IllegalArgumentException("The SP product ID is already mapped to an Material, please review the existing mapping and retry.");
            }
        } else {
            if(updatedSPCrossWalks.contains(spMaterialNumber)) {
                throw new IllegalArgumentException("Duplicate Entry");
            }
        }
    }


    private void createOutputCSV(OutputStream outputStream, Map<MaterialCrossWalkConfigRecordDTO, String> failedCrossWalks) {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader("Buy & Bill Material ID", "Buy & Bill Description", "Buy & Bill NDC", "SP Product ID", "SP Product Name", "SP NDC", "Message"))) {

            for (Map.Entry<MaterialCrossWalkConfigRecordDTO, String> e : failedCrossWalks.entrySet()) {
                MaterialCrossWalkConfigRecordDTO crossWalkConfigRecordDTO = e.getKey();
                csvPrinter.printRecord(crossWalkConfigRecordDTO.getBnbMaterialNumber(), crossWalkConfigRecordDTO.getBnbDescription()
                        , crossWalkConfigRecordDTO.getBnbNDC(), crossWalkConfigRecordDTO.getSpProductId(), crossWalkConfigRecordDTO.getSpProductName(), crossWalkConfigRecordDTO.getSpNDC(), e.getValue());
            }
        }
        catch (IOException e) {
            log.error("Unable to process output csv file, error message: {}", e.getMessage());
            throw new RuntimeException("Failed to generate output csv file");
        }
    }


}
