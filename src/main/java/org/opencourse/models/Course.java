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

import org.opencourse.utils.typeinfo.CourseType;

/**
 * Course entity class in OpenCourse.
 * 
 * @author !EEExp3rt
 */
@Entity
@Table(name = "Course")
public class Course extends ActionObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "code", length = 31, nullable = false, unique = true)
    private String code;

    @Column(name = "name", length = 31, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_type", nullable = false)
    private CourseType courseType;

    @Column(name = "credits", nullable = false, precision = 3, scale = 1)
    private Float credits;

    /**
     * Default constructor.
     */
    protected Course() {
    }

    /**
     * Constructor.
     * 
     * @param code       The course code.
     * @param name       The course name.
     * @param department The department offering the course.
     * @param courseType The type of the course.
     * @param credits    The number of credits for the course.
     */
    public Course(String code, String name, Department department, CourseType courseType, Float credits) {
        this.code = code;
        this.name = name;
        this.department = department;
        this.courseType = courseType;
        this.credits = credits;
    }

    // Getters and Setters

    @Override
    public Short getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Float getCredits() {
        return credits;
    }

    public void setCredits(Float credits) {
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
