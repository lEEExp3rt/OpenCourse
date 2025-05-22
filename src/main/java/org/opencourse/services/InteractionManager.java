package org.opencourse.services;

import org.opencourse.models.Interaction;
import org.opencourse.repositories.InteractionRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interaction service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class InteractionManager {

    private final InteractionRepo interactionRepo; // Data access object.    

    /**
     * Constructor.
     * 
     * @param interactionRepo The interaction repository.
     */
    @Autowired
    public InteractionManager(InteractionRepo interactionRepo) {
        this.interactionRepo = interactionRepo;
    }

    /**
     * Add an interaction comment.
     */
    public Interaction addInteraction() {
        return null; // TODO: Implement this method.
    }

    /**
     * Get all interaction comments.
     */
    public List<Interaction> getInteractions() {
        return interactionRepo.findAll(); // TODO: Implement this method.
    }

    /**
     * Like a comment.
     */
    public void likeInteraction() {
        return ; // TODO: Implement this method.
    }

    /**
     * Unlike a comment.
     */
    public void unlikeInteraction() {
        return ; // TODO: Implement this method.
    }

    /**
     * Dislike a comment.
     */
    public void dislikeInteraction() {
        return ; // TODO: Implement this method.
    }

    /**
     * Undislike a comment.
     */
    public void undislikeInteraction() {
        return ; // TODO: Implement this method.
    }
}
