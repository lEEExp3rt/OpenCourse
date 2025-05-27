package org.opencourse.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opencourse.models.Department;
import org.opencourse.models.User;
import org.opencourse.repositories.DepartmentRepo;
import org.opencourse.repositories.UserRepo;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DepartmentManager}.
 * 
 * @author !EEExp3rt
 */
@ExtendWith(MockitoExtension.class)
class DepartmentManagerTest {

    @Mock
    private DepartmentRepo departmentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private HistoryManager historyManager;

    @InjectMocks
    private DepartmentManager departmentManager;

    private User testUser;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        // Create test user.
        testUser = new User(
            "testUser",
            "test@example.com",
            "hashedPassword",
            User.UserRole.ADMIN
        );
        // Simulate user with ID 1.
        testUser = spy(testUser);
        lenient().when(testUser.getId()).thenReturn(1);

        // Create test department.
        testDepartment = new Department("Computer Science");
        testDepartment = spy(testDepartment);
        lenient().when(testDepartment.getId()).thenReturn((byte) 1);
    }

    @Test
    @DisplayName("Should successfully add a new department when name is valid and user exists")
    void addDepartment_WithValidNameAndUser_ShouldReturnCreatedDepartment() {
        // Given.
        String departmentName = "Mathematics";
        Integer userId = 1;

        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));
        when(departmentRepo.existsByName(departmentName)).thenReturn(false);
        when(departmentRepo.save(any(Department.class))).thenReturn(testDepartment);

        // When.
        Department result = departmentManager.addDepartment(departmentName, userId);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testDepartment);

        verify(departmentRepo).existsByName(departmentName);
        verify(departmentRepo).save(any(Department.class));
        verify(historyManager).logCreateDepartment(testUser, testDepartment);
    }

    @Test
    @DisplayName("Should return null when department name already exists")
    void addDepartment_WithExistingName_ShouldReturnNull() {
        // Given.
        String existingName = "Computer Science";
        Integer userId = 1;

        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));
        when(departmentRepo.existsByName(existingName)).thenReturn(true);

        // When.
        Department result = departmentManager.addDepartment(existingName, userId);

        // Then.
        assertThat(result).isNull();

        verify(departmentRepo).existsByName(existingName);
        verify(departmentRepo, never()).save(any(Department.class));
        verify(historyManager, never()).logCreateDepartment(any(), any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when department name is null")
    void addDepartment_WithNullName_ShouldThrowException() {
        // Given.
        String nullName = null;
        Integer userId = 1;

        // When & Then.
        assertThatThrownBy(() -> departmentManager.addDepartment(nullName, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Department name cannot be null or empty");

        verify(userRepo, never()).findById(any());
        verify(departmentRepo, never()).existsByName(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when department name is empty")
    void addDepartment_WithEmptyName_ShouldThrowException() {
        // Given.
        String emptyName = "";
        Integer userId = 1;

        // When & Then.
        assertThatThrownBy(() -> departmentManager.addDepartment(emptyName, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Department name cannot be null or empty");

        verify(userRepo, never()).findById(any());
        verify(departmentRepo, never()).existsByName(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user does not exist")
    void addDepartment_WithNonExistentUser_ShouldThrowException() {
        // Given.
        String departmentName = "Physics";
        Integer nonExistentUserId = 999;

        when(userRepo.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> departmentManager.addDepartment(departmentName, nonExistentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");

        verify(userRepo).findById(nonExistentUserId);
        verify(departmentRepo, never()).existsByName(any());
    }

    @Test
    @DisplayName("Should successfully update department when new name is valid")
    void updateDepartment_WithValidName_ShouldReturnUpdatedDepartment() {
        // Given.
        Byte departmentId = (byte) 1;
        String newName = "Computer Engineering";
        Integer userId = 1;

        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));
        when(departmentRepo.existsByName(newName)).thenReturn(false);
        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(departmentRepo.save(testDepartment)).thenReturn(testDepartment);

        // When.
        Department result = departmentManager.updateDepartment(departmentId, newName, userId);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testDepartment);

        verify(testDepartment).setName(newName);
        verify(departmentRepo).save(testDepartment);
        verify(historyManager).logUpdateDepartment(testUser, testDepartment);
    }

    @Test
    @DisplayName("Should return null when updating to an existing department name")
    void updateDepartment_WithExistingName_ShouldReturnNull() {
        // Given.
        Byte departmentId = (byte) 1;
        String existingName = "Mathematics";
        Integer userId = 1;

        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));
        when(departmentRepo.existsByName(existingName)).thenReturn(true);

        // When.
        Department result = departmentManager.updateDepartment(departmentId, existingName, userId);

        // Then.
        assertThat(result).isNull();

        verify(departmentRepo).existsByName(existingName);
        verify(departmentRepo, never()).findById(any());
        verify(departmentRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should return null when department to update does not exist")
    void updateDepartment_WithNonExistentDepartment_ShouldReturnNull() {
        // Given.
        Byte nonExistentId = (byte) 99;
        String newName = "New Department";
        Integer userId = 1;

        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));
        when(departmentRepo.existsByName(newName)).thenReturn(false);
        when(departmentRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // When.
        Department result = departmentManager.updateDepartment(nonExistentId, newName, userId);

        // Then.
        assertThat(result).isNull();

        verify(departmentRepo).findById(nonExistentId);
        verify(departmentRepo, never()).save(any());
        verify(historyManager, never()).logUpdateDepartment(any(), any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when update name is null")
    void updateDepartment_WithNullName_ShouldThrowException() {
        // Given.
        Byte departmentId = (byte) 1;
        String nullName = null;
        Integer userId = 1;

        // When & Then.
        assertThatThrownBy(() -> departmentManager.updateDepartment(departmentId, nullName, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Department name cannot be null or empty");
    }

    @Test
    @DisplayName("Should successfully delete department when it exists")
    void deleteDepartment_WithExistingDepartment_ShouldReturnTrue() {
        // Given.
        Byte departmentId = (byte) 1;
        Integer userId = 1;

        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));

        // When.
        boolean result = departmentManager.deleteDepartment(departmentId, userId);

        // Then.
        assertThat(result).isTrue();

        verify(historyManager).logDeleteDepartment(testUser, testDepartment);
        verify(departmentRepo).delete(testDepartment);
    }

    @Test
    @DisplayName("Should return false when department to delete does not exist")
    void deleteDepartment_WithNonExistentDepartment_ShouldReturnFalse() {
        // Given.
        Byte nonExistentId = (byte) 99;
        Integer userId = 1;

        when(departmentRepo.findById(nonExistentId)).thenReturn(Optional.empty());
        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));

        // When.
        boolean result = departmentManager.deleteDepartment(nonExistentId, userId);

        // Then.
        assertThat(result).isFalse();

        verify(departmentRepo, never()).delete(any());
        verify(historyManager, never()).logDeleteDepartment(any(), any());
    }

    @Test
    @DisplayName("Should return false when user does not exist for deletion")
    void deleteDepartment_WithNonExistentUser_ShouldReturnFalse() {
        // Given.
        Byte departmentId = (byte) 1;
        Integer nonExistentUserId = 999;

        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(userRepo.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When.
        boolean result = departmentManager.deleteDepartment(departmentId, nonExistentUserId);

        // Then.
        assertThat(result).isFalse();

        verify(departmentRepo, never()).delete(any());
        verify(historyManager, never()).logDeleteDepartment(any(), any());
    }

    @Test
    @DisplayName("Should return department when it exists")
    void getDepartment_WithExistingId_ShouldReturnDepartment() {
        // Given.
        Byte departmentId = (byte) 1;

        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(testDepartment));

        // When.
        Department result = departmentManager.getDepartment(departmentId);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testDepartment);

        verify(departmentRepo).findById(departmentId);
    }

    @Test
    @DisplayName("Should return null when department does not exist")
    void getDepartment_WithNonExistentId_ShouldReturnNull() {
        // Given.
        Byte nonExistentId = (byte) 99;

        when(departmentRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // When.
        Department result = departmentManager.getDepartment(nonExistentId);

        // Then.
        assertThat(result).isNull();

        verify(departmentRepo).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should return all departments ordered by name")
    void getDepartments_ShouldReturnAllDepartmentsInOrder() {
        // Given.
        List<Department> expectedDepartments = List.of(
            new Department("Computer Science"),
            new Department("Mathematics"),
            new Department("Physics")
        );

        when(departmentRepo.findAllByOrderByNameAsc()).thenReturn(expectedDepartments);

        // When.
        List<Department> result = departmentManager.getDepartments();

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).isEqualTo(expectedDepartments);

        verify(departmentRepo).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("Should return departments matching name when searching with valid name")
    void getDepartments_WithValidName_ShouldReturnMatchingDepartments() {
        // Given.
        String searchName = "Computer";
        List<Department> matchingDepartments = List.of(
            new Department("Computer Science"),
            new Department("Computer Engineering")
        );

        when(departmentRepo.findByNameContainingIgnoreCase(searchName))
            .thenReturn(matchingDepartments);

        // When.
        List<Department> result = departmentManager.getDepartments(searchName);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(matchingDepartments);

        verify(departmentRepo).findByNameContainingIgnoreCase(searchName);
        verify(departmentRepo, never()).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("Should return all departments when searching with null name")
    void getDepartments_WithNullName_ShouldReturnAllDepartments() {
        // Given.
        String nullName = null;
        List<Department> allDepartments = List.of(
            new Department("Computer Science"),
            new Department("Mathematics")
        );

        when(departmentRepo.findAllByOrderByNameAsc()).thenReturn(allDepartments);

        // When.
        List<Department> result = departmentManager.getDepartments(nullName);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(allDepartments);

        verify(departmentRepo).findAllByOrderByNameAsc();
        verify(departmentRepo, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should return all departments when searching with empty name")
    void getDepartments_WithEmptyName_ShouldReturnAllDepartments() {
        // Given.
        String emptyName = "";
        List<Department> allDepartments = List.of(
            new Department("Computer Science"),
            new Department("Mathematics")
        );

        when(departmentRepo.findAllByOrderByNameAsc()).thenReturn(allDepartments);

        // When.
        List<Department> result = departmentManager.getDepartments(emptyName);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(allDepartments);

        verify(departmentRepo).findAllByOrderByNameAsc();
        verify(departmentRepo, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should return empty list when no departments match search criteria")
    void getDepartments_WithNoMatches_ShouldReturnEmptyList() {
        // Given.
        String searchName = "NonExistent";
        List<Department> emptyList = List.of();

        when(departmentRepo.findByNameContainingIgnoreCase(searchName)).thenReturn(emptyList);

        // When.
        List<Department> result = departmentManager.getDepartments(searchName);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(departmentRepo).findByNameContainingIgnoreCase(searchName);
    }
}
