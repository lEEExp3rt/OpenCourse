package org.opencourse.utils.typeinfo;

import org.opencourse.models.*;

/**
 * Action type information represents user actions in history.
 * 
 * @author !EEExp3rt
 */
public enum ActionType implements TypeInfo {

    CREATE_COURSE((byte) 21, "Create-Course", "创建课程", Course.class),
    UPDATE_COURSE((byte) 22, "Update-Course", "更新课程", Course.class),
    DELETE_COURSE((byte) 23, "Delete-Course", "删除课程", Course.class),

    CREATE_DEPARTMENT((byte) 24, "Create-Department", "创建院系", Department.class),
    UPDATE_DEPARTMENT((byte) 25, "Update-Department", "更新院系", Department.class),
    DELETE_DEPARTMENT((byte) 26, "Delete-Department", "删除院系", Department.class),

    CREATE_RESOURCE((byte) 27, "Create-Resource", "创建资源", Resource.class),
    UPDATE_RESOURCE((byte) 28, "Update-Resource", "更新资源", Resource.class),
    DELETE_RESOURCE((byte) 29, "Delete-Resource", "删除资源", Resource.class),
    LIKE_RESOURCE((byte) 30, "Like-Resource", "点赞资源", Resource.class),
    UNLIKE_RESOURCE((byte) 31, "Unlike-Resource", "取消点赞", Resource.class),
    VIEW_RESOURCE((byte) 32, "View-Resource", "查看资源", Resource.class),

    CREATE_INTERACTION((byte) 33, "Create-Interaction", "发表评论", Interaction.class),
    UPDATE_INTERACTION((byte) 34, "Update-Interaction", "更新评论", Interaction.class),
    DELETE_INTERACTION((byte) 35, "Delete-Interaction", "删除评论", Interaction.class),
    LIKE_INTERACTION((byte) 36, "Like-Interaction", "点赞评论", Interaction.class),
    UNLIKE_INTERACTION((byte) 37, "Unlike-Interaction", "取消点赞", Interaction.class),
    RATE_COURSE((byte) 38, "Rate-Course", "评分课程", Course.class),

    CREATE_USER((byte) 39, "Create-User", "创建用户", User.class),
    UPDATE_USER((byte) 40, "Update-User", "更新用户", User.class),
    DELETE_USER((byte) 41, "Delete-User", "删除用户", User.class);

    private final byte id;
    private final String name;
    private final String description;
    private final Class<? extends Model<?>> objectClass;

    ActionType(byte id, String name, String description, Class<? extends Model<?>> objectClass) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.objectClass = objectClass;
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

    public Class<? extends Model<?>> getObjectClass() {
        return objectClass;
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
