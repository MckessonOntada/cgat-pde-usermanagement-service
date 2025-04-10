package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialCrossWalkConfigRecordDTO {
    private String bnbMaterialNumber;
    private String bnbDescription;
    private String bnbNDC;

    private String spProductId;
    private String spProductName;
    private String spNDC;

}
