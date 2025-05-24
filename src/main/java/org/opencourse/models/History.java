package org.opencourse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;

import org.opencourse.utils.typeinfo.ActionType;

/**
 * History entity class to record user actions in OpenCourse.
 * 
 * @author !EEExp3rt
 */
@Entity
@Table(name = "History")
public class History extends Model {

    // The ID of the history record.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // The user who performed the action.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // The action type performed by the user.
    @Enumerated(value = EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    // The object related to the action.
    @Column(name = "object_id")
    private Integer objectId;
    
    // The timestamp of when the action was performed.
    @Column(name = "timestamp", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;

    /**
     * Default constructor.
     */
    protected History() {
    }

    /**
     * Constructor.
     *
     * @param user         The user who performed the action.
     * @param actionType   The action type performed by the user.
     * @param objectId     The ID of the object related to the action.
     */
    public History(User user, ActionType actionType, Integer objectId) {
        this.user = user;
        this.actionType = actionType;
        this.objectId = objectId;
        this.timestamp = null;
    }

    /**
     * Constructor.
     *
     * @param user         The user who performed the action.
     * @param actionType   The action type performed by the user.
     */
    public History(User user, ActionType actionType) {
        this.user = user;
        this.actionType = actionType;
        this.objectId = null;
        this.timestamp = null;
    }

    /**
     * Set action timestamp on creation.
     * 
     * @apiNote This method is called by JPA automatically.
     */
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", user=" + user +
                ", actionType=" + actionType +
                ", objectId=" + objectId +
                ", timestamp=" + timestamp +
                '}';
    }
}
