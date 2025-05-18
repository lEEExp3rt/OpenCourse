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
public class History {

    // The ID of the history record.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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
    private ActionObject actionObject;
    
    // The timestamp of when the action was performed.
    @Column(name = "timestamp", columnDefinition = "timestamp default current_timestamp")
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
     * @param actionObject The object related to the action.
     * @param timestamp    The timestamp of when the action was performed.
     */
    public History(User user, ActionType actionType, ActionObject actionObject, LocalDateTime timestamp) {
        this.user = user;
        this.actionType = actionType;
        this.actionObject = actionObject;
        this.timestamp = timestamp;
    }

    // TODO
}
