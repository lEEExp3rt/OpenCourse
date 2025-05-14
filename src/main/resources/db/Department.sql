/**
 * @brief `Department` table initializer script.
 * @description This table records the departments information.
 * @Author !EEExp3rt
 */

create table `Department` if not exists (
    `id` tinyint auto_increment primary key,
    `name` varchar(31) not null unique,
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
