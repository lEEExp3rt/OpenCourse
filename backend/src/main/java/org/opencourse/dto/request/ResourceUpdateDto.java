package org.opencourse.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.opencourse.utils.typeinfo.ResourceType;
import org.opencourse.models.Resource.ResourceFile.FileType;

/**
 * DTO for resource update requests.
 * 
 * @author !EEExp3rt
 * @apiNote Only metadata is included in this DTO.
 */
public class ResourceUpdateDto {

    @NotNull
    private Integer id;

    @NotBlank(message = "资源名称不能为空")
    @Size(max = 63, message = "资源名称太长")
    private String name;

    @Size(max = 255, message = "资源描述太长")
    private String description;

    @NotNull(message = "资源类型不能为空")
    private ResourceType resourceType;

    @NotNull(message = "文件类型不能为空")
    private FileType fileType;

    @NotNull
    private Short courseId;

    /**
     * Default constructor.
     */
    public ResourceUpdateDto() {
    }

    /**
     * Constructor.
     * 
     * @param id             Resource id.
     * @param name           Resource name.
     * @param description    Resource description.
     * @param resourceTypeId Resource type id.
     * @param fileTypeName   File type name.
     * @param courseId       Course id.
     */
    public ResourceUpdateDto(
        Integer id,
        String name,
        String description,
        Byte resourceTypeId,
        String fileTypeName,
        Short courseId
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.resourceType = ResourceType.getById(resourceTypeId.byteValue());
        this.fileType = FileType.from(fileTypeName);
        this.courseId = courseId;
    }

    /**
     * Constructor.
     * 
     * @param id           Resource id.
     * @param name         Resource name.
     * @param description  Resource description.
     * @param resourceType Resource type.
     * @param fileType     File type.
     * @param courseId     Course id.
     */
    public ResourceUpdateDto(
        Integer id,
        String name,
        String description,
        ResourceType resourceType,
        FileType fileType,
        Short courseId
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.resourceType = resourceType;
        this.fileType = fileType;
        this.courseId = courseId;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    @JsonSetter("typeId")
    public void setResourceType(Byte resourceTypeId) {
        this.resourceType = ResourceType.getById(resourceTypeId);
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(String fileTypeName) {
        this.fileType = FileType.from(fileTypeName);
    }

    public Short getCourseId() {
        return courseId;
    }

    public void setCourseId(Short courseId) {
        this.courseId = courseId;
    }
}
