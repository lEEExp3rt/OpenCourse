package org.opencourse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.opencourse.configs.TestSecurityConfig;
import org.opencourse.dto.request.CourseCreationDto;
import org.opencourse.services.CourseManager;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests specifically for validation behavior in CourseController.
 */
@WebMvcTest(CourseController.class)
@Import(TestSecurityConfig.class)
class CourseControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseManager courseManager;

    @Test
    @DisplayName("Should return 400 for validation failure - blank name")
    void testValidationFailure_BlankName() throws Exception {
        CourseCreationDto invalidDto = new CourseCreationDto(
                "", // blank name - should trigger @NotBlank
                "CS201",
                (byte) 1,
                (byte) 13,
                new BigDecimal("3.0"));

        mockMvc.perform(post("/course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for validation failure - invalid credits")
    void testValidationFailure_InvalidCredits() throws Exception {
        CourseCreationDto invalidDto = new CourseCreationDto(
                "Valid Name",
                "CS201",
                (byte) 1,
                (byte) 13,
                new BigDecimal("100.0")); // exceeds max value

        mockMvc.perform(post("/course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
