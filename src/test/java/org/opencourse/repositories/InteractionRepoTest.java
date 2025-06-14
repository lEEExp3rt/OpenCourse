package org.opencourse.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.models.Interaction;
import org.opencourse.models.User;
import org.opencourse.utils.typeinfo.CourseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link InteractionRepo}.
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

    // Test data.
    private User user1, user2, user3;
    private Department department;
    private Course course1, course2;
    private Interaction interaction1, interaction2, interaction3, interaction4;

    @BeforeEach
    void setUp() {
        // Create and persist test department.
        department = new Department("Computer Science");
        entityManager.persistAndFlush(department);

        // Create and persist test users.
        user1 = new User("alice", "alice@test.com", "password123", User.UserRole.USER);
        user2 = new User("bob", "bob@test.com", "password456", User.UserRole.USER);
        user3 = new User("charlie", "charlie@test.com", "password789", User.UserRole.ADMIN);
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        // Create and persist test courses.
        course1 = new Course("Data Structures", "CS101", department, CourseType.MAJOR_REQUIRED, new BigDecimal("3.0"));
        course2 = new Course("Web Development", "CS201", department, CourseType.MAJOR_OPTIONAL, new BigDecimal("2.5"));
        
        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);

        // Create test interactions with controlled timestamps and likes for sorting tests.
        LocalDateTime baseTime = LocalDateTime.now().minusDays(5);

        // Course1 interactions - designed for sorting verification.
        interaction1 = new Interaction(course1, user1, "Excellent course!", (byte) 5);
        interaction2 = new Interaction(course1, user2, (byte) 4); // Rating only
        interaction3 = new Interaction(course1, user3, "Very informative content"); // Comment only
        
        // Course2 interaction.
        interaction4 = new Interaction(course2, user1, "Good practical examples", (byte) 4);

        // Persist and setup for sorting tests.
        entityManager.persist(interaction1);
        entityManager.flush();
        interaction1.setCreatedAt(baseTime.plusDays(1));
        interaction1.likes(); // 1 like
        entityManager.merge(interaction1);

        entityManager.persist(interaction2);
        entityManager.flush();
        interaction2.setCreatedAt(baseTime.plusDays(2));
        interaction2.likes();
        interaction2.likes();
        interaction2.likes(); // 3 likes (highest)
        entityManager.merge(interaction2);

        entityManager.persist(interaction3);
        entityManager.flush();
        interaction3.setCreatedAt(baseTime.plusDays(3)); // Most recent
        interaction3.likes();
        interaction3.likes(); // 2 likes
        entityManager.merge(interaction3);

        entityManager.persist(interaction4);
        entityManager.flush();
        interaction4.setCreatedAt(baseTime);
        // 0 likes
        entityManager.merge(interaction4);

        entityManager.flush();
    }

    @Test
    @DisplayName("Repository should be properly initialized")
    void contextLoads() {
        assertThat(interactionRepo).isNotNull();
        assertThat(entityManager).isNotNull();
    }

    // findAllByCourse Tests

    @Test
    @DisplayName("Should find all interactions for a specific course")
    void findAllByCourse_WithValidCourse_ShouldReturnInteractions() {
        // When.
        List<Interaction> course1Interactions = interactionRepo.findAllByCourse(course1);
        List<Interaction> course2Interactions = interactionRepo.findAllByCourse(course2);

        // Then.
        assertThat(course1Interactions).hasSize(3);
        assertThat(course1Interactions).extracting(Interaction::getCourse)
            .allMatch(course -> course.getId().equals(course1.getId()));
        
        assertThat(course2Interactions).hasSize(1);
        assertThat(course2Interactions.get(0).getCourse().getId()).isEqualTo(course2.getId());
    }

    @Test
    @DisplayName("Should return empty list for course with no interactions")
    void findAllByCourse_WithCourseHavingNoInteractions_ShouldReturnEmptyList() {
        // Given.
        Course emptyCourse = new Course("Empty Course", "EMPTY001", department, CourseType.GENERAL_OPTIONAL, new BigDecimal("1.0"));
        entityManager.persistAndFlush(emptyCourse);

        // When.
        List<Interaction> interactions = interactionRepo.findAllByCourse(emptyCourse);

        // Then.
        assertThat(interactions).isEmpty();
    }

    // findByCourseIdOrderByLikesDescCreatedAtDesc Tests (Core Sorting Feature)

    @Test
    @DisplayName("Should return interactions sorted by likes desc, then by creation time desc")
    void findByCourseIdOrderByLikesDescCreatedAtDesc_ShouldReturnCorrectOrder() {
        // When.
        List<Interaction> sortedInteractions = interactionRepo.findByCourseIdOrderByLikesDescCreatedAtDesc(course1.getId());

        // Then.
        assertThat(sortedInteractions).hasSize(3);
        
        // Verify correct ordering: highest likes first, then most recent for equal likes.
        assertThat(sortedInteractions.get(0).getId()).isEqualTo(interaction2.getId()); // 3 likes
        assertThat(sortedInteractions.get(1).getId()).isEqualTo(interaction3.getId()); // 2 likes
        assertThat(sortedInteractions.get(2).getId()).isEqualTo(interaction1.getId()); // 1 like

        // Verify actual likes values.
        assertThat(sortedInteractions.get(0).getLikes()).isEqualTo(3);
        assertThat(sortedInteractions.get(1).getLikes()).isEqualTo(2);
        assertThat(sortedInteractions.get(2).getLikes()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle sorting for equal likes by creation time")
    void findByCourseIdOrderByLikesDescCreatedAtDesc_WithEqualLikes_ShouldSortByTime() {
        // Given - Create two interactions with same likes but different times.
        Interaction sameTime1 = new Interaction(course1, user2, "Comment A", (byte) 3);
        Interaction sameTime2 = new Interaction(course1, user3, "Comment B", (byte) 3);
        
        LocalDateTime baseTime = LocalDateTime.now().minusDays(1);
        
        entityManager.persist(sameTime1);
        entityManager.flush();
        sameTime1.setCreatedAt(baseTime);
        sameTime1.likes();
        sameTime1.likes(); // 2 likes
        entityManager.merge(sameTime1);
        
        entityManager.persist(sameTime2);
        entityManager.flush();
        sameTime2.setCreatedAt(baseTime.plusHours(1)); // More recent
        sameTime2.likes();
        sameTime2.likes(); // 2 likes
        entityManager.merge(sameTime2);
        
        entityManager.flush();

        // When.
        List<Interaction> sortedInteractions = interactionRepo.findByCourseIdOrderByLikesDescCreatedAtDesc(course1.getId());

        // Then.
        assertThat(sortedInteractions).hasSizeGreaterThan(3);
        
        // Find the two interactions with 2 likes (excluding interaction3).
        List<Interaction> twoLikesInteractions = sortedInteractions.stream()
            .filter(i -> i.getLikes() == 2)
            .toList();
        
        // More recent should come first when likes are equal.
        if (twoLikesInteractions.size() >= 2) {
            assertThat(twoLikesInteractions.get(0).getCreatedAt())
                .isAfter(twoLikesInteractions.get(1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("Should return empty list for non-existent course ID")
    void findByCourseIdOrderByLikesDescCreatedAtDesc_WithNonExistentCourse_ShouldReturnEmpty() {
        // When.
        List<Interaction> interactions = interactionRepo.findByCourseIdOrderByLikesDescCreatedAtDesc((short) 9999);

        // Then.
        assertThat(interactions).isEmpty();
    }

    // findByUserId Tests

    @Test
    @DisplayName("Should find all interactions by user ID")
    void findByUserId_WithValidUserId_ShouldReturnUserInteractions() {
        // When.
        List<Interaction> user1Interactions = interactionRepo.findByUserId(user1.getId());
        List<Interaction> user2Interactions = interactionRepo.findByUserId(user2.getId());
        List<Interaction> user3Interactions = interactionRepo.findByUserId(user3.getId());

        // Then.
        assertThat(user1Interactions).hasSize(2); // interaction1, interaction4
        assertThat(user1Interactions).extracting(Interaction::getUser)
            .allMatch(user -> user.getId().equals(user1.getId()));
        
        assertThat(user2Interactions).hasSize(1); // interaction2
        assertThat(user2Interactions.get(0).getUser().getId()).isEqualTo(user2.getId());
        
        assertThat(user3Interactions).hasSize(1); // interaction3
        assertThat(user3Interactions.get(0).getUser().getId()).isEqualTo(user3.getId());
    }

    @Test
    @DisplayName("Should return empty list for user with no interactions")
    void findByUserId_WithUserHavingNoInteractions_ShouldReturnEmptyList() {
        // Given.
        User newUser = new User("newuser", "new@test.com", "password", User.UserRole.USER);
        entityManager.persistAndFlush(newUser);

        // When.
        List<Interaction> interactions = interactionRepo.findByUserId(newUser.getId());

        // Then.
        assertThat(interactions).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list for non-existent user ID")
    void findByUserId_WithNonExistentUserId_ShouldReturnEmptyList() {
        // When.
        List<Interaction> interactions = interactionRepo.findByUserId(99999);

        // Then.
        assertThat(interactions).isEmpty();
    }

    // findByCourseAndUser Tests

    @Test
    @DisplayName("Should find interaction by course and user")
    void findByCourseAndUser_WithValidCourseAndUser_ShouldReturnInteraction() {
        // When.
        Optional<Interaction> found1 = interactionRepo.findByCourseAndUser(course1, user1);
        Optional<Interaction> found2 = interactionRepo.findByCourseAndUser(course2, user1);

        // Then.
        assertThat(found1).isPresent();
        assertThat(found1.get().getCourse().getId()).isEqualTo(course1.getId());
        assertThat(found1.get().getUser().getId()).isEqualTo(user1.getId());
        assertThat(found1.get().getContent()).isEqualTo("Excellent course!");
        assertThat(found1.get().getRating()).isEqualTo((byte) 5);
        
        assertThat(found2).isPresent();
        assertThat(found2.get().getCourse().getId()).isEqualTo(course2.getId());
        assertThat(found2.get().getUser().getId()).isEqualTo(user1.getId());
        assertThat(found2.get().getContent()).isEqualTo("Good practical examples");
        assertThat(found2.get().getRating()).isEqualTo((byte) 4);
    }

    @Test
    @DisplayName("Should return empty when no interaction exists between course and user")
    void findByCourseAndUser_WithNoInteraction_ShouldReturnEmpty() {
        // When.
        Optional<Interaction> found = interactionRepo.findByCourseAndUser(course2, user3);

        // Then.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should distinguish interactions across different courses for same user")
    void findByCourseAndUser_WithMultipleCoursesForSameUser_ShouldReturnCorrectInteraction() {
        // When.
        Optional<Interaction> course1Interaction = interactionRepo.findByCourseAndUser(course1, user1);
        Optional<Interaction> course2Interaction = interactionRepo.findByCourseAndUser(course2, user1);

        // Then.
        assertThat(course1Interaction).isPresent();
        assertThat(course2Interaction).isPresent();
        assertThat(course1Interaction.get().getId()).isNotEqualTo(course2Interaction.get().getId());
        assertThat(course1Interaction.get().getCourse().getId()).isEqualTo(course1.getId());
        assertThat(course2Interaction.get().getCourse().getId()).isEqualTo(course2.getId());
    }

    // existsByCourseAndUser Tests

    @Test
    @DisplayName("Should return true when interaction exists between course and user")
    void existsByCourseAndUser_WithExistingInteraction_ShouldReturnTrue() {
        // When & Then.
        assertThat(interactionRepo.existsByCourseAndUser(course1, user1)).isTrue();
        assertThat(interactionRepo.existsByCourseAndUser(course1, user2)).isTrue();
        assertThat(interactionRepo.existsByCourseAndUser(course1, user3)).isTrue();
        assertThat(interactionRepo.existsByCourseAndUser(course2, user1)).isTrue();
    }

    @Test
    @DisplayName("Should return false when no interaction exists between course and user")
    void existsByCourseAndUser_WithNoInteraction_ShouldReturnFalse() {
        // When & Then.
        assertThat(interactionRepo.existsByCourseAndUser(course2, user2)).isFalse();
        assertThat(interactionRepo.existsByCourseAndUser(course2, user3)).isFalse();
    }

    @Test
    @DisplayName("Should be consistent with findByCourseAndUser results")
    void existsByCourseAndUser_ShouldBeConsistentWithFindByCourseAndUser() {
        // When.
        boolean exists = interactionRepo.existsByCourseAndUser(course1, user1);
        Optional<Interaction> found = interactionRepo.findByCourseAndUser(course1, user1);
        
        boolean notExists = interactionRepo.existsByCourseAndUser(course2, user3);
        Optional<Interaction> notFound = interactionRepo.findByCourseAndUser(course2, user3);

        // Then.
        assertThat(exists).isTrue();
        assertThat(found).isPresent();
        
        assertThat(notExists).isFalse();
        assertThat(notFound).isEmpty();
    }

    // Entity Behavior Tests

    @Test
    @DisplayName("Should properly handle different interaction types")
    void interactions_ShouldHandleDifferentTypes() {
        // When.
        Optional<Interaction> commentAndRating = interactionRepo.findById(interaction1.getId());
        Optional<Interaction> ratingOnly = interactionRepo.findById(interaction2.getId());
        Optional<Interaction> commentOnly = interactionRepo.findById(interaction3.getId());

        // Then.
        assertThat(commentAndRating).isPresent();
        assertThat(commentAndRating.get().getContent()).isNotNull();
        assertThat(commentAndRating.get().getRating()).isNotNull();
        
        assertThat(ratingOnly).isPresent();
        assertThat(ratingOnly.get().getContent()).isNull();
        assertThat(ratingOnly.get().getRating()).isNotNull();
        
        assertThat(commentOnly).isPresent();
        assertThat(commentOnly.get().getContent()).isNotNull();
        assertThat(commentOnly.get().getRating()).isNull();
    }

    @Test
    @DisplayName("Should correctly handle likes and dislikes operations")
    void interactions_ShouldHandleLikesAndDislikes() {
        // Given.
        Interaction testInteraction = interaction4;
        assertThat(testInteraction.getLikes()).isEqualTo(0);
        assertThat(testInteraction.getDislikes()).isEqualTo(0);

        // When - Add likes.
        testInteraction.likes();
        testInteraction.likes();
        interactionRepo.save(testInteraction);

        // Then.
        Optional<Interaction> updated = interactionRepo.findById(testInteraction.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getLikes()).isEqualTo(2);

        // When - Remove a like.
        updated.get().unlikes();
        interactionRepo.save(updated.get());

        // Then.
        Optional<Interaction> afterUnlike = interactionRepo.findById(testInteraction.getId());
        assertThat(afterUnlike).isPresent();
        assertThat(afterUnlike.get().getLikes()).isEqualTo(1);

        // When - Add dislikes.
        afterUnlike.get().dislikes();
        interactionRepo.save(afterUnlike.get());

        // Then.
        Optional<Interaction> withDislikes = interactionRepo.findById(testInteraction.getId());
        assertThat(withDislikes).isPresent();
        assertThat(withDislikes.get().getDislikes()).isEqualTo(1);
        assertThat(withDislikes.get().getLikes()).isEqualTo(1); // Should remain unchanged
    }

    @Test
    @DisplayName("Should auto-generate creation timestamp")
    void interaction_ShouldAutoGenerateTimestamp() {
        // Given.
        LocalDateTime beforeSave = LocalDateTime.now();
        Interaction newInteraction = new Interaction(course1, user2, "Test comment", (byte) 3);

        // When.
        Interaction saved = interactionRepo.save(newInteraction);
        LocalDateTime afterSave = LocalDateTime.now();

        // Then.
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isBetween(beforeSave.minusSeconds(1), afterSave.plusSeconds(1));
    }

    @Test
    @DisplayName("Should maintain referential integrity")
    void interactions_ShouldMaintainReferentialIntegrity() {
        // When.
        List<Interaction> allInteractions = interactionRepo.findAll();

        // Then.
        assertThat(allInteractions).hasSize(4);
        assertThat(allInteractions).allSatisfy(interaction -> {
            assertThat(interaction.getCourse()).isNotNull();
            assertThat(interaction.getCourse().getId()).isNotNull();
            assertThat(interaction.getUser()).isNotNull();
            assertThat(interaction.getUser().getId()).isNotNull();
            assertThat(interaction.getCreatedAt()).isNotNull();
            assertThat(interaction.getLikes()).isGreaterThanOrEqualTo(0);
            assertThat(interaction.getDislikes()).isGreaterThanOrEqualTo(0);
        });
    }

    @Test
    @DisplayName("Should handle boundary rating values correctly")
    void interactions_ShouldHandleBoundaryRatings() {
        // Given.
        Interaction minRating = new Interaction(course1, user2, "Min rating", (byte) 1);
        Interaction maxRating = new Interaction(course1, user3, "Max rating", (byte) 10);

        // When.
        Interaction savedMin = interactionRepo.save(minRating);
        Interaction savedMax = interactionRepo.save(maxRating);

        // Then.
        assertThat(savedMin.getRating()).isEqualTo((byte) 1);
        assertThat(savedMax.getRating()).isEqualTo((byte) 10);
        
        // Verify they can be retrieved correctly.
        Optional<Interaction> foundMin = interactionRepo.findById(savedMin.getId());
        Optional<Interaction> foundMax = interactionRepo.findById(savedMax.getId());
        
        assertThat(foundMin).isPresent();
        assertThat(foundMin.get().getRating()).isEqualTo((byte) 1);
        assertThat(foundMax).isPresent();
        assertThat(foundMax.get().getRating()).isEqualTo((byte) 10);
    }

    @Test
    @DisplayName("Should handle empty content correctly")
    void interactions_ShouldHandleEmptyContent() {
        // Given.
        Interaction emptyContent = new Interaction(course2, user2, "", (byte) 5);

        // When.
        Interaction saved = interactionRepo.save(emptyContent);

        // Then.
        Optional<Interaction> found = interactionRepo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("");
        assertThat(found.get().getRating()).isEqualTo((byte) 5);
    }
}