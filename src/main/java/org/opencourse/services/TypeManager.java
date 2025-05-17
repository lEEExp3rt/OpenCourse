package org.opencourse.services;

/**
 * Type service manager for OpenCourse.
 * 
 * @author !EEExp3rt
 */

import org.opencourse.models.Type;
import org.opencourse.repositories.TypeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

/**
 * Type manager to handle type-related operations.
 */
@Service
public class TypeManager {

    // Type repository for DAO operations.
    private final TypeRepository typeRepository; 

    /**
     * Constructor for TypeService.
     *
     * @param typeRepository the type repository
     */
    public TypeManager(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    /**
     * Initializes the TypeService by inserting default types into the database.
     */
    public void init() {
        List<Type> types = new ArrayList<Type>();

        // Course types.
        types.add(new Type((byte) 11, "General-Required", "通识必修课"));
        types.add(new Type((byte) 12, "General-Elective", "通识选修课"));
        types.add(new Type((byte) 13, "Major-Required", "专业必修课"));
        types.add(new Type((byte) 14, "Major-Optional", "专业选修课"));
        // History action types.
        types.add(new Type((byte) 21, "Create-User", "创建用户"));
        types.add(new Type((byte) 22, "Update-User", "更新用户"));
        types.add(new Type((byte) 23, "Create-Department", "创建院系"));
        types.add(new Type((byte) 24, "Update-Department", "更新院系"));
        types.add(new Type((byte) 25, "Create-Course", "创建课程"));
        types.add(new Type((byte) 26, "Update-Course", "更新课程"));
        types.add(new Type((byte) 27, "Create-Resource", "创建资源"));
        types.add(new Type((byte) 28, "Update-Resource", "更新资源"));
        types.add(new Type((byte) 29, "Create-Interaction", "发表评论"));
        types.add(new Type((byte) 30, "Delete-Interaction", "删除评论"));
        types.add(new Type((byte) 31, "Like-Interaction", "点赞评论"));
        types.add(new Type((byte) 32, "Unlike-Interaction", "取消点赞"));
        types.add(new Type((byte) 33, "Dislike-Interaction", "点踩评论"));
        types.add(new Type((byte) 34, "Undislike-Interaction", "取消点踩"));
        types.add(new Type((byte) 35, "Rate-Course", "评分课程"));
        // Resource types.
        types.add(new Type((byte) 51, "Exam", "历年卷"));
        types.add(new Type((byte) 52, "Assignment", "作业"));
        types.add(new Type((byte) 53, "Note", "笔记"));
        types.add(new Type((byte) 54, "Textbook", "教材"));
        types.add(new Type((byte) 55, "Slides", "课件"));
        types.add(new Type((byte) 56, "Other", "其它"));

        typeRepository.saveAll(types);
        return ;
    }

    /**
     * Find the type by its ID.
     * 
     * @param id the ID of the type
     * @return the type with the given ID, or null if not found
     */
    Type getTypeById(byte id) {
        return typeRepository.findById(id).orElse(null);
    }

}
