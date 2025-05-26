package org.opencourse.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.opencourse.models.Interaction;
import org.opencourse.models.User;
import org.opencourse.models.Department;
import org.opencourse.models.Course;
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
 * Test class for {@link InteractionRepo}.
 * 
 * @author !EEExp3rt
 */
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = {InteractionRepo.class, UserRepo.class, DepartmentRepo.class, CourseRepo.class})
@EntityScan(basePackageClasses = {Interaction.class, User.class, Department.class, Course.class})
class InteractionRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InteractionRepo interactionRepo;

    // Test data
    private User user1;
    private User user2;
    private User user3;
    private Department department;
    private Course course1;
    private Course course2;
    private Interaction interaction1;
    private Interaction interaction2;
    private Interaction interaction3;
    private Interaction interaction4;
    private Interaction interaction5;

    @BeforeEach
    void setUp() {
        // Create test department.
        department = new Department("Computer Science Department");
        entityManager.persistAndFlush(department);

        // Create test users.
        user1 = new User("testuser1", "test1@example.com", "hashedPassword123", User.UserRole.USER);
        user2 = new User("testuser2", "test2@example.com", "hashedPassword456", User.UserRole.USER);
        user3 = new User("admin", "admin@example.com", "hashedPassword789", User.UserRole.ADMIN);
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        // Create test courses.
        course1 = new Course("Data Structures", "CS101", department, CourseType.MAJOR_REQUIRED, new BigDecimal("3.0"));
        course2 = new Course("Web Development", "CS301", department, CourseType.MAJOR_OPTIONAL, new BigDecimal("2.5"));
        
        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);

        // Create test interactions.
        interaction1 = new Interaction(course1, user1, "Great course! Very helpful for understanding algorithms.", (byte) 5);
        interaction2 = new Interaction(course1, user2, (byte) 4); // Rating only
        interaction3 = new Interaction(course1, user3, "Excellent teaching methodology."); // Comment only
        interaction4 = new Interaction(course2, user1, "Good practical experience.", (byte) 4);
        interaction5 = new Interaction(course2, user2, "Could be more challenging.", (byte) 3);

        // Manually set different creation timestamps for testing.
        LocalDateTime baseTime = LocalDateTime.now().minusDays(5);
        
        entityManager.persist(interaction1);
        entityManager.flush();
        interaction1.setCreatedAt(baseTime.plusDays(1));
        entityManager.merge(interaction1);
        
        entityManager.persist(interaction2);
        entityManager.flush();
        interaction2.setCreatedAt(baseTime.plusDays(2));
        entityManager.merge(interaction2);
        
        entityManager.persist(interaction3);
        entityManager.flush();
        interaction3.setCreatedAt(baseTime.plusDays(3));
        entityManager.merge(interaction3);
        
        entityManager.persist(interaction4);
        entityManager.flush();
        interaction4.setCreatedAt(baseTime.plusDays(4));
        entityManager.merge(interaction4);
        
        entityManager.persist(interaction5);
        entityManager.flush();
        interaction5.setCreatedAt(baseTime.plusDays(5));
        entityManager.merge(interaction5);

        entityManager.flush();
    }

    @Test
    void contextLoads() {
        assertThat(interactionRepo).isNotNull();
        assertThat(entityManager).isNotNull();
    }

    @Test
    void testFindAllByCourse_WhenCourseHasInteractions_ShouldReturnAllInteractions() {
        // When
        List<Interaction> course1Interactions = interactionRepo.findAllByCourse(course1);
        List<Interaction> course2Interactions = interactionRepo.findAllByCourse(course2);

        // Then
        assertThat(course1Interactions).hasSize(3);
        assertThat(course1Interactions).allMatch(interaction -> 
            interaction.getCourse().getId().equals(course1.getId()));
        
        assertThat(course2Interactions).hasSize(2);
        assertThat(course2Interactions).allMatch(interaction -> 
            interaction.getCourse().getId().equals(course2.getId()));
    }

    @Test
    void testFindAllByCourse_WhenCourseHasNoInteractions_ShouldReturnEmptyList() {
        // Given - Create a course without interactions
        Course emptyCourse = new Course("Empty Course", "EMPTY001", department, CourseType.GENERAL_OPTIONAL, new BigDecimal("1.0"));
        entityManager.persistAndFlush(emptyCourse);

        // When
        List<Interaction> interactions = interactionRepo.findAllByCourse(emptyCourse);

        // Then
        assertThat(interactions).isEmpty();
    }

    @Test
    void testFindAllByCourseId_WhenCourseHasInteractions_ShouldReturnAllInteractions() {
        // When
        List<Interaction> course1Interactions = interactionRepo.findAllByCourseId(course1.getId());
        List<Interaction> course2Interactions = interactionRepo.findAllByCourseId(course2.getId());

        // Then
        assertThat(course1Interactions).hasSize(3);
        assertThat(course1Interactions).allMatch(interaction -> 
            interaction.getCourse().getId().equals(course1.getId()));
        
        assertThat(course2Interactions).hasSize(2);
        assertThat(course2Interactions).allMatch(interaction -> 
            interaction.getCourse().getId().equals(course2.getId()));
    }

    @Test
    void testFindAllByCourseId_WhenCourseIdNotExists_ShouldReturnEmptyList() {
        // When
        List<Interaction> interactions = interactionRepo.findAllByCourseId((short) 9999);

        // Then
        assertThat(interactions).isEmpty();
    }

    @Test
    void testFindAllByCourseId_CompareWithFindAllByCourse() {
        // When
        List<Interaction> interactionsByCourse = interactionRepo.findAllByCourse(course1);
        List<Interaction> interactionsByCourseId = interactionRepo.findAllByCourseId(course1.getId());

        // Then - Both methods should return the same results
        assertThat(interactionsByCourse).hasSize(interactionsByCourseId.size());
        assertThat(interactionsByCourse).containsExactlyInAnyOrderElementsOf(interactionsByCourseId);
    }

    @Test
    void testFindByUser_WhenUserHasInteractions_ShouldReturnAllUserInteractions() {
        // When
        List<Interaction> user1Interactions = interactionRepo.findByUser(user1);
        List<Interaction> user2Interactions = interactionRepo.findByUser(user2);
        List<Interaction> user3Interactions = interactionRepo.findByUser(user3);

        // Then
        assertThat(user1Interactions).hasSize(2);
        assertThat(user1Interactions).allMatch(interaction -> 
            interaction.getUser().getId().equals(user1.getId()));
        
        assertThat(user2Interactions).hasSize(2);
        assertThat(user2Interactions).allMatch(interaction -> 
            interaction.getUser().getId().equals(user2.getId()));
            
        assertThat(user3Interactions).hasSize(1);
        assertThat(user3Interactions.get(0).getUser().getId()).isEqualTo(user3.getId());
    }

    @Test
    void testFindByUser_WhenUserHasNoInteractions_ShouldReturnEmptyList() {
        // Given - Create a user without interactions
        User newUser = new User("newuser", "new@example.com", "password", User.UserRole.VISITOR);
        entityManager.persistAndFlush(newUser);

        // When
        List<Interaction> interactions = interactionRepo.findByUser(newUser);

        // Then
        assertThat(interactions).isEmpty();
    }

    @Test
    void testFindByCourseAndUser_WhenInteractionExists_ShouldReturnInteraction() {
        // When
        Optional<Interaction> found1 = interactionRepo.findByCourseAndUser(course1, user1);
        Optional<Interaction> found2 = interactionRepo.findByCourseAndUser(course2, user2);

        // Then
        assertThat(found1).isPresent();
        assertThat(found1.get().getCourse().getId()).isEqualTo(course1.getId());
        assertThat(found1.get().getUser().getId()).isEqualTo(user1.getId());
        assertThat(found1.get().getContent()).isEqualTo("Great course! Very helpful for understanding algorithms.");
        assertThat(found1.get().getRating()).isEqualTo((byte) 5);
        
        assertThat(found2).isPresent();
        assertThat(found2.get().getCourse().getId()).isEqualTo(course2.getId());
        assertThat(found2.get().getUser().getId()).isEqualTo(user2.getId());
        assertThat(found2.get().getContent()).isEqualTo("Could be more challenging.");
        assertThat(found2.get().getRating()).isEqualTo((byte) 3);
    }

    @Test
    void testFindByCourseAndUser_WhenInteractionNotExists_ShouldReturnEmpty() {
        // Given - Create a new user without interactions
        User newUser = new User("newuser", "new@example.com", "password", User.UserRole.USER);
        entityManager.persistAndFlush(newUser);

        // When
        Optional<Interaction> found = interactionRepo.findByCourseAndUser(course1, newUser);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByCourseAndUser_WhenMultipleInteractionsExist_ShouldReturnOne() {
        // When - user1 has interaction with course1
        Optional<Interaction> found = interactionRepo.findByCourseAndUser(course1, user1);

        // Then - Should return exactly one interaction
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(user1.getId());
        assertThat(found.get().getCourse().getId()).isEqualTo(course1.getId());
    }

    @Test
    void testExistsByCourseAndUser_WhenInteractionExists_ShouldReturnTrue() {
        // When & Then
        assertThat(interactionRepo.existsByCourseAndUser(course1, user1)).isTrue();
        assertThat(interactionRepo.existsByCourseAndUser(course1, user2)).isTrue();
        assertThat(interactionRepo.existsByCourseAndUser(course1, user3)).isTrue();
        assertThat(interactionRepo.existsByCourseAndUser(course2, user1)).isTrue();
        assertThat(interactionRepo.existsByCourseAndUser(course2, user2)).isTrue();
    }

    @Test
    void testExistsByCourseAndUser_WhenInteractionNotExists_ShouldReturnFalse() {
        // Given - Create a new user without interactions
        User newUser = new User("newuser", "new@example.com", "password", User.UserRole.USER);
        entityManager.persistAndFlush(newUser);

        // When & Then
        assertThat(interactionRepo.existsByCourseAndUser(course1, newUser)).isFalse();
        assertThat(interactionRepo.existsByCourseAndUser(course2, newUser)).isFalse();
        
        // user3 doesn't have interaction with course2
        assertThat(interactionRepo.existsByCourseAndUser(course2, user3)).isFalse();
    }

    @Test
    void testExistsByCourseAndUser_ConsistencyWithFindByCourseAndUser() {
        // When
        boolean exists1 = interactionRepo.existsByCourseAndUser(course1, user1);
        Optional<Interaction> found1 = interactionRepo.findByCourseAndUser(course1, user1);
        
        boolean exists2 = interactionRepo.existsByCourseAndUser(course2, user3);
        Optional<Interaction> found2 = interactionRepo.findByCourseAndUser(course2, user3);

        // Then - Results should be consistent
        assertThat(exists1).isTrue();
        assertThat(found1).isPresent();
        
        assertThat(exists2).isFalse();
        assertThat(found2).isEmpty();
    }

    @Test
    void testInteractionEntityBasicFunctionality() {
        // When
        Optional<Interaction> foundInteraction = interactionRepo.findById(interaction1.getId());

        // Then
        assertThat(foundInteraction).isPresent();
        Interaction interaction = foundInteraction.get();
        
        assertThat(interaction.getId()).isNotNull();
        assertThat(interaction.getCourse()).isEqualTo(course1);
        assertThat(interaction.getUser()).isEqualTo(user1);
        assertThat(interaction.getContent()).isEqualTo("Great course! Very helpful for understanding algorithms.");
        assertThat(interaction.getRating()).isEqualTo((byte) 5);
        assertThat(interaction.getLikes()).isEqualTo(0);
        assertThat(interaction.getDislikes()).isEqualTo(0);
        assertThat(interaction.getCreatedAt()).isNotNull();
    }

    @Test
    void testInteractionWithRatingOnly() {
        // When - Find interaction with rating only (no content)
        Optional<Interaction> found = interactionRepo.findByCourseAndUser(course1, user2);

        // Then
        assertThat(found).isPresent();
        Interaction interaction = found.get();
        
        assertThat(interaction.getContent()).isNull();
        assertThat(interaction.getRating()).isEqualTo((byte) 4);
        assertThat(interaction.getCourse().getId()).isEqualTo(course1.getId());
        assertThat(interaction.getUser().getId()).isEqualTo(user2.getId());
    }

    @Test
    void testInteractionWithContentOnly() {
        // When - Find interaction with content only (no rating)
        Optional<Interaction> found = interactionRepo.findByCourseAndUser(course1, user3);

        // Then
        assertThat(found).isPresent();
        Interaction interaction = found.get();
        
        assertThat(interaction.getContent()).isEqualTo("Excellent teaching methodology.");
        assertThat(interaction.getRating()).isNull();
        assertThat(interaction.getCourse().getId()).isEqualTo(course1.getId());
        assertThat(interaction.getUser().getId()).isEqualTo(user3.getId());
    }

    @Test
    void testInteractionLikesAndDislikes() {
        // Given
        Interaction interaction = interaction1;
        
        // When - Test likes
        interaction.likes();
        interaction.likes();
        interactionRepo.save(interaction);
        
        // Then
        Optional<Interaction> found = interactionRepo.findById(interaction.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getLikes()).isEqualTo(2);
        
        // When - Test unlikes
        found.get().unlikes();
        interactionRepo.save(found.get());
        
        // Then
        Optional<Interaction> updated = interactionRepo.findById(interaction.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getLikes()).isEqualTo(1);
        
        // When - Test dislikes
        updated.get().dislikes();
        updated.get().dislikes();
        interactionRepo.save(updated.get());
        
        // Then
        Optional<Interaction> final_interaction = interactionRepo.findById(interaction.getId());
        assertThat(final_interaction).isPresent();
        assertThat(final_interaction.get().getDislikes()).isEqualTo(2);
        
        // When - Test undislikes
        final_interaction.get().undislikes();
        interactionRepo.save(final_interaction.get());
        
        // Then
        Optional<Interaction> result = interactionRepo.findById(interaction.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getDislikes()).isEqualTo(1);
    }

    @Test
    void testTimestampAutoGeneration() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now();
        Interaction newInteraction = new Interaction(course1, user2, "New test comment", (byte) 3);
        
        // When
        Interaction saved = interactionRepo.save(newInteraction);
        LocalDateTime afterSave = LocalDateTime.now();

        // Then
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isAfter(beforeSave.minusSeconds(1)); // Allow 1 second margin
        assertThat(saved.getCreatedAt()).isBefore(afterSave.plusSeconds(1));  // Allow 1 second margin
    }

    @Test
    void testInteractionToString() {
        // When
        String interactionString = interaction1.toString();

        // Then
        assertThat(interactionString).contains("Interaction{");
        assertThat(interactionString).contains("id=" + interaction1.getId());
        assertThat(interactionString).contains("course=" + course1);
        assertThat(interactionString).contains("user=" + user1);
        assertThat(interactionString).contains("content='Great course! Very helpful for understanding algorithms.'");
        assertThat(interactionString).contains("rating=5");
        assertThat(interactionString).contains("likes=0");
        assertThat(interactionString).contains("dislikes=0");
    }

    @Test
    void testCourseRelationship() {
        // When
        List<Interaction> course1Interactions = interactionRepo.findAllByCourse(course1);

        // Then
        assertThat(course1Interactions).hasSize(3);
        assertThat(course1Interactions).allMatch(interaction -> {
            Course interactionCourse = interaction.getCourse();
            return interactionCourse.getId().equals(course1.getId()) &&
                   interactionCourse.getName().equals("Data Structures") &&
                   interactionCourse.getCode().equals("CS101") &&
                   interactionCourse.getCourseType().equals(CourseType.MAJOR_REQUIRED);
        });
    }

    @Test
    void testUserRelationship() {
        // When
        List<Interaction> user1Interactions = interactionRepo.findByUser(user1);

        // Then
        assertThat(user1Interactions).hasSize(2);
        assertThat(user1Interactions).allMatch(interaction -> {
            User interactionUser = interaction.getUser();
            return interactionUser.getId().equals(user1.getId()) &&
                   interactionUser.getName().equals("testuser1") &&
                   interactionUser.getEmail().equals("test1@example.com") &&
                   interactionUser.getRole().equals(User.UserRole.USER);
        });
    }

    @Test
    void testRatingScenarios() {
        // When
        List<Interaction> allInteractions = interactionRepo.findAll();

        // Then - Verify different rating scenarios
        long ratingsCount = allInteractions.stream()
            .filter(interaction -> interaction.getRating() != null)
            .count();
        
        long commentsCount = allInteractions.stream()
            .filter(interaction -> interaction.getContent() != null && !interaction.getContent().isEmpty())
            .count();
            
        assertThat(ratingsCount).isEqualTo(4); // interaction1, 2, 4, 5 have ratings
        assertThat(commentsCount).isEqualTo(4); // interaction1, 3, 4, 5 have content
        
        // Verify rating ranges
        assertThat(allInteractions.stream()
            .filter(interaction -> interaction.getRating() != null)
            .allMatch(interaction -> interaction.getRating() >= 1 && interaction.getRating() <= 5))
            .isTrue();
    }

    @Test
    void testComplexQueryScenarios() {
        // Scenario 1: Find all interactions for a course with ratings
        List<Interaction> course1Interactions = interactionRepo.findAllByCourse(course1);
        long course1WithRatings = course1Interactions.stream()
            .filter(interaction -> interaction.getRating() != null)
            .count();
        assertThat(course1WithRatings).isEqualTo(2); // interaction1 and interaction2
        
        // Scenario 2: Find all interactions by a user across different courses
        List<Interaction> user1AllInteractions = interactionRepo.findByUser(user1);
        assertThat(user1AllInteractions).hasSize(2);
        assertThat(user1AllInteractions.stream()
            .map(interaction -> interaction.getCourse().getId())
            .distinct()
            .count()).isEqualTo(2); // user1 has interactions with 2 different courses
        
        // Scenario 3: Verify unique constraint (one interaction per user-course pair)
        boolean user1HasMultipleForCourse1 = interactionRepo.findAllByCourse(course1).stream()
            .filter(interaction -> interaction.getUser().getId().equals(user1.getId()))
            .count() == 1;
        assertThat(user1HasMultipleForCourse1).isTrue();
    }

    @Test
    void testEdgeCases() {
        // Test with null or empty content
        Interaction emptyContentInteraction = new Interaction(course2, user3, "", (byte) 2);
        Interaction saved = interactionRepo.save(emptyContentInteraction);
        
        Optional<Interaction> found = interactionRepo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("");
        assertThat(found.get().getRating()).isEqualTo((byte) 2);
        
        // Test rating boundary values
        Interaction minRatingInteraction = new Interaction(course2, user3, (byte) 1);
        Interaction maxRatingInteraction = new Interaction(course1, user3, (byte) 5);
        
        interactionRepo.saveAll(List.of(minRatingInteraction, maxRatingInteraction));
        
        assertThat(minRatingInteraction.getRating()).isEqualTo((byte) 1);
        assertThat(maxRatingInteraction.getRating()).isEqualTo((byte) 5);
    }

    @Test
    void testDataConsistency() {
        // When
        List<Interaction> allInteractions = interactionRepo.findAll();

        // Then
        assertThat(allInteractions).hasSize(5);
        
        // Verify all interactions have valid course associations
        assertThat(allInteractions).allMatch(interaction -> interaction.getCourse() != null);
        assertThat(allInteractions).allMatch(interaction -> interaction.getCourse().getId() != null);
        
        // Verify all interactions have valid user associations
        assertThat(allInteractions).allMatch(interaction -> interaction.getUser() != null);
        assertThat(allInteractions).allMatch(interaction -> interaction.getUser().getId() != null);
        
        // Verify all interactions have creation timestamps
        assertThat(allInteractions).allMatch(interaction -> interaction.getCreatedAt() != null);
        
        // Verify likes and dislikes are non-negative
        assertThat(allInteractions).allMatch(interaction -> interaction.getLikes() >= 0);
        assertThat(allInteractions).allMatch(interaction -> interaction.getDislikes() >= 0);
    }
}