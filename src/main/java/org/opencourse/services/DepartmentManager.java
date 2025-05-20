package org.opencourse.services;

import org.opencourse.models.Department;
import org.opencourse.repositories.DepartmentRepo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Department service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class DepartmentManager {

    // Data Access Object.
    private final DepartmentRepo repo;

    /**
     * Constructor.
     * 
     * @param repo The department repository.
     */
    @Autowired
    public DepartmentManager(DepartmentRepo repo) {
        this.repo = repo;
    }

    /**
     * Adds a new department.
     * 
     * @param name The name of the department.
     * @return The created department or null if it already exists.
     */
    public Department addDepartment(String name) {
        return name == null || name.isEmpty() || repo.existsByName(name)
                ? null
                : repo.save(new Department(name));
    }

    /**
     * Get a department by its name.
     * 
     * @return The department with the given name or null if it doesn't exist.
     */
    public Department getDepartment(String name) {
        return name == null || name.isEmpty() ?
            null :
            repo.findByName(name).orElse(null);
    }

    /**
     * Get all departments in order.
     * 
     * @return A list of all departments.
     */
    public List<Department> getDepartments() {
        return repo.findAllByOrderByNameAsc();
    }
}