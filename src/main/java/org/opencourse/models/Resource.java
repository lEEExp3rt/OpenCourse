package org.opencourse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.opencourse.utils.typeinfo.ResourceType;

/**
 * Resource entity class in OpenCourse.
 * 
 * @author !EEExp3rt
 */
@Entity
@Table(name = "Resource")
public class Resource extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 63)
    private String name;

    @Column(length = 255)
    private String description;

    // The type of the resource.
    @Enumerated(EnumType.STRING)
    @Column(name = "resourse_type", nullable = false)
    private ResourceType resourceType;

    /**
     * Resource file class to represent a concrete file object.
     */
    @Embeddable
    public static class ResourceFile {
    
        /**
         * Resource file type.
         */
        public enum FileType {

            PDF,
            TEXT,
            OTHER;

            /**
             * Get the file type from a string.
             * 
             * @param fileTypeName The name of the file type.
             * @return The corresponding FileType enum value if matched or OTHER if not.
             */
            public static FileType from(String fileTypeName) {
                try {
                    return FileType.valueOf(fileTypeName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return OTHER;
                }
            }
        }
    
        @Enumerated(EnumType.STRING)
        @Column(name = "file_type", nullable = false)
        private FileType fileType;
    
        @Column(name = "file_size", nullable = false, precision = 3, scale = 2)
        private BigDecimal fileSize;
    
        @Column(name = "file_path", nullable = false, length = 255)
        private String filePath;

        /**
         * Default constructor.
         */
        protected ResourceFile() {
        }

        /**
         * Constructor.
         * 
         * @param fileType The type of the resource file.
         * @param fileSize The size of the resource file.
         * @param filePath The path to the resource file.
         */
        public ResourceFile(FileType fileType, BigDecimal fileSize, String filePath) {
            this.fileType = fileType;
            this.fileSize = fileSize;
            this.filePath = filePath;
        }

        public FileType getFileType() {
            return fileType;
        }

        public void setFileType(FileType fileType) {
            this.fileType = fileType;
        }

        public BigDecimal getFileSize() {
            return fileSize;
        }

        public void setFileSize(BigDecimal fileSize) {
            this.fileSize = fileSize;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    @Embedded
    private ResourceFile resourceFile;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // The course to which the resource belongs.
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // The user who uploaded the resource.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer views;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likes;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer dislikes;

    /**
     * Default constructor.
     */
    protected Resource() {
    }

    /**
     * Constructor.
     * 
     * @param name         The name of the resource.
     * @param resourceType The type of the resource.
     * @param resourceFile The file associated with the resource.
     * @param course       The course to which the resource belongs.
     * @param user         The user who uploaded the resource.
     */
    public Resource(
        String name,
        ResourceType resourceType,
        ResourceFile resourceFile,
        Course course,
        User user
    ) {
        this.name = name;
        this.description = null;
        this.resourceType = resourceType;
        this.resourceFile = resourceFile;
        this.createdAt = null;
        this.course = course;
        this.user = user;
        this.views = 0;
        this.likes = 0;
        this.dislikes = 0;
    }

    /**
     * Constructor.
     * 
     * @param name         The name of the resource.
     * @param description  The description of the resource.
     * @param resourceType The type of the resource.
     * @param resourceFile The file associated with the resource.
     * @param course       The course to which the resource belongs.
     * @param user         The user who uploaded the resource.
     */
    public Resource(
        String name,
        String description,
        ResourceType resourceType,
        ResourceFile resourceFile,
        Course course,
        User user
    ) {
        this.name = name;
        this.description = description;
        this.resourceType = resourceType;
        this.resourceFile = resourceFile;
        this.createdAt = null;
        this.course = course;
        this.user = user;
        this.views = 0;
        this.likes = 0;
        this.dislikes = 0;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceFile getResourceFile() {
        return resourceFile;
    }

    public void setResourceFile(ResourceFile resourceFile) {
        this.resourceFile = resourceFile;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
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

    @Override
    public String toString() {
        return "Resource{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", resourceType=" + resourceType +
            ", resourceFile=" + resourceFile +
            ", createdAt=" + createdAt +
            ", course=" + course +
            ", user=" + user +
            ", views=" + views +
            ", likes=" + likes +
            ", dislikes=" + dislikes +
            '}';
    }
}
