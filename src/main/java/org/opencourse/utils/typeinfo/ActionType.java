package org.opencourse.utils.typeinfo;

/**
 * Action type information represents user actions in history.
 * 
 * @author !EEExp3rt
 */
public enum ActionType implements TypeInfo {
    
    CREATE_USER((byte) 21, "Create-User", "创建用户"),
    UPDATE_USER((byte) 22, "Update-User", "更新用户"),
    CREATE_DEPARTMENT((byte) 23, "Create-Department", "创建院系"),
    UPDATE_DEPARTMENT((byte) 24, "Update-Department", "更新院系"),
    CREATE_COURSE((byte) 25, "Create-Course", "创建课程"),
    UPDATE_COURSE((byte) 26, "Update-Course", "更新课程"),
    CREATE_RESOURCE((byte) 27, "Create-Resource", "创建资源"),
    UPDATE_RESOURCE((byte) 28, "Update-Resource", "更新资源"),
    CREATE_INTERACTION((byte) 29, "Create-Interaction", "发表评论"),
    DELETE_INTERACTION((byte) 30, "Delete-Interaction", "删除评论"),
    LIKE_INTERACTION((byte) 31, "Like-Interaction", "点赞评论"),
    UNLIKE_INTERACTION((byte) 32, "Unlike-Interaction", "取消点赞"),
    DISLIKE_INTERACTION((byte) 33, "Dislike-Interaction", "点踩评论"),
    UNDISLIKE_INTERACTION((byte) 34, "Undislike-Interaction", "取消点踩"),
    RATE_COURSE((byte) 35, "Rate-Course", "评分课程"),
    VIEW_RESOURCE((byte) 36, "View-Resource", "查看资源");
    
    private final byte id;
    private final String name;
    private final String description;
    
    ActionType(byte id, String name, String description) {
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
     * Get ActionType by ID.
     * 
     * @param id The ID of the action type.
     * @return The action type with the given ID, or null if not found.
     */
    public static ActionType getById(byte id) {
        for (ActionType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
}
