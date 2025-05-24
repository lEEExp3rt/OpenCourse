package org.opencourse.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * User entity class in OpenCourse.
 * 
 * @author LJX
 * @author !EEExp3rt
 */
@Entity
@Table(name = "User")
public class User extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Column(name = "name", length = 31, nullable = false)
    private String name;

    @Email(message = "邮箱格式不正确")
    @Column(name = "email", length = 63, unique = true, nullable = false)
    private String email;

    @NotBlank(message = "密码不能为空")
    @Column(name = "password", nullable = false)
    private String password;

    public enum UserRole {
        USER, VISITOR, ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "activity", columnDefinition = "int default 1")
    private Integer activity;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    protected User() {
    }

    /**
     * Constructor.
     * 
     * @param name     The name of the user.
     * @param email    The email of the user.
     * @param password The password of the user after hashing.
     * @param role     The role of the user.
     */
    public User(String name, String email, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.activity = 1;
    }

    /**
     * Set creation timestamp on creation.
     * 
     * @apiNote This method is called by JPA automatically.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Set update timestamp on update.
     * 
     * @apiNote This method is called by JPA automatically.
     */
    @PrePersist
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Integer getActivity() {
        return activity;
    }

    public void addActivity(Integer activity) {
        this.activity += activity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", activity=" + activity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
