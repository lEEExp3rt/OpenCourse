package org.opencourse.services;

import org.opencourse.configs.ApplicationConfig;
import org.opencourse.dto.request.ResourceUploadDto;
import org.opencourse.dto.request.ResourceUpdateDto;
import org.opencourse.models.Course;
import org.opencourse.models.Resource;
import org.opencourse.models.Resource.ResourceFile;
import org.opencourse.models.User;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.ResourceRepo;
import org.opencourse.repositories.UserRepo;
import org.opencourse.services.storage.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.util.List;

/**
 * Resource service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class ResourceManager {

    private final CourseRepo courseRepo;
    private final ResourceRepo resourceRepo;
    private final UserRepo userRepo;
    private final ApplicationConfig applicationConfig;
    private final FileStorageService fileStorageService;
    private final HistoryManager historyManager;

    /**
     * Constructor.
     * 
     * @param courseRepo         The course repository.
     * @param resourceRepo       The resource repository.
     * @param userRepo           The user repository.
     * @param applicationConfig  The application configuration.
     * @param fileStorageService The file storage service.
     * @param historyManager     The history manager.
     */
    @Autowired
    public ResourceManager(
        CourseRepo courseRepo,
        ResourceRepo resourceRepo,
        UserRepo userRepo,
        ApplicationConfig applicationConfig,
        FileStorageService fileStorageService,
        HistoryManager historyManager
    ) {
        this.courseRepo = courseRepo;
        this.resourceRepo = resourceRepo;
        this.userRepo = userRepo;
        this.applicationConfig = applicationConfig;
        this.fileStorageService = fileStorageService;
        this.historyManager = historyManager;
    }

    /**
     * Add a new resource.
     * 
     * @param dto  The resource metadata.
     * @param file The file to add.
     * @param user The uploader.
     * @return The added resource.
     * @throws IllegalArgumentException If the course is not found.
     * @throws RuntimeException         If the file storage fails or other error happens.
     */
    @Transactional
    public Resource addResource(ResourceUploadDto dto, MultipartFile file, User user) throws IllegalArgumentException, RuntimeException {
        // Get course infomation.
        Course course = courseRepo.findById(dto.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        // Store the file.
        ResourceFile resourceFile = fileStorageService.storeFile(
            file,
            dto.getFileType(),
            course.getId()
        );
        if (resourceFile == null) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename());
        }
        try {
            // Create the resource.
            Resource resource = new Resource(
                dto.getName(),
                dto.getDescription(),
                dto.getResourceType(),
                resourceFile,
                course,
                user
            );
            // Save the resource.
            resource = resourceRepo.save(resource);
            // Add user activity.
            user.addActivity(applicationConfig.getActivity().getResource().getAdd());
            user = userRepo.save(user);
            // Add resource creation history record.
            historyManager.logCreateResource(user, resource);
            return resource;
        } catch (Exception e) {
            // Rollback the file storage.
            if (!fileStorageService.deleteFile(resourceFile.getFilePath())) {
                throw new RuntimeException("Failed to delete file while rollbacking ", e);
            }
            throw e;
        }
    }

    /**
     * Delete a resource.
     * 
     * @param id The resource id.
     * @param user The deleter.
     * @return True if the resource is deleted successfully, false otherwise.
     * @throws RuntimeException If failed to delete the resource.
     */
    @Transactional
    public boolean deleteResource(Integer id, User user) throws RuntimeException {
        // Get the resource.
        Resource resource = resourceRepo.findById(id).orElse(null);
        if (resource == null) {
            return false;
        }
        User creator = resource.getUser();
        // Check if the user is the creator of the resource or administrator.
        if (!user.getId().equals(creator.getId()) && !user.getRole().equals(User.UserRole.ADMIN)) {
            return false;
        }
        // Delete the resource.
        try {
            creator.addActivity(applicationConfig.getActivity().getResource().getDelete());
            creator = userRepo.save(creator);
            historyManager.logDeleteResource(user, resource);
            resourceRepo.delete(resource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete resource", e);
        }
        // Delete the resource file.
        if (!fileStorageService.deleteFile(resource.getResourceFile().getFilePath())) {
            throw new RuntimeException("Failed to delete resource file");
        }
        return true;
    }

    /**
     * Update a resource.
     * 
     * @param dto The resource metadata.
     * @param user The updater.
     * @return The updated resource.
     * @throws IllegalArgumentException If the resource is not found.
     */
    @Transactional
    public Resource updateResource(ResourceUpdateDto dto, User user) throws IllegalArgumentException {
        return null; // TODO: Implement this method.
    }

    /**
     * Update a resource.
     * 
     * @param dto  The resource metadata.
     * @param file The file to update.
     * @param user The updater.
     * @return The updated resource.
     * @throws IllegalArgumentException If the resource is not found.
     */
    @Transactional
    public Resource updateResource(ResourceUpdateDto dto, MultipartFile file, User user) throws IllegalArgumentException {
        return null; // TODO: Implement this method.
    }

    /**
     * Get a resource.
     * 
     * @param id The resource id.
     * @return The resource entity if found else null.
     */
    public Resource getResource(Integer id) {
        return resourceRepo.findById(id).orElse(null);
    }

    /**
     * Get all resources from a course.
     * @param courseId The course ID.
     * @return The list of resources.
     */
    public List<Resource> getResourcesByCourse(Short courseId) {
        return resourceRepo.findByCourseId(courseId);
    }

    /**
     * Get all resources from a user creator.
     * @param userId The user creator ID.
     * @return The list of resources.
     */
    public List<Resource> getResourcesByUser(Integer userId) {
        return resourceRepo.findByUserId(userId);
    }

    /**
     * Like a resource.
     * 
     * @param id The resource id.
     * @param user The user.
     * @return True if the resource is liked, false if the user has already liked it.
     * @throws IllegalArgumentException If the resource is not found.
     */
    @Transactional
    public boolean likeResource(Integer id, User user) throws IllegalArgumentException {
        // Get the resource.
        Resource resource = resourceRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        // Check if the user has already liked the resource.
        if (historyManager.getLikeStatus(user, resource)) {
            return false;
        }
        resource.likes();
        resource = resourceRepo.save(resource);
        // Add creator activity.
        User creator = resource.getUser();
        creator.addActivity(applicationConfig.getActivity().getResource().getLike());
        userRepo.save(creator);
        // Add a like history record.
        historyManager.logLikeResource(user, resource);
        return true;
    }

    /**
     * Unlike a resource.
     * 
     * @param id The resource id.
     * @param user The user.
     * @throws IllegalArgumentException If the resource is not found.
     */
    @Transactional
    public boolean unlikeResource(Integer id, User user) throws IllegalArgumentException {
        // Get the resource.
        Resource resource = resourceRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        // Check if the user has liked the resource.
        if (!historyManager.getLikeStatus(user, resource)) {
            return false;
        }
        resource.unlikes();
        resource = resourceRepo.save(resource);
        // Add creator activity.
        User creator = resource.getUser();
        creator.addActivity(applicationConfig.getActivity().getResource().getUnlike());
        userRepo.save(creator);
        // Add a unlike history record.
        historyManager.logUnlikeResource(user, resource);
        return true;
    }

    /**
     * View a resource file.
     * 
     * @param id The resource id.
     * @param user The user.
     * @throws IllegalArgumentException If the resource is not found.
     */
    @Transactional
    public InputStream viewResource(Integer id, User user) throws IllegalArgumentException {
        // Get the resource.
        Resource resource = resourceRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        // Add creator activity.
        User creator = resource.getUser();
        creator.addActivity(applicationConfig.getActivity().getResource().getView());
        userRepo.save(creator);
        // Add a view history record.
        historyManager.logViewResource(user, resource);
        return fileStorageService.getFile(resource.getResourceFile());
    }
}
