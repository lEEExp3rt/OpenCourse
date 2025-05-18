package org.opencourse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * An action object in user history in OpenCourse.
 * 
 * @apiNote This class is abstract and should be extended by other classes to represent specific action objects.
 * @author !EEExp3rt
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ActionObject {

    /**
     * Default constructor.
     */
    protected ActionObject() {
    }

    public abstract Object getId();

}
