# OpenCourse 数据库表设计文档

本文档为 OpenCourse 团队数据库表设计文档，主要记录数据库表的定义和说明信息，用于开发时参考使用

> 注：请按表名称字典序整理

## Campus

- 学院所属大类表，存储学院所属大类信息
- 学院信息表，存储所有学院的信息

```sql
create table `Campus_Category` if not exists (
    `id` tinyint primary key,          /* 学院大类 ID*/
    `name` varchar(31) not null unique /* 学院大类名称 */
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table `Campus` if not exists (
    `id` tinyint auto_increment primary key, /* 学院标识 ID */
    `name` varchar(63) not null unique,      /* 学院名称 */
    `category_id` tinyint not null           /* 学院所属大类 */
    foreign key (`category_id`) references `Campus_Category`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
```

## Course

- 课程所属类别表，存储课程所属类别信息
- 课程信息表，存储课程的信息

```sql
create table `Course_Category` if not exists (
    `id` tinyint primary key,          /* 课程类型 ID*/
    `name` varchar(63) not null unique /* 课程类型名称 */
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table `Course` if not exists (
    `id` int primary key,               /* 课程 ID */
    `code` varchar(15) unique not null, /* 课程代码 */
    `name` varchar(127) not null,       /* 课程名称 */
    `campus_id` tinyint not null,       /* 课程所属学院 */
    `credits` decimal(3, 1) not null,   /* 课程学分 */
    `category_id` tinyint not null,     /* 课程类别 */
    foreign key (`campus_id`) references `Campus`(`id`),
    foreign key (`category_id`) references `Course_Category`(`id`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
```

## Resource

- 资源类型表，存储资源所属类型
- 资源信息表，存储所有资源的信息

```sql
create table `Resource_Type` if not exists (
    `id` tinyint auto_increment primary key, /* 资源类型 ID */
    `name` varchar(31) not null,             /* 资源类型名称 */
    `description` varchar(255) default null, /* 资源类型描述 */
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
```
