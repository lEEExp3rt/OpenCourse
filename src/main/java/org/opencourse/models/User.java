package org.opencourse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * User entity class in OpenCourse.
 * 
 * @author !EEExp3rt
 */
@Entity
@Table(name = "User")
public class User extends ActionObject {

    @Override
    public Integer getId() {
        return null; // TODO
    }
}
