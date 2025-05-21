package org.opencourse.services;

import org.opencourse.dto.request.CourseCreationDto;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.utils.typeinfo.CourseType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 * Course service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class CourseManager {

    private final CourseRepo courseRepo; // Data access object.
    private final DepartmentManager departmentManager;

    /**
     * Constructor.
     * 
     * @param courseRepo        The course repository.
     * @param departmentManager The department manager.
     */
    @Autowired
    public CourseManager(CourseRepo courseRepo, DepartmentManager departmentManager) {
        this.courseRepo = courseRepo;
        this.departmentManager = departmentManager;
    }

    /**
     * Add a new course.
     * 
     * @param dto The course creation DTO.
     * @return The created course if successful or null if the course already exists.
     * @throws IllegalArgumentException if the department is not found.
     */
    @Transactional
    public Course addCourse(CourseCreationDto dto) {
        // Find the department by name.
        Department department = departmentManager.getDepartment(dto.getDepartmentName());
        if (department == null) {
            throw new IllegalArgumentException(
                "Department " + dto.getDepartmentName() + " not found."
            );
        }
        // Check if the course with the same code already exists.
        if (courseRepo.existsByCode(dto.getCode())) {
            return null;
        }
        // Create a new course and save.
        Course course = new Course(
            dto.getName(),
            dto.getCode(),
            department,
            dto.getCourseType(),
            dto.getCredits()
        );
        return courseRepo.save(course);
    }

    /**
     * Get a course by its name or code.
     * 
     * @param keyword The name or code of the course.
     * @return The course if found, null otherwise.
     * @throws IllegalArgumentException if the keyword is null or empty.
     */
    public Course getCourse(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be null or empty.");
        }
        return courseRepo.findByName(keyword)
            .orElseGet(() -> courseRepo.findByCode(keyword).orElse(null));
    }

    /**
     * Get all courses in name ascending order.
     * 
     * @return A list of all courses.
     */
    public List<Course> getCourses() {
        return courseRepo.findAllByOrderByNameAsc();
    }

    /**
     * Get all courses that match the given keyword.
     * 
     * @param keyword The keyword to search for.
     * @return A list of courses that match the keyword.
     * @apiNote The argument must be non-null and non-empty.
     */
    public List<Course> getCourses(String keyword) {
        return Stream.concat(
            courseRepo.findByNameContainingIgnoreCase(keyword).stream(),
            courseRepo.findByCodeContainingIgnoreCase(keyword).stream()
        ).distinct().collect(Collectors.toList());
    }

    /**
     * Get all courses that belongs to the department.
     * 
     * @param department The department.
     * @return A list of courses that belong to the department.
     * @apiNote The argument must be non-null and non-empty.
     */
    public List<Course> getCourses(Department department) {
        return courseRepo.findByDepartment(department);
    }

    /**
     * Get all courses that belongs to the course type.
     * 
     * @param courseType The course type.
     * @return A list of courses that belong to the course type.
     * @apiNote The argument must be non-null and non-empty.
     */
    public List<Course> getCourses(CourseType courseType) {
        return courseRepo.findByCourseType(courseType);
    }
}