package com.mckesson.cgat.pde.usermanagement.mapper;

import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TradeCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;

import java.util.*;

public class TradeCatalogResponseMapper {

	public static TradeCatalogResponseDTO mapToTradeCatalogResponse(TherapyCatalogResponseDTO projection) {
		List<TherapyDTO> therapies = mapTherapies(projection);
		TradeCatalogResponseDTO dto = new TradeCatalogResponseDTO();
		dto.setTradeName(projection.getTradeName());
		dto.setChannelType(safeParseChannelType(projection.getChannelType()));
		dto.setTherapies(therapies);

		return dto;
	}

	private static List<TherapyDTO> mapTherapies(TherapyCatalogResponseDTO row) {
		String[] ids = splitSafe(row.getId());
		String[] therapyNumbers = splitSafe(row.getTherapyId());
		String[] descriptions = splitSafe(row.getTherapyDescription());
		String[] ndcs = splitSafe(row.getNdc());
		String[] strengths = splitSafe(row.getStrength());
		String[] dosages = splitSafe(row.getDosage());
		String[] manufacturerNames = splitSafe(row.getManufacturerName());
		String[] genericNames = splitSafe(row.getGenericName());
		String[] drugForm = splitSafe(row.getDrugForm());

		int maxLength = maxLength(ids, therapyNumbers, descriptions, ndcs, strengths, dosages, manufacturerNames,
				genericNames, drugForm);

		Map<String, TherapyDTO> therapyMap = new HashMap<>();
		Set<String> addedNdcs = new HashSet<>();

		for (int i = 0; i < maxLength; i++) {
			String id = safeGet(ids, i);
			String ndc = safeGet(ndcs, i);

			// Skip if id is null or already exists in the map
			if (id == null || therapyMap.containsKey(id) || ndc == null || addedNdcs.contains(ndc)) {
				continue;
			}
			addedNdcs.add(ndc);

			TherapyDTO dto = new TherapyDTO();
			dto.setId(id);
			dto.setMaterialNumber(safeGet(therapyNumbers, i));
			dto.setMaterialDescription(safeGet(descriptions, i));
			dto.setNdc(ndc);
			dto.setStrength(safeGet(strengths, i));
			dto.setDosage(safeGet(dosages, i));
			dto.setManufacturerName(safeGet(manufacturerNames, i));
			dto.setGenericName(safeGet(genericNames, i));
			dto.setDrugForm(safeGet(drugForm, i));
			therapyMap.put(id, dto);

		}

		return new ArrayList<>(therapyMap.values());

	}

	private static String[] splitSafe(String value) {
		return value == null || value.isBlank() ? new String[0] : value.split("\\s*,\\s*");
	}

	private static String safeGet(String[] arr, int i) {
		return (arr != null && i < arr.length) ? arr[i].trim() : null;
	}

	private static int maxLength(String[]... arrays) {
		int max = 0;
		for (String[] arr : arrays) {
			if (arr != null) {
				max = Math.max(max, arr.length);
			}
		}
		return max;
	}

	private static ChannelType safeParseChannelType(String value) {
		try {
			return value != null ? ChannelType.valueOf(value) : null;
		}
		catch (IllegalArgumentException ex) {
			return null; // or handle default fallback
		}
	}

}
