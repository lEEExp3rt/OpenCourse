/**
 * @file schema.sql
 * @brief Database schema initializer script.
 * @description This file defines the database schema for OpenCourse.
 * @Author !EEExp3rt
 */

-- `Department` table records the departments information.
create table `Department` if not exists (
    `id` tinyint auto_increment primary key,
    `name` varchar(31) not null unique
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- `Course` table records the courses information.
create table `Course` if not exists (
    `id` smallint primary key auto_increment,
    `name` varchar(31) not null,
    `code` varchar(31) unique not null,
    `department_id` tinyint not null,
    `course_type` enum(
        'GENERAL_REQUIRED',
        'GENERAL_OPTIONAL',
        'MAJOR_REQUIRED',
        'MAJOR_OPTIONAL'
    ) not null,
    `credits` decimal(3, 1) not null,
    foreign key (`department_id`) references `Department`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- `User` table records the users information.
create table `User` if not exists (
    `id` int auto_increment primary key,
    `name` varchar(31) not null unique,
    `email` varchar(63) not null unique,
    `password` varchar(255) not null,
    `role` enum('user', 'visitor', 'admin') not null,
    `activity` int default 1,
    `created_at` timestamp default current_timestamp,
    `updated_at` timestamp default null on update current_timestamp
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- `Resource` table records the resources information.
create table `Resource` if not exists (
    /* Basic Information */
    `id` int auto_increment primary key,
    `name` varchar(63) not null,
    `description` varchar(255) default null,
    `resourse_type` enum(
        'EXAM',
        'ASSIGNMENT',
        'NOTE',
        'TEXTBOOK',
        'SLIDES',
        'OTHER'
    ) not null,
    /* File Metadata */
    `file_type` enum('pdf', 'text', 'other') not null,
    `file_size` decimal(3, 2) not null,
    `file_path` varchar(255) not null,
    /* Time Metadata */
    `created_at` timestamp default current_timestamp,
    /* Relevant Information */
    `course_id` smallint not null,
    `user_id` int not null,
    /* Statistics */
    `views` int default 0,
    `likes` int default 0,
    `dislikes` int default 0,
    /* Foreign Keys */
    foreign key (`course_id`) references `Course`(`id`),
    foreign key (`user_id`) references `User`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- `Interaction` table records the user interactions in courses.
create table `Interaction` if not exists (
    /* Basic Information */
    `id` int primary key auto_increment,
    `course_id` smallint not null,
    `user_id` int not null,
    `content` text default null,
    `rating` tinyint default null,
    /* Statistics */
    `likes` int default 0,
    `dislikes` int default 0,
    /* Time Metadata */
    `created_at` datetime default current_timestamp,
    foreign key (`course_id`) references `Course`(`id`),
    foreign key (`user_id`) references `User`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- `History` table records the history of user actions.
create table `History` if not exists (
    `id` bigint auto_increment primary key,
    `user_id` int not null,
    `action_type` enum(
        'CREATE_COURSE',      -- 21
        'UPDATE_COURSE',      -- 22
        'DELETE_COURSE',      -- 23
        'CREATE_DEPARTMENT',  -- 24
        'UPDATE_DEPARTMENT',  -- 25
        'DELETE_DEPARTMENT',  -- 26
        'CREATE_RESOURCE',    -- 27
        'UPDATE_RESOURCE',    -- 28
        'DELETE_RESOURCE',    -- 29
        'LIKE_RESOURCE',      -- 30
        'UNLIKE_RESOURCE',    -- 31
        'VIEW_RESOURCE',      -- 32
        'CREATE_INTERACTION', -- 33
        'UPDATE_INTERACTION', -- 34
        'DELETE_INTERACTION', -- 35
        'LIKE_INTERACTION',   -- 36
        'UNLIKE_INTERACTION', -- 37
        'RATE_COURSE',        -- 38
        'CREATE_USER',        -- 39
        'UPDATE_USER',        -- 40
        'DELETE_USER'         -- 41
    ) not null,
    `object_id` int default null,
    `timestamp` timestamp default current_timestamp,
    foreign key (`user_id`) references `User`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
