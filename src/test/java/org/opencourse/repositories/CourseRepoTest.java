package org.opencourse.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.utils.typeinfo.CourseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link CourseRepo}.
 * 
 * @author !EEExp3rt
 */
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = {CourseRepo.class, DepartmentRepo.class})
@EntityScan(basePackageClasses = {Course.class, Department.class})
class CourseRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepo courseRepo;

    // Data.
    private Department csDepartment;
    private Department mathDepartment;
    private Department liberalArtsDepartment;
    private Course course1;
    private Course course2;
    private Course course3;
    private Course course4;
    private Course course5;

    @BeforeEach
    void setUp() {
        csDepartment = new Department("计算机科学与技术学院");
        mathDepartment = new Department("数学学院");
        liberalArtsDepartment = new Department("文理学院");
        
        entityManager.persistAndFlush(csDepartment);
        entityManager.persistAndFlush(mathDepartment);
        entityManager.persistAndFlush(liberalArtsDepartment);

        course1 = new Course("数据结构", "CS101", csDepartment, CourseType.MAJOR_REQUIRED, new BigDecimal("3.0"));
        course2 = new Course("算法设计与分析", "CS201", csDepartment, CourseType.MAJOR_REQUIRED, new BigDecimal("3.5"));
        course3 = new Course("Web开发技术", "CS301", csDepartment, CourseType.MAJOR_OPTIONAL, new BigDecimal("2.5"));
        course4 = new Course("高等数学", "MATH101", mathDepartment, CourseType.GENERAL_REQUIRED, new BigDecimal("4.0"));
        course5 = new Course("艺术欣赏", "ART101", liberalArtsDepartment, CourseType.GENERAL_OPTIONAL, new BigDecimal("2.0"));

        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);
        entityManager.persistAndFlush(course3);
        entityManager.persistAndFlush(course4);
        entityManager.persistAndFlush(course5);
    }

    @Test
    void contextLoads() {
        assertThat(courseRepo).isNotNull();
        assertThat(entityManager).isNotNull();
    }

    @Test
    void testFindByNameContainingIgnoreCase_WhenMatchingCourses_ShouldReturnOrderedList() {
        // When
        List<Course> results = courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc("数");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Course::getName).containsExactly("数据结构", "高等数学");
    }

    @Test
    void testFindByNameContainingIgnoreCase_WhenNoMatches_ShouldReturnEmptyList() {
        // When
        List<Course> results = courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc("不存在的课程");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testFindByNameContainingIgnoreCase_WhenIgnoreCase_ShouldWork() {
        // When
        List<Course> results1 = courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc("WEB");
        List<Course> results2 = courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc("web");

        // Then
        assertThat(results1).hasSize(1);
        assertThat(results2).hasSize(1);
        assertThat(results1.get(0).getName()).isEqualTo("Web开发技术");
        assertThat(results2.get(0).getName()).isEqualTo("Web开发技术");
    }

    // 测试
    @Test
    void testFindByCode_WhenCourseExists_ShouldReturnCourse() {
        // When
        Optional<Course> found = courseRepo.findByCode("CS101");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("数据结构");
        assertThat(found.get().getCode()).isEqualTo("CS101");
    }

    @Test
    void testFindByCode_WhenCourseNotExists_ShouldReturnEmpty() {
        // When
        Optional<Course> found = courseRepo.findByCode("NOTEXIST");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByCode_CaseSensitive() {
        // When
        Optional<Course> found1 = courseRepo.findByCode("CS101");
        Optional<Course> found2 = courseRepo.findByCode("cs101");

        // Then
        assertThat(found1).isPresent();
        assertThat(found2).isEmpty();
    }

    // 测试 existsByCode
    @Test
    void testExistsByCode_WhenCourseExists_ShouldReturnTrue() {
        // When & Then
        assertThat(courseRepo.existsByCode("CS101")).isTrue();
        assertThat(courseRepo.existsByCode("MATH101")).isTrue();
        assertThat(courseRepo.existsByCode("ART101")).isTrue();
    }

    @Test
    void testExistsByCode_WhenCourseNotExists_ShouldReturnFalse() {
        // When & Then
        assertThat(courseRepo.existsByCode("NOTEXIST")).isFalse();
        assertThat(courseRepo.existsByCode("cs101")).isFalse();
    }

    // 测试 findByDepartmentId
    @Test
    void testFindByDepartmentId_WhenCoursesExist_ShouldReturnList() {
        // When
        List<Course> csCourses = courseRepo.findByDepartmentId(csDepartment.getId());
        List<Course> mathCourses = courseRepo.findByDepartmentId(mathDepartment.getId());
        List<Course> artCourses = courseRepo.findByDepartmentId(liberalArtsDepartment.getId());

        // Then
        assertThat(csCourses).hasSize(3);
        assertThat(csCourses).extracting(Course::getName)
            .containsExactlyInAnyOrder("数据结构", "算法设计与分析", "Web开发技术");

        assertThat(mathCourses).hasSize(1);
        assertThat(mathCourses.get(0).getName()).isEqualTo("高等数学");

        assertThat(artCourses).hasSize(1);
        assertThat(artCourses.get(0).getName()).isEqualTo("艺术欣赏");
    }

    @Test
    void testFindByDepartmentId_WhenNoCourses_ShouldReturnEmptyList() {
        // Given
        Department emptyDept = new Department("空白学院");
        entityManager.persistAndFlush(emptyDept);

        // When
        List<Course> courses = courseRepo.findByDepartmentId(emptyDept.getId());

        // Then
        assertThat(courses).isEmpty();
    }

    @Test
    void testFindByCourseType_WhenCoursesExist_ShouldReturnList() {
        // When
        List<Course> generalRequiredCourses = courseRepo.findByCourseType(CourseType.GENERAL_REQUIRED);
        List<Course> generalOptionalCourses = courseRepo.findByCourseType(CourseType.GENERAL_OPTIONAL);
        List<Course> majorRequiredCourses = courseRepo.findByCourseType(CourseType.MAJOR_REQUIRED);
        List<Course> majorOptionalCourses = courseRepo.findByCourseType(CourseType.MAJOR_OPTIONAL);

        // Then
        assertThat(generalRequiredCourses).hasSize(1);
        assertThat(generalRequiredCourses.get(0).getName()).isEqualTo("高等数学");
        assertThat(generalRequiredCourses.get(0).getCourseType().getDescription()).isEqualTo("通识必修课");

        assertThat(generalOptionalCourses).hasSize(1);
        assertThat(generalOptionalCourses.get(0).getName()).isEqualTo("艺术欣赏");
        assertThat(generalOptionalCourses.get(0).getCourseType().getDescription()).isEqualTo("通识选修课");

        assertThat(majorRequiredCourses).hasSize(2);
        assertThat(majorRequiredCourses).extracting(Course::getName)
            .containsExactlyInAnyOrder("数据结构", "算法设计与分析");

        assertThat(majorOptionalCourses).hasSize(1);
        assertThat(majorOptionalCourses.get(0).getName()).isEqualTo("Web开发技术");
        assertThat(majorOptionalCourses.get(0).getCourseType().getDescription()).isEqualTo("专业选修课");
    }

    @Test
    void testFindByCourseType_WhenNoCoursesOfType_ShouldReturnEmptyList() {
        // Given
        courseRepo.delete(course5);
        entityManager.flush();

        // When
        List<Course> generalOptionalCourses = courseRepo.findByCourseType(CourseType.GENERAL_OPTIONAL);

        // Then
        assertThat(generalOptionalCourses).isEmpty();
    }

    @Test
    void testFindByDepartmentIdAndCourseType_WhenMatching_ShouldReturnList() {
        // When
        List<Course> csMajorRequiredCourses = courseRepo.findByDepartmentIdAndCourseType(
            csDepartment.getId(), CourseType.MAJOR_REQUIRED);
        List<Course> csMajorOptionalCourses = courseRepo.findByDepartmentIdAndCourseType(
            csDepartment.getId(), CourseType.MAJOR_OPTIONAL);
        List<Course> mathGeneralRequiredCourses = courseRepo.findByDepartmentIdAndCourseType(
            mathDepartment.getId(), CourseType.GENERAL_REQUIRED);

        // Then
        assertThat(csMajorRequiredCourses).hasSize(2);
        assertThat(csMajorRequiredCourses).extracting(Course::getName)
            .containsExactlyInAnyOrder("数据结构", "算法设计与分析");

        assertThat(csMajorOptionalCourses).hasSize(1);
        assertThat(csMajorOptionalCourses.get(0).getName()).isEqualTo("Web开发技术");

        assertThat(mathGeneralRequiredCourses).hasSize(1);
        assertThat(mathGeneralRequiredCourses.get(0).getName()).isEqualTo("高等数学");
    }

    @Test
    void testFindByDepartmentIdAndCourseType_WhenNoMatching_ShouldReturnEmptyList() {
        // When
        List<Course> csGeneralRequiredCourses = courseRepo.findByDepartmentIdAndCourseType(
                csDepartment.getId(), CourseType.GENERAL_REQUIRED);

        // Then
        assertThat(csGeneralRequiredCourses).isEmpty();
    }

    @Test
    void testFindAllByOrderByNameAsc_ShouldReturnAllCoursesInAlphabeticalOrder() {
        // When
        List<Course> allCourses = courseRepo.findAllByOrderByNameAsc();

        // Then
        assertThat(allCourses).hasSize(5);
        assertThat(allCourses).extracting(Course::getName)
            .containsExactly("Web开发技术", "数据结构", "算法设计与分析", "艺术欣赏", "高等数学");
    }

    @Test
    void testFindAllByOrderByNameAsc_WhenNoCourses_ShouldReturnEmptyList() {
        // Given
        courseRepo.deleteAll();
        entityManager.flush();

        // When
        List<Course> allCourses = courseRepo.findAllByOrderByNameAsc();

        // Then
        assertThat(allCourses).isEmpty();
    }

    @Test
    void testCourseDataIntegrity() {
        // When
        Optional<Course> foundCourse = courseRepo.findByCode("CS101");

        // Then
        assertThat(foundCourse).isPresent();
        Course course = foundCourse.get();
        
        assertThat(course.getName()).isEqualTo("数据结构");
        assertThat(course.getCode()).isEqualTo("CS101");
        assertThat(course.getDepartment().getName()).isEqualTo("计算机科学与技术学院");
        assertThat(course.getCourseType()).isEqualTo(CourseType.MAJOR_REQUIRED);
        assertThat(course.getCourseType().getDescription()).isEqualTo("专业必修课");
        assertThat(course.getCredits()).isEqualByComparingTo(new BigDecimal("3.0"));
        assertThat(course.getId()).isNotNull();
    }

    @Test
    void testCourseTypeEnumFunctionality() {
        // When
        Optional<Course> mathCourse = courseRepo.findByCode("MATH101");
        Optional<Course> csCourse = courseRepo.findByCode("CS101");
        Optional<Course> artCourse = courseRepo.findByCode("ART101");

        // Then
        assertThat(mathCourse).isPresent();
        assertThat(mathCourse.get().getCourseType().getId()).isEqualTo((byte) 11);
        assertThat(mathCourse.get().getCourseType().getName()).isEqualTo("General-Required");
        assertThat(mathCourse.get().getCourseType().getDescription()).isEqualTo("通识必修课");

        assertThat(csCourse).isPresent();
        assertThat(csCourse.get().getCourseType().getId()).isEqualTo((byte) 13);
        assertThat(csCourse.get().getCourseType().getName()).isEqualTo("Major-Required");
        assertThat(csCourse.get().getCourseType().getDescription()).isEqualTo("专业必修课");

        assertThat(artCourse).isPresent();
        assertThat(artCourse.get().getCourseType().getId()).isEqualTo((byte) 12);
        assertThat(artCourse.get().getCourseType().getName()).isEqualTo("General-Optional");
        assertThat(artCourse.get().getCourseType().getDescription()).isEqualTo("通识选修课");
    }

    @Test
    void testCourseTypeGetById() {
        // When & Then
        assertThat(CourseType.getById((byte) 11)).isEqualTo(CourseType.GENERAL_REQUIRED);
        assertThat(CourseType.getById((byte) 12)).isEqualTo(CourseType.GENERAL_OPTIONAL);
        assertThat(CourseType.getById((byte) 13)).isEqualTo(CourseType.MAJOR_REQUIRED);
        assertThat(CourseType.getById((byte) 14)).isEqualTo(CourseType.MAJOR_OPTIONAL);
        assertThat(CourseType.getById((byte) 99)).isNull();
    }

    @Test
    void testDepartmentRelationship() {
        // When
        List<Course> csCourses = courseRepo.findByDepartmentId(csDepartment.getId());

        // Then
        assertThat(csCourses).hasSize(3);
        assertThat(csCourses).allMatch(course -> 
            course.getDepartment().getId().equals(csDepartment.getId()));
        assertThat(csCourses).allMatch(course -> 
            course.getDepartment().getName().equals("计算机科学与技术学院"));
    }

    @Test
    void testCreditsDecimalPrecision() {
        // Given
        Course precisionCourse = new Course(
            "精度测试课程", 
            "TEST001", 
            csDepartment, 
            CourseType.MAJOR_OPTIONAL, 
            new BigDecimal("2.5")
        );
        
        // When
        courseRepo.save(precisionCourse);
        Optional<Course> found = courseRepo.findByCode("TEST001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCredits()).isEqualByComparingTo(new BigDecimal("2.5"));
        assertThat(found.get().getCredits().scale()).isEqualTo(1);
    }

    @Test
    void testCourseToString() {
        // When
        Optional<Course> found = courseRepo.findByCode("CS101");

        // Then
        assertThat(found).isPresent();
        String courseString = found.get().toString();
        
        assertThat(courseString).contains("Course{");
        assertThat(courseString).contains("code='CS101'");
        assertThat(courseString).contains("name='数据结构'");
        assertThat(courseString).contains("credits=3.0");
        assertThat(courseString).contains("courseType=MAJOR_REQUIRED");
    }

    @Test
    void testEdgeCases() {
        List<Course> emptySearch = courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc("");
        assertThat(emptySearch).hasSize(5);

        List<Course> singleChar = courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc("1");
        assertThat(singleChar).isEmpty();

        List<Course> specialChar = courseRepo.findByNameContainingIgnoreCaseOrderByNameAsc("与");
        assertThat(specialChar).hasSize(1);
    }

    @Test
    void testCourseTypeDistribution() {
        // When
        List<Course> allCourses = courseRepo.findAll();

        // Then
        long generalRequiredCount = allCourses.stream()
            .filter(course -> course.getCourseType() == CourseType.GENERAL_REQUIRED)
            .count();
        long generalOptionalCount = allCourses.stream()
            .filter(course -> course.getCourseType() == CourseType.GENERAL_OPTIONAL)
            .count();
        long majorRequiredCount = allCourses.stream()
            .filter(course -> course.getCourseType() == CourseType.MAJOR_REQUIRED)
            .count();
        long majorOptionalCount = allCourses.stream()
            .filter(course -> course.getCourseType() == CourseType.MAJOR_OPTIONAL)
            .count();

        assertThat(generalRequiredCount).isEqualTo(1);
        assertThat(generalOptionalCount).isEqualTo(1);
        assertThat(majorRequiredCount).isEqualTo(2);
        assertThat(majorOptionalCount).isEqualTo(1);
    }
}
