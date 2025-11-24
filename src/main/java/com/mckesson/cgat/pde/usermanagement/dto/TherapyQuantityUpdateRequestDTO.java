package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class TherapyQuantityUpdateRequestDTO {

	@NotNull(message = "NDC numbers must not be null.")
	@Size(min = 1, message = "At least one NDC number must be provided.")
	private List<String> ndcNumbers;

	@NotNull(message = "Minimum quantity is required.")
	@Min(value = 1, message = "Minimum quantity must be at least 1.")
	@Max(value = 99, message = "Minimum quantity cannot exceed 99.")
	private Integer minQuantity;

	@NotNull(message = "Maximum quantity is required.")
	@Min(value = 1, message = "Maximum quantity must be at least 1.")
	@Max(value = 99, message = "Maximum quantity cannot exceed 99.")
	private Integer maxQuantity;

	private ChannelType channelType = ChannelType.BUY_AND_BILL;

	@AssertTrue(message = "Minimum quantity cannot be greater than maximum quantity.")
	public boolean isMinLessThanOrEqualToMax() {
		if (minQuantity == null || maxQuantity == null) {
			return true;
		}
		return minQuantity <= maxQuantity;
	}

}