package org.opencourse.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

import org.opencourse.utils.typeinfo.CourseType;

import com.fasterxml.jackson.annotation.JsonSetter;

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

    @NotNull
    private Byte departmentId;

    @NotNull(message = "课程类型不能为空")
    private CourseType courseType;

    @NotNull(message = "学分不能为空")
    @DecimalMin(value = "0.0", message = "学分必须非负")
    @DecimalMax(value = "99.9", message = "学分不能超过 100")
    @Digits(integer = 2, fraction = 1, message = "学分格式错误")
    private BigDecimal credits;

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
     */
    public CourseUpdateDto(
            Short id,
            String name,
            String code,
            Byte departmentId,
            Byte courseTypeId,
            BigDecimal credits) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.departmentId = departmentId;
        this.courseType = CourseType.getById(courseTypeId);
        this.credits = credits;
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

    @JsonSetter("typeId")
    public void setCourseType(Byte courseTypeId) {
        this.courseType = CourseType.getById(courseTypeId);
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }
}
