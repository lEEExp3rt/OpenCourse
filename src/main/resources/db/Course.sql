/**
 * @brief `Course` table initializer script.
 * @description This table records the courses information.
 * @Author !EEExp3rt
 */

create table `Course` if not exists (
    `id` smallint primary key auto_increment,
    `code` varchar(31) unique not null,
    `name` varchar(31) not null,
    `department_id` tinyint not null,
    `type_id` tinyint not null,
    `credits` decimal(3, 1) not null,
    foreign key (`department_id`) references `Department`(`id`),
    foreign key (`type_id`) references `Type`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
