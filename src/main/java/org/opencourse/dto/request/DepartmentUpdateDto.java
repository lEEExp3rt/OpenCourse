package org.opencourse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for department update requests.
 * 
 * @author Li JIng XIoNg
 */
public class DepartmentUpdateDto {

    @NotNull(message = "部门ID不能为空")
    private Byte id;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 31, message = "部门名称太长")
    private String name;

    /**
     * Default constructor.
     */
    public DepartmentUpdateDto() {
    }

    /**
     * Constructor.
     * 
     * @param id   The ID of the department.
     * @param name The name of the department.
     */
    public DepartmentUpdateDto(Byte id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters

    public Byte getId() {
        return id;
    }

    public void setId(Byte id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
