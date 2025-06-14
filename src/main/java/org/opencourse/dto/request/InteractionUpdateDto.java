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
     * @param content  The content of the interaction.
     * @param rating   The rating of the interaction.
     */
    public InteractionUpdateDto(Integer id, String content, Byte rating) {
        this.id = id;
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