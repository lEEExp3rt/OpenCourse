package org.opencourse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.opencourse.dto.request.CourseCreationDto;

import java.math.BigDecimal;

/**
 * Simple JSON serialization test for CourseCreationDto.
 */
class CourseCreationDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSerialization() throws Exception {
        CourseCreationDto dto = new CourseCreationDto(
                "", // blank name
                "CS201",
                (byte) 1,
                (byte) 13,
                new BigDecimal("3.0"));

        String json = objectMapper.writeValueAsString(dto);
        System.out.println("Serialized JSON: " + json);

        CourseCreationDto deserialized = objectMapper.readValue(json, CourseCreationDto.class);
        System.out.println("Deserialized DTO name: '" + deserialized.getName() + "'");
    }
}
