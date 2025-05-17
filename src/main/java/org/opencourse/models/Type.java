package org.opencourse.models;

/**
 * Types in OpenCourse system.
 * 
 * @author !EEExp3rt
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

/**
 * Type entity recording the types of courses, resources and other entities in OpenCourse.
 */
@Entity
@Table(name = "Type")
public class Type {

    // Type ID.
    @Id
    @Column(name = "id")
    private Byte id;

    // Type name.
    @Column(name = "name", nullable = false, length = 31, unique = true)
    private String name;

    // Type description.
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Default constructor.
     */
    protected Type() {
    }

    /**
     * Constructor for Type entity.
     *
     * @param id   The unique identifier for the type.
     * @param name The name of the type.
     */
    public Type(Byte id, String name) {
        this.id = id;
        this.name = name;
        this.description = null;
    }

    /**
     * Constructor for Type entity.
     *
     * @param id          The unique identifier for the type.
     * @param name        The name of the type.
     * @param description A brief description of the type.
     */
    public Type(Byte id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Get the name of the type.
     * 
     * @return The name of the type.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of the type.
     * 
     * @return The description of the type.
     */
    public String getDescription() {
        return description;
    }

}
