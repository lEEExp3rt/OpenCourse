/**
 * @brief `History` table initializer script.
 * @description This table keeps track of the change history for the whole application in user view.
 * @Author !EEExp3rt
 * @note Types of changes:
 *     1. Create / Update user account
 *     2. Create / Update course and department information
 *     3. Create / Update resource post
 *     4. Create / Delete interaction comment
 *     5. Like / Unlike / Dislike / Undislike interaction
 *     6. Rate course
 */

create table `History` if not exists (
    `id` int auto_increment primary key,
    `user_id` int not null,
    `action_id` tinyint not null,
    `object_id` int default null,
    `timestamp` timestamp default current_timestamp,
    foreign key (`user_id`) references `User`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
