/**
 * @brief `Type` table initializer script.
 * @description This table works as a hash table to record the mapping for type ID and their name, description.
 * @Author !EEExp3rt
 */

create table `Type` if not exists (
    `id` tinyint primary key,
    `name` varchar(31) not null unique,
    `description` varchar(255) default null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Insert the default values for the `Type` table.

insert into `Type` (`id`, `name`, `description`) values
    -- Course types.
    (11, 'General-Required', '通识必修课'),
    (12, 'General-Elective', '通识选修课'),
    (13, 'Major-Required',   '专业必修课'),
    (14, 'Major-Optional',   '专业选修课'),
    -- History action types.
    (21, 'Create-User',           '创建用户'),
    (22, 'Update-User',           '更新用户'),
    (23, 'Create-Department',     '创建院系'),
    (24, 'Update-Department',     '更新院系'),
    (25, 'Create-Course',         '创建课程'),
    (26, 'Update-Course',         '更新课程'),
    (27, 'Create-Resource',       '创建资源'),
    (28, 'Update-Resource',       '更新资源'),
    (29, 'Create-Interaction',    '发表评论'),
    (30, 'Delete-Interaction',    '删除评论'),
    (31, 'Like-Interaction',      '点赞评论'),
    (32, 'Unlike-Interaction',    '取消点赞'),
    (33, 'Dislike-Interaction',   '点踩评论'),
    (34, 'Undislike-Interaction', '取消点踩'),
    (35, 'Rate-Course',           '评分课程'),
    (36, 'View-Resource',         '查看资源'),
    -- Resource types.
    (51, 'Exam',       '历年卷'),
    (52, 'Assignment', '作业'),
    (53, 'Note',       '笔记'),
    (54, 'Textbook',   '教材'),
    (55, 'Slides',     '课件'),
    (56, 'Other',      '其它');
