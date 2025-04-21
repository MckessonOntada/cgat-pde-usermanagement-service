package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TherapyResponseDTO {
    private Therapy primaryTherapy;
    private List<Therapy> crosswalkedTherapies;
    private ChannelType primaryTherapyChannelType;
}
