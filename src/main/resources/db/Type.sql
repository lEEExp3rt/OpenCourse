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

-- Insert the default values for the `Type` table --

insert into `Type` (`id`, `name`, `description`) values
    (1, 'Lecture', 'Lecture notes'),
    (2, 'Lab', 'Lab notes'),
    (3, 'Project', 'Project notes'),
    (4, 'Assignment', 'Assignment notes'),
    (5, 'Exam', 'Exam notes');
