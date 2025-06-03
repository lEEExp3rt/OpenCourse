package org.opencourse.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.opencourse.models.Resource;
import org.opencourse.models.User;
import org.opencourse.models.Department;
import org.opencourse.models.Course;
import org.opencourse.utils.typeinfo.ResourceType;
import org.opencourse.utils.typeinfo.CourseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link ResourceRepo}.
 *
 * @author !EEExp3rt
 */
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = {ResourceRepo.class, UserRepo.class, DepartmentRepo.class, CourseRepo.class})
@EntityScan(basePackageClasses = {Resource.class, User.class, Department.class, Course.class})
class ResourceRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ResourceRepo resourceRepo;

    // Test data
    private User teacher1;
    private User teacher2;
    private User student1;
    private Department department1;
    private Department department2;
    private Course course1;
    private Course course2;
    private Resource resource1;
    private Resource resource2;
    private Resource resource3;
    private Resource resource4;
    private Resource resource5;

    @BeforeEach
    void setUp() {
        // Create test departments
        department1 = new Department("Computer Science Department");
        department2 = new Department("Mathematics Department");
        entityManager.persistAndFlush(department1);
        entityManager.persistAndFlush(department2);

        // Create test users
        teacher1 = new User("teacher1", "teacher1@example.com", "hashedPassword123", User.UserRole.USER);
        teacher2 = new User("teacher2", "teacher2@example.com", "hashedPassword456", User.UserRole.USER);
        student1 = new User("student1", "student1@example.com", "hashedPassword789", User.UserRole.USER);

        entityManager.persistAndFlush(teacher1);
        entityManager.persistAndFlush(teacher2);
        entityManager.persistAndFlush(student1);

        // Create test courses
        course1 = new Course("Data Structures", "CS101", department1, CourseType.MAJOR_REQUIRED, new BigDecimal("3.0"));
        course2 = new Course("Linear Algebra", "MATH201", department2, CourseType.GENERAL_REQUIRED, new BigDecimal("4.0"));

        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);

        // Create test resource files
        Resource.ResourceFile pdfFile1 = new Resource.ResourceFile(
            Resource.ResourceFile.FileType.PDF,
            new BigDecimal("2.50"),
            "/uploads/cs101/exam_2023.pdf"
        );
        Resource.ResourceFile pdfFile2 = new Resource.ResourceFile(
            Resource.ResourceFile.FileType.PDF,
            new BigDecimal("1.20"),
            "/uploads/cs101/slides_chapter1.pdf"
        );
        Resource.ResourceFile textFile = new Resource.ResourceFile(
            Resource.ResourceFile.FileType.TEXT,
            new BigDecimal("0.05"),
            "/uploads/cs101/notes.txt"
        );
        Resource.ResourceFile otherFile = new Resource.ResourceFile(
            Resource.ResourceFile.FileType.OTHER,
            new BigDecimal("5.00"),
            "/uploads/math201/assignment.zip"
        );

        // Create test resources
        resource1 = new Resource(
            "Data Structures Final Exam 2023",
            "Final exam paper for Data Structures course",
            ResourceType.EXAM,
            pdfFile1,
            course1,
            teacher1
        );

        resource2 = new Resource(
            "Chapter 1 Slides",
            ResourceType.SLIDES,
            pdfFile2,
            course1,
            teacher1
        );

        resource3 = new Resource(
            "Study Notes",
            "Personal study notes for algorithms",
            ResourceType.NOTE,
            textFile,
            course1,
            student1
        );

        resource4 = new Resource(
            "Linear Algebra Assignment",
            ResourceType.ASSIGNMENT,
            otherFile,
            course2,
            teacher2
        );

        resource5 = new Resource(
            "Textbook Chapter 3",
            "Essential reading material",
            ResourceType.TEXTBOOK,
            new Resource.ResourceFile(Resource.ResourceFile.FileType.PDF, new BigDecimal("3.75"), "/uploads/textbook.pdf"),
            course2,
            teacher2
        );

        // Save test resources
        entityManager.persistAndFlush(resource1);
        entityManager.persistAndFlush(resource2);
        entityManager.persistAndFlush(resource3);
        entityManager.persistAndFlush(resource4);
        entityManager.persistAndFlush(resource5);
    }

    @Test
    void contextLoads() {
        assertThat(resourceRepo).isNotNull();
        assertThat(entityManager).isNotNull();
    }

    @Test
    void testFindByCourseId_WhenCourseHasResources_ShouldReturnAllResources() {
        // When
        List<Resource> course1Resources = resourceRepo.findByCourseId(course1.getId());
        List<Resource> course2Resources = resourceRepo.findByCourseId(course2.getId());

        // Then
        assertThat(course1Resources).hasSize(3);
        assertThat(course1Resources).allMatch(resource ->
            resource.getCourse().getId().equals(course1.getId()));
        assertThat(course1Resources).extracting(Resource::getName)
            .containsExactlyInAnyOrder("Data Structures Final Exam 2023", "Chapter 1 Slides", "Study Notes");

        assertThat(course2Resources).hasSize(2);
        assertThat(course2Resources).allMatch(resource ->
            resource.getCourse().getId().equals(course2.getId()));
        assertThat(course2Resources).extracting(Resource::getName)
            .containsExactlyInAnyOrder("Linear Algebra Assignment", "Textbook Chapter 3");
    }

    @Test
    void testFindByCourseId_WhenCourseHasNoResources_ShouldReturnEmptyList() {
        // Given - Create a course without resources
        Course emptyCourse = new Course("Empty Course", "EMPTY001", department1, CourseType.GENERAL_OPTIONAL, new BigDecimal("1.0"));
        entityManager.persistAndFlush(emptyCourse);

        // When
        List<Resource> resources = resourceRepo.findByCourseId(emptyCourse.getId());

        // Then
        assertThat(resources).isEmpty();
    }

    @Test
    void testFindByCourseId_WhenCourseIdNotExists_ShouldReturnEmptyList() {
        // When
        List<Resource> resources = resourceRepo.findByCourseId((short) 9999);

        // Then
        assertThat(resources).isEmpty();
    }

    @Test
    void testFindByCourseId_VerifyResourceTypes() {
        // When
        List<Resource> course1Resources = resourceRepo.findByCourseId(course1.getId());

        // Then - Verify different resource types for course1
        long examCount = course1Resources.stream()
            .filter(resource -> resource.getResourceType() == ResourceType.EXAM)
            .count();
        long slidesCount = course1Resources.stream()
            .filter(resource -> resource.getResourceType() == ResourceType.SLIDES)
            .count();
        long noteCount = course1Resources.stream()
            .filter(resource -> resource.getResourceType() == ResourceType.NOTE)
            .count();

        assertThat(examCount).isEqualTo(1);
        assertThat(slidesCount).isEqualTo(1);
        assertThat(noteCount).isEqualTo(1);
    }

    @Test
    void testFindByUserId_WhenUserHasResources_ShouldReturnAllResources() {
        // When
        List<Resource> teacher1Resources = resourceRepo.findByUserId(teacher1.getId());
        List<Resource> teacher2Resources = resourceRepo.findByUserId(teacher2.getId());
        List<Resource> student1Resources = resourceRepo.findByUserId(student1.getId());

        // Then
        assertThat(teacher1Resources).hasSize(2);
        assertThat(teacher1Resources).allMatch(resource ->
            resource.getUser().getId().equals(teacher1.getId()));
        assertThat(teacher1Resources).extracting(Resource::getName)
            .containsExactlyInAnyOrder("Data Structures Final Exam 2023", "Chapter 1 Slides");

        assertThat(teacher2Resources).hasSize(2);
        assertThat(teacher2Resources).allMatch(resource ->
            resource.getUser().getId().equals(teacher2.getId()));
        assertThat(teacher2Resources).extracting(Resource::getName)
            .containsExactlyInAnyOrder("Linear Algebra Assignment", "Textbook Chapter 3");

        assertThat(student1Resources).hasSize(1);
        assertThat(student1Resources.get(0).getUser().getId()).isEqualTo(student1.getId());
        assertThat(student1Resources.get(0).getName()).isEqualTo("Study Notes");
    }

    @Test
    void testFindByUserId_WhenUserHasNoResources_ShouldReturnEmptyList() {
        // Given - Create a user without resources
        User newUser = new User("newuser", "new@example.com", "password", User.UserRole.VISITOR);
        entityManager.persistAndFlush(newUser);

        // When
        List<Resource> resources = resourceRepo.findByUserId(newUser.getId());

        // Then
        assertThat(resources).isEmpty();
    }

    @Test
    void testFindByUserId_WhenUserIdNotExists_ShouldReturnEmptyList() {
        // When
        List<Resource> resources = resourceRepo.findByUserId(99999);

        // Then
        assertThat(resources).isEmpty();
    }

    @Test
    void testFindByUserId_VerifyResourceTypes() {
        // When
        List<Resource> teacher2Resources = resourceRepo.findByUserId(teacher2.getId());

        // Then - Verify different resource types for teacher2
        long assignmentCount = teacher2Resources.stream()
            .filter(resource -> resource.getResourceType() == ResourceType.ASSIGNMENT)
            .count();
        long textbookCount = teacher2Resources.stream()
            .filter(resource -> resource.getResourceType() == ResourceType.TEXTBOOK)
            .count();

        assertThat(assignmentCount).isEqualTo(1);
        assertThat(textbookCount).isEqualTo(1);
    }

    @Test
    void testResourceEntityBasicFunctionality() {
        // When
        Optional<Resource> foundResource = resourceRepo.findById(resource1.getId());

        // Then
        assertThat(foundResource).isPresent();
        Resource resource = foundResource.get();

        assertThat(resource.getId()).isNotNull();
        assertThat(resource.getName()).isEqualTo("Data Structures Final Exam 2023");
        assertThat(resource.getDescription()).isEqualTo("Final exam paper for Data Structures course");
        assertThat(resource.getResourceType()).isEqualTo(ResourceType.EXAM);
        assertThat(resource.getCourse()).isEqualTo(course1);
        assertThat(resource.getUser()).isEqualTo(teacher1);
        assertThat(resource.getViews()).isEqualTo(0);
        assertThat(resource.getLikes()).isEqualTo(0);
        assertThat(resource.getDislikes()).isEqualTo(0);
        assertThat(resource.getCreatedAt()).isNotNull();
    }

    @Test
    void testResourceWithoutDescription() {
        // When - Find resource created without description
        Optional<Resource> found = resourceRepo.findById(resource2.getId());

        // Then
        assertThat(found).isPresent();
        Resource resource = found.get();

        assertThat(resource.getName()).isEqualTo("Chapter 1 Slides");
        assertThat(resource.getDescription()).isNull();
        assertThat(resource.getResourceType()).isEqualTo(ResourceType.SLIDES);
    }

    @Test
    void testResourceFileEmbeddedFunctionality() {
        // When
        Optional<Resource> foundResource = resourceRepo.findById(resource1.getId());

        // Then
        assertThat(foundResource).isPresent();
        Resource resource = foundResource.get();
        Resource.ResourceFile resourceFile = resource.getResourceFile();

        assertThat(resourceFile).isNotNull();
        assertThat(resourceFile.getFileType()).isEqualTo(Resource.ResourceFile.FileType.PDF);
        assertThat(resourceFile.getFileSize()).isEqualByComparingTo(new BigDecimal("2.50"));
        assertThat(resourceFile.getFilePath()).isEqualTo("/uploads/cs101/exam_2023.pdf");
    }

    @Test
    void testResourceFileTypeEnum() {
        // When
        List<Resource> allResources = resourceRepo.findAll();

        // Then - Verify different file types
        long pdfCount = allResources.stream()
            .filter(resource -> resource.getResourceFile().getFileType() == Resource.ResourceFile.FileType.PDF)
            .count();
        long textCount = allResources.stream()
            .filter(resource -> resource.getResourceFile().getFileType() == Resource.ResourceFile.FileType.TEXT)
            .count();
        long otherCount = allResources.stream()
            .filter(resource -> resource.getResourceFile().getFileType() == Resource.ResourceFile.FileType.OTHER)
            .count();

        assertThat(pdfCount).isEqualTo(3); // resource1, resource2, resource5
        assertThat(textCount).isEqualTo(1); // resource3
        assertThat(otherCount).isEqualTo(1); // resource4
    }

    @Test
    void testResourceFileTypeFromMethod() {
        // When & Then - Test FileType.from() method
        assertThat(Resource.ResourceFile.FileType.from("pdf")).isEqualTo(Resource.ResourceFile.FileType.PDF);
        assertThat(Resource.ResourceFile.FileType.from("PDF")).isEqualTo(Resource.ResourceFile.FileType.PDF);
        assertThat(Resource.ResourceFile.FileType.from("text")).isEqualTo(Resource.ResourceFile.FileType.TEXT);
        assertThat(Resource.ResourceFile.FileType.from("TEXT")).isEqualTo(Resource.ResourceFile.FileType.TEXT);
        assertThat(Resource.ResourceFile.FileType.from("unknown")).isEqualTo(Resource.ResourceFile.FileType.OTHER);
        assertThat(Resource.ResourceFile.FileType.from("")).isEqualTo(Resource.ResourceFile.FileType.OTHER);
    }

    @Test
    void testResourceTypeEnumFunctionality() {
        // When
        List<Resource> allResources = resourceRepo.findAll();

        // Then - Verify ResourceType in database storage and retrieval
        Resource examResource = allResources.stream()
            .filter(resource -> resource.getResourceType() == ResourceType.EXAM)
            .findFirst()
            .orElse(null);

        assertThat(examResource).isNotNull();
        assertThat(examResource.getResourceType().getId()).isEqualTo((byte) 51);
        assertThat(examResource.getResourceType().getName()).isEqualTo("Exam");
        assertThat(examResource.getResourceType().getDescription()).isEqualTo("历年卷");

        Resource slidesResource = allResources.stream()
            .filter(resource -> resource.getResourceType() == ResourceType.SLIDES)
            .findFirst()
            .orElse(null);

        assertThat(slidesResource).isNotNull();
        assertThat(slidesResource.getResourceType().getId()).isEqualTo((byte) 55);
        assertThat(slidesResource.getResourceType().getName()).isEqualTo("Slides");
        assertThat(slidesResource.getResourceType().getDescription()).isEqualTo("课件");
    }

    @Test
    void testResourceTypeGetById() {
        // When & Then
        assertThat(ResourceType.getById((byte) 51)).isEqualTo(ResourceType.EXAM);
        assertThat(ResourceType.getById((byte) 52)).isEqualTo(ResourceType.ASSIGNMENT);
        assertThat(ResourceType.getById((byte) 53)).isEqualTo(ResourceType.NOTE);
        assertThat(ResourceType.getById((byte) 54)).isEqualTo(ResourceType.TEXTBOOK);
        assertThat(ResourceType.getById((byte) 55)).isEqualTo(ResourceType.SLIDES);
        assertThat(ResourceType.getById((byte) 56)).isEqualTo(ResourceType.OTHER);
        assertThat(ResourceType.getById((byte) 99)).isNull(); // Non-existent ID
    }

    @Test
    void testResourceLikesAndDislikes() {
        // Given
        Resource resource = resource1;

        // When - Test likes
        resource.likes();
        resource.likes();
        resourceRepo.save(resource);

        // Then
        Optional<Resource> found = resourceRepo.findById(resource.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getLikes()).isEqualTo(2);

        // When - Test unlikes
        found.get().unlikes();
        resourceRepo.save(found.get());

        // Then
        Optional<Resource> updated = resourceRepo.findById(resource.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getLikes()).isEqualTo(1);

        // When - Test dislikes
        updated.get().dislikes();
        updated.get().dislikes();
        resourceRepo.save(updated.get());

        // Then
        Optional<Resource> finalResource = resourceRepo.findById(resource.getId());
        assertThat(finalResource).isPresent();
        assertThat(finalResource.get().getDislikes()).isEqualTo(2);

        // When - Test undislikes
        finalResource.get().undislikes();
        resourceRepo.save(finalResource.get());

        // Then
        Optional<Resource> result = resourceRepo.findById(resource.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getDislikes()).isEqualTo(1);
    }

    @Test
    void testTimestampAutoGeneration() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now();
        Resource.ResourceFile newFile = new Resource.ResourceFile(
            Resource.ResourceFile.FileType.PDF,
            new BigDecimal("1.00"),
            "/uploads/test.pdf"
        );
        Resource newResource = new Resource("Test Resource", ResourceType.OTHER, newFile, course1, teacher1);

        // When
        Resource saved = resourceRepo.save(newResource);
        LocalDateTime afterSave = LocalDateTime.now();

        // Then
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isAfter(beforeSave.minusSeconds(1)); // Allow 1 second margin
        assertThat(saved.getCreatedAt()).isBefore(afterSave.plusSeconds(1));  // Allow 1 second margin
    }

    @Test
    void testResourceToString() {
        // When
        String resourceString = resource1.toString();

        // Then
        assertThat(resourceString).contains("Resource{");
        assertThat(resourceString).contains("id=" + resource1.getId());
        assertThat(resourceString).contains("name='Data Structures Final Exam 2023'");
        assertThat(resourceString).contains("description='Final exam paper for Data Structures course'");
        assertThat(resourceString).contains("resourceType=" + ResourceType.EXAM);
        assertThat(resourceString).contains("course=" + course1);
        assertThat(resourceString).contains("user=" + teacher1);
        assertThat(resourceString).contains("views=0");
        assertThat(resourceString).contains("likes=0");
        assertThat(resourceString).contains("dislikes=0");
        assertThat(resourceString).contains("createdAt=");
    }

    @Test
    void testCourseRelationship() {
        // When
        List<Resource> course1Resources = resourceRepo.findByCourseId(course1.getId());

        // Then
        assertThat(course1Resources).hasSize(3);
        assertThat(course1Resources).allMatch(resource -> {
            Course resourceCourse = resource.getCourse();
            return resourceCourse.getId().equals(course1.getId()) &&
                   resourceCourse.getName().equals("Data Structures") &&
                   resourceCourse.getCode().equals("CS101") &&
                   resourceCourse.getCourseType().equals(CourseType.MAJOR_REQUIRED);
        });
    }

    @Test
    void testUserRelationship() {
        // When
        List<Resource> teacher1Resources = resourceRepo.findByUserId(teacher1.getId());

        // Then
        assertThat(teacher1Resources).hasSize(2);
        assertThat(teacher1Resources).allMatch(resource -> {
            User resourceUser = resource.getUser();
            return resourceUser.getId().equals(teacher1.getId()) &&
                   resourceUser.getName().equals("teacher1") &&
                   resourceUser.getEmail().equals("teacher1@example.com") &&
                   resourceUser.getRole().equals(User.UserRole.USER);
        });
    }

    @Test
    void testResourceViews() {
        // Given
        Resource resource = resource1;

        // When - Set views
        resource.setViews(100);
        resourceRepo.save(resource);

        // Then
        Optional<Resource> found = resourceRepo.findById(resource.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getViews()).isEqualTo(100);

        // When - Increment views
        found.get().setViews(found.get().getViews() + 1);
        resourceRepo.save(found.get());

        // Then
        Optional<Resource> updated = resourceRepo.findById(resource.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getViews()).isEqualTo(101);
    }

    @Test
    void testComplexQueryScenarios() {
        // Scenario 1: Find all PDF resources across all courses
        List<Resource> allResources = resourceRepo.findAll();
        long pdfResourcesCount = allResources.stream()
            .filter(resource -> resource.getResourceFile().getFileType() == Resource.ResourceFile.FileType.PDF)
            .count();
        assertThat(pdfResourcesCount).isEqualTo(3);

        // Scenario 2: Find resources by teacher1 for course1
        List<Resource> teacher1Course1Resources = resourceRepo.findByCourseId(course1.getId()).stream()
            .filter(resource -> resource.getUser().getId().equals(teacher1.getId()))
            .toList();
        assertThat(teacher1Course1Resources).hasSize(2);

        // Scenario 3: Find all exam type resources
        long examResourcesCount = allResources.stream()
            .filter(resource -> resource.getResourceType() == ResourceType.EXAM)
            .count();
        assertThat(examResourcesCount).isEqualTo(1);

        // Scenario 4: Find resources with description
        long resourcesWithDescription = allResources.stream()
            .filter(resource -> resource.getDescription() != null && !resource.getDescription().isEmpty())
            .count();
        assertThat(resourcesWithDescription).isEqualTo(3); // resource1, resource3, resource4, resource5
    }

    @Test
    void testFileSizePrecision() {
        // When
        List<Resource> allResources = resourceRepo.findAll();

        // Then - Verify BigDecimal precision for file sizes
        Resource largestResource = allResources.stream()
            .max((r1, r2) -> r1.getResourceFile().getFileSize().compareTo(r2.getResourceFile().getFileSize()))
            .orElse(null);

        assertThat(largestResource).isNotNull();
        assertThat(largestResource.getResourceFile().getFileSize()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(largestResource.getResourceFile().getFileSize().scale()).isEqualTo(2);

        Resource smallestResource = allResources.stream()
            .min((r1, r2) -> r1.getResourceFile().getFileSize().compareTo(r2.getResourceFile().getFileSize()))
            .orElse(null);

        assertThat(smallestResource).isNotNull();
        assertThat(smallestResource.getResourceFile().getFileSize()).isEqualByComparingTo(new BigDecimal("0.05"));
        assertThat(smallestResource.getResourceFile().getFileSize().scale()).isEqualTo(2);
    }

    @Test
    void testEdgeCases() {
        // Test resource with minimal required fields
        Resource.ResourceFile minimalFile = new Resource.ResourceFile(
            Resource.ResourceFile.FileType.OTHER,
            new BigDecimal("0.01"),
            "/minimal.txt"
        );
        Resource minimalResource = new Resource("Minimal", ResourceType.OTHER, minimalFile, course1, student1);

        Resource saved = resourceRepo.save(minimalResource);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isNull();
        assertThat(saved.getViews()).isEqualTo(0);
        assertThat(saved.getLikes()).isEqualTo(0);
        assertThat(saved.getDislikes()).isEqualTo(0);

        // Test resource with maximum file size
        Resource.ResourceFile maxSizeFile = new Resource.ResourceFile(
            Resource.ResourceFile.FileType.PDF,
            new BigDecimal("9.99"), // Maximum for precision 3, scale 2
            "/large_file.pdf"
        );
        Resource maxSizeResource = new Resource("Large File", ResourceType.TEXTBOOK, maxSizeFile, course2, teacher2);

        Resource savedMax = resourceRepo.save(maxSizeResource);
        assertThat(savedMax.getResourceFile().getFileSize()).isEqualByComparingTo(new BigDecimal("9.99"));
    }

    @Test
    void testDataConsistency() {
        // When
        List<Resource> allResources = resourceRepo.findAll();

        // Then
        assertThat(allResources).hasSize(5);

        // Verify all resources have valid course associations
        assertThat(allResources).allMatch(resource -> resource.getCourse() != null);
        assertThat(allResources).allMatch(resource -> resource.getCourse().getId() != null);

        // Verify all resources have valid user associations
        assertThat(allResources).allMatch(resource -> resource.getUser() != null);
        assertThat(allResources).allMatch(resource -> resource.getUser().getId() != null);

        // Verify all resources have valid file associations
        assertThat(allResources).allMatch(resource -> resource.getResourceFile() != null);
        assertThat(allResources).allMatch(resource -> resource.getResourceFile().getFileType() != null);
        assertThat(allResources).allMatch(resource -> resource.getResourceFile().getFileSize() != null);
        assertThat(allResources).allMatch(resource -> resource.getResourceFile().getFilePath() != null);

        // Verify all resources have creation timestamps
        assertThat(allResources).allMatch(resource -> resource.getCreatedAt() != null);

        // Verify all resources have non-negative stats
        assertThat(allResources).allMatch(resource -> resource.getViews() >= 0);
        assertThat(allResources).allMatch(resource -> resource.getLikes() >= 0);
        assertThat(allResources).allMatch(resource -> resource.getDislikes() >= 0);

        // Verify resource type distribution
        long examCount = allResources.stream().filter(r -> r.getResourceType() == ResourceType.EXAM).count();
        long slidesCount = allResources.stream().filter(r -> r.getResourceType() == ResourceType.SLIDES).count();
        long noteCount = allResources.stream().filter(r -> r.getResourceType() == ResourceType.NOTE).count();
        long assignmentCount = allResources.stream().filter(r -> r.getResourceType() == ResourceType.ASSIGNMENT).count();
        long textbookCount = allResources.stream().filter(r -> r.getResourceType() == ResourceType.TEXTBOOK).count();

        assertThat(examCount).isEqualTo(1);
        assertThat(slidesCount).isEqualTo(1);
        assertThat(noteCount).isEqualTo(1);
        assertThat(assignmentCount).isEqualTo(1);
        assertThat(textbookCount).isEqualTo(1);
    }

    @Test
    void testBulkOperations() {
        // Given - Create multiple resources for bulk testing
        Resource.ResourceFile file1 = new Resource.ResourceFile(Resource.ResourceFile.FileType.PDF, new BigDecimal("1.00"), "/bulk1.pdf");
        Resource.ResourceFile file2 = new Resource.ResourceFile(Resource.ResourceFile.FileType.TEXT, new BigDecimal("0.10"), "/bulk2.txt");

        List<Resource> bulkResources = List.of(
            new Resource("Bulk Resource 1", ResourceType.NOTE, file1, course1, student1),
            new Resource("Bulk Resource 2", "Bulk description", ResourceType.OTHER, file2, course2, teacher1)
        );

        // When
        List<Resource> savedResources = resourceRepo.saveAll(bulkResources);

        // Then
        assertThat(savedResources).hasSize(2);
        assertThat(savedResources).allMatch(resource -> resource.getId() != null);

        // Verify total count
        long totalResources = resourceRepo.count();
        assertThat(totalResources).isEqualTo(7); // 5 original + 2 bulk

        // Verify bulk resources can be found
        List<Resource> student1Resources = resourceRepo.findByUserId(student1.getId());
        assertThat(student1Resources).hasSize(2); // original + bulk
    }
}
