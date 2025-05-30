package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.service.CacheService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/cache/therapies", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cache Controller ", description = "Controller for cache demo.")
@Slf4j
@Validated
public class CacheDemoController {

    private CacheService cacheService;

    public CacheDemoController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping
    @RequirePermissions({Permission.VIEW_MATERIAL})
    @Operation(summary = "Fetch all therapies.")
    public ResponseEntity<Page<TherapyCatalogResponseDTO>> findAllThreapies(
            @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String query) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        return new ResponseEntity<>(cacheService.findAllGlobalTherapies(query, paging), HttpStatus.OK);
    }

    @GetMapping("/config/{tradeName}")
    @RequirePermissions({Permission.VIEW_MATERIAL, Permission.EDIT_ORDERS, Permission.VIEW_SP_MATERIAL})
    @Operation(summary = "Find Therapy details by Trade Name")
    public ResponseEntity<List<MaterialDetailsResponseDTO>> findTherapyDetailsByTradeName(
            @PathVariable String tradeName, @RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType)
            throws ResourceNotFoundException {
        log.debug("Fetching therapy details with tradeName: {}", tradeName);
        return new ResponseEntity<>(cacheService.findTherapyDetailsByTradeName(tradeName, channelType), HttpStatus.OK);

    }

    @DeleteMapping("/config/{materialNumber}")
    @RequirePermissions({Permission.MANAGE_MATERIAL})
    @Operation(summary = "Delete Configuration by Material Number")
    public ResponseEntity<String> deleteConfigByMaterialNumber(
            @PathVariable @NotNull(message = "Material number cannot be null ") @NotEmpty(
                    message = "Material number cannot be empty") String materialNumber)
            throws ResourceNotFoundException {

        cacheService.deleteConfigByMaterialNumber(materialNumber);
        return new ResponseEntity<>(materialNumber, HttpStatus.OK);
    }

    @GetMapping("/test")
    public String testCache() {
        cacheService.testCacheEvents();
        return "Cache events triggered!";
    }

}
