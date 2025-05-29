package org.opencourse.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import org.opencourse.configs.ApplicationConfig;
import org.opencourse.dto.request.ResourceUploadDto;
import org.opencourse.dto.request.ResourceUpdateDto;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.models.Resource;
import org.opencourse.models.Resource.ResourceFile;
import org.opencourse.models.User;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.ResourceRepo;
import org.opencourse.repositories.UserRepo;
import org.opencourse.services.storage.FileStorageService;
import org.opencourse.utils.typeinfo.CourseType;
import org.opencourse.utils.typeinfo.ResourceType;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ResourceManager}.
 * 
 * @author !EEExp3rt
 */
@ExtendWith(MockitoExtension.class)
class ResourceManagerTest {

    @Mock
    private CourseRepo courseRepo;

    @Mock
    private ResourceRepo resourceRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private HistoryManager historyManager;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private ApplicationConfig.Activity activityConfig;

    @Mock
    private ApplicationConfig.Activity.Resource activityResourceConfig;

    @InjectMocks
    private ResourceManager resourceManager;

    // Test data.
    private Course testCourse;
    private Department testDepartment;
    private User testUser;
    private User testCreator;
    private Resource testResource;
    private ResourceFile testResourceFile;
    private ResourceUploadDto testUploadDto;
    private ResourceUpdateDto testUpdateDto;

    @BeforeEach
    void setUp() {
        // Test department.
        testDepartment = new Department("Test Department");
        testDepartment = spy(testDepartment);
        lenient().when(testDepartment.getId()).thenReturn((byte) 1);

        // Test course.
        testCourse = new Course(
            "Test Course",
            "TEST101",
            testDepartment,
            CourseType.GENERAL_OPTIONAL,
            new BigDecimal("3.00")
        );
        testCourse = spy(testCourse);
        lenient().when(testCourse.getId()).thenReturn((short) 1);

        // Test user.
        testUser = new User(
            "testUser",
            "test@example.com",
            "hashedPassword",
            User.UserRole.ADMIN
        );
        testUser = spy(testUser);
        lenient().when(testUser.getId()).thenReturn(1);

        // Test creator.
        testCreator = new User(
            "testCreator",
            "test@example.com",
            "hashedPassword",
            User.UserRole.USER
        );
        testCreator = spy(testCreator);
        lenient().when(testCreator.getId()).thenReturn(2);

        // Test resource file.
        testResourceFile = new ResourceFile(
            ResourceFile.FileType.PDF,
            new BigDecimal("1.50"),
            "resources/1/test-file.pdf"
        );

        // Test resource.
        testResource = new Resource(
            "Test Resource",
            "Test Description",
            ResourceType.EXAM,
            testResourceFile,
            testCourse,
            testCreator
        );
        testResource = spy(testResource);
        lenient().when(testResource.getId()).thenReturn(1);

        // Test upload DTO.
        testUploadDto = new ResourceUploadDto(
            "Test Resource",
            "Test Description",
            ResourceType.EXAM,
            ResourceFile.FileType.PDF,
            (short) 1,
            2
        );

        // Test update DTO.
        testUpdateDto = new ResourceUpdateDto(
            1,
            "Updated Resource",
            "Updated Description",
            ResourceType.NOTE,
            ResourceFile.FileType.TEXT,
            (short) 1,
            2
        );

        // Mock configuration.
        lenient().when(applicationConfig.getActivity()).thenReturn(activityConfig);
        lenient().when(activityConfig.getResource()).thenReturn(activityResourceConfig);
        lenient().when(activityResourceConfig.getAdd()).thenReturn(10);
        lenient().when(activityResourceConfig.getDelete()).thenReturn(-5);
        lenient().when(activityResourceConfig.getLike()).thenReturn(1);
        lenient().when(activityResourceConfig.getUnlike()).thenReturn(-1);
        lenient().when(activityResourceConfig.getView()).thenReturn(5);
    }

    // Resource Add Tests.

    @Test
    @DisplayName("Should successfully add resource when all data is valid")
    void addResource_WithValidData_ShouldReturnResource() {
        // Given.
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(userRepo.findById(2)).thenReturn(Optional.of(testCreator));
        when(fileStorageService.storeFile(eq(mockFile), eq(ResourceFile.FileType.PDF), eq((short) 1)))
            .thenReturn(testResourceFile);
        when(resourceRepo.save(any(Resource.class))).thenReturn(testResource);
        when(userRepo.save(eq(testCreator))).thenReturn(testCreator);

        // When.
        Resource result = resourceManager.addResource(testUploadDto, mockFile);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testResource);

        verify(applicationConfig).getActivity();
        verify(activityConfig).getResource();
        verify(activityResourceConfig).getAdd();
        verify(testCreator).addActivity(10);

        verify(courseRepo).findById((short) 1);
        verify(userRepo).findById(2);
        verify(fileStorageService).storeFile(eq(mockFile), eq(ResourceFile.FileType.PDF), eq((short) 1));
        verify(resourceRepo).save(any(Resource.class));
        verify(userRepo).save(eq(testCreator));
        verify(historyManager).logCreateResource(eq(testCreator), any(Resource.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when course not found")
    void addResource_WithInvalidCourseId_ShouldThrowException() {
        // Given.
        when(courseRepo.findById((short) 1)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> resourceManager.addResource(testUploadDto, mockFile))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Course not found");

        verify(courseRepo).findById((short) 1);
        verifyNoInteractions(userRepo, fileStorageService, resourceRepo, historyManager);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user not found")
    void addResource_WithInvalidUserId_ShouldThrowException() {
        // Given.
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(userRepo.findById(2)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> resourceManager.addResource(testUploadDto, mockFile))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");

        verify(courseRepo).findById((short) 1);
        verify(userRepo).findById(2);
        verifyNoInteractions(fileStorageService, resourceRepo, historyManager);
    }

    @Test
    @DisplayName("Should throw RuntimeException when file storage fails")
    void addResource_WithFileStorageFailure_ShouldThrowException() {
        // Given.
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(userRepo.findById(2)).thenReturn(Optional.of(testCreator));
        when(fileStorageService.storeFile(eq(mockFile), eq(ResourceFile.FileType.PDF), eq((short) 1)))
            .thenReturn(null);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");

        // When & Then.
        assertThatThrownBy(() -> resourceManager.addResource(testUploadDto, mockFile))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to store file test.pdf");

        verify(courseRepo).findById((short) 1);
        verify(userRepo).findById(2);
        verify(fileStorageService).storeFile(eq(mockFile), eq(ResourceFile.FileType.PDF), eq((short) 1));
        verifyNoInteractions(resourceRepo, historyManager);
    }

    @Test
    @DisplayName("Should rollback file storage when resource save fails")
    void addResource_WithResourceSaveFailure_ShouldRollbackFileStorage() {
        // Given.
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(userRepo.findById(2)).thenReturn(Optional.of(testCreator));
        when(fileStorageService.storeFile(eq(mockFile), eq(ResourceFile.FileType.PDF), eq((short) 1)))
            .thenReturn(testResourceFile);
        when(resourceRepo.save(any(Resource.class))).thenThrow(new RuntimeException("Database error"));
        when(fileStorageService.deleteFile(testResourceFile.getFilePath())).thenReturn(true);

        // When & Then.
        assertThatThrownBy(() -> resourceManager.addResource(testUploadDto, mockFile))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(fileStorageService).deleteFile(testResourceFile.getFilePath());
    }

    @Test
    @DisplayName("Should throw RuntimeException when file rollback fails")
    void addResource_WithFileRollbackFailure_ShouldThrowRollbackException() {
        // Given.
        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(testCourse));
        when(userRepo.findById(2)).thenReturn(Optional.of(testCreator));
        when(fileStorageService.storeFile(eq(mockFile), eq(ResourceFile.FileType.PDF), eq((short) 1)))
            .thenReturn(testResourceFile);
        RuntimeException originalException = new RuntimeException("Database error");
        when(resourceRepo.save(any(Resource.class))).thenThrow(originalException);
        when(fileStorageService.deleteFile(testResourceFile.getFilePath())).thenReturn(false);

        // When & Then.
        assertThatThrownBy(() -> resourceManager.addResource(testUploadDto, mockFile))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to delete file while rollbacking ")
            .hasCause(originalException);

        verify(fileStorageService).deleteFile(testResourceFile.getFilePath());
    }

    // Resource Delete Tests.

    @Test
    @DisplayName("Should successfully delete resource when data is valid")
    void deleteResource_WithValidData_ShouldReturnTrueAndDeleteSuccessfully() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.save(eq(testCreator))).thenReturn(testCreator);
        when(fileStorageService.deleteFile(testResourceFile.getFilePath())).thenReturn(true);

        // When.
        boolean result = resourceManager.deleteResource(1, 2);

        // Then.
        assertThat(result).isTrue();
        verify(resourceRepo).findById(1);

        verify(applicationConfig).getActivity();
        verify(activityConfig).getResource();
        verify(activityResourceConfig).getDelete();
        verify(testCreator).addActivity(-5);

        verify(userRepo).save(eq(testCreator));
        verify(historyManager).logDeleteResource(eq(testCreator), eq(testResource));
        verify(resourceRepo).delete(eq(testResource));
        verify(fileStorageService).deleteFile(testResourceFile.getFilePath());
    }

    @Test
    @DisplayName("Should return false when deleting non-existent resource")
    void deleteResource_WithInvalidResourceId_ShouldReturnFalse() {
        // Given.
        when(resourceRepo.findById(999)).thenReturn(Optional.empty());

        // When.
        boolean result = resourceManager.deleteResource(999, 1);

        // Then.
        assertThat(result).isFalse();

        verify(resourceRepo).findById(999);
        verifyNoInteractions(
            userRepo,
            historyManager,
            fileStorageService,
            applicationConfig
        );

        verify(userRepo, never()).save(any());
        verify(resourceRepo, never()).delete(any());
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    @DisplayName("Should return false when deleting with invalid user")
    void deleteResource_WithInvalidUserId_ShouldReturnFalse() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));

        // When.
        boolean result = resourceManager.deleteResource(1, 999);

        // Then.
        assertThat(result).isFalse();

        verify(resourceRepo).findById(1);
        verifyNoInteractions(
            userRepo,
            historyManager,
            fileStorageService,
            applicationConfig
        );

        verify(userRepo, never()).save(any());
        verify(resourceRepo, never()).delete(any());
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    @DisplayName("Should throw RuntimeException when resource deletion fails")
    void deleteResource_WithResourceDeleteFailure_ShouldThrowException() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.save(eq(testCreator))).thenReturn(testCreator);
        doThrow(new RuntimeException("Database error")).when(resourceRepo).delete(eq(testResource));

        // When & Then.
        assertThatThrownBy(() -> resourceManager.deleteResource(1, 2))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to delete resource");

        verify(resourceRepo).delete(eq(testResource));
        verifyNoInteractions(fileStorageService);
    }

    @Test
    @DisplayName("Should throw RuntimeException when file deletion fails")
    void deleteResource_WithFileDeleteFailure_ShouldThrowException() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.save(eq(testCreator))).thenReturn(testCreator);
        when(fileStorageService.deleteFile(testResourceFile.getFilePath())).thenReturn(false);

        // When & Then.
        assertThatThrownBy(() -> resourceManager.deleteResource(1, 2))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to delete resource file");

        verify(fileStorageService).deleteFile(testResourceFile.getFilePath());
    }

    // Resource Update Tests.

    @Test
    @DisplayName("Should update resource metadata only - NOT IMPLEMENTED")
    void updateResource_WithMetadataOnly_ShouldReturnUpdatedResource() {
        // Given.
        // TODO: This method is not implemented yet.

        // When.
        Resource result = resourceManager.updateResource(testUpdateDto);

        // Then.
        assertThat(result).isNull();
        // verify(resourceRepo).findById(1);
        // verify(resourceRepo).save(any(Resource.class));
        // verify(historyManager).logUpdateResource(any(User.class), any(Resource.class));
    }

    @Test
    @DisplayName("Should update resource with new file - NOT IMPLEMENTED")
    void updateResource_WithFile_ShouldReturnUpdatedResource() {
        // Given.
        // TODO: This method is not implemented yet.

        // When.
        Resource result = resourceManager.updateResource(testUpdateDto, mockFile);

        // Then.
        assertThat(result).isNull();
        // verify(resourceRepo).findById(1);
        // verify(fileStorageService).deleteFile(anyString()); // Delete old file
        // verify(fileStorageService).storeFile(eq(mockFile), any(), any()); // Store new file
        // verify(resourceRepo).save(any(Resource.class));
        // verify(historyManager).logUpdateResource(any(User.class), any(Resource.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating non-existent resource - NOT IMPLEMENTED")
    void updateResource_WithInvalidResourceId_ShouldThrowException() {
        // Given.
        ResourceUpdateDto invalidDto = new ResourceUpdateDto(
            999, "Updated", "Description", ResourceType.NOTE, 
            ResourceFile.FileType.TEXT, (short) 1, 2
        );

        // When.
        Resource result = resourceManager.updateResource(invalidDto);

        // Then.
        // TODO: This method is not implemented yet.
        assertThat(result).isNull();
        // assertThatThrownBy(() -> resourceManager.updateResource(invalidDto))
        //     .isInstanceOf(IllegalArgumentException.class)
        //     .hasMessage("Resource not found");
    }

    // Resource Get Tests.

    @Test
    @DisplayName("Should return resource when found")
    void getResource_WithValidId_ShouldReturnResource() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));

        // When.
        Resource result = resourceManager.getResource(1);

        // Then.
        assertThat(result).isEqualTo(testResource);
        verify(resourceRepo).findById(1);
    }

    @Test
    @DisplayName("Should return null when resource not found")
    void getResource_WithInvalidId_ShouldReturnNull() {
        // Given.
        when(resourceRepo.findById(999)).thenReturn(Optional.empty());

        // When.
        Resource result = resourceManager.getResource(999);

        // Then.
        assertThat(result).isNull();
        verify(resourceRepo).findById(999);
    }

    @Test
    @DisplayName("Should return resources by course")
    void getResourcesByCourse_WithValidCourseId_ShouldReturnResources() {
        // Given.
        List<Resource> resources = Arrays.asList(testResource);
        when(resourceRepo.findByCourseId((short) 1)).thenReturn(resources);

        // When.
        List<Resource> result = resourceManager.getResourcesByCourse((short) 1);

        // Then.
        assertThat(result).hasSize(1);
        assertThat(result).contains(testResource);
        verify(resourceRepo).findByCourseId((short) 1);
    }

    @Test
    @DisplayName("Should return empty list when no resources found for course")
    void getResourcesByCourse_WithNoResources_ShouldReturnEmptyList() {
        // Given.
        when(resourceRepo.findByCourseId((short) 999)).thenReturn(Arrays.asList());

        // When.
        List<Resource> result = resourceManager.getResourcesByCourse((short) 999);

        // Then.
        assertThat(result).isEmpty();
        verify(resourceRepo).findByCourseId((short) 999);
    }

    @Test
    @DisplayName("Should return resources by user")
    void getResourcesByUser_WithValidUserId_ShouldReturnResources() {
        // Given.
        List<Resource> resources = Arrays.asList(testResource);
        when(resourceRepo.findByUserId(2)).thenReturn(resources);

        // When.
        List<Resource> result = resourceManager.getResourcesByUser(2);

        // Then.
        assertThat(result).hasSize(1);
        assertThat(result).contains(testResource);
        verify(resourceRepo).findByUserId(2);
    }

    @Test
    @DisplayName("Should return empty list when no resources found for user")
    void getResourcesByUser_WithNoResources_ShouldReturnEmptyList() {
        // Given.
        when(resourceRepo.findByUserId(999)).thenReturn(Arrays.asList());

        // When.
        List<Resource> result = resourceManager.getResourcesByUser(999);

        // Then.
        assertThat(result).isEmpty();
        verify(resourceRepo).findByUserId(999);
    }

    @Test
    @DisplayName("Should successfully like resource when user hasn't liked it")
    void likeResource_WithUserNotLiked_ShouldReturnTrue() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
        when(historyManager.getLikeStatus(testUser, testResource)).thenReturn(false);
        when(resourceRepo.save(eq(testResource))).thenReturn(testResource);
        when(userRepo.save(eq(testCreator))).thenReturn(testCreator);

        // When.
        boolean result = resourceManager.likeResource(1, 1);

        // Then.
        assertThat(result).isTrue();

        verify(resourceRepo).findById(1);
        verify(userRepo).findById(1);

        verify(applicationConfig).getActivity();
        verify(activityConfig).getResource();
        verify(activityResourceConfig).getLike();
        verify(testCreator).addActivity(1);

        verify(historyManager).getLikeStatus(testUser, testResource);
        verify(resourceRepo).save(eq(testResource));
        verify(userRepo).save(eq(testCreator));
        verify(historyManager).logLikeResource(testUser, testResource);
    }

    @Test
    @DisplayName("Should return false when user already liked resource")
    void likeResource_WithUserAlreadyLiked_ShouldReturnFalse() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
        when(historyManager.getLikeStatus(testUser, testResource)).thenReturn(true);

        // When.
        boolean result = resourceManager.likeResource(1, 1);

        // Then.
        assertThat(result).isFalse();
        verify(resourceRepo).findById(1);
        verify(userRepo).findById(1);
        verify(historyManager).getLikeStatus(testUser, testResource);
        verifyNoMoreInteractions(resourceRepo, userRepo, historyManager);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when liking non-existent resource")
    void likeResource_WithInvalidResourceId_ShouldThrowException() {
        // Given.
        when(resourceRepo.findById(999)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> resourceManager.likeResource(999, 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Resource not found");

        verify(resourceRepo).findById(999);
        verifyNoInteractions(userRepo, historyManager);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when liking with invalid user")
    void likeResource_WithInvalidUserId_ShouldThrowException() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> resourceManager.likeResource(1, 999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");

        verify(resourceRepo).findById(1);
        verify(userRepo).findById(999);
        verifyNoMoreInteractions(historyManager);
    }

    // Resource Unlike Tests.

    @Test
    @DisplayName("Should successfully unlike resource when user has liked it")
    void unlikeResource_WithUserHasLiked_ShouldReturnTrue() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
        when(historyManager.getLikeStatus(testUser, testResource)).thenReturn(true);
        when(resourceRepo.save(eq(testResource))).thenReturn(testResource);
        when(userRepo.save(eq(testCreator))).thenReturn(testCreator);

        // When.
        boolean result = resourceManager.unlikeResource(1, 1);

        // Then.
        assertThat(result).isTrue();

        verify(resourceRepo).findById(1);
        verify(userRepo).findById(1);

        verify(applicationConfig).getActivity();
        verify(activityConfig).getResource();
        verify(activityResourceConfig).getUnlike();
        verify(testCreator).addActivity(-1);

        verify(historyManager).getLikeStatus(testUser, testResource);
        verify(resourceRepo).save(eq(testResource));
        verify(userRepo).save(eq(testCreator));
        verify(historyManager).logUnlikeResource(testUser, testResource);
    }

    @Test
    @DisplayName("Should return false when user hasn't liked resource")
    void unlikeResource_WithUserNotLiked_ShouldReturnFalse() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
        when(historyManager.getLikeStatus(testUser, testResource)).thenReturn(false);

        // When.
        boolean result = resourceManager.unlikeResource(1, 1);

        // Then.
        assertThat(result).isFalse();
        verify(resourceRepo).findById(1);
        verify(userRepo).findById(1);
        verify(historyManager).getLikeStatus(testUser, testResource);
        verifyNoMoreInteractions(resourceRepo, userRepo, historyManager);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unliking non-existent resource")
    void unlikeResource_WithInvalidResourceId_ShouldThrowException() {
        // Given.
        when(resourceRepo.findById(999)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> resourceManager.unlikeResource(999, 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Resource not found");

        verify(resourceRepo).findById(999);
        verifyNoInteractions(userRepo, historyManager);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unliking with invalid user")
    void unlikeResource_WithInvalidUserId_ShouldThrowException() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> resourceManager.unlikeResource(1, 999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");

        verify(resourceRepo).findById(1);
        verify(userRepo).findById(999);
        verifyNoMoreInteractions(historyManager);
    }

    // Resource View Tests.

    @Test
    @DisplayName("Should successfully view resource")
    void viewResource_WithValidData_ShouldReturnInputStream() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepo.save(eq(testCreator))).thenReturn(testCreator);
        when(fileStorageService.getFile(testResourceFile)).thenReturn(mockInputStream);

        // When.
        InputStream result = resourceManager.viewResource(1, 1);

        // Then.
        assertThat(result).isEqualTo(mockInputStream);

        verify(resourceRepo).findById(1);
        verify(userRepo).findById(1);

        verify(applicationConfig).getActivity();
        verify(activityConfig).getResource();
        verify(activityResourceConfig).getView();
        verify(testCreator).addActivity(5);

        verify(userRepo).save(eq(testCreator));
        verify(historyManager).logViewResource(testUser, testResource);
        verify(fileStorageService).getFile(testResourceFile);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when viewing non-existent resource")
    void viewResource_WithInvalidResourceId_ShouldThrowException() {
        // Given.
        when(resourceRepo.findById(999)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> resourceManager.viewResource(999, 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Resource not found");

        verify(resourceRepo).findById(999);
        verifyNoInteractions(userRepo, historyManager, fileStorageService);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when viewing with invalid user")
    void viewResource_WithInvalidUserId_ShouldThrowException() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        // When & Then.
        assertThatThrownBy(() -> resourceManager.viewResource(1, 999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");

        verify(resourceRepo).findById(1);
        verify(userRepo).findById(999);
        verifyNoInteractions(historyManager, fileStorageService);
    }

    @Test
    @DisplayName("Should return null when file service returns null")
    void viewResource_WithFileServiceReturnsNull_ShouldReturnNull() {
        // Given.
        when(resourceRepo.findById(1)).thenReturn(Optional.of(testResource));
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepo.save(eq(testCreator))).thenReturn(testCreator);
        when(fileStorageService.getFile(testResourceFile)).thenReturn(null);

        // When.
        InputStream result = resourceManager.viewResource(1, 1);

        // Then.
        assertThat(result).isNull();
        verify(resourceRepo).findById(1);
        verify(userRepo).findById(1);
        verify(userRepo).save(eq(testCreator));
        verify(historyManager).logViewResource(testUser, testResource);
        verify(fileStorageService).getFile(testResourceFile);
    }
}
