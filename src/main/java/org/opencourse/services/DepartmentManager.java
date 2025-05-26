package org.opencourse.services;

import org.opencourse.models.Department;
import org.opencourse.models.User;
import org.opencourse.repositories.DepartmentRepo;
import org.opencourse.repositories.UserRepo;
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

    private final DepartmentRepo departmentRepo;
    private final UserRepo userRepo;
    private final HistoryManager historyManager;

    /**
     * Constructor.
     * 
     * @param departmentRepo The department repository.
     * @param userRepo The user repository.
     * @param historyManager The history manager.
     */
    @Autowired
    public DepartmentManager(
        DepartmentRepo departmentRepo,
        UserRepo userRepo,
        HistoryManager historyManager
    ) {
        this.departmentRepo = departmentRepo;
        this.userRepo = userRepo;
        this.historyManager = historyManager;
    }

    /**
     * Adds a new department.
     * 
     * @param name The name of the department.
     * @param userId The ID of the user who is adding the department.
     * @return The created department or null if it already exists.
     * @throws IllegalArgumentException If the name is null or empty, or the user does not exist.
     */
    @Transactional
    public Department addDepartment(String name, Integer userId) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (departmentRepo.existsByName(name)) {
            return null; // Department with this name already exists.
        }
        Department department = new Department(name);
        department = departmentRepo.save(department);
        historyManager.logCreateDepartment(user, department);
        return department;
    }

    /**
     * Updates an existing department.
     * 
     * @param id The ID of the department.
     * @param name The new name of the department.
     * @param userId The ID of the user who is updating the department.
     * @return The updated department or null if the name already exists.
     * @throws IllegalArgumentException If the name is null or empty, or the user does not exist.
     */
    @Transactional
    public Department updateDepartment(Byte id, String name, Integer userId) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // If the department name already exists, return null.
        if (departmentRepo.existsByName(name)) {
            return null;
        }
        Department department = departmentRepo.findById(id).orElse(null);
        if (department != null) {
            department.setName(name);
            department = departmentRepo.save(department);
            historyManager.logUpdateDepartment(user, department);
            return department;
        }
        return null;
    }

    /**
     * Deletes a department.
     * 
     * @param id The ID of the department.
     * @param userId The ID of the user who is deleting the department.
     * @return True if the department was deleted, false otherwise.
     */
    @Transactional
    public boolean deleteDepartment(Byte id, Integer userId) {
        Department department = departmentRepo.findById(id).orElse(null);
        User user = userRepo.findById(userId).orElse(null);
        if (department != null && user != null) {
            historyManager.logDeleteDepartment(user, department);
            departmentRepo.delete(department);
            return true;
        }
        return false;
    }

    /**
     * Get a department by its ID.
     * 
     * @param id The ID of the department.
     * @return The department with the given ID or null if it doesn't exist.
     */
    public Department getDepartment(Byte id) {
        return departmentRepo.findById(id).orElse(null);
    }

    /**
     * Get all departments.
     * 
     * @return All departments in order.
     */
    public List<Department> getDepartments() {
        return departmentRepo.findAllByOrderByNameAsc();
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
            getDepartments() :
            departmentRepo.findByNameContainingIgnoreCase(name);
    }
}