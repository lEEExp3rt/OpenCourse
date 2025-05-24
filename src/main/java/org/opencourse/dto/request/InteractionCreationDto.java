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

    @NotNull(message = "用户不能为空")
    private Integer userId;

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
     * @param userId   The ID of the user.
     * @param content  The content of the interaction.
     * @param rating   The rating of the interaction.
     */
    public InteractionCreationDto(Short courseId, Integer userId, String content, Byte rating) {
        this.courseId = courseId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
    }

    /**
     * Constructor.
     * 
     * @param courseId The ID of the course.
     * @param userId   The ID of the user.
     * @param content  The content of the interaction.
     */
    public InteractionCreationDto(Short courseId, Integer userId, String content) {
        this.courseId = courseId;
        this.userId = userId;
        this.content = content;
    }

    // Getters and Setters.

    public Short getCourseId() {
        return courseId;
    }

    public void setCourseId(Short courseId) {
        this.courseId = courseId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
