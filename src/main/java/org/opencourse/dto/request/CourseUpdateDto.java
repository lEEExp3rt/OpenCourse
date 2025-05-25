package org.opencourse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.opencourse.utils.typeinfo.CourseType;

/**
 * DTO for course update requests.
 * 
 * @author !EEExp3rt
 */
public class CourseUpdateDto {

    private Short id;

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 31, message = "课程名称太长")
    private String name;

    @NotBlank(message = "课程代码不能为空")
    @Size(max = 31, message = "课程代码太长")
    private String code;

    private Byte departmentId;

    @NotNull(message = "课程类型不能为空")
    private CourseType courseType;

    @NotNull(message = "学分不能为空")
    private Float credits;

    private Integer updatorId;

    /**
     * Default constructor.
     */
    public CourseUpdateDto() {
    }

    /**
     * Constructor.
     * 
     * @param id           The course ID.
     * @param name         The course name.
     * @param code         The course code.
     * @param departmentId The department ID.
     * @param courseTypeId The course type ID.
     * @param credits      The course credits.
     * @param updatorId    The ID of the user updating the course.
     */
    public CourseUpdateDto(
        Short id,
        String name,
        String code,
        Byte departmentId,
        Byte courseTypeId,
        Float credits,
        Integer updatorId
    ) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.departmentId = departmentId;
        this.courseType = CourseType.getById(courseTypeId.byteValue());
        this.credits = credits;
        this.updatorId = updatorId;
    }

    // Getters and Setters.

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

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

    public Byte getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Byte departmentId) {
        this.departmentId = departmentId;
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

    public Integer getUpdatorId() {
        return updatorId;
    }

    public void setUpdatorId(Integer updatorId) {
        this.updatorId = updatorId;
    }
}
