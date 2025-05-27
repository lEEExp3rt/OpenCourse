package org.opencourse.dto.request;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for interaction update requests.
 * 
 * @author !EEExp3rt
 */
public class InteractionUpdateDto {

    @NotNull(message = "评论ID不能为空")
    private Integer id;

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    private String content;

    @Range(min = 1, max = 10, message = "评分必须在 1 到 10 之间")
    private Byte rating;

    /**
     * Default constructor.
     */
    public InteractionUpdateDto() {
    }

    /**
     * Constructor.
     * 
     * @param id       The ID of the interaction.
     * @param userId   The ID of the user.
     * @param content  The content of the interaction.
     * @param rating   The rating of the interaction.
     */
    public InteractionUpdateDto(Integer id, Integer userId, String content, Byte rating) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
    }

    // Getters and Setters.

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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