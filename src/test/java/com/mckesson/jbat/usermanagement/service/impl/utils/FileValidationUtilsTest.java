package com.mckesson.jbat.usermanagement.service.impl.utils;

import com.mckesson.cgat.pde.usermanagement.service.impl.utils.FileValidationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileValidationUtilsTest {

    @Test
    public void testValidateMultiPartFile_WithValidFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());
        assertDoesNotThrow(() -> FileValidationUtils.validateMultiPartFile(file));
    }
}
