/**
 * @brief `Course` table initializer script.
 * @Author !EEExp3rt
 */

create table `Course_Category` if not exists (
    `id` tinyint primary key,
    `name` varchar(63) not null unique
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table `Course` if not exists (
    `id` int primary key,
    `code` varchar(15) unique not null,
    `name` varchar(127) not null,
    `campus_id` tinyint not null,
    `credits` decimal(3, 1) not null,
    `category_id` tinyint not null,
    foreign key (`campus_id`) references `Campus`(`id`),
    foreign key (`category_id`) references `Course_Category`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
