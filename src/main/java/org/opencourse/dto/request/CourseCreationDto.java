package org.opencourse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.opencourse.utils.typeinfo.CourseType;

/**
 * DTO for course creation requests.
 * 
 * @author !EEExp3rt
 */
public class CourseCreationDto {

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 31, message = "课程名称太长")
    private String name;

    @NotBlank(message = "课程代码不能为空")
    @Size(max = 31, message = "课程代码太长")
    private String code;

    @NotBlank(message = "所属院系不能为空")
    private String departmentName;

    @NotNull(message = "课程类型不能为空")
    private CourseType courseType;

    @NotNull(message = "学分不能为空")
    private Float credits;

    /**
     * Default constructor.
     */
    public CourseCreationDto() {
    }

    /**
     * Constructor.
     * 
     * @param name           The name of the course.
     * @param code           The code of the course.
     * @param departmentName The name of the department.
     * @param courseTypeId   The ID of the course type.
     * @param credits        The credits of the course.
     */
    public CourseCreationDto(
        String name,
        String code,
        String departmentName,
        Byte courseTypeId,
        Float credits
    ) {
        this.name = name;
        this.code = code;
        this.departmentName = departmentName;
        this.courseType = CourseType.getById(courseTypeId.byteValue());
        this.credits = credits;
    }

    /**
     * Constructor.
     * 
     * @param name           The name of the course.
     * @param code           The code of the course.
     * @param departmentName The name of the department.
     * @param courseType     The type of the course.
     * @param credits        The credits of the course.
     */
    public CourseCreationDto(
        String name,
        String code,
        String departmentName,
        CourseType courseType,
        Float credits
    ) {
        this.name = name;
        this.code = code;
        this.departmentName = departmentName;
        this.courseType = courseType;
        this.credits = credits;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public void setCourseType(Byte courseTypeId) {
        this.courseType = CourseType.getById(courseTypeId.byteValue());
    }

    public Float getCredits() {
        return credits;
    }

    public void setCredits(Float credits) {
        this.credits = credits;
    }
}
