package org.opencourse.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 用户实体类测试
 */
public class UserTest {

    @Test
    void testUserConstructor() {
        // Act
        User user = new User("testuser", "test@example.com", "password123", User.UserRole.USER);

        // Assert
        assertThat(user.getName()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRole()).isEqualTo(User.UserRole.USER);
        assertThat(user.getActivity()).isEqualTo(1); // 默认活跃度为1
    }

    @Test
    void testUserEnumRoles() {
        // Act & Assert
        User adminUser = new User("admin", "admin@example.com", "password", User.UserRole.ADMIN);
        User normalUser = new User("user", "user@example.com", "password", User.UserRole.USER);
        User visitorUser = new User("visitor", "visitor@example.com", "password", User.UserRole.VISITOR);

        assertThat(adminUser.getRole()).isEqualTo(User.UserRole.ADMIN);
        assertThat(normalUser.getRole()).isEqualTo(User.UserRole.USER);
        assertThat(visitorUser.getRole()).isEqualTo(User.UserRole.VISITOR);
    }

    @Test
    void testSetters() {
        // Arrange
        User user = new User("original", "original@example.com", "original123", User.UserRole.USER);

        // Act - 使用setter方法
        user.setName("updated");
        user.setEmail("updated@example.com");
        user.setPassword("updated123");
        user.setRole(User.UserRole.ADMIN);
        user.setActivity(10);

        // Assert
        assertThat(user.getName()).isEqualTo("updated");
        assertThat(user.getEmail()).isEqualTo("updated@example.com");
        assertThat(user.getPassword()).isEqualTo("updated123");
        assertThat(user.getRole()).isEqualTo(User.UserRole.ADMIN);
        assertThat(user.getActivity()).isEqualTo(10);
    }

    @Test
    void testAddActivity() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password", User.UserRole.USER);
        assertThat(user.getActivity()).isEqualTo(1); // 确认初始值为1

        // Act
        user.addActivity(5);

        // Assert
        assertThat(user.getActivity()).isEqualTo(6);

        // Act - 再次增加
        user.addActivity(10);

        // Assert
        assertThat(user.getActivity()).isEqualTo(16);

        // Act - 增加负值
        user.addActivity(-3);

        // Assert
        assertThat(user.getActivity()).isEqualTo(13);
    }

    @Test
    void testToString() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password", User.UserRole.USER);
        
        // Act
        String userString = user.toString();
        
        // Assert - 验证toString包含预期信息
        assertThat(userString).contains("testuser");
        assertThat(userString).contains("test@example.com");
        assertThat(userString).contains("USER");
        assertThat(userString).doesNotContain("password"); // 不应包含密码
    }
} 