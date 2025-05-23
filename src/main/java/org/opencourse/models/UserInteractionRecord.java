package org.opencourse.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 用户交互记录实体类，用于记录用户对评论的点赞/点踩状态
 */
@Entity
@Table(name = "UserInteractionRecord", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "interaction_id"})
})
public class UserInteractionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "interaction_id", nullable = false)
    private Interaction interaction;

    @Column(name = "liked", nullable = false)
    private boolean liked;

    @Column(name = "disliked", nullable = false)
    private boolean disliked;

    /**
     * 默认构造函数
     */
    protected UserInteractionRecord() {
    }

    /**
     * 构造函数
     * 
     * @param user 用户
     * @param interaction 评论
     */
    public UserInteractionRecord(User user, Interaction interaction) {
        this.user = user;
        this.interaction = interaction;
        this.liked = false;
        this.disliked = false;
    }

    /**
     * 点赞
     */
    public void like() {
        if (!this.liked) {
            this.liked = true;
            if (this.disliked) {
                this.disliked = false;
                this.interaction.undislikes();
            }
            this.interaction.likes();
        }
    }

    /**
     * 取消点赞
     */
    public void unlike() {
        if (this.liked) {
            this.liked = false;
            this.interaction.unlikes();
        }
    }

    /**
     * 点踩
     */
    public void dislike() {
        if (!this.disliked) {
            this.disliked = true;
            if (this.liked) {
                this.liked = false;
                this.interaction.unlikes();
            }
            this.interaction.dislikes();
        }
    }

    /**
     * 取消点踩
     */
    public void undislike() {
        if (this.disliked) {
            this.disliked = false;
            this.interaction.undislikes();
        }
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

    @Override
    public String toString() {
        return "UserInteractionRecord{" +
                "id=" + id +
                ", user=" + user +
                ", interaction=" + interaction +
                ", liked=" + liked +
                ", disliked=" + disliked +
                '}';
    }
} 