package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.model.DiagnosisCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DiagnosisCodeService {

	Page<DiagnosisCode> searchDiagnosisCodes(String searchTerm, Pageable pageable);

	void uploadCsv(MultipartFile file) throws Exception;

}
