package org.opencourse.services;

import org.opencourse.dto.request.CourseCreationDto;
import org.opencourse.dto.request.CourseUpdateDto;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.models.User;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.DepartmentRepo;
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

    private final CourseRepo courseRepo;
    private final DepartmentRepo departmentRepo;
    private final HistoryManager historyManager;

    /**
     * Constructor.
     * 
     * @param courseRepo     The course repository.
     * @param departmentRepo The department repository.
     * @param historyManager The history manager.
     */
    @Autowired
    public CourseManager(
        CourseRepo courseRepo,
        DepartmentRepo departmentRepo,
        HistoryManager historyManager
    ) {
        this.courseRepo = courseRepo;
        this.departmentRepo = departmentRepo;
        this.historyManager = historyManager;
    }

    /**
     * Add a new course.
     * 
     * @param dto The course creation DTO.
     * @return The created course if successful or null if the course already exists.
     * @throws IllegalArgumentException If the department is not found.
     */
    @Transactional
    public Course addCourse(CourseCreationDto dto, User user) throws IllegalArgumentException {
        // Find the department by name.
        Department department = departmentRepo.findById(dto.getDepartmentId())
            .orElseThrow(() -> new IllegalArgumentException("Department not found."));
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
        course = courseRepo.save(course);
        // Add the course creation history record.
        historyManager.logCreateCourse(user, course);
        return course;
    }

    /**
     * Update an existing course.
     * 
     * @param dto The course update DTO.
     * @return The updated course if successful or null if the course does not exist.
     * @throws IllegalArgumentException If the department or course is not found.
     */
    @Transactional
    public Course updateCourse(CourseUpdateDto dto, User user) throws IllegalArgumentException {
        // Find the department by name.
        Department department = departmentRepo.findById(dto.getDepartmentId())
            .orElseThrow(() -> new IllegalArgumentException("Department not found."));
        // Check if the course with the same code already exists.
        if (courseRepo.existsByCode(dto.getCode())) {
            return null;
        }
        // Find the course by ID.
        Course course = courseRepo.findById(dto.getId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found."));
        // Update the course details.
        course.setName(dto.getName());
        course.setCode(dto.getCode());
        course.setDepartment(department);
        course.setCourseType(dto.getCourseType());
        course.setCredits(dto.getCredits());
        // Save the updated course.
        course = courseRepo.save(course);
        // Add the course update history record.
        historyManager.logUpdateCourse(user, course);
        return course;
    }

    /**
     * Delete an existing course.
     * 
     * @param courseId The course ID.
     * @param user     The operator.
     * @return True if the course deleted successfully, false if the course is not found.
     */
    @Transactional
    public boolean deleteCourse(Short courseId, User user) {
        Course course = courseRepo.findById(courseId).orElse(null);
        if (course == null) {
            return false;
        }
        historyManager.logDeleteCourse(user, course);
        courseRepo.delete(course);
        return true;
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
     */
    public List<Course> getCourses(String keyword) {
        return keyword == null || keyword.isBlank() ?
            getCourses() :
            Stream.concat(
                courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc(keyword).stream(),
                courseRepo.findByCodeContainingIgnoreCaseOrderByNameAsc(keyword).stream()
            ).distinct().collect(Collectors.toList());
    }

    /**
     * Get all courses that belongs to the department.
     * 
     * @param departmentId The department.
     * @return A list of courses that belong to the department.
     */
    public List<Course> getCoursesByDepartment(Byte departmentId) {
        return courseRepo.findByDepartmentId(departmentId);
    }

    /**
     * Get all courses that belongs to the course type.
     * 
     * @param courseTypeId The course type ID.
     * @return A list of courses that belong to the course type.
     */
    public List<Course> getCoursesByType(byte courseTypeId) {
        return courseRepo.findByCourseType(CourseType.getById(courseTypeId));
    }

    /**
     * Get all courses that belongs to the department and the course type.
     * 
     * @param departmentId The department ID.
     * @param courseTypeId The course type ID.
     * @return A list of courses that belong to the department and the course type.
     */
    public List<Course> getCoursesByDepartmentAndType(Byte departmentId, byte courseTypeId) {
        return courseRepo.findByDepartmentIdAndCourseType(departmentId, CourseType.getById(courseTypeId));
    }
}