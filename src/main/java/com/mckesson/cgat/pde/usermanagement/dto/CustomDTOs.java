package com.mckesson.cgat.pde.usermanagement.dto;

public class CustomDTOs {

	public interface TherapyCatalogResponseDTO {

		String getId();

		String getMaterialDescription();

		String getNdc();

		String getUdi();

	}

	public interface SoldToAccountsResponseDTO {

		String getSoldToCustomer();

		String getsoldToCustomerName();

	}

	public interface ShipToAccountsResponseDTO {

		String getShipToCustomer();

		String getshipToCustomerName();

	}

}
