package org.opencourse.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.opencourse.models.History;
import org.opencourse.models.User;
import org.opencourse.models.Department;
import org.opencourse.models.Course;
import org.opencourse.utils.typeinfo.ActionType;
import org.opencourse.utils.typeinfo.CourseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link HistoryRepo}.
 * 
 * @author !EEExp3rt
 */
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = {HistoryRepo.class, UserRepo.class, DepartmentRepo.class, CourseRepo.class})
@EntityScan(basePackageClasses = {History.class, User.class, Department.class, Course.class})
class HistoryRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HistoryRepo historyRepo;

    // Data.
    private User adminUser;
    private User normalUser;
    private User visitorUser;
    private Department department;
    private Course course;
    private History history1;
    private History history2;
    private History history3;
    private History history4;
    private History history5;

    @BeforeEach
    void setUp() {
        // Establish departments for testing.
        department = new Department("计算机科学与技术学院");
        entityManager.persistAndFlush(department);

        // Establish users for testing.
        adminUser = new User("admin", "admin@example.com", "hashedPassword123", User.UserRole.ADMIN);
        normalUser = new User("normaluser", "user@example.com", "hashedPassword456", User.UserRole.USER);
        visitorUser = new User("visitor", "visitor@example.com", "hashedPassword789", User.UserRole.VISITOR);

        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(normalUser);
        entityManager.persistAndFlush(visitorUser);

        // Establish course for testing.
        course = new Course("数据结构", "CS101", department, CourseType.MAJOR_REQUIRED, new BigDecimal("3.0"));
        entityManager.persistAndFlush(course);

        // Establish histories for testing.
        history1 = new History(normalUser, ActionType.CREATE_COURSE, course.getId().intValue());
        history2 = new History(normalUser, ActionType.VIEW_RESOURCE, 1001);
        history3 = new History(normalUser, ActionType.LIKE_RESOURCE, 1001);
        history4 = new History(adminUser, ActionType.UPDATE_COURSE, course.getId().intValue());
        history5 = new History(adminUser, ActionType.DELETE_DEPARTMENT, department.getId().intValue());

        // Set different timestamps for each history record.
        LocalDateTime baseTime = LocalDateTime.now().minusDays(5);

        entityManager.persist(history1);
        entityManager.flush();
        history1.setTimestamp(baseTime.plusDays(1));
        entityManager.merge(history1);

        entityManager.persist(history2);
        entityManager.flush();
        history2.setTimestamp(baseTime.plusDays(2));
        entityManager.merge(history2);

        entityManager.persist(history3);
        entityManager.flush();
        history3.setTimestamp(baseTime.plusDays(3));
        entityManager.merge(history3);

        entityManager.persist(history4);
        entityManager.flush();
        history4.setTimestamp(baseTime.plusDays(4));
        entityManager.merge(history4);

        entityManager.persist(history5);
        entityManager.flush();
        history5.setTimestamp(baseTime.plusDays(5));
        entityManager.merge(history5);

        entityManager.flush();
    }

    @Test
    void contextLoads() {
        assertThat(historyRepo).isNotNull();
        assertThat(entityManager).isNotNull();
    }

    @Test
    void testFindAllByUserIdOrderByTimestampDesc_WhenUserHasHistory_ShouldReturnOrderedList() {
        // When
        List<History> normalUserHistories = historyRepo.findAllByUserIdOrderByTimestampDesc(normalUser.getId());

        // Then
        assertThat(normalUserHistories).hasSize(3);
        assertThat(normalUserHistories.get(0).getActionType()).isEqualTo(ActionType.LIKE_RESOURCE);
        assertThat(normalUserHistories.get(1).getActionType()).isEqualTo(ActionType.VIEW_RESOURCE);
        assertThat(normalUserHistories.get(2).getActionType()).isEqualTo(ActionType.CREATE_COURSE);

        LocalDateTime firstTimestamp = normalUserHistories.get(0).getTimestamp();
        LocalDateTime secondTimestamp = normalUserHistories.get(1).getTimestamp();
        LocalDateTime thirdTimestamp = normalUserHistories.get(2).getTimestamp();
        assertThat(firstTimestamp).isAfter(secondTimestamp);
        assertThat(secondTimestamp).isAfter(thirdTimestamp);
    }

    @Test
    void testFindAllByUserIdOrderByTimestampDesc_WhenUserHasNoHistory_ShouldReturnEmptyList() {
        // When
        List<History> histories = historyRepo.findAllByUserIdOrderByTimestampDesc(visitorUser.getId());

        // Then
        assertThat(histories).isEmpty();
    }

    @Test
    void testFindAllByUserIdOrderByTimestampDesc_WhenUserIdNotExists_ShouldReturnEmptyList() {
        // When
        List<History> histories = historyRepo.findAllByUserIdOrderByTimestampDesc(99999);

        // Then
        assertThat(histories).isEmpty();
    }

    @Test
    void testFindAllByUserIdOrderByTimestampDesc_MultipleUsers() {
        // When
        List<History> normalUserHistories = historyRepo.findAllByUserIdOrderByTimestampDesc(normalUser.getId());
        List<History> adminUserHistories = historyRepo.findAllByUserIdOrderByTimestampDesc(adminUser.getId());

        // Then
        assertThat(normalUserHistories).hasSize(3);
        assertThat(adminUserHistories).hasSize(2);

        assertThat(normalUserHistories).allMatch(history -> history.getUser().getId().equals(normalUser.getId()));

        assertThat(adminUserHistories).allMatch(history -> history.getUser().getId().equals(adminUser.getId()));
        assertThat(adminUserHistories.get(0).getActionType()).isEqualTo(ActionType.DELETE_DEPARTMENT);
        assertThat(adminUserHistories.get(1).getActionType()).isEqualTo(ActionType.UPDATE_COURSE);
    }

    @Test
    void testFindFirstByUserAndObjectIdAndActionTypeIn_WhenMatchingRecord_ShouldReturnLatest() {
        // Given
        Integer objectId = 1001;
        List<ActionType> resourceActionTypes = Arrays.asList(
            ActionType.VIEW_RESOURCE, 
            ActionType.LIKE_RESOURCE, 
            ActionType.UNLIKE_RESOURCE
        );

        // When
        Optional<History> latestHistory = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            normalUser, objectId, resourceActionTypes);

        // Then
        assertThat(latestHistory).isPresent();
        assertThat(latestHistory.get().getActionType()).isEqualTo(ActionType.LIKE_RESOURCE);
        assertThat(latestHistory.get().getUser()).isEqualTo(normalUser);
        assertThat(latestHistory.get().getObjectId()).isEqualTo(objectId);
    }

    @Test
    void testFindFirstByUserAndObjectIdAndActionTypeIn_WhenNoMatchingRecord_ShouldReturnEmpty() {
        // Given
        Integer objectId = 9999;
        List<ActionType> actionTypes = Arrays.asList(ActionType.VIEW_RESOURCE);

        // When
        Optional<History> result = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            normalUser, objectId, actionTypes);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testFindFirstByUserAndObjectIdAndActionTypeIn_WhenDifferentUser_ShouldReturnEmpty() {
        // Given
        Integer objectId = 1001;
        List<ActionType> actionTypes = Arrays.asList(ActionType.VIEW_RESOURCE, ActionType.LIKE_RESOURCE);

        // When
        Optional<History> result = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            adminUser, objectId, actionTypes);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testFindFirstByUserAndObjectIdAndActionTypeIn_WhenDifferentActionType_ShouldReturnEmpty() {
        // Given
        Integer objectId = 1001;
        List<ActionType> interactionActionTypes = Arrays.asList(
            ActionType.CREATE_INTERACTION, 
            ActionType.LIKE_INTERACTION
        );

        // When
        Optional<History> result = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            normalUser, objectId, interactionActionTypes);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testFindFirstByUserAndObjectIdAndActionTypeIn_MultipleCourseActions() {
        // Given
        Integer courseId = course.getId().intValue();
        List<ActionType> courseActionTypes = Arrays.asList(
            ActionType.CREATE_COURSE, 
            ActionType.UPDATE_COURSE, 
            ActionType.DELETE_COURSE
        );

        // When
        Optional<History> latestCourseAction = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            normalUser, courseId, courseActionTypes);

        // Then
        assertThat(latestCourseAction).isPresent();
        assertThat(latestCourseAction.get().getActionType()).isEqualTo(ActionType.CREATE_COURSE);
        assertThat(latestCourseAction.get().getObjectId()).isEqualTo(courseId);
    }

    @Test
    void testHistoryEntityBasicFunctionality() {
        // When
        Optional<History> foundHistory = historyRepo.findById(history1.getId());

        // Then
        assertThat(foundHistory).isPresent();
        History history = foundHistory.get();

        assertThat(history.getId()).isNotNull();
        assertThat(history.getUser()).isEqualTo(normalUser);
        assertThat(history.getActionType()).isEqualTo(ActionType.CREATE_COURSE);
        assertThat(history.getObjectId()).isEqualTo(course.getId().intValue());
        assertThat(history.getTimestamp()).isNotNull();
    }

    @Test
    void testHistoryWithoutObjectId() {
        // Given
        History userCreationHistory = new History(normalUser, ActionType.CREATE_USER);

        // When
        History saved = historyRepo.save(userCreationHistory);
        Optional<History> found = historyRepo.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getObjectId()).isNull();
        assertThat(found.get().getActionType()).isEqualTo(ActionType.CREATE_USER);
        assertThat(found.get().getUser()).isEqualTo(normalUser);
    }

    @Test
    void testUserEntityFunctionality() {
        // When
        Optional<History> foundHistory = historyRepo.findById(history1.getId());

        // Then
        assertThat(foundHistory).isPresent();
        User historyUser = foundHistory.get().getUser();

        assertThat(historyUser.getId()).isEqualTo(normalUser.getId());
        assertThat(historyUser.getName()).isEqualTo("normaluser");
        assertThat(historyUser.getEmail()).isEqualTo("user@example.com");
        assertThat(historyUser.getRole()).isEqualTo(User.UserRole.USER);
        assertThat(historyUser.getActivity()).isEqualTo(1);
        assertThat(historyUser.getCreatedAt()).isNotNull();
        assertThat(historyUser.getUpdatedAt()).isNull();
    }

    @Test
    void testUserRoleEnumFunctionality() {
        // When
        List<History> allHistories = historyRepo.findAll();

        // Then
        History adminHistory = allHistories.stream()
            .filter(h -> h.getUser().getRole() == User.UserRole.ADMIN)
            .findFirst()
            .orElse(null);

        History userHistory = allHistories.stream()
            .filter(h -> h.getUser().getRole() == User.UserRole.USER)
            .findFirst()
            .orElse(null);

        assertThat(adminHistory).isNotNull();
        assertThat(adminHistory.getUser().getName()).isEqualTo("admin");
        assertThat(adminHistory.getUser().getRole()).isEqualTo(User.UserRole.ADMIN);

        assertThat(userHistory).isNotNull();
        assertThat(userHistory.getUser().getName()).isEqualTo("normaluser");
        assertThat(userHistory.getUser().getRole()).isEqualTo(User.UserRole.USER);
    }

    @Test
    void testActionTypeEnumFunctionality() {
        // When
        List<History> allHistories = historyRepo.findAll();

        // Then
        assertThat(allHistories).isNotEmpty();

        History courseHistory = allHistories.stream()
            .filter(h -> h.getActionType() == ActionType.CREATE_COURSE)
            .findFirst()
            .orElse(null);

        assertThat(courseHistory).isNotNull();
        assertThat(courseHistory.getActionType().getId()).isEqualTo((byte) 21);
        assertThat(courseHistory.getActionType().getName()).isEqualTo("Create-Course");
        assertThat(courseHistory.getActionType().getDescription()).isEqualTo("创建课程");
        assertThat(courseHistory.getActionType().getObjectClass()).isEqualTo(Course.class);
    }

    @Test
    void testActionTypeGetById() {
        // When & Then
        assertThat(ActionType.getById((byte) 21)).isEqualTo(ActionType.CREATE_COURSE);
        assertThat(ActionType.getById((byte) 30)).isEqualTo(ActionType.LIKE_RESOURCE);
        assertThat(ActionType.getById((byte) 38)).isEqualTo(ActionType.RATE_COURSE);
        assertThat(ActionType.getById((byte) 39)).isEqualTo(ActionType.CREATE_USER);
        assertThat(ActionType.getById((byte) 99)).isNull();
    }

    @Test
    void testTimestampAutoGeneration() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now();
        History newHistory = new History(normalUser, ActionType.VIEW_RESOURCE, 2001);

        // When
        History saved = historyRepo.save(newHistory);
        LocalDateTime afterSave = LocalDateTime.now();

        // Then
        assertThat(saved.getTimestamp()).isNotNull();
        assertThat(saved.getTimestamp()).isAfter(beforeSave.minusSeconds(1));
        assertThat(saved.getTimestamp()).isBefore(afterSave.plusSeconds(1));
    }

    @Test
    void testHistoryToString() {
        // When
        String historyString = history1.toString();

        // Then
        assertThat(historyString).contains("History{");
        assertThat(historyString).contains("id=" + history1.getId());
        assertThat(historyString).contains("actionType=" + ActionType.CREATE_COURSE);
        assertThat(historyString).contains("objectId=" + course.getId().intValue());
        assertThat(historyString).contains("timestamp=");
    }

    @Test
    void testUserToString() {
        // When
        String userString = normalUser.toString();

        // Then
        assertThat(userString).contains("User{");
        assertThat(userString).contains("id=" + normalUser.getId());
        assertThat(userString).contains("name='normaluser'");
        assertThat(userString).contains("email='user@example.com'");
        assertThat(userString).contains("role=" + User.UserRole.USER);
        assertThat(userString).contains("activity=1");
    }

    @Test
    void testUserRelationship() {
        // When
        List<History> normalUserHistories = historyRepo.findAllByUserIdOrderByTimestampDesc(normalUser.getId());

        // Then
        assertThat(normalUserHistories).hasSize(3);
        assertThat(normalUserHistories).allMatch(history -> {
            User historyUser = history.getUser();
            return historyUser.getId().equals(normalUser.getId()) &&
                   historyUser.getName().equals("normaluser") &&
                   historyUser.getEmail().equals("user@example.com") &&
                   historyUser.getRole().equals(User.UserRole.USER);
        });
    }

    @Test
    void testComplexQueryScenarios() {
        List<ActionType> resourceActions = Arrays.asList(
            ActionType.VIEW_RESOURCE, 
            ActionType.LIKE_RESOURCE
        );

        Optional<History> latestResourceAction = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            normalUser, 1001, resourceActions);

        assertThat(latestResourceAction).isPresent();
        assertThat(latestResourceAction.get().getActionType()).isEqualTo(ActionType.LIKE_RESOURCE);

        List<ActionType> courseActions = Arrays.asList(
            ActionType.CREATE_COURSE, 
            ActionType.UPDATE_COURSE
        );

        Optional<History> normalUserCourseAction = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            normalUser, course.getId().intValue(), courseActions);
        Optional<History> adminUserCourseAction = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            adminUser, course.getId().intValue(), courseActions);

        assertThat(normalUserCourseAction).isPresent();
        assertThat(normalUserCourseAction.get().getActionType()).isEqualTo(ActionType.CREATE_COURSE);

        assertThat(adminUserCourseAction).isPresent();
        assertThat(adminUserCourseAction.get().getActionType()).isEqualTo(ActionType.UPDATE_COURSE);
    }

    @Test
    void testUserActivityFunctionality() {
        // Given
        normalUser.addActivity(5);
        entityManager.merge(normalUser);
        entityManager.flush();

        // When
        List<History> histories = historyRepo.findAllByUserIdOrderByTimestampDesc(normalUser.getId());

        // Then
        assertThat(histories).isNotEmpty();
        User historyUser = histories.get(0).getUser();
        assertThat(historyUser.getActivity()).isEqualTo(6);
    }

    @Test
    void testUserRoleBasedActions() {
        // Given
        History visitorAction = new History(visitorUser, ActionType.VIEW_RESOURCE, 2001);
        History userAction = new History(normalUser, ActionType.LIKE_RESOURCE, 2001);
        History adminAction = new History(adminUser, ActionType.CREATE_COURSE, course.getId().intValue());

        historyRepo.saveAll(Arrays.asList(visitorAction, userAction, adminAction));

        // When
        List<History> visitorHistories = historyRepo.findAllByUserIdOrderByTimestampDesc(visitorUser.getId());
        List<History> userHistories = historyRepo.findAllByUserIdOrderByTimestampDesc(normalUser.getId());
        List<History> adminHistories = historyRepo.findAllByUserIdOrderByTimestampDesc(adminUser.getId());

        // Then
        assertThat(visitorHistories).hasSize(1);
        assertThat(visitorHistories.get(0).getUser().getRole()).isEqualTo(User.UserRole.VISITOR);

        assertThat(userHistories).hasSize(4);
        assertThat(userHistories.get(0).getUser().getRole()).isEqualTo(User.UserRole.USER);

        assertThat(adminHistories).hasSize(3);
        assertThat(adminHistories.get(0).getUser().getRole()).isEqualTo(User.UserRole.ADMIN);
    }

    @Test
    void testEdgeCases() {
        Optional<History> emptyActionTypes = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByTimestampDesc(
            normalUser, 1001, Arrays.asList());
        assertThat(emptyActionTypes).isEmpty();

        History nullObjectHistory = new History(normalUser, ActionType.CREATE_USER);
        historyRepo.save(nullObjectHistory);

        List<History> normalUserAllHistories = historyRepo.findAllByUserIdOrderByTimestampDesc(normalUser.getId());
        assertThat(normalUserAllHistories).hasSize(4);

        boolean hasNullObjectHistory = normalUserAllHistories.stream()
            .anyMatch(h -> h.getObjectId() == null && h.getActionType() == ActionType.CREATE_USER);
        assertThat(hasNullObjectHistory).isTrue();
    }

    @Test
    void testDataConsistency() {
        // When
        List<History> allHistories = historyRepo.findAll();

        // Then
        assertThat(allHistories).hasSize(5);

        assertThat(allHistories).allMatch(history -> history.getUser() != null);
        assertThat(allHistories).allMatch(history -> history.getUser().getId() != null);
        assertThat(allHistories).allMatch(history -> history.getUser().getName() != null);
        assertThat(allHistories).allMatch(history -> history.getUser().getEmail() != null);
        assertThat(allHistories).allMatch(history -> history.getUser().getRole() != null);

        assertThat(allHistories).allMatch(history -> history.getActionType() != null);

        assertThat(allHistories).allMatch(history -> history.getTimestamp() != null);

        assertThat(allHistories).allMatch(history -> history.getUser().getCreatedAt() != null);
    }

    @Test
    void testUserPasswordSecurity() {
        // When
        String userString = normalUser.toString();

        // Then
        assertThat(userString).doesNotContain("hashedPassword456");
        assertThat(userString).doesNotContain("password");

        assertThat(normalUser.getPassword()).isEqualTo("hashedPassword456");
    }
}
