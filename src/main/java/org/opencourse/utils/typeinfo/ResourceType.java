package org.opencourse.utils.typeinfo;

/**
 * Resource type information categorizes learning resources.
 * 
 * @author !EEExp3rt
 */
public enum ResourceType implements TypeInfo {
    
    EXAM((byte) 51, "Exam", "历年卷"),
    ASSIGNMENT((byte) 52, "Assignment", "作业"),
    NOTE((byte) 53, "Note", "笔记"),
    TEXTBOOK((byte) 54, "Textbook", "教材"),
    SLIDES((byte) 55, "Slides", "课件"),
    OTHER((byte) 56, "Other", "其它");
    
    private final byte id;
    private final String name;
    private final String description;
    
    ResourceType(byte id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    @Override
    public byte getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * Get ResourceType by ID.
     * 
     * @param id The ID of the resource type.
     * @return The resource type with the given ID, or null if not found.
     */
    public static ResourceType getById(byte id) {
        for (ResourceType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
}
