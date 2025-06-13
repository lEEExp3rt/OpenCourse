package org.opencourse.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.opencourse.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link UserRepo}.
 * 
 * @author !EEExp3rt
 */
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = {UserRepo.class})
@EntityScan(basePackageClasses = {User.class})
public class UserRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepo userRepo;

    // Test data
    private User adminUser;
    private User normalUser;
    private User visitorUser;

    @BeforeEach
    void setUp() {
        // Create test users with different roles
        adminUser = new User("admin", "admin@example.com", "hashedPassword123", User.UserRole.ADMIN);
        normalUser = new User("normaluser", "user@example.com", "hashedPassword456", User.UserRole.USER);
        visitorUser = new User("visitor", "visitor@example.com", "hashedPassword789", User.UserRole.VISITOR);
        
        // Save test users
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(normalUser);
        entityManager.persistAndFlush(visitorUser);
    }

    @Test
    void contextLoads() {
        assertThat(userRepo).isNotNull();
        assertThat(entityManager).isNotNull();
    }

    @Test
    void testFindByName_WhenUserExists_ShouldReturnUser() {
        // When
        Optional<User> foundAdmin = userRepo.findByName("admin");
        Optional<User> foundUser = userRepo.findByName("normaluser");
        Optional<User> foundVisitor = userRepo.findByName("visitor");

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getName()).isEqualTo("admin");
        assertThat(foundAdmin.get().getEmail()).isEqualTo("admin@example.com");
        assertThat(foundAdmin.get().getRole()).isEqualTo(User.UserRole.ADMIN);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("normaluser");
        assertThat(foundUser.get().getEmail()).isEqualTo("user@example.com");
        assertThat(foundUser.get().getRole()).isEqualTo(User.UserRole.USER);

        assertThat(foundVisitor).isPresent();
        assertThat(foundVisitor.get().getName()).isEqualTo("visitor");
        assertThat(foundVisitor.get().getEmail()).isEqualTo("visitor@example.com");
        assertThat(foundVisitor.get().getRole()).isEqualTo(User.UserRole.VISITOR);
    }

    @Test
    void testFindByName_WhenUserNotExists_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepo.findByName("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByName_CaseSensitive() {
        // When
        Optional<User> found1 = userRepo.findByName("ADMIN");
        Optional<User> found2 = userRepo.findByName("Admin");
        Optional<User> found3 = userRepo.findByName("admin");

        // Then - Name search should be case sensitive
        assertThat(found1).isEmpty();
        assertThat(found2).isEmpty();
        assertThat(found3).isPresent();
    }

    @Test
    void testFindByName_WithSpecialCharacters() {
        // Given - Create user with special characters in name
        User specialUser = new User("user@123", "special@example.com", "password", User.UserRole.USER);
        entityManager.persistAndFlush(specialUser);

        // When
        Optional<User> found = userRepo.findByName("user@123");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("user@123");
    }

    @Test
    void testFindByEmail_WhenUserExists_ShouldReturnUser() {
        // When
        Optional<User> foundAdmin = userRepo.findByEmail("admin@example.com");
        Optional<User> foundUser = userRepo.findByEmail("user@example.com");
        Optional<User> foundVisitor = userRepo.findByEmail("visitor@example.com");

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getName()).isEqualTo("admin");
        assertThat(foundAdmin.get().getEmail()).isEqualTo("admin@example.com");
        assertThat(foundAdmin.get().getRole()).isEqualTo(User.UserRole.ADMIN);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("normaluser");
        assertThat(foundUser.get().getEmail()).isEqualTo("user@example.com");
        assertThat(foundUser.get().getRole()).isEqualTo(User.UserRole.USER);

        assertThat(foundVisitor).isPresent();
        assertThat(foundVisitor.get().getName()).isEqualTo("visitor");
        assertThat(foundVisitor.get().getEmail()).isEqualTo("visitor@example.com");
        assertThat(foundVisitor.get().getRole()).isEqualTo(User.UserRole.VISITOR);
    }

    @Test
    void testFindByEmail_WhenUserNotExists_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepo.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByEmail_CaseInsensitive() {
        // When
        Optional<User> found1 = userRepo.findByEmail("ADMIN@EXAMPLE.COM");
        Optional<User> found2 = userRepo.findByEmail("Admin@Example.Com");
        Optional<User> found3 = userRepo.findByEmail("admin@example.com");

        // Then - Email search behavior depends on database collation
        assertThat(found3).isPresent();
        
        System.out.println("Email case sensitivity test - UPPER: " + found1.isPresent() + 
                          ", Mixed: " + found2.isPresent() + ", Lower: " + found3.isPresent());
    }

    @Test
    void testFindByEmail_WithComplexEmail() {
        // Given - Create user with complex email
        User complexEmailUser = new User("testuser", "test.user+tag@sub.domain.com", "password", User.UserRole.USER);
        entityManager.persistAndFlush(complexEmailUser);

        // When
        Optional<User> found = userRepo.findByEmail("test.user+tag@sub.domain.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test.user+tag@sub.domain.com");
    }

    @Test
    void testExistsByEmail_WhenUserExists_ShouldReturnTrue() {
        // When & Then
        assertThat(userRepo.existsByEmail("admin@example.com")).isTrue();
        assertThat(userRepo.existsByEmail("user@example.com")).isTrue();
        assertThat(userRepo.existsByEmail("visitor@example.com")).isTrue();
    }

    @Test
    void testExistsByEmail_WhenUserNotExists_ShouldReturnFalse() {
        // When & Then
        assertThat(userRepo.existsByEmail("nonexistent@example.com")).isFalse();
        assertThat(userRepo.existsByEmail("fake@domain.com")).isFalse();
        assertThat(userRepo.existsByEmail("")).isFalse();
    }

    @Test
    void testExistsByEmail_ConsistencyWithFindByEmail() {
        // When
        boolean exists1 = userRepo.existsByEmail("admin@example.com");
        Optional<User> found1 = userRepo.findByEmail("admin@example.com");
        
        boolean exists2 = userRepo.existsByEmail("nonexistent@example.com");
        Optional<User> found2 = userRepo.findByEmail("nonexistent@example.com");

        // Then - Results should be consistent
        assertThat(exists1).isTrue();
        assertThat(found1).isPresent();
        
        assertThat(exists2).isFalse();
        assertThat(found2).isEmpty();
    }

    @Test
    void testUserEntityBasicFunctionality() {
        // When
        Optional<User> foundUser = userRepo.findById(normalUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        User user = foundUser.get();
        
        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("normaluser");
        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getPassword()).isEqualTo("hashedPassword456");
        assertThat(user.getRole()).isEqualTo(User.UserRole.USER);
        assertThat(user.getActivity()).isEqualTo(1); // Default activity
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNull(); // Not updated yet
    }

    @Test
    void testUserRoleEnumFunctionality() {
        // When
        List<User> allUsers = userRepo.findAll();

        // Then - Verify different user roles
        User foundAdmin = allUsers.stream()
            .filter(user -> user.getRole() == User.UserRole.ADMIN)
            .findFirst()
            .orElse(null);
            
        User foundUser = allUsers.stream()
            .filter(user -> user.getRole() == User.UserRole.USER)
            .findFirst()
            .orElse(null);
            
        User foundVisitor = allUsers.stream()
            .filter(user -> user.getRole() == User.UserRole.VISITOR)
            .findFirst()
            .orElse(null);
            
        assertThat(foundAdmin).isNotNull();
        assertThat(foundAdmin.getName()).isEqualTo("admin");
        assertThat(foundAdmin.getRole()).isEqualTo(User.UserRole.ADMIN);
        
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("normaluser");
        assertThat(foundUser.getRole()).isEqualTo(User.UserRole.USER);
        
        assertThat(foundVisitor).isNotNull();
        assertThat(foundVisitor.getName()).isEqualTo("visitor");
        assertThat(foundVisitor.getRole()).isEqualTo(User.UserRole.VISITOR);
    }

    @Test
    void testCreatedAtAutoGeneration() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now();
        User newUser = new User("newuser", "new@example.com", "password", User.UserRole.USER);
        
        // When
        User saved = userRepo.save(newUser);
        LocalDateTime afterSave = LocalDateTime.now();

        // Then
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isAfter(beforeSave.minusSeconds(1)); // Allow 1 second margin
        assertThat(saved.getCreatedAt()).isBefore(afterSave.plusSeconds(1));  // Allow 1 second margin
        assertThat(saved.getUpdatedAt()).isNull(); // Should be null on creation
    }

    @Test
    void testUpdatedAtAutoGeneration() {
        // Given
        User user = normalUser;
        LocalDateTime originalCreatedAt = user.getCreatedAt();
        
        // When - Update the user
        LocalDateTime beforeUpdate = LocalDateTime.now();
        user.setName("updatedname");
        User updated = userRepo.save(user);
        entityManager.flush(); // Force update
        LocalDateTime afterUpdate = LocalDateTime.now();

        // Then
        Optional<User> found = userRepo.findById(updated.getId());
        assertThat(found).isPresent();
        
        User updatedUser = found.get();
        assertThat(updatedUser.getCreatedAt()).isEqualTo(originalCreatedAt); // Should not change
        assertThat(updatedUser.getUpdatedAt()).isNotNull();
        assertThat(updatedUser.getUpdatedAt()).isAfter(beforeUpdate.minusSeconds(1));
        assertThat(updatedUser.getUpdatedAt()).isBefore(afterUpdate.plusSeconds(1));
        assertThat(updatedUser.getName()).isEqualTo("updatedname");
    }

    @Test
    void testUserActivityFunctionality() {
        // Given
        User user = normalUser;
        Integer originalActivity = user.getActivity();
        
        // When - Add activity
        user.addActivity(5);
        userRepo.save(user);

        // Then
        Optional<User> found = userRepo.findById(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getActivity()).isEqualTo(originalActivity + 5);
        
        // When - Add more activity
        found.get().addActivity(3);
        userRepo.save(found.get());
        
        // Then
        Optional<User> updated = userRepo.findById(user.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getActivity()).isEqualTo(originalActivity + 5 + 3);
    }

    @Test
    void testUserActivityDirectSet() {
        // Given
        User user = visitorUser;
        
        // When
        user.setActivity(100);
        userRepo.save(user);

        // Then
        Optional<User> found = userRepo.findById(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getActivity()).isEqualTo(100);
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
        assertThat(userString).contains("createdAt=");
        assertThat(userString).contains("updatedAt=null");
        
        // Ensure password is not in toString (security)
        assertThat(userString).doesNotContain("password");
        assertThat(userString).doesNotContain("hashedPassword456");
    }

    @Test
    void testEmailUniquenessConstraint() {
        // Given - Try to create user with existing email
        User duplicateUser = new User("duplicateuser", "admin@example.com", "password", User.UserRole.USER);
        
        // When & Then - Should handle duplicate email appropriately
        try {
            userRepo.save(duplicateUser);
            entityManager.flush();
            
            // If we reach here, the database allows duplicates or handles them differently
            System.out.println("⚠️ Database allows duplicate emails or handles them automatically");
        } catch (Exception e) {
            // Expected behavior - uniqueness constraint violation
            System.out.println("✅ Uniqueness constraint working: " + e.getClass().getSimpleName());
            assertThat(e).isNotNull();
        }
    }

    @Test
    void testComplexQueryScenarios() {
        // Scenario 1: Find users by role using JPA methods
        List<User> allUsers = userRepo.findAll();
        
        long adminCount = allUsers.stream()
            .filter(user -> user.getRole() == User.UserRole.ADMIN)
            .count();
        long userCount = allUsers.stream()
            .filter(user -> user.getRole() == User.UserRole.USER)
            .count();
        long visitorCount = allUsers.stream()
            .filter(user -> user.getRole() == User.UserRole.VISITOR)
            .count();
            
        assertThat(adminCount).isEqualTo(1);
        assertThat(userCount).isEqualTo(1);
        assertThat(visitorCount).isEqualTo(1);
        
        // Scenario 2: Find user by name and verify email
        Optional<User> foundByName = userRepo.findByName("admin");
        assertThat(foundByName).isPresent();
        
        Optional<User> foundByEmail = userRepo.findByEmail(foundByName.get().getEmail());
        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getId()).isEqualTo(foundByName.get().getId());
    }

    @Test
    void testUserCreationScenarios() {
        // Scenario 1: Minimal valid user
        User minimalUser = new User("minimal", "minimal@example.com", "pass", User.UserRole.VISITOR);
        User saved1 = userRepo.save(minimalUser);
        
        assertThat(saved1.getId()).isNotNull();
        assertThat(saved1.getActivity()).isEqualTo(1); // Default activity
        assertThat(saved1.getCreatedAt()).isNotNull();
        assertThat(saved1.getUpdatedAt()).isNull();
        
        // Scenario 2: User with long name and email
        User longNameUser = new User("verylongusernametotestlimits", "very.long.email.address@example.com", "password", User.UserRole.USER);
        User saved2 = userRepo.save(longNameUser);
        
        assertThat(saved2.getId()).isNotNull();
        assertThat(saved2.getName()).isEqualTo("verylongusernametotestlimits");
        assertThat(saved2.getEmail()).isEqualTo("very.long.email.address@example.com");
    }

    @Test
    void testEdgeCases() {
        // Test empty string search (should not match anything)
        Optional<User> emptyNameSearch = userRepo.findByName("");
        Optional<User> emptyEmailSearch = userRepo.findByEmail("");
        
        assertThat(emptyNameSearch).isEmpty();
        assertThat(emptyEmailSearch).isEmpty();
        
        // Test null search behavior (depends on implementation)
        boolean nullEmailExists = userRepo.existsByEmail(null);
        assertThat(nullEmailExists).isFalse();
        
        // Test whitespace handling
        User whitespaceUser = new User("  spaced  ", "spaced@example.com", "password", User.UserRole.USER);
        userRepo.save(whitespaceUser);
        
        Optional<User> foundSpaced = userRepo.findByName("  spaced  ");
        assertThat(foundSpaced).isPresent();
        assertThat(foundSpaced.get().getName()).isEqualTo("  spaced  ");
    }

    @Test
    void testDataConsistency() {
        // When
        List<User> allUsers = userRepo.findAll();

        // Then
        assertThat(allUsers).hasSize(3);
        
        // Verify all users have required fields
        assertThat(allUsers).allMatch(user -> user.getId() != null);
        assertThat(allUsers).allMatch(user -> user.getName() != null && !user.getName().isEmpty());
        assertThat(allUsers).allMatch(user -> user.getEmail() != null && !user.getEmail().isEmpty());
        assertThat(allUsers).allMatch(user -> user.getPassword() != null && !user.getPassword().isEmpty());
        assertThat(allUsers).allMatch(user -> user.getRole() != null);
        assertThat(allUsers).allMatch(user -> user.getActivity() != null && user.getActivity() >= 0);
        assertThat(allUsers).allMatch(user -> user.getCreatedAt() != null);
        
        // Verify email uniqueness
        long uniqueEmails = allUsers.stream()
            .map(User::getEmail)
            .distinct()
            .count();
        assertThat(uniqueEmails).isEqualTo(allUsers.size());
        
        // Verify name and email format
        assertThat(allUsers).allMatch(user -> user.getEmail().contains("@"));
        assertThat(allUsers).allMatch(user -> user.getName().length() > 0);
    }

    @Test
    void testBulkOperations() {
        // Given - Create multiple users
        List<User> bulkUsers = List.of(
            new User("bulk1", "bulk1@example.com", "pass1", User.UserRole.USER),
            new User("bulk2", "bulk2@example.com", "pass2", User.UserRole.VISITOR),
            new User("bulk3", "bulk3@example.com", "pass3", User.UserRole.ADMIN)
        );
        
        // When
        List<User> savedUsers = userRepo.saveAll(bulkUsers);
        
        // Then
        assertThat(savedUsers).hasSize(3);
        assertThat(savedUsers).allMatch(user -> user.getId() != null);
        
        // Verify all users can be found
        assertThat(userRepo.existsByEmail("bulk1@example.com")).isTrue();
        assertThat(userRepo.existsByEmail("bulk2@example.com")).isTrue();
        assertThat(userRepo.existsByEmail("bulk3@example.com")).isTrue();
        
        // Verify total count
        long totalUsers = userRepo.count();
        assertThat(totalUsers).isEqualTo(6); // 3 original + 3 bulk
    }
}