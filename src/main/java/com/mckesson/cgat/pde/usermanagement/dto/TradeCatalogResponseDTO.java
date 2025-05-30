package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeCatalogResponseDTO {

	private String tradeName;

	private ChannelType channelType;

	private List<TherapyDTO> therapies;

}
