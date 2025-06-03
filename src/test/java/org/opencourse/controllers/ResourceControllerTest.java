package org.opencourse.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.opencourse.configs.TestSecurityConfig;
import org.opencourse.dto.request.ResourceUploadDto;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.models.Resource;
import org.opencourse.models.User;
import org.opencourse.services.ResourceManager;
import org.opencourse.utils.security.SecurityUtils;
import org.opencourse.utils.typeinfo.CourseType;
import org.opencourse.utils.typeinfo.ResourceType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Complete unit tests for {@link ResourceController}.
 * This test class covers all resource-related controller operations.
 * 
 * @author GitHub Copilot
 */
@WebMvcTest(ResourceController.class)
@Import(TestSecurityConfig.class)
@DisplayName("ResourceController Tests")
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceManager resourceManager;

    private User testUser;
    private Course testCourse;
    private Resource testResource;
    private Department testDepartment;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User(
                "testUser",
                "test@example.com",
                "hashedPassword",
                User.UserRole.USER);
        testUser = spy(testUser);
        when(testUser.getId()).thenReturn(1);

        // Create test department
        testDepartment = new Department("Computer Science");
        testDepartment = spy(testDepartment);
        when(testDepartment.getId()).thenReturn((byte) 1);

        // Create test course
        testCourse = new Course(
                "Data Structures",
                "CS201",
                testDepartment,
                CourseType.MAJOR_REQUIRED,
                new BigDecimal("3.0"));
        testCourse = spy(testCourse);
        when(testCourse.getId()).thenReturn((short) 1);
        when(testCourse.getName()).thenReturn("Data Structures");
        when(testCourse.getCode()).thenReturn("CS201");

        // Create test resource file
        Resource.ResourceFile resourceFile = new Resource.ResourceFile(
                Resource.ResourceFile.FileType.PDF,
                new BigDecimal("1.5"),
                "/path/to/test.pdf");

        // Create test resource
        testResource = new Resource(
                "Test Resource",
                "A test resource for unit testing",
                ResourceType.NOTE,
                resourceFile,
                testCourse,
                testUser);
        testResource = spy(testResource);
        when(testResource.getId()).thenReturn(1);
        when(testResource.getName()).thenReturn("Test Resource");
        when(testResource.getDescription()).thenReturn("A test resource for unit testing");
        when(testResource.getResourceType()).thenReturn(ResourceType.NOTE);
        when(testResource.getCourse()).thenReturn(testCourse);
        when(testResource.getUser()).thenReturn(testUser);
        when(testResource.getViews()).thenReturn(10);
        when(testResource.getLikes()).thenReturn(5);
        when(testResource.getDislikes()).thenReturn(1);
        when(testResource.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(testResource.getResourceFile()).thenReturn(resourceFile);

        // Create test file
        testFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes());
    }

    @Nested
    @DisplayName("Resource Creation Tests")
    class ResourceCreationTests {

        @Test
        @DisplayName("Should successfully create resource with valid data")
        void addResource_WithValidData_ShouldReturnSuccess() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.addResource(any(ResourceUploadDto.class), any(), eq(testUser)))
                        .thenReturn(testResource);

                // When & Then
                mockMvc.perform(multipart("/resource")
                        .file(testFile)
                        .param("name", "Test Resource")
                        .param("description", "A test resource for unit testing")
                        .param("resourceType", "NOTE")
                        .param("fileType", "PDF")
                        .param("courseId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("资源创建成功"))
                        .andExpect(jsonPath("$.data.id").value(1))
                        .andExpect(jsonPath("$.data.name").value("Test Resource"));

                verify(resourceManager).addResource(any(ResourceUploadDto.class), any(), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should return bad request when resource creation fails")
        void addResource_WhenCreationFails_ShouldReturnBadRequest() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.addResource(any(ResourceUploadDto.class), any(), eq(testUser)))
                        .thenReturn(null);

                // When & Then
                mockMvc.perform(multipart("/resource")
                        .file(testFile)
                        .param("name", "Test Resource")
                        .param("description", "A test resource for unit testing")
                        .param("resourceType", "NOTE")
                        .param("fileType", "PDF")
                        .param("courseId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("资源创建失败"));

                verify(resourceManager).addResource(any(ResourceUploadDto.class), any(), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException from service")
        void addResource_WithInvalidCourse_ShouldReturnBadRequest() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.addResource(any(ResourceUploadDto.class), any(), eq(testUser)))
                        .thenThrow(new IllegalArgumentException("Course not found"));

                // When & Then
                mockMvc.perform(multipart("/resource")
                        .file(testFile)
                        .param("name", "Test Resource")
                        .param("description", "A test resource for unit testing")
                        .param("resourceType", "NOTE")
                        .param("fileType", "PDF")
                        .param("courseId", "999")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("参数错误: Course not found"));

                verify(resourceManager).addResource(any(ResourceUploadDto.class), any(), eq(testUser));
            }
        }
    }

    @Nested
    @DisplayName("Resource Deletion Tests")
    class ResourceDeletionTests {

        @Test
        @DisplayName("Should successfully delete resource")
        void deleteResource_WithValidId_ShouldReturnSuccess() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.deleteResource(eq(1), eq(testUser))).thenReturn(true);

                // When & Then
                mockMvc.perform(delete("/resource/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("资源删除成功"));

                verify(resourceManager).deleteResource(eq(1), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should return bad request when resource deletion fails")
        void deleteResource_WhenDeletionFails_ShouldReturnBadRequest() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.deleteResource(eq(1), eq(testUser))).thenReturn(false);

                // When & Then
                mockMvc.perform(delete("/resource/1"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("删除资源失败，资源不存在或无权限删除"));

                verify(resourceManager).deleteResource(eq(1), eq(testUser));
            }
        }
    }

    @Nested
    @DisplayName("Resource Retrieval Tests")
    class ResourceRetrievalTests {

        @Test
        @DisplayName("Should successfully get resource by id")
        void getResource_WithValidId_ShouldReturnResource() throws Exception {
            // Given
            when(resourceManager.getResource(eq(1))).thenReturn(testResource);

            // When & Then
            mockMvc.perform(get("/resource/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("获取资源信息成功"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Test Resource"))
                    .andExpect(jsonPath("$.data.description").value("A test resource for unit testing"))
                    .andExpect(jsonPath("$.data.views").value(10))
                    .andExpect(jsonPath("$.data.likes").value(5))
                    .andExpect(jsonPath("$.data.course.id").value(1))
                    .andExpect(jsonPath("$.data.user.id").value(1));

            verify(resourceManager).getResource(eq(1));
        }

        @Test
        @DisplayName("Should return not found when resource does not exist")
        void getResource_WithInvalidId_ShouldReturnNotFound() throws Exception {
            // Given
            when(resourceManager.getResource(eq(999))).thenReturn(null);

            // When & Then
            mockMvc.perform(get("/resource/999"))
                    .andExpect(status().isNotFound());

            verify(resourceManager).getResource(eq(999));
        }

        @Test
        @DisplayName("Should successfully get resources by course")
        void getResourcesByCourse_WithValidCourseId_ShouldReturnResources() throws Exception {
            // Given
            List<Resource> resources = Arrays.asList(testResource);
            when(resourceManager.getResourcesByCourse(eq((short) 1))).thenReturn(resources);

            // When & Then
            mockMvc.perform(get("/resource/course/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("获取课程资源列表成功"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Test Resource"));

            verify(resourceManager).getResourcesByCourse(eq((short) 1));
        }

        @Test
        @DisplayName("Should successfully get resources by user")
        void getResourcesByUser_WithValidUserId_ShouldReturnResources() throws Exception {
            // Given
            List<Resource> resources = Arrays.asList(testResource);
            when(resourceManager.getResourcesByUser(eq(1))).thenReturn(resources);

            // When & Then
            mockMvc.perform(get("/resource/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("获取用户资源列表成功"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Test Resource"));

            verify(resourceManager).getResourcesByUser(eq(1));
        }

        @Test
        @DisplayName("Should return empty list when no resources found")
        void getResourcesByCourse_WithNoResources_ShouldReturnEmptyList() throws Exception {
            // Given
            when(resourceManager.getResourcesByCourse(eq((short) 1))).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/resource/course/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(resourceManager).getResourcesByCourse(eq((short) 1));
        }
    }

    @Nested
    @DisplayName("Resource Like/Unlike Tests")
    class ResourceLikeTests {

        @Test
        @DisplayName("Should successfully like resource")
        void likeResource_WithValidId_ShouldReturnSuccess() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.likeResource(eq(1), eq(testUser))).thenReturn(true);

                // When & Then
                mockMvc.perform(post("/resource/1/like"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("点赞成功"));

                verify(resourceManager).likeResource(eq(1), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should return bad request when like fails")
        void likeResource_WhenLikeFails_ShouldReturnBadRequest() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.likeResource(eq(1), eq(testUser))).thenReturn(false);

                // When & Then
                mockMvc.perform(post("/resource/1/like"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("点赞失败，资源不存在或已点赞"));

                verify(resourceManager).likeResource(eq(1), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should successfully unlike resource")
        void unlikeResource_WithValidId_ShouldReturnSuccess() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.unlikeResource(eq(1), eq(testUser))).thenReturn(true);

                // When & Then
                mockMvc.perform(post("/resource/1/unlike"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("取消点赞成功"));

                verify(resourceManager).unlikeResource(eq(1), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should return bad request when unlike fails")
        void unlikeResource_WhenUnlikeFails_ShouldReturnBadRequest() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.unlikeResource(eq(1), eq(testUser))).thenReturn(false);

                // When & Then
                mockMvc.perform(post("/resource/1/unlike"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("取消点赞失败，资源不存在或未点赞"));

                verify(resourceManager).unlikeResource(eq(1), eq(testUser));
            }
        }
    }

    @Nested
    @DisplayName("Resource View/Download Tests")
    class ResourceViewTests {

        @Test
        @DisplayName("Should successfully view/download resource")
        void viewResource_WithValidId_ShouldReturnFileStream() throws Exception {
            // Given
            byte[] fileContent = "test file content".getBytes();
            InputStream inputStream = new ByteArrayInputStream(fileContent);
            
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.getResource(eq(1))).thenReturn(testResource);
                when(resourceManager.viewResource(eq(1), eq(testUser))).thenReturn(inputStream);

                // When & Then
                mockMvc.perform(get("/resource/1/view"))
                        .andExpect(status().isOk())
                        .andExpect(header().string("Content-Disposition", "attachment; filename=\"Test Resource\""))
                        .andExpect(content().contentType(MediaType.APPLICATION_PDF));

                verify(resourceManager).getResource(eq(1));
                verify(resourceManager).viewResource(eq(1), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should return not found when resource does not exist for view")
        void viewResource_WithInvalidId_ShouldReturnNotFound() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.getResource(eq(999))).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/resource/999/view"))
                        .andExpect(status().isNotFound());

                verify(resourceManager).getResource(eq(999));
                verify(resourceManager, never()).viewResource(any(), any());
            }
        }

        @Test
        @DisplayName("Should return bad request when file stream is null")
        void viewResource_WhenFileStreamIsNull_ShouldReturnBadRequest() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.getResource(eq(1))).thenReturn(testResource);
                when(resourceManager.viewResource(eq(1), eq(testUser))).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/resource/1/view"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("无法获取资源文件"));

                verify(resourceManager).getResource(eq(1));
                verify(resourceManager).viewResource(eq(1), eq(testUser));
            }
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle runtime exception in resource creation")
        void addResource_WithRuntimeException_ShouldReturnInternalServerError() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.addResource(any(ResourceUploadDto.class), any(), eq(testUser)))
                        .thenThrow(new RuntimeException("File storage failed"));

                // When & Then
                mockMvc.perform(multipart("/resource")
                        .file(testFile)
                        .param("name", "Test Resource")
                        .param("description", "A test resource for unit testing")
                        .param("resourceType", "NOTE")
                        .param("fileType", "PDF")
                        .param("courseId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("服务器内部错误: File storage failed"));

                verify(resourceManager).addResource(any(ResourceUploadDto.class), any(), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException in like operation")
        void likeResource_WithIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
            // Given
            try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                when(resourceManager.likeResource(eq(999), eq(testUser)))
                        .thenThrow(new IllegalArgumentException("Resource not found"));

                // When & Then
                mockMvc.perform(post("/resource/999/like"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("参数错误: Resource not found"));

                verify(resourceManager).likeResource(eq(999), eq(testUser));
            }
        }

        @Test
        @DisplayName("Should handle exception in resource retrieval")
        void getResource_WithException_ShouldReturnInternalServerError() throws Exception {
            // Given
            when(resourceManager.getResource(eq(1))).thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/resource/1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("服务器内部错误: Database error"));

            verify(resourceManager).getResource(eq(1));
        }
    }
}
