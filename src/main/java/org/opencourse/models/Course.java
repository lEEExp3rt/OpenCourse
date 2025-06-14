package org.opencourse.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

import org.opencourse.utils.typeinfo.CourseType;

/**
 * Course entity class in OpenCourse.
 * 
 * @author !EEExp3rt
 */
@Entity
@Table(name = "`Course`")
public class Course extends Model<Short> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "name", length = 31, nullable = false)
    private String name;

    @Column(name = "code", length = 31, nullable = false, unique = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_type", nullable = false)
    private CourseType courseType;

    @Column(name = "credits", nullable = false, precision = 3, scale = 1)
    private BigDecimal credits;

    /**
     * Default constructor.
     */
    protected Course() {
    }

    /**
     * Constructor.
     * 
     * @param name       The course name.
     * @param code       The course code in official academic system.
     * @param department The department offering the course.
     * @param courseType The type of the course.
     * @param credits    The number of credits for the course.
     */
    public Course(String name, String code, Department department, CourseType courseType, BigDecimal credits) {
        this.name = name;
        this.code = code;
        this.department = department;
        this.courseType = courseType;
        this.credits = credits;
    }

    // Getters and Setters

    @Override
    public Short getId() {
        return id;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", department=" + department +
                ", courseType=" + courseType +
                ", credits=" + credits +
                '}';
    }
}
