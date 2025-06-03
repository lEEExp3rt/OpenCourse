# OpenCourse 后端开发文档 - 仓库层

本文档为 OpenCourse 后端开发文档之仓库层，主要记录仓库层的方法接口和其它信息，用于开发时参考

## Introduction

每个[模型层实体](./models.md)都有对应的仓库层，用于将数据从 Java 运行时的逻辑类对象转化为持久化在数据库中的数据，同时将持久化数据从数据库中读出并转化为业务的实体对象，总结来说就是**通过仓库层访问数据库**

所有的仓库命名为**实体名+Repo**的形式，如课程仓库命名为 `CourseRepo`

仓库层的数据访问采用 Spring JPA，每个仓库通过继承 `JpaRepository` 的方式实现基本的 CRUD 操作，开发者只需要根据所需要的方法的函数命名规范进行声明即可由 JPA 仓库层自动生成对应的 SQL 语句进行增删改查，而无需自行实现

所有基于主键的方法都已经继承自 `JpaRepository`，额外的方法需要再接口类中声明

## CourseRepo

课程实体管理仓库

**基本方法**：

```java
/**
 * 通过课程名称查找课程
 * 
 * @param name 课程名称
 * @return 对应课程
 */
Optional<Course> findByName(String name);

/**
 * 通过课程名称模糊查找课程
 * 
 * @param name 课程名称
 * @return 匹配的课程列表
 */
List<Course> findByNameContainingIgnoreCase(String name);

/**
 * 通过课程代码查找课程
 * 
 * @param code 课程代码
 * @return 找到的课程
 */
Optional<Course> findByCode(String code);

/**
 * 通过课程代码模糊查找课程
 * 
 * @param code 课程代码
 * @return 匹配的课程列表
 */
List<Course> findByCodeContainingIgnoreCase(String code);

/**
 * 检查指定代码的课程是否存在
 * 
 * @param code 课程代码
 * @return 存在返回true，否则返回false
 */
boolean existsByCode(String code);

/**
 * 查找属于特定院系的所有课程
 * 
 * @param department 院系
 * @return 该院系的课程列表
 */
List<Course> findByDepartment(Department department);

/**
 * 查找特定类型的所有课程
 * 
 * @param courseType 课程类型
 * @return 指定类型的课程列表
 */
List<Course> findByCourseType(CourseType courseType);

/**
 * 查找所有课程并按名称排序
 * 
 * @return 按名称升序排列的所有课程列表
 */
List<Course> findAllByOrderByNameAsc();
```

## DepartmentRepo

院系实体管理仓库

**基本方法**：

```java
/**
 * 通过院系名称查找院系
 * 
 * @param name 院系名称
 * @return 找到的院系
 */
Optional<Department> findByName(String name);

/**
 * 通过院系名称模糊查找院系
 * 
 * @param name 院系名称
 * @return 匹配的院系列表
 */
List<Department> findByNameContainingIgnoreCase(String name);

/**
 * 检查指定名称的院系是否存在
 * 
 * @param name 院系名称
 * @return 存在返回true，否则返回false
 */
boolean existsByName(String name);

/**
 * 查找所有院系并按名称排序
 * 
 * @return 按名称升序排列的所有院系列表
 */
List<Department> findAllByOrderByNameAsc();
```

## HistoryRepo

历史记录实体管理仓库

**基本方法**：

暂无额外自定义方法

## InteractionRepo

互动实体管理仓库

**基本方法**：

暂无额外自定义方法

## ResourceRepo

资源实体管理仓库

**基本方法**：

```java
/**
 * 通过课程ID查找资源
 * 
 * @param courseId 课程ID
 * @return 与该课程关联的资源列表
 */
List<Resource> findByCourseId(Short courseId);

/**
 * 通过用户ID查找资源
 * 
 * @param userId 用户ID
 * @return 与该用户关联的资源列表
 */
List<Resource> findByUserId(Integer userId);
```

## UserRepo

用户实体管理仓库

**基本方法**：

```java
/**
 * 通过邮箱地址查找用户
 * 
 * @param email 邮箱地址
 * @return 找到的用户
 */
Optional<User> findByEmail(String email);

/**
 * 检查指定邮箱的用户是否存在
 * 
 * @param email 邮箱地址
 * @return 存在返回true，否则返回false
 */
boolean existsByEmail(String email);
```
