/**
 * @brief `Campus` table initializer script.
 * @Author !EEExp3rt
 */

create table `Campus_Category` if not exists (
    `id` tinyint primary key,
    `name` varchar(31) not null unique
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table `Campus` if not exists (
    `id` tinyint auto_increment primary key,
    `name` varchar(63) not null unique,
    `category_id` tinyint not null,
    foreign key (`category_id`) references `Campus_Category`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
