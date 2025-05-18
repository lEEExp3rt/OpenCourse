package org.opencourse.utils.typeinfo;

/**
 * Course types information to categorize different types of courses.
 * 
 * @author !EEExp3rt
 */
public enum CourseType implements TypeInfo {
    
    GENERAL_REQUIRED((byte) 11, "General-Required", "通识必修课"),
    GENERAL_OPTIONAL((byte) 12, "General-Optional", "通识选修课"),
    MAJOR_REQUIRED((byte) 13, "Major-Required", "专业必修课"),
    MAJOR_OPTIONAL((byte) 14, "Major-Optional", "专业选修课");
    
    private final byte id;
    private final String name;
    private final String description;
    
    CourseType(byte id, String name, String description) {
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
     * Get CourseType by ID.
     * 
     * @param id The ID of the course type.
     * @return The course type with the given ID, or null if not found.
     */
    public static CourseType getById(byte id) {
        for (CourseType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
}
