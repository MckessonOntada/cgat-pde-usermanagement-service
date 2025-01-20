package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class CatalogConfigurationDTO {

    @NotEmpty(message = "At least one catalog price is required")
    private List<CatalogPrice> catalogPrices;

    @NotEmpty(message = "At least one step is required")
    private List<StepDTO> steps;
}
