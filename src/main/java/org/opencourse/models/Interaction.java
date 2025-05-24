package org.opencourse.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Interaction entity class to represent a comment in interaction system in OpenCourse.
 * 
 * @author !EEExp3rt
 */
@Entity
@Table(name = "Interaction")
public class Interaction extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // The rating of the course rated by the user
    @Column(name = "rating")
    private Byte rating;

    @Column(name = "likes", columnDefinition = "INT DEFAULT 0")
    private Integer likes;

    @Column(name = "dislikes", columnDefinition = "INT DEFAULT 0")
    private Integer dislikes;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    protected Interaction() {
    }

    /**
     * Constructor.
     *
     * @param course  The course associated with the interaction.
     * @param user    The user who made the interaction.
     * @param rating  The rating rated by the user.
     */
    public Interaction(Course course, User user, Byte rating) {
        this.course = course;
        this.user = user;
        this.content = null;
        this.rating = rating;
        this.likes = 0;
        this.dislikes = 0;
        this.createdAt = null;
    }

    /**
     * Constructor.
     *
     * @param course  The course associated with the interaction.
     * @param user    The user who made the interaction.
     * @param content The content of the interaction.
     */
    public Interaction(Course course, User user, String content) {
        this.course = course;
        this.user = user;
        this.content = content;
        this.rating = null;
        this.likes = 0;
        this.dislikes = 0;
        this.createdAt = null;
    }

    /**
     * Constructor.
     *
     * @param course  The course associated with the interaction.
     * @param user    The user who made the interaction.
     * @param content The content of the interaction.
     * @param rating  The rating rated by the user.
     */
    public Interaction(Course course, User user, String content, Byte rating) {
        this.course = course;
        this.user = user;
        this.content = content;
        this.rating = rating;
        this.likes = 0;
        this.dislikes = 0;
        this.createdAt = null;
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

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Byte getRating() {
        return rating;
    }

    public void setRating(Byte rating) {
        this.rating = rating;
    }

    public Integer getLikes() {
        return likes;
    }

    public void likes() {
        this.likes++;
    }

    public void unlikes() {
        this.likes--;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void dislikes() {
        this.dislikes++;
    }

    public void undislikes() {
        this.dislikes--;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Interaction{" +
                "id=" + id +
                ", course=" + course +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", createdAt=" + createdAt +
                '}';
    }
}
