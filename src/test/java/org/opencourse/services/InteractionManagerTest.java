package org.opencourse.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.opencourse.configs.ApplicationConfig;
import org.opencourse.dto.request.InteractionCreationDto;
import org.opencourse.dto.request.InteractionUpdateDto;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.models.Interaction;
import org.opencourse.models.User;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.InteractionRepo;
import org.opencourse.repositories.UserRepo;
import org.opencourse.utils.typeinfo.CourseType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link InteractionManager}.
 * 
 * @author !EEExp3rt
 */
@ExtendWith(MockitoExtension.class)
class InteractionManagerTest {

    @Mock
    private InteractionRepo interactionRepo;

    @Mock
    private CourseRepo courseRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private ApplicationConfig.Activity activityConfig;

    @Mock
    private ApplicationConfig.Activity.Interaction activityInteractionConfig;

    @Mock
    private HistoryManager historyManager;

    @InjectMocks
    private InteractionManager interactionManager;

    // Test data.
    private User testUser;
    private User testCreator;
    private User testAdmin;
    private Department testDepartment;
    private Course testCourse;
    private Interaction testInteraction;
    private InteractionCreationDto testCreationDto;
    private InteractionUpdateDto testUpdateDto;

    @BeforeEach
    void setUp() {
        // Test department.
        testDepartment = new Department("Computer Science");
        testDepartment = spy(testDepartment);
        lenient().when(testDepartment.getId()).thenReturn((byte) 1);

        // Test course.
        testCourse = new Course(
            "Data Structures",
            "CS101",
            testDepartment,
            CourseType.MAJOR_REQUIRED,
            new BigDecimal("3.0")
        );
        testCourse = spy(testCourse);
        lenient().when(testCourse.getId()).thenReturn((short) 1);

        // Test users.
        testUser = new User(
            "testUser",
            "testuser@example.com",
            "hashedPassword",
            User.UserRole.USER
        );
        testUser = spy(testUser);
        lenient().when(testUser.getId()).thenReturn(1);

        testCreator = new User(
            "testCreator",
            "testcreator@example.com",
            "hashedPassword",
            User.UserRole.USER
        );
        testCreator = spy(testCreator);
        lenient().when(testCreator.getId()).thenReturn(2);

        testAdmin = new User(
            "testAdmin",
            "testadmin@example.com",
            "hashedPassword",
            User.UserRole.ADMIN
        );
        testAdmin = spy(testAdmin);
        lenient().when(testAdmin.getId()).thenReturn(3);

        // Test interaction.
        testInteraction = new Interaction(testCourse, testCreator, "Great course!", (byte) 5);
        testInteraction = spy(testInteraction);
        lenient().when(testInteraction.getId()).thenReturn(1);
        lenient().when(testInteraction.getUser()).thenReturn(testCreator);
        lenient().when(testInteraction.getCourse()).thenReturn(testCourse);

        // Test DTOs.
        testCreationDto = new InteractionCreationDto((short) 1, "Test comment", (byte) 4);
        testUpdateDto = new InteractionUpdateDto(1, "Updated comment", (byte) 5);

        // Mock configuration.
        lenient().when(applicationConfig.getActivity()).thenReturn(activityConfig);
        lenient().when(activityConfig.getInteraction()).thenReturn(activityInteractionConfig);
        lenient().when(activityInteractionConfig.getAdd()).thenReturn(10);
        lenient().when(activityInteractionConfig.getDelete()).thenReturn(-5);
        lenient().when(activityInteractionConfig.getLike()).thenReturn(2);
        lenient().when(activityInteractionConfig.getUnlike()).thenReturn(-1);
    }

    // Add Interaction Tests.

    @Test
    @DisplayName("Should successfully create new interaction when user hasn't interacted with course")
    void addInteraction_WithNewUser_ShouldCreateInteraction() {
        // Given.
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(interactionRepo.findByCourseAndUser(testCourse, testUser)).thenReturn(Optional.empty());
        when(interactionRepo.save(any(Interaction.class))).thenReturn(testInteraction);
        when(userRepo.save(testUser)).thenReturn(testUser);

        // When.
        Interaction result = interactionManager.addInteraction(testCreationDto, testUser);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testInteraction);

        verify(courseRepo).findById((short) 1);
        verify(interactionRepo).findByCourseAndUser(testCourse, testUser);
        verify(interactionRepo).save(any(Interaction.class));
        verify(testUser).addActivity(10);
        verify(userRepo).save(testUser);
        verify(historyManager).logCreateInteraction(testUser, testInteraction);
        verify(historyManager).logRateCourse(testUser, testCourse);
    }

    @Test
    @DisplayName("Should update existing interaction when user has already interacted with course")
    void addInteraction_WithExistingInteraction_ShouldUpdateInteraction() {
        // Given.
        Interaction existingInteraction = new Interaction(testCourse, testUser, "Old comment", (byte) 3);
        existingInteraction = spy(existingInteraction);
        
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(interactionRepo.findByCourseAndUser(testCourse, testUser)).thenReturn(Optional.of(existingInteraction));
        when(interactionRepo.save(existingInteraction)).thenReturn(existingInteraction);

        // When.
        Interaction result = interactionManager.addInteraction(testCreationDto, testUser);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(existingInteraction);

        verify(courseRepo).findById((short) 1);
        verify(interactionRepo).findByCourseAndUser(testCourse, testUser);
        verify(existingInteraction).setContent("Test comment");
        verify(existingInteraction).setRating((byte) 4);
        verify(interactionRepo).save(existingInteraction);
        verify(historyManager).logUpdateInteraction(testUser, existingInteraction);
        verify(historyManager).logRateCourse(testUser, testCourse);
        
        // Should not add activity for updates.
        verify(testUser, never()).addActivity(anyInt());
        verify(userRepo, never()).save(testUser);
    }

    @Test
    @DisplayName("Should update only content when rating is null in existing interaction")
    void addInteraction_WithExistingInteractionContentOnly_ShouldUpdateContentOnly() {
        // Given.
        InteractionCreationDto contentOnlyDto = new InteractionCreationDto((short) 1, "Only content");
        Interaction existingInteraction = new Interaction(testCourse, testUser, "Old comment", (byte) 3);
        existingInteraction = spy(existingInteraction);

        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(interactionRepo.findByCourseAndUser(testCourse, testUser)).thenReturn(Optional.of(existingInteraction));
        when(interactionRepo.save(existingInteraction)).thenReturn(existingInteraction);

        // When.
        Interaction result = interactionManager.addInteraction(contentOnlyDto, testUser);

        // Then.
        assertThat(result).isNotNull();
        
        verify(existingInteraction).setContent("Only content");
        verify(existingInteraction, never()).setRating(any());
        verify(historyManager).logUpdateInteraction(testUser, existingInteraction);
        verify(historyManager, never()).logRateCourse(testUser, testCourse);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when course does not exist")
    void addInteraction_WithNonExistentCourse_ShouldThrowException() {
        // Given.
        when(courseRepo.findById((short) 1)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> interactionManager.addInteraction(testCreationDto, testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("课程不存在");

        verify(courseRepo).findById((short) 1);
        verifyNoInteractions(interactionRepo, userRepo, historyManager);
    }

    @Test
    @DisplayName("Should create interaction without rating when rating is null")
    void addInteraction_WithoutRating_ShouldCreateInteractionWithoutRating() {
        // Given.
        InteractionCreationDto noRatingDto = new InteractionCreationDto((short) 1, "Just comment");
        
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(interactionRepo.findByCourseAndUser(testCourse, testUser)).thenReturn(Optional.empty());
        when(interactionRepo.save(any(Interaction.class))).thenReturn(testInteraction);
        when(userRepo.save(testUser)).thenReturn(testUser);

        // When.
        Interaction result = interactionManager.addInteraction(noRatingDto, testUser);

        // Then.
        assertThat(result).isNotNull();
        
        verify(historyManager).logCreateInteraction(testUser, testInteraction);
        verify(historyManager, never()).logRateCourse(testUser, testCourse);
    }

    // Update Interaction Tests.

    @Test
    @DisplayName("Should successfully update interaction when user is owner")
    void updateInteraction_WithValidOwner_ShouldUpdateInteraction() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(interactionRepo.save(testInteraction)).thenReturn(testInteraction);

        // When.
        Interaction result = interactionManager.updateInteraction(testUpdateDto, testCreator);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testInteraction);

        verify(interactionRepo).findById(1);
        verify(testInteraction).setContent("Updated comment");
        verify(testInteraction).setRating((byte) 5);
        verify(interactionRepo).save(testInteraction);
        verify(historyManager).logUpdateInteraction(testCreator, testInteraction);
        verify(historyManager).logRateCourse(testCreator, testCourse);
    }

    @Test
    @DisplayName("Should update only content when rating is null")
    void updateInteraction_WithNullRating_ShouldUpdateContentOnly() {
        // Given.
        InteractionUpdateDto contentOnlyDto = new InteractionUpdateDto(1, "Updated content", null);
        
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(interactionRepo.save(testInteraction)).thenReturn(testInteraction);

        // When.
        Interaction result = interactionManager.updateInteraction(contentOnlyDto, testCreator);

        // Then.
        assertThat(result).isNotNull();
        
        verify(testInteraction).setContent("Updated content");
        verify(testInteraction, never()).setRating(any());
        verify(historyManager).logUpdateInteraction(testCreator, testInteraction);
        verify(historyManager, never()).logRateCourse(eq(testCreator), any(Course.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when interaction does not exist")
    void updateInteraction_WithNonExistentInteraction_ShouldThrowException() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> interactionManager.updateInteraction(testUpdateDto, testCreator))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("评论不存在");

        verify(interactionRepo).findById(1);
        verifyNoMoreInteractions(interactionRepo);
        verifyNoInteractions(historyManager);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user is not the owner")
    void updateInteraction_WithNonOwner_ShouldThrowException() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));

        // When & Then.
        assertThatThrownBy(() -> interactionManager.updateInteraction(testUpdateDto, testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("您只能修改自己的评论");

        verify(interactionRepo).findById(1);
        verify(testInteraction).getUser();
        verifyNoMoreInteractions(interactionRepo);
        verifyNoInteractions(historyManager);
    }

    @Test
    @DisplayName("Should not update content when content is null")
    void updateInteraction_WithNullContent_ShouldNotUpdateContent() {
        // Given.
        InteractionUpdateDto ratingOnlyDto = new InteractionUpdateDto(1, null, (byte) 8);
        
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(interactionRepo.save(testInteraction)).thenReturn(testInteraction);

        // When.
        Interaction result = interactionManager.updateInteraction(ratingOnlyDto, testCreator);

        // Then.
        assertThat(result).isNotNull();
        
        verify(testInteraction, never()).setContent(any());
        verify(testInteraction).setRating((byte) 8);
        verify(historyManager).logUpdateInteraction(testCreator, testInteraction);
        verify(historyManager).logRateCourse(testCreator, testCourse);
    }

    // Delete Interaction Tests.

    @Test
    @DisplayName("Should successfully delete interaction when user is owner")
    void deleteInteraction_WithOwner_ShouldDeleteInteraction() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(userRepo.save(testCreator)).thenReturn(testCreator);

        // When.
        boolean result = interactionManager.deleteInteraction(1, testCreator);

        // Then.
        assertThat(result).isTrue();

        verify(interactionRepo).findById(1);
        verify(interactionRepo).delete(testInteraction);
        verify(testCreator).addActivity(-5);
        verify(userRepo).save(testCreator);
        verify(historyManager).logDeleteInteraction(testCreator, testInteraction);
    }

    @Test
    @DisplayName("Should successfully delete interaction when user is admin")
    void deleteInteraction_WithAdmin_ShouldDeleteInteraction() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(testAdmin.getRole()).thenReturn(User.UserRole.ADMIN);
        when(userRepo.save(testAdmin)).thenReturn(testAdmin);

        // When.
        boolean result = interactionManager.deleteInteraction(1, testAdmin);

        // Then.
        assertThat(result).isTrue();

        verify(interactionRepo).findById(1);
        verify(interactionRepo).delete(testInteraction);
        verify(testAdmin).addActivity(-5);
        verify(userRepo).save(testAdmin);
        verify(historyManager).logDeleteInteraction(testAdmin, testInteraction);
    }

    @Test
    @DisplayName("Should return false when interaction does not exist")
    void deleteInteraction_WithNonExistentInteraction_ShouldReturnFalse() {
        // Given.
        when(interactionRepo.findById(999)).thenReturn(Optional.empty());

        // When.
        boolean result = interactionManager.deleteInteraction(999, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(interactionRepo).findById(999);
        verifyNoMoreInteractions(interactionRepo);
        verifyNoInteractions(userRepo, historyManager);
    }

    @Test
    @DisplayName("Should return false when user is not owner and not admin")
    void deleteInteraction_WithUnauthorizedUser_ShouldReturnFalse() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(testUser.getRole()).thenReturn(User.UserRole.USER);

        // When.
        boolean result = interactionManager.deleteInteraction(1, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(interactionRepo).findById(1);
        verifyNoMoreInteractions(interactionRepo);
        verifyNoInteractions(userRepo, historyManager);
    }

    // Get Interactions Tests.

    @Test
    @DisplayName("Should return interactions sorted by likes and creation time")
    void getInteractions_WithValidCourseId_ShouldReturnSortedInteractions() {
        // Given.
        List<Interaction> expectedInteractions = Arrays.asList(testInteraction);
        when(interactionRepo.findByCourseIdOrderByLikesDescCreatedAtDesc((short) 1))
            .thenReturn(expectedInteractions);

        // When.
        List<Interaction> result = interactionManager.getInteractions((short) 1);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedInteractions);

        verify(interactionRepo).findByCourseIdOrderByLikesDescCreatedAtDesc((short) 1);
    }

    @Test
    @DisplayName("Should return empty list when course has no interactions")
    void getInteractions_WithCourseHavingNoInteractions_ShouldReturnEmptyList() {
        // Given.
        when(interactionRepo.findByCourseIdOrderByLikesDescCreatedAtDesc((short) 999))
            .thenReturn(Arrays.asList());

        // When.
        List<Interaction> result = interactionManager.getInteractions((short) 999);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(interactionRepo).findByCourseIdOrderByLikesDescCreatedAtDesc((short) 999);
    }

    // Get Interactions By User Tests.

    @Test
    @DisplayName("Should return all interactions by specific user")
    void getInteractionsByUser_WithValidUserId_ShouldReturnUserInteractions() {
        // Given.
        List<Interaction> expectedInteractions = Arrays.asList(testInteraction);
        when(interactionRepo.findByUserId(2)).thenReturn(expectedInteractions);

        // When.
        List<Interaction> result = interactionManager.getInteractionsByUser(2);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedInteractions);

        verify(interactionRepo).findByUserId(2);
    }

    @Test
    @DisplayName("Should return empty list when user has no interactions")
    void getInteractionsByUser_WithUserHavingNoInteractions_ShouldReturnEmptyList() {
        // Given.
        when(interactionRepo.findByUserId(999)).thenReturn(Arrays.asList());

        // When.
        List<Interaction> result = interactionManager.getInteractionsByUser(999);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(interactionRepo).findByUserId(999);
    }

    // Like Interaction Tests.

    @Test
    @DisplayName("Should successfully like interaction when user hasn't liked it")
    void likeInteraction_WithUserNotLiked_ShouldReturnTrue() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(historyManager.getLikeStatus(testUser, testInteraction)).thenReturn(false);
        when(userRepo.save(testCreator)).thenReturn(testCreator);
        when(interactionRepo.save(testInteraction)).thenReturn(testInteraction);

        // When.
        boolean result = interactionManager.likeInteraction(1, testUser);

        // Then.
        assertThat(result).isTrue();

        verify(interactionRepo).findById(1);
        verify(historyManager).getLikeStatus(testUser, testInteraction);
        verify(historyManager).logLikeInteraction(testUser, testInteraction);
        verify(testCreator).addActivity(2);
        verify(userRepo).save(testCreator);
        verify(testInteraction).likes();
        verify(interactionRepo).save(testInteraction);
    }

    @Test
    @DisplayName("Should return false when user has already liked interaction")
    void likeInteraction_WithUserAlreadyLiked_ShouldReturnFalse() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(historyManager.getLikeStatus(testUser, testInteraction)).thenReturn(true);

        // When.
        boolean result = interactionManager.likeInteraction(1, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(interactionRepo).findById(1);
        verify(historyManager).getLikeStatus(testUser, testInteraction);
        verifyNoMoreInteractions(historyManager, userRepo, interactionRepo);
    }

    @Test
    @DisplayName("Should return false when interaction does not exist")
    void likeInteraction_WithNonExistentInteraction_ShouldReturnFalse() {
        // Given.
        when(interactionRepo.findById(999)).thenReturn(Optional.empty());

        // When.
        boolean result = interactionManager.likeInteraction(999, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(interactionRepo).findById(999);
        verifyNoInteractions(historyManager, userRepo);
    }

    // Unlike Interaction Tests.

    @Test
    @DisplayName("Should successfully unlike interaction when user has liked it")
    void unlikeInteraction_WithUserHasLiked_ShouldReturnTrue() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(historyManager.getLikeStatus(testUser, testInteraction)).thenReturn(true);
        when(userRepo.save(testCreator)).thenReturn(testCreator);
        when(interactionRepo.save(testInteraction)).thenReturn(testInteraction);

        // When.
        boolean result = interactionManager.unlikeInteraction(1, testUser);

        // Then.
        assertThat(result).isTrue();

        verify(interactionRepo).findById(1);
        verify(historyManager).getLikeStatus(testUser, testInteraction);
        verify(historyManager).logUnlikeInteraction(testUser, testInteraction);
        verify(testCreator).addActivity(-1);
        verify(userRepo).save(testCreator);
        verify(testInteraction).unlikes();
        verify(interactionRepo).save(testInteraction);
    }

    @Test
    @DisplayName("Should return false when user hasn't liked interaction")
    void unlikeInteraction_WithUserNotLiked_ShouldReturnFalse() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(historyManager.getLikeStatus(testUser, testInteraction)).thenReturn(false);

        // When.
        boolean result = interactionManager.unlikeInteraction(1, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(interactionRepo).findById(1);
        verify(historyManager).getLikeStatus(testUser, testInteraction);
        verifyNoMoreInteractions(historyManager, userRepo, interactionRepo);
    }

    @Test
    @DisplayName("Should return false when interaction does not exist for unlike")
    void unlikeInteraction_WithNonExistentInteraction_ShouldReturnFalse() {
        // Given.
        when(interactionRepo.findById(999)).thenReturn(Optional.empty());

        // When.
        boolean result = interactionManager.unlikeInteraction(999, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(interactionRepo).findById(999);
        verifyNoInteractions(historyManager, userRepo);
    }

    // Get User Interaction Status Tests.

    @Test
    @DisplayName("Should return true when user has liked interaction")
    void getUserInteractionStatus_WithUserLiked_ShouldReturnTrue() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(historyManager.getLikeStatus(testUser, testInteraction)).thenReturn(true);

        // When.
        boolean result = interactionManager.getUserInteractionStatus(1, testUser);

        // Then.
        assertThat(result).isTrue();

        verify(interactionRepo).findById(1);
        verify(historyManager).getLikeStatus(testUser, testInteraction);
    }

    @Test
    @DisplayName("Should return false when user hasn't liked interaction")
    void getUserInteractionStatus_WithUserNotLiked_ShouldReturnFalse() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(historyManager.getLikeStatus(testUser, testInteraction)).thenReturn(false);

        // When.
        boolean result = interactionManager.getUserInteractionStatus(1, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(interactionRepo).findById(1);
        verify(historyManager).getLikeStatus(testUser, testInteraction);
    }

    @Test
    @DisplayName("Should return false when interaction does not exist for status check")
    void getUserInteractionStatus_WithNonExistentInteraction_ShouldReturnFalse() {
        // Given.
        when(interactionRepo.findById(999)).thenReturn(Optional.empty());

        // When.
        boolean result = interactionManager.getUserInteractionStatus(999, testUser);

        // Then.
        assertThat(result).isFalse();

        verify(interactionRepo).findById(999);
        verifyNoInteractions(historyManager);
    }

    // Edge Cases and Integration Tests.

    @Test
    @DisplayName("Should handle null content in creation DTO")
    void addInteraction_WithNullContent_ShouldCreateInteractionWithNullContent() {
        // Given.
        InteractionCreationDto nullContentDto = new InteractionCreationDto((short) 1, null, (byte) 4);
        
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(interactionRepo.findByCourseAndUser(testCourse, testUser)).thenReturn(Optional.empty());
        when(interactionRepo.save(any(Interaction.class))).thenReturn(testInteraction);
        when(userRepo.save(testUser)).thenReturn(testUser);

        // When.
        Interaction result = interactionManager.addInteraction(nullContentDto, testUser);

        // Then.
        assertThat(result).isNotNull();
        
        verify(interactionRepo).save(any(Interaction.class));
        verify(historyManager).logCreateInteraction(testUser, testInteraction);
        verify(historyManager).logRateCourse(testUser, testCourse); // Still has rating
    }

    @Test
    @DisplayName("Should maintain consistency across like and unlike operations")
    void likeUnlikeInteraction_ShouldMaintainConsistency() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        
        // First: User hasn't liked, then likes.
        when(historyManager.getLikeStatus(testUser, testInteraction)).thenReturn(false);
        when(userRepo.save(testCreator)).thenReturn(testCreator);
        when(interactionRepo.save(testInteraction)).thenReturn(testInteraction);

        // When - Like.
        boolean likeResult = interactionManager.likeInteraction(1, testUser);

        // Then.
        assertThat(likeResult).isTrue();
        verify(testInteraction).likes();

        // Given - Now user has liked.
        when(historyManager.getLikeStatus(testUser, testInteraction)).thenReturn(true);

        // When - Unlike.
        boolean unlikeResult = interactionManager.unlikeInteraction(1, testUser);

        // Then.
        assertThat(unlikeResult).isTrue();
        verify(testInteraction).unlikes();

        // Verify activity changes.
        verify(testCreator).addActivity(2); // Like
        verify(testCreator).addActivity(-1); // Unlike
    }

    @Test
    @DisplayName("Should handle both content and rating updates correctly")
    void updateInteraction_WithBothContentAndRating_ShouldUpdateBoth() {
        // Given.
        InteractionUpdateDto bothDto = new InteractionUpdateDto(1, "Both content and rating", (byte) 9);
        
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(interactionRepo.save(testInteraction)).thenReturn(testInteraction);

        // When.
        Interaction result = interactionManager.updateInteraction(bothDto, testCreator);

        // Then.
        assertThat(result).isNotNull();
        
        verify(testInteraction).setContent("Both content and rating");
        verify(testInteraction).setRating((byte) 9);
        verify(historyManager).logUpdateInteraction(testCreator, testInteraction);
        verify(historyManager).logRateCourse(testCreator, testCourse);
    }

    @Test
    @DisplayName("Should correctly handle the activity deletion workflow")
    void deleteInteraction_ShouldCorrectlyCalculateActivity() {
        // Given.
        when(interactionRepo.findById(1)).thenReturn(Optional.of(testInteraction));
        when(userRepo.save(testCreator)).thenReturn(testCreator);

        // When.
        boolean result = interactionManager.deleteInteraction(1, testCreator);

        // Then.
        assertThat(result).isTrue();
        
        // Verify the activity is correctly reduced.
        verify(testCreator).addActivity(-5); // Should be negative for deletion
        verify(userRepo).save(testCreator);
        verify(interactionRepo).delete(testInteraction);
        verify(historyManager).logDeleteInteraction(testCreator, testInteraction);
    }

    @Test
    @DisplayName("Should handle empty lists correctly")
    void getInteractions_WithMultipleCalls_ShouldHandleCorrectly() {
        // Given.
        List<Interaction> nonEmptyList = Arrays.asList(testInteraction);
        List<Interaction> emptyList = Arrays.asList();
        
        when(interactionRepo.findByCourseIdOrderByLikesDescCreatedAtDesc((short) 1))
            .thenReturn(nonEmptyList);
        when(interactionRepo.findByCourseIdOrderByLikesDescCreatedAtDesc((short) 2))
            .thenReturn(emptyList);
        when(interactionRepo.findByUserId(2)).thenReturn(nonEmptyList);
        when(interactionRepo.findByUserId(999)).thenReturn(emptyList);

        // When.
        List<Interaction> courseResult1 = interactionManager.getInteractions((short) 1);
        List<Interaction> courseResult2 = interactionManager.getInteractions((short) 2);
        List<Interaction> userResult1 = interactionManager.getInteractionsByUser(2);
        List<Interaction> userResult2 = interactionManager.getInteractionsByUser(999);

        // Then.
        assertThat(courseResult1).hasSize(1);
        assertThat(courseResult2).isEmpty();
        assertThat(userResult1).hasSize(1);
        assertThat(userResult2).isEmpty();
    }
}