/**
 * @brief `User` table initializer script.
 * @description This table records the users information.
 * @Author !EEExp3rt
 */

create table `User` if not exists (
    `id` int auto_increment primary key,
    `name` varchar(31),
    `email` varchar(63) not null unique,
    `password` varchar(255) not null,
    `role` enum('user', 'visitor', 'admin') not null,
    `activity` int default 1,
    `created_at` timestamp default current_timestamp,
    `updated_at` timestamp default null on update current_timestamp
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
