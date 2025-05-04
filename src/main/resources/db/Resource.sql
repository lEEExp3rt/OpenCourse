/**
 * @brief `Resource` table initializer script.
 * @Author !EEExp3rt
 */

create table `Resource_Type` if not exists (
    `id` tinyint auto_increment primary key,
    `name` varchar(31) not null,
    `description` varchar(255) default null,
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table `Resource` if not exists (
    `id` int auto_increment primary key,
    `name` varchar(255) not null,
    `description` varchar(255) default null,
    `file_type` enum('pdf', 'text', 'other') not null,
    `resourse_type_id` tinyint not null,
    `course_id` int not null,
    `user_id` int not null, /* TODO */
    `created_at` timestamp default current_timestamp,
    `updated_at` timestamp default null on update current_timestamp,
    ``,
    foreign key (`course_id`) references `Cource`(`id`),
    foreign key (`resourse_type_id`) references `Resource_Type`(`id`),
    foreign key (`user_id`) references `User`(`id`) /* TODO */
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
