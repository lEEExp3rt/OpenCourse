package org.opencourse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for department creation requests.
 * 
 * @author LLLLjx
 */
public class DepartmentCreationDto {

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 31, message = "部门名称太长")
    private String name;

    /**
     * Default constructor.
     */
    public DepartmentCreationDto() {
    }

    /**
     * Constructor.
     * 
     * @param name The name of the department.
     */
    public DepartmentCreationDto(String name) {
        this.name = name;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
