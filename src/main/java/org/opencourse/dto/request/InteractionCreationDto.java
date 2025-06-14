package org.opencourse.dto.request;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for interaction creation requests.
 * 
 * @author !EEExp3rt
 */
public class InteractionCreationDto {

    @NotNull(message = "课程不能为空")
    private Short courseId;

    private String content;

    @Range(min = 1, max = 10, message = "评分必须在 1 到 10 之间")
    private Byte rating;

    /**
     * Default constructor.
     */
    public InteractionCreationDto() {
    }

    /**
     * Constructor.
     * 
     * @param courseId The ID of the course.
     * @param content  The content of the interaction.
     * @param rating   The rating of the interaction.
     */
    public InteractionCreationDto(Short courseId, String content, Byte rating) {
        this.courseId = courseId;
        this.content = content;
        this.rating = rating;
    }

    /**
     * Constructor.
     * 
     * @param courseId The ID of the course.
     * @param content  The content of the interaction.
     */
    public InteractionCreationDto(Short courseId, String content) {
        this.courseId = courseId;
        this.content = content;
    }

    // Getters and Setters.

    public Short getCourseId() {
        return courseId;
    }

    public void setCourseId(Short courseId) {
        this.courseId = courseId;
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
}
