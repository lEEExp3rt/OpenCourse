package org.opencourse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

/**
 * Department entity class in OpenCourse.
 * 
 * @author !EEExp3rt
 */
@Entity
@Table(name = "Department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(name = "name", nullable = false, length = 31, unique = true)
    private String name;

    /**
     * Default constructor.
     */
    protected Department() {
    }

    /**
     * Constructor.
     * 
     * @param name The name of the department.
     */
    public Department(String name) {
        this.name = name;
    }

    // Getters and Setters

    public Byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
