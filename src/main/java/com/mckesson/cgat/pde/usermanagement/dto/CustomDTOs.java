package com.mckesson.cgat.pde.usermanagement.dto;

public class CustomDTOs {

	public interface TherapyCatalogResponseDTO {

		String getId();

		String getMaterialDescription();

		String getNdc();

		String getUdi();

	}

	public interface AccountsResponseDTO {

		String getSapCustomer();

		String getName1();

		String getHouseNumber();

		String getStreet();

		String getStreet2();

		String getStreet3();

		String getTelephone();

		String getCity();

		String getDistrict();

		String getState();

		String getCountry();

		String getZipCode();

	}

}
