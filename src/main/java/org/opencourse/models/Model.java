package org.opencourse.models;

/**
 * Abstract base class for all models in OpenCourse.
 * 
 * @author !EEExp3rt
 * @apiNote This class serves as a base for all models in the OpenCourse application.
 */
public abstract class Model<T extends Number> {

    /**
     * Get the ID of the model.
     * 
     * @return The ID of the model.
     */
    public abstract T getId();
}
