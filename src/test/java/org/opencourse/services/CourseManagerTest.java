package org.opencourse.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opencourse.dto.request.CourseCreationDto;
import org.opencourse.dto.request.CourseUpdateDto;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.models.User;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.DepartmentRepo;
import org.opencourse.utils.typeinfo.CourseType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CourseManager}.
 * 
 * @author !EEExp3rt
 */
@ExtendWith(MockitoExtension.class)
class CourseManagerTest {

    @Mock
    private CourseRepo courseRepo;

    @Mock
    private DepartmentRepo departmentRepo;

    @Mock
    private HistoryManager historyManager;

    @InjectMocks
    private CourseManager courseManager;

    // Test data.
    private User testUser;
    private Department testDepartment;
    private Course testCourse;
    private CourseCreationDto testCreationDto;
    private CourseUpdateDto testUpdateDto;

    @BeforeEach
    void setUp() {
        // Create test user.
        testUser = new User(
            "testUser",
            "test@example.com",
            "hashedPassword",
            User.UserRole.ADMIN
        );
        testUser = spy(testUser);
        lenient().when(testUser.getId()).thenReturn(1);

        // Create test department.
        testDepartment = new Department("Computer Science");
        testDepartment = spy(testDepartment);
        lenient().when(testDepartment.getId()).thenReturn((byte) 1);

        // Create test course.
        testCourse = new Course(
            "Data Structures",
            "CS101",
            testDepartment,
            CourseType.MAJOR_REQUIRED,
            new BigDecimal("3.0")
        );
        testCourse = spy(testCourse);
        lenient().when(testCourse.getId()).thenReturn((short) 1);

        // Create test DTOs (移除了 userId 参数).
        testCreationDto = new CourseCreationDto(
            "Data Structures",
            "CS101",
            (byte) 1,
            (byte) 13, // CourseType.MAJOR_REQUIRED.
            new BigDecimal("3.0")
        );

        testUpdateDto = new CourseUpdateDto(
            (short) 1,
            "Advanced Data Structures",
            "CS102",
            (byte) 1,
            (byte) 13, // CourseType.MAJOR_REQUIRED.
            new BigDecimal("4.0")
        );
    }

    // Course creation tests.

    @Test
    @DisplayName("Should successfully add a new course when data is valid")
    void addCourse_WithValidData_ShouldReturnCreatedCourse() {
        // Given.
        when(departmentRepo.findById(testCreationDto.getDepartmentId()))
            .thenReturn(Optional.of(testDepartment));
        when(courseRepo.existsByCode(testCreationDto.getCode()))
            .thenReturn(false);
        when(courseRepo.save(any(Course.class))).thenReturn(testCourse);

        // When.
        Course result = courseManager.addCourse(testCreationDto, testUser);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testCourse);

        verify(departmentRepo).findById(testCreationDto.getDepartmentId());
        verify(courseRepo).existsByCode(testCreationDto.getCode());
        verify(courseRepo).save(any(Course.class));
        verify(historyManager).logCreateCourse(testUser, testCourse);
    }

    @Test
    @DisplayName("Should return null when course code already exists")
    void addCourse_WithExistingCode_ShouldReturnNull() {
        // Given.
        when(departmentRepo.findById(testCreationDto.getDepartmentId()))
            .thenReturn(Optional.of(testDepartment));
        when(courseRepo.existsByCode(testCreationDto.getCode()))
            .thenReturn(true);

        // When.
        Course result = courseManager.addCourse(testCreationDto, testUser);

        // Then.
        assertThat(result).isNull();

        verify(courseRepo).existsByCode(testCreationDto.getCode());
        verify(courseRepo, never()).save(any(Course.class));
        verify(historyManager, never()).logCreateCourse(any(), any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when department not found")
    void addCourse_WithNonExistentDepartment_ShouldThrowException() {
        // Given.
        when(departmentRepo.findById(testCreationDto.getDepartmentId()))
            .thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> courseManager.addCourse(testCreationDto, testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Department not found.");

        verify(departmentRepo).findById(testCreationDto.getDepartmentId());
        verify(courseRepo, never()).save(any());
        verify(historyManager, never()).logCreateCourse(any(), any());
    }

    @Test
    @DisplayName("Should create course with correct properties from DTO")
    void addCourse_WithValidData_ShouldCreateCourseWithCorrectProperties() {
        // Given.
        when(departmentRepo.findById(testCreationDto.getDepartmentId()))
            .thenReturn(Optional.of(testDepartment));
        when(courseRepo.existsByCode(testCreationDto.getCode()))
            .thenReturn(false);
        when(courseRepo.save(any(Course.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When.
        courseManager.addCourse(testCreationDto, testUser);

        // Then.
        verify(courseRepo).save(argThat(course ->
            course.getName().equals(testCreationDto.getName()) &&
            course.getCode().equals(testCreationDto.getCode()) &&
            course.getDepartment().equals(testDepartment) &&
            course.getCourseType().equals(testCreationDto.getCourseType()) &&
            course.getCredits().equals(testCreationDto.getCredits())
        ));
    }

    // Course update tests

    @Test
    @DisplayName("Should successfully update course when data is valid")
    void updateCourse_WithValidData_ShouldReturnUpdatedCourse() {
        // Given.
        when(departmentRepo.findById(testUpdateDto.getDepartmentId()))
            .thenReturn(Optional.of(testDepartment));
        when(courseRepo.existsByCode(testUpdateDto.getCode()))
            .thenReturn(false);
        when(courseRepo.findById(testUpdateDto.getId()))
            .thenReturn(Optional.of(testCourse));
        when(courseRepo.save(testCourse)).thenReturn(testCourse);

        // When.
        Course result = courseManager.updateCourse(testUpdateDto, testUser);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testCourse);

        verify(courseRepo).save(testCourse);
        verify(historyManager).logUpdateCourse(testUser, testCourse);
    }

    @Test
    @DisplayName("Should return null when updating to existing course code")
    void updateCourse_WithExistingCode_ShouldReturnNull() {
        // Given.
        when(departmentRepo.findById(testUpdateDto.getDepartmentId()))
            .thenReturn(Optional.of(testDepartment));
        when(courseRepo.existsByCode(testUpdateDto.getCode()))
            .thenReturn(true);

        // When.
        Course result = courseManager.updateCourse(testUpdateDto, testUser);

        // Then.
        assertThat(result).isNull();

        verify(courseRepo, never()).findById(any());
        verify(courseRepo, never()).save(any());
        verify(historyManager, never()).logUpdateCourse(any(), any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when department not found for update")
    void updateCourse_WithNonExistentDepartment_ShouldThrowException() {
        // Given.
        when(departmentRepo.findById(testUpdateDto.getDepartmentId()))
            .thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> courseManager.updateCourse(testUpdateDto, testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Department not found.");

        verify(departmentRepo).findById(testUpdateDto.getDepartmentId());
        verify(courseRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when course to update not found")
    void updateCourse_WithNonExistentCourse_ShouldThrowException() {
        // Given.
        when(departmentRepo.findById(testUpdateDto.getDepartmentId()))
            .thenReturn(Optional.of(testDepartment));
        when(courseRepo.existsByCode(testUpdateDto.getCode())).thenReturn(false);
        when(courseRepo.findById(testUpdateDto.getId())).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> courseManager.updateCourse(testUpdateDto, testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Course not found.");

        verify(courseRepo, never()).save(any());
        verify(historyManager, never()).logUpdateCourse(any(), any());
    }

    @Test
    @DisplayName("Should update course properties correctly")
    void updateCourse_WithValidData_ShouldUpdateCourseProperties() {
        // Given.
        when(departmentRepo.findById(testUpdateDto.getDepartmentId()))
            .thenReturn(Optional.of(testDepartment));
        when(courseRepo.existsByCode(testUpdateDto.getCode())).thenReturn(false);
        when(courseRepo.findById(testUpdateDto.getId())).thenReturn(Optional.of(testCourse));
        when(courseRepo.save(testCourse)).thenReturn(testCourse);

        // When.
        courseManager.updateCourse(testUpdateDto, testUser);

        // Then.
        verify(testCourse).setName(testUpdateDto.getName());
        verify(testCourse).setCode(testUpdateDto.getCode());
        verify(testCourse).setDepartment(testDepartment);
        verify(testCourse).setCourseType(testUpdateDto.getCourseType());
        verify(testCourse).setCredits(testUpdateDto.getCredits());
    }

    // Course deletion tests

    @Test
    @DisplayName("Should successfully delete course when course and user exist")
    void deleteCourse_WithValidData_ShouldReturnTrue() {
        // Given.
        Short courseId = (short) 1;

        when(courseRepo.findById(courseId)).thenReturn(Optional.of(testCourse));

        // When.
        boolean result = courseManager.deleteCourse(courseId, testUser);

        // Then.
        assertThat(result).isTrue();

        verify(historyManager).logDeleteCourse(testUser, testCourse);
        verify(courseRepo).delete(testCourse);
    }

    @Test
    @DisplayName("Should return false when course to delete not found")
    void deleteCourse_WithNonExistentCourse_ShouldReturnFalse() {
        // Given.
        Short courseId = (short) 999;

        when(courseRepo.findById(courseId)).thenReturn(Optional.empty());

        // When.
        boolean result = courseManager.deleteCourse(courseId, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(courseRepo, never()).delete(any());
        verify(historyManager, never()).logDeleteCourse(any(), any());
    }

    // Course retrieval tests

    @Test
    @DisplayName("Should return all courses ordered by name")
    void getCourses_ShouldReturnAllCoursesInOrder() {
        // Given.
        List<Course> expectedCourses = Arrays.asList(
            new Course(
                "Algorithms",
                "CS201",
                testDepartment,
                CourseType.MAJOR_REQUIRED,
                new BigDecimal("3.0")
            ),
            new Course(
                "Data Structures",
                "CS101",
                testDepartment,
                CourseType.MAJOR_REQUIRED,
                new BigDecimal("3.0")
            ),
            new Course(
                "Software Engineering",
                "CS301",
                testDepartment,
                CourseType.MAJOR_OPTIONAL,
                new BigDecimal("4.0")
            )
        );

        when(courseRepo.findAllByOrderByNameAsc()).thenReturn(expectedCourses);

        // When.
        List<Course> result = courseManager.getCourses();

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).isEqualTo(expectedCourses);

        verify(courseRepo).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("Should return courses matching keyword when searching")
    void getCourses_WithKeyword_ShouldReturnMatchingCourses() {
        // Given.
        String keyword = "Data";
        Course course1 = new Course(
            "Data Structures",
            "CS101",
            testDepartment,
            CourseType.MAJOR_REQUIRED,
            new BigDecimal("3.0")
        );
        Course course2 = new Course(
            "Database Systems",
            "CS201",
            testDepartment,
            CourseType.MAJOR_REQUIRED,
            new BigDecimal("3.0")
        );

        List<Course> coursesByName = Arrays.asList(course1, course2);
        List<Course> coursesByCode = Collections.emptyList();

        when(courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc(keyword))
            .thenReturn(coursesByName);
        when(courseRepo.findByCodeContainingIgnoreCaseOrderByNameAsc(keyword))
            .thenReturn(coursesByCode);

        // When.
        List<Course> result = courseManager.getCourses(keyword);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(course1, course2);

        verify(courseRepo).findByNameContainingIgnoreCaseOrderByNameAsc(keyword);
        verify(courseRepo).findByCodeContainingIgnoreCaseOrderByNameAsc(keyword);
    }

    @Test
    @DisplayName("Should return combined results when searching by keyword")
    void getCourses_WithKeyword_ShouldCombineNameAndCodeResults() {
        // Given.
        String keyword = "CS";
        Course course1 = new Course(
            "Computer Science",
            "MATH101",
            testDepartment,
            CourseType.MAJOR_REQUIRED,
            new BigDecimal("3.0")
        );
        Course course2 = new Course(
            "Database Systems",
            "CS201",
            testDepartment,
            CourseType.MAJOR_REQUIRED,
            new BigDecimal("3.0")
        );
        Course course3 = new Course(
            "Algorithms",
            "CS301",
            testDepartment,
            CourseType.MAJOR_REQUIRED,
            new BigDecimal("3.0")
        );

        List<Course> coursesByName = Arrays.asList(course1);
        List<Course> coursesByCode = Arrays.asList(course2, course3);

        when(courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc(keyword))
            .thenReturn(coursesByName);
        when(courseRepo.findByCodeContainingIgnoreCaseOrderByNameAsc(keyword))
            .thenReturn(coursesByCode);

        // When.
        List<Course> result = courseManager.getCourses(keyword);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(course1, course2, course3);
    }

    @Test
    @DisplayName("Should return all courses when searching with null keyword")
    void getCourses_WithNullKeyword_ShouldReturnAllCourses() {
        // Given.
        String nullKeyword = null;
        List<Course> allCourses = Arrays.asList(testCourse);

        when(courseRepo.findAllByOrderByNameAsc()).thenReturn(allCourses);

        // When.
        List<Course> result = courseManager.getCourses(nullKeyword);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(allCourses);

        verify(courseRepo).findAllByOrderByNameAsc();
        verify(courseRepo, never()).findByNameContainingIgnoreCaseOrderByNameAsc(any());
        verify(courseRepo, never()).findByCodeContainingIgnoreCaseOrderByNameAsc(any());
    }

    @Test
    @DisplayName("Should return all courses when searching with empty keyword")
    void getCourses_WithEmptyKeyword_ShouldReturnAllCourses() {
        // Given.
        String emptyKeyword = "";
        List<Course> allCourses = Arrays.asList(testCourse);

        when(courseRepo.findAllByOrderByNameAsc()).thenReturn(allCourses);

        // When.
        List<Course> result = courseManager.getCourses(emptyKeyword);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(allCourses);

        verify(courseRepo).findAllByOrderByNameAsc();
        verify(courseRepo, never()).findByNameContainingIgnoreCaseOrderByNameAsc(any());
        verify(courseRepo, never()).findByCodeContainingIgnoreCaseOrderByNameAsc(any());
    }

    @Test
    @DisplayName("Should return all courses when searching with blank keyword")
    void getCourses_WithBlankKeyword_ShouldReturnAllCourses() {
        // Given.
        String blankKeyword = "   ";
        List<Course> allCourses = Arrays.asList(testCourse);

        when(courseRepo.findAllByOrderByNameAsc()).thenReturn(allCourses);

        // When.
        List<Course> result = courseManager.getCourses(blankKeyword);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(allCourses);

        verify(courseRepo).findAllByOrderByNameAsc();
        verify(courseRepo, never()).findByNameContainingIgnoreCaseOrderByNameAsc(any());
        verify(courseRepo, never()).findByCodeContainingIgnoreCaseOrderByNameAsc(any());
    }

    @Test
    @DisplayName("Should return courses by department when department ID provided")
    void getCoursesByDepartment_WithValidDepartmentId_ShouldReturnCourses() {
        // Given.
        Byte departmentId = (byte) 1;
        List<Course> expectedCourses = Arrays.asList(testCourse);

        when(courseRepo.findByDepartmentId(departmentId)).thenReturn(expectedCourses);

        // When.
        List<Course> result = courseManager.getCoursesByDepartment(departmentId);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedCourses);

        verify(courseRepo).findByDepartmentId(departmentId);
    }

    @Test
    @DisplayName("Should return courses by type when course type ID provided")
    void getCoursesByType_WithValidTypeId_ShouldReturnCourses() {
        // Given.
        byte courseTypeId = 13; // CourseType.MAJOR_REQUIRED.getId()
        CourseType courseType = CourseType.getById(courseTypeId);
        List<Course> expectedCourses = Arrays.asList(testCourse);

        when(courseRepo.findByCourseType(courseType)).thenReturn(expectedCourses);

        // When.
        List<Course> result = courseManager.getCoursesByType(courseTypeId);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedCourses);
        assertThat(courseType).isEqualTo(CourseType.MAJOR_REQUIRED);

        verify(courseRepo).findByCourseType(courseType);
    }

    @Test
    @DisplayName("Should return courses by department and type when both IDs provided")
    void getCoursesByDepartmentAndType_WithValidIds_ShouldReturnCourses() {
        // Given.
        Byte departmentId = (byte) 1;
        byte courseTypeId = 13; // CourseType.MAJOR_REQUIRED.getId()
        CourseType courseType = CourseType.getById(courseTypeId);
        List<Course> expectedCourses = Arrays.asList(testCourse);

        when(courseRepo.findByDepartmentIdAndCourseType(departmentId, courseType))
            .thenReturn(expectedCourses);

        // When.
        List<Course> result = courseManager.getCoursesByDepartmentAndType(departmentId, courseTypeId);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedCourses);

        verify(courseRepo).findByDepartmentIdAndCourseType(departmentId, courseType);
    }

    @Test
    @DisplayName("Should return empty list when no courses found by department")
    void getCoursesByDepartment_WithNonExistentDepartment_ShouldReturnEmptyList() {
        // Given.
        Byte departmentId = (byte) 999;
        List<Course> emptyCourses = Collections.emptyList();

        when(courseRepo.findByDepartmentId(departmentId)).thenReturn(emptyCourses);

        // When.
        List<Course> result = courseManager.getCoursesByDepartment(departmentId);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(courseRepo).findByDepartmentId(departmentId);
    }

    @Test
    @DisplayName("Should return empty list when no courses found by type")
    void getCoursesByType_WithNonExistentType_ShouldReturnEmptyList() {
        // Given.
        byte courseTypeId = 11; // CourseType.GENERAL_REQUIRED.getId()
        CourseType courseType = CourseType.getById(courseTypeId);
        List<Course> emptyCourses = Collections.emptyList();

        when(courseRepo.findByCourseType(courseType)).thenReturn(emptyCourses);

        // When.
        List<Course> result = courseManager.getCoursesByType(courseTypeId);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(courseRepo).findByCourseType(courseType);
    }

    @Test
    @DisplayName("Should handle invalid course type ID gracefully")
    void getCoursesByType_WithInvalidTypeId_ShouldHandleGracefully() {
        // Given.
        byte invalidTypeId = 99;
        CourseType courseType = CourseType.getById(invalidTypeId);

        // When.
        List<Course> result = courseManager.getCoursesByType(invalidTypeId);

        // Then.
        assertThat(courseType).isNull();
        assertThat(result).isEmpty();
    }

    // Edge cases and additional tests

    @Test
    @DisplayName("Should not save course when repository throws exception")
    void addCourse_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given.
        when(departmentRepo.findById(testCreationDto.getDepartmentId()))
            .thenReturn(Optional.of(testDepartment));
        when(courseRepo.existsByCode(testCreationDto.getCode()))
            .thenReturn(false);
        when(courseRepo.save(any(Course.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then.
        assertThatThrownBy(() -> courseManager.addCourse(testCreationDto, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(historyManager, never()).logCreateCourse(any(), any());
    }

    @Test
    @DisplayName("Should validate CourseType enum values")
    void shouldValidateCourseTypeEnumValues() {
        // Given & When & Then.
        assertThat(CourseType.GENERAL_REQUIRED.getId()).isEqualTo((byte) 11);
        assertThat(CourseType.GENERAL_OPTIONAL.getId()).isEqualTo((byte) 12);
        assertThat(CourseType.MAJOR_REQUIRED.getId()).isEqualTo((byte) 13);
        assertThat(CourseType.MAJOR_OPTIONAL.getId()).isEqualTo((byte) 14);

        assertThat(CourseType.getById((byte) 11)).isEqualTo(CourseType.GENERAL_REQUIRED);
        assertThat(CourseType.getById((byte) 12)).isEqualTo(CourseType.GENERAL_OPTIONAL);
        assertThat(CourseType.getById((byte) 13)).isEqualTo(CourseType.MAJOR_REQUIRED);
        assertThat(CourseType.getById((byte) 14)).isEqualTo(CourseType.MAJOR_OPTIONAL);
        assertThat(CourseType.getById((byte) 99)).isNull();
    }
}