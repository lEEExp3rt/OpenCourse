package org.opencourse.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opencourse.models.*;
import org.opencourse.repositories.HistoryRepo;
import org.opencourse.services.history.HistoryObjectService;
import org.opencourse.utils.typeinfo.ActionType;
import org.opencourse.utils.typeinfo.CourseType;
import org.opencourse.utils.typeinfo.ResourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link HistoryManager}.
 * 
 * @author !EEExp3rt
 */
@ExtendWith(MockitoExtension.class)
class HistoryManagerTest {

    @Mock
    private HistoryRepo historyRepo;

    @Mock
    private HistoryObjectService historyObjectService;

    @InjectMocks
    private HistoryManager historyManager;

    // Test data.
    private User testUser;
    private Course testCourse;
    private Department testDepartment;
    private Resource testResource;
    private Interaction testInteraction;
    private History testHistory;

    @BeforeEach
    void setUp() {
        // Create test user.
        testUser = new User(
            "testUser",
            "test@example.com",
            "hashedPassword",
            User.UserRole.USER
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

        // Create test resource.
        Resource.ResourceFile testFile = new Resource.ResourceFile(
            Resource.ResourceFile.FileType.PDF, 
            new BigDecimal("1.0"), 
            "/test.pdf"
        );
        testResource = new Resource(
            "Test Resource",
            "Description",
            ResourceType.EXAM,
            testFile,
            testCourse,
            testUser
        );
        testResource = spy(testResource);
        lenient().when(testResource.getId()).thenReturn(1);

        // Create test interaction.
        testInteraction = new Interaction(
            testCourse,
            testUser,
            "Test comment"
        );
        testInteraction = spy(testInteraction);
        lenient().when(testInteraction.getId()).thenReturn(1);

        // Create test history.
        testHistory = new History(
            testUser,
            ActionType.CREATE_COURSE,
            testCourse.getId().intValue()
        );
        testHistory = spy(testHistory);
        lenient().when(testHistory.getId()).thenReturn(1L);
        lenient().when(testHistory.getTimestamp()).thenReturn(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return user histories in descending order of timestamp")
    void getHistories_WithValidUserId_ShouldReturnHistoriesInDescendingOrder() {
        // Given.
        Integer userId = 1;
        History history1 = new History(testUser, ActionType.CREATE_COURSE, 1);
        History history2 = new History(testUser, ActionType.UPDATE_COURSE, 1);
        List<History> expectedHistories = Arrays.asList(history1, history2);

        when(historyRepo.findAllByUserIdOrderByTimestampDesc(userId))
            .thenReturn(expectedHistories);

        // When.
        List<History> result = historyManager.getHistories(userId);

        // Then.
        assertThat(result).isEqualTo(expectedHistories);
        assertThat(result).hasSize(2);
        verify(historyRepo).findAllByUserIdOrderByTimestampDesc(userId);
    }

    @Test
    @DisplayName("Should return empty list when user has no histories")
    void getHistories_WithUserWithoutHistories_ShouldReturnEmptyList() {
        // Given.
        Integer userId = 999;
        List<History> emptyHistories = Arrays.asList();

        when(historyRepo.findAllByUserIdOrderByTimestampDesc(userId))
            .thenReturn(emptyHistories);

        // When.
        List<History> result = historyManager.getHistories(userId);

        // Then.
        assertThat(result).isEmpty();
        verify(historyRepo).findAllByUserIdOrderByTimestampDesc(userId);
    }

    @Test
    @DisplayName("Should return history object when history is valid")
    void getHistoryObject_WithValidHistory_ShouldReturnObject() {
        // Given.
        doReturn(testCourse).when(historyObjectService).getHistoryObject(testHistory);

        // When.
        Model<? extends Number> result = historyManager.getHistoryObject(testHistory);

        // Then.
        assertThat(result).isEqualTo(testCourse);
        verify(historyObjectService).getHistoryObject(testHistory);
    }

    @Test
    @DisplayName("Should return null when history is null")
    void getHistoryObject_WithNullHistory_ShouldReturnNull() {
        // Given.
        History nullHistory = null;

        doReturn(null).when(historyObjectService).getHistoryObject(nullHistory);

        // When.
        Model<? extends Number> result = historyManager.getHistoryObject(nullHistory);

        // Then.
        assertThat(result).isNull();
        verify(historyObjectService).getHistoryObject(nullHistory);
    }

    @Test
    @DisplayName("Should return true when user has liked interaction")
    void getLikeStatus_WithLikedInteraction_ShouldReturnTrue() {
        // Given.
        History likeHistory = new History(
            testUser,
            ActionType.LIKE_INTERACTION,
            testInteraction.getId()
        );
        List<ActionType> actionTypes = Arrays.asList(
            ActionType.LIKE_INTERACTION,
            ActionType.UNLIKE_INTERACTION
        );
        Integer id = testInteraction.getId();

        when(historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        )).thenReturn(Optional.of(likeHistory));

        // When.
        boolean result = historyManager.getLikeStatus(testUser, testInteraction);

        // Then.
        assertThat(result).isTrue();
        verify(historyRepo).findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        );
    }

    @Test
    @DisplayName("Should return false when user has unliked interaction")
    void getLikeStatus_WithUnlikedInteraction_ShouldReturnFalse() {
        // Given.
        History unlikeHistory = new History(
            testUser,
            ActionType.UNLIKE_INTERACTION,
            testInteraction.getId()
        );
        Integer id = testInteraction.getId();
        List<ActionType> actionTypes = Arrays.asList(
            ActionType.LIKE_INTERACTION,
            ActionType.UNLIKE_INTERACTION
        );

        when(historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        )).thenReturn(Optional.of(unlikeHistory));

        // When.
        boolean result = historyManager.getLikeStatus(testUser, testInteraction);

        // Then.
        assertThat(result).isFalse();
        verify(historyRepo).findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        );
    }

    @Test
    @DisplayName("Should return false when no like history exists for interaction")
    void getLikeStatus_WithNoHistoryForInteraction_ShouldReturnFalse() {
        // Given.
        Integer id = testInteraction.getId();
        List<ActionType> actionTypes = Arrays.asList(
            ActionType.LIKE_INTERACTION,
            ActionType.UNLIKE_INTERACTION
        );

        when(historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        )).thenReturn(Optional.empty());

        // When.
        boolean result = historyManager.getLikeStatus(testUser, testInteraction);

        // Then.
        assertThat(result).isFalse();
        verify(historyRepo).findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        );
    }

    @Test
    @DisplayName("Should return true when user has liked resource")
    void getLikeStatus_WithLikedResource_ShouldReturnTrue() {
        // Given.
        History likeHistory = new History(
            testUser,
            ActionType.LIKE_RESOURCE,
            testResource.getId()
        );
        Integer id = testResource.getId();
        List<ActionType> actionTypes = Arrays.asList(
            ActionType.LIKE_RESOURCE,
            ActionType.UNLIKE_RESOURCE
        );

        when(historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        )).thenReturn(Optional.of(likeHistory));

        // When.
        boolean result = historyManager.getLikeStatus(testUser, testResource);

        // Then.
        assertThat(result).isTrue();
        verify(historyRepo).findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        );
    }

    @Test
    @DisplayName("Should return false when user has unliked resource")
    void getLikeStatus_WithUnlikedResource_ShouldReturnFalse() {
        // Given.
        History unlikeHistory = new History(
            testUser,
            ActionType.UNLIKE_RESOURCE,
            testResource.getId()
        );
        Integer id = testResource.getId();
        List<ActionType> actionTypes = Arrays.asList(
            ActionType.LIKE_RESOURCE,
            ActionType.UNLIKE_RESOURCE
        );

        when(historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        )).thenReturn(Optional.of(unlikeHistory));

        // When.
        boolean result = historyManager.getLikeStatus(testUser, testResource);

        // Then.
        assertThat(result).isFalse();
        verify(historyRepo).findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        );
    }

    @Test
    @DisplayName("Should return false when no like history exists for resource")
    void getLikeStatus_WithNoHistoryForResource_ShouldReturnFalse() {
        // Given.
        Integer id = testResource.getId();
        List<ActionType> actionTypes = Arrays.asList(
            ActionType.LIKE_RESOURCE,
            ActionType.UNLIKE_RESOURCE
        );

        when(historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        )).thenReturn(Optional.empty());

        // When.
        boolean result = historyManager.getLikeStatus(testUser, testResource);

        // Then.
        assertThat(result).isFalse();
        verify(historyRepo).findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            eq(testUser),
            eq(id),
            eq(actionTypes)
        );
    }

    // Course logging tests

    @Test
    @DisplayName("Should successfully log create course action")
    void logCreateCourse_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.CREATE_COURSE,
            testCourse.getId().intValue()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logCreateCourse(testUser, testCourse);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.CREATE_COURSE &&
            history.getObjectId().equals(testCourse.getId().intValue())
        ));
    }

    @Test
    @DisplayName("Should throw RuntimeException when save fails for create course")
    void logCreateCourse_WhenSaveFails_ShouldThrowRuntimeException() {
        // Given.
        when(historyRepo.save(any(History.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then.
        assertThatThrownBy(() -> historyManager.logCreateCourse(testUser, testCourse))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Create-Course")
            .hasCauseInstanceOf(RuntimeException.class);

        verify(historyRepo).save(any(History.class));
    }

    @Test
    @DisplayName("Should successfully log update course action")
    void logUpdateCourse_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.UPDATE_COURSE,
            testCourse.getId().intValue()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logUpdateCourse(testUser, testCourse);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.UPDATE_COURSE &&
            history.getObjectId().equals(testCourse.getId().intValue())
        ));
    }

    @Test
    @DisplayName("Should successfully log delete course action")
    void logDeleteCourse_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.DELETE_COURSE,
            testCourse.getId().intValue()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logDeleteCourse(testUser, testCourse);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.DELETE_COURSE &&
            history.getObjectId().equals(testCourse.getId().intValue())
        ));
    }

    // Department logging tests

    @Test
    @DisplayName("Should successfully log create department action")
    void logCreateDepartment_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.CREATE_DEPARTMENT,
            testDepartment.getId().intValue()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logCreateDepartment(testUser, testDepartment);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.CREATE_DEPARTMENT &&
            history.getObjectId().equals(testDepartment.getId().intValue())
        ));
    }

    @Test
    @DisplayName("Should successfully log update department action")
    void logUpdateDepartment_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.UPDATE_DEPARTMENT,
            testDepartment.getId().intValue()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logUpdateDepartment(testUser, testDepartment);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.UPDATE_DEPARTMENT &&
            history.getObjectId().equals(testDepartment.getId().intValue())
        ));
    }

    @Test
    @DisplayName("Should successfully log delete department action")
    void logDeleteDepartment_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.DELETE_DEPARTMENT,
            testDepartment.getId().intValue()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logDeleteDepartment(testUser, testDepartment);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.DELETE_DEPARTMENT &&
            history.getObjectId().equals(testDepartment.getId().intValue())
        ));
    }

    @Test
    @DisplayName("Should throw RuntimeException when save fails for department actions")
    void logDepartmentActions_WhenSaveFails_ShouldThrowRuntimeException() {
        // Given.
        when(historyRepo.save(any(History.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then.
        assertThatThrownBy(() -> historyManager.logCreateDepartment(testUser, testDepartment))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Create-Department")
            .hasCauseInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> historyManager.logUpdateDepartment(testUser, testDepartment))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Update-Department")
            .hasCauseInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> historyManager.logDeleteDepartment(testUser, testDepartment))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Delete-Department")
            .hasCauseInstanceOf(RuntimeException.class);
    }

    // Resource logging tests

    @Test
    @DisplayName("Should successfully log create resource action")
    void logCreateResource_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.CREATE_RESOURCE,
            testResource.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logCreateResource(testUser, testResource);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.CREATE_RESOURCE &&
            history.getObjectId().equals(testResource.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log update resource action")
    void logUpdateResource_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.UPDATE_RESOURCE,
            testResource.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logUpdateResource(testUser, testResource);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.UPDATE_RESOURCE &&
            history.getObjectId().equals(testResource.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log delete resource action")
    void logDeleteResource_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.DELETE_RESOURCE,
            testResource.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logDeleteResource(testUser, testResource);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.DELETE_RESOURCE &&
            history.getObjectId().equals(testResource.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log like resource action")
    void logLikeResource_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.LIKE_RESOURCE,
            testResource.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logLikeResource(testUser, testResource);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.LIKE_RESOURCE &&
            history.getObjectId().equals(testResource.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log unlike resource action")
    void logUnlikeResource_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.UNLIKE_RESOURCE,
            testResource.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logUnlikeResource(testUser, testResource);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.UNLIKE_RESOURCE &&
            history.getObjectId().equals(testResource.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log view resource action")
    void logViewResource_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.VIEW_RESOURCE,
            testResource.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logViewResource(testUser, testResource);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.VIEW_RESOURCE &&
            history.getObjectId().equals(testResource.getId())
        ));
    }

    @Test
    @DisplayName("Should throw RuntimeException when save fails for resource actions")
    void logResourceActions_WhenSaveFails_ShouldThrowRuntimeException() {
        // Given.
        when(historyRepo.save(any(History.class)))
        .thenThrow(new RuntimeException("Database error"));

        // When & Then.
        assertThatThrownBy(() -> historyManager.logCreateResource(testUser, testResource))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Create-Resource");

        assertThatThrownBy(() -> historyManager.logLikeResource(testUser, testResource))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Like-Resource");

        assertThatThrownBy(() -> historyManager.logUnlikeResource(testUser, testResource))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Unlike-Resource");
    }

    // Interaction logging tests

    @Test
    @DisplayName("Should successfully log create interaction action")
    void logCreateInteraction_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.CREATE_INTERACTION,
            testInteraction.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logCreateInteraction(testUser, testInteraction);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.CREATE_INTERACTION &&
            history.getObjectId().equals(testInteraction.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log update interaction action")
    void logUpdateInteraction_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.UPDATE_INTERACTION,
            testInteraction.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logUpdateInteraction(testUser, testInteraction);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.UPDATE_INTERACTION &&
            history.getObjectId().equals(testInteraction.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log delete interaction action")
    void logDeleteInteraction_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.DELETE_INTERACTION,
            testInteraction.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logDeleteInteraction(testUser, testInteraction);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.DELETE_INTERACTION &&
            history.getObjectId().equals(testInteraction.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log like interaction action")
    void logLikeInteraction_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.LIKE_INTERACTION,
            testInteraction.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logLikeInteraction(testUser, testInteraction);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.LIKE_INTERACTION &&
            history.getObjectId().equals(testInteraction.getId())
        ));
    }

    @Test
    @DisplayName("Should successfully log unlike interaction action")
    void logUnlikeInteraction_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.UNLIKE_INTERACTION,
            testInteraction.getId()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logUnlikeInteraction(testUser, testInteraction);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.UNLIKE_INTERACTION &&
            history.getObjectId().equals(testInteraction.getId())
        ));
    }

    @Test
    @DisplayName("Should throw RuntimeException when save fails for interaction actions")
    void logInteractionActions_WhenSaveFails_ShouldThrowRuntimeException() {
        // Given.
        when(historyRepo.save(any(History.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then.
        assertThatThrownBy(() -> historyManager.logCreateInteraction(testUser, testInteraction))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Create-Interaction");

        assertThatThrownBy(() -> historyManager.logLikeInteraction(testUser, testInteraction))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Like-Interaction");

        assertThatThrownBy(() -> historyManager.logUnlikeInteraction(testUser, testInteraction))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Unlike-Interaction");
    }

    // Course rating and user actions tests

    @Test
    @DisplayName("Should successfully log rate course action")
    void logRateCourse_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(
            testUser,
            ActionType.RATE_COURSE,
            testCourse.getId().intValue()
        );
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logRateCourse(testUser, testCourse);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.RATE_COURSE &&
            history.getObjectId().equals(testCourse.getId().intValue())
        ));
    }

    @Test
    @DisplayName("Should successfully log create user action")
    void logCreateUser_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(testUser, ActionType.CREATE_USER);
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logCreateUser(testUser);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.CREATE_USER &&
            history.getObjectId() == null
        ));
    }

    @Test
    @DisplayName("Should successfully log update user action")
    void logUpdateUser_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(testUser, ActionType.UPDATE_USER);
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logUpdateUser(testUser);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.UPDATE_USER &&
            history.getObjectId() == null
        ));
    }

    @Test
    @DisplayName("Should successfully log delete user action")
    void logDeleteUser_WithValidParameters_ShouldSaveHistory() {
        // Given.
        History expectedHistory = new History(testUser, ActionType.DELETE_USER);
        when(historyRepo.save(any(History.class))).thenReturn(expectedHistory);

        // When.
        historyManager.logDeleteUser(testUser);

        // Then.
        verify(historyRepo).save(argThat(history -> 
            history.getUser().equals(testUser) &&
            history.getActionType() == ActionType.DELETE_USER &&
            history.getObjectId() == null
        ));
    }

    @Test
    @DisplayName("Should throw RuntimeException when save fails for user actions")
    void logUserActions_WhenSaveFails_ShouldThrowRuntimeException() {
        // Given.
        when(historyRepo.save(any(History.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then.
        assertThatThrownBy(() -> historyManager.logCreateUser(testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Create-User");

        assertThatThrownBy(() -> historyManager.logUpdateUser(testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Update-User");

        assertThatThrownBy(() -> historyManager.logDeleteUser(testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Delete-User");
    }

    @Test
    @DisplayName("Should throw RuntimeException when save fails for rate course")
    void logRateCourse_WhenSaveFails_ShouldThrowRuntimeException() {
        // Given.
        when(historyRepo.save(any(History.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then.
        assertThatThrownBy(() -> historyManager.logRateCourse(testUser, testCourse))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to log Rate-Course")
            .hasCauseInstanceOf(RuntimeException.class);

        verify(historyRepo).save(any(History.class));
    }
}
