package org.opencourse.services;

import org.opencourse.models.Department;
import org.opencourse.repositories.DepartmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import jakarta.transaction.Transactional;

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
     * @throws IllegalArgumentException if the name is null or empty.
     */
    @Transactional
    public Department addDepartment(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        return repo.existsByName(name) ? null : repo.save(new Department(name));
    }

    /**
     * Updates an existing department.
     * 
     * @param id The ID of the department.
     * @param name The new name of the department.
     * @return The updated department or null if it doesn't exist.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    @Transactional
    public Department updateDepartment(Byte id, String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        Department department = repo.findById(id).orElse(null);
        if (department != null) {
            department.setName(name);
            return repo.save(department);
        }
        return null;
    }

    /**
     * Get a department by its ID.
     * 
     * @param id The ID of the department.
     * @return The department with the given ID or null if it doesn't exist.
     */
    public Department getDepartment(Byte id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * Get departments by the fuzzy matching name.
     * 
     * If no name is provided, all departments are returned in order.
     * 
     * @param name The name of the department.
     * @return The departments matching the given name.
     */
    public List<Department> getDepartments(String name) {
        return name == null || name.isEmpty() ?
            repo.findAllByOrderByNameAsc() :
            repo.findByNameContainingIgnoreCase(name);
    }
}