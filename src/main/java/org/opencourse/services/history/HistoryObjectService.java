package org.opencourse.services.history;

import org.opencourse.models.*;
import org.opencourse.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * History object query service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class HistoryObjectService {

    private final CourseRepo courseRepo;
    private final DepartmentRepo departmentRepo;
    private final InteractionRepo interactionRepo;
    private final ResourceRepo resourceRepo;
    private final UserRepo userRepo;

    /**
     * Constructor for HistoryObjectService.
     *
     * @param courseRepo       Course repository
     * @param departmentRepo   Department repository
     * @param interactionRepo  Interaction repository
     * @param resourceRepo     Resource repository
     * @param userRepo         User repository
     */
    @Autowired
    public HistoryObjectService(
        CourseRepo courseRepo,
        DepartmentRepo departmentRepo,
        InteractionRepo interactionRepo,
        ResourceRepo resourceRepo,
        UserRepo userRepo
    ) {
        this.courseRepo = courseRepo;
        this.departmentRepo = departmentRepo;
        this.interactionRepo = interactionRepo;
        this.resourceRepo = resourceRepo;
        this.userRepo = userRepo;
    }

    /**
     * Get the object from the history record.
     * 
     * @param history The history record.
     * @return The object from the history record.
     */
    public Model<? extends Number> getHistoryObject(History history) {
        if (history == null) {
            return null;
        }

        Integer objectId = history.getObjectId();
        if (objectId == null) {
            return null;
        }

        Class<?> objectClass = history.getActionType().getObjectClass();
        return switch (objectClass.getSimpleName()) {
            case "Course" -> courseRepo.findById(objectId.shortValue()).orElse(null);
            case "Department" -> departmentRepo.findById(objectId.byteValue()).orElse(null);
            case "Interaction" -> interactionRepo.findById(objectId).orElse(null);
            case "Resource" -> resourceRepo.findById(objectId).orElse(null);
            case "User" -> userRepo.findById(objectId).orElse(null);
            default -> null;
        };
    }

}
