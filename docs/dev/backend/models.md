# OpenCourse 后端开发文档 - 模型层文档

本文档为 OpenCourse 后端开发文档之模型层，主要记录模型层的实体定义和其它信息，用于开发时参考使用

## Introduction

OpenCourse 系统包含以下主要实体类：

| 名称 | 含义 |
|------|------|
| `ActionObject` | 抽象基类 |
| `User` | 用户实体 |
| `Course` | 课程实体 |
| `Department` | 院系实体 |
| `Resource` | 资源实体 |
| `Interaction` | 互动实体 |
| `History` | 历史记录实体 |

## ActionObject

**描述**: 用户历史记录中的操作对象基类，使用 `TABLE_PER_CLASS` 继承策略

**字段**: 无

**方法**:
- `abstract Object getId()` - 获取实体ID的抽象方法

## User

**描述**: 表示 OpenCourse 系统中的用户

**数据库表**: `User`

**字段**:

- `Integer id` - 用户ID（主键，自增）
- `String name` - 用户名（非空，最大长度31）
- `String email` - 邮箱（唯一，非空，最大长度63）
- `String password` - 密码（非空，已加密）
- `UserRole role` - 用户角色（枚举：USER, VISITOR, ADMIN）
- `Integer activity` - 活跃度（默认值1）
- `LocalDateTime createdAt` - 创建时间（不可更新）
- `LocalDateTime updatedAt` - 更新时间

**构造方法**:

- `User(String name, String email, String password, UserRole role)` - 创建用户

**主要方法**:

- `Integer getId()` - 获取用户ID
- `String getName()` / `setName(String name)` - 获取/设置用户名
- `String getEmail()` / `setEmail(String email)` - 获取/设置邮箱
- `String getPassword()` / `setPassword(String password)` - 获取/设置密码
- `UserRole getRole()` / `setRole(UserRole role)` - 获取/设置角色
- `Integer getActivity()` / `addActivity(Integer activity)` - 获取/增加活跃度
- `LocalDateTime getCreatedAt()` / `getUpdatedAt()` - 获取时间戳
- `void onCreate()` - JPA自动调用，设置创建时间
- `void onUpdate()` - JPA自动调用，设置更新时间

## Course

**描述**: 表示 OpenCourse 系统中的课程

**数据库表**: `Course`

**字段**:

- `Short id` - 课程ID（主键，自增）
- `String name` - 课程名称（非空，最大长度31）
- `String code` - 课程代码（唯一，非空，最大长度31）
- `Department department` - 所属院系（外键关联）
- `CourseType courseType` - 课程类型（枚举）
- `Float credits` - 学分（非空，精度3位小数1位）

**构造方法**:

- `Course(String name, String code, Department department, CourseType courseType, Float credits)` - 创建课程

**主要方法**:

- `Short getId()` - 获取课程ID
- `String getName()` / `setName(String name)` - 获取/设置课程名称
- `String getCode()` / `setCode(String code)` - 获取/设置课程代码
- `Department getDepartment()` / `setDepartment(Department department)` - 获取/设置院系
- `CourseType getCourseType()` / `setCourseType(CourseType courseType)` - 获取/设置课程类型
- `Float getCredits()` / `setCredits(Float credits)` - 获取/设置学分

## Department

**描述**: 表示 OpenCourse 系统中的院系

**数据库表**: `Department`

**字段**:

- `Byte id` - 院系ID（主键，自增）
- `String name` - 院系名称（唯一，非空，最大长度31）

**构造方法**:

- `Department(String name)` - 创建院系

**主要方法**:

- `Byte getId()` - 获取院系ID
- `String getName()` / `setName(String name)` - 获取/设置院系名称

## Resource

**描述**: 表示 OpenCourse 系统中的学习资源

**数据库表**: `Resource`

**字段**:

- `Integer id` - 资源ID（主键，自增）
- `String name` - 资源名称（非空，最大长度63）
- `String description` - 资源描述（可选，最大长度255）
- `ResourceType resourceType` - 资源类型（枚举）
- `ResourceFile resourceFile` - 资源文件信息（嵌入对象）
- `LocalDateTime createdAt` - 创建时间（不可更新）
- `Course course` - 所属课程（外键关联）
- `User user` - 上传用户（外键关联）
- `Integer views` - 浏览次数（默认0）
- `Integer likes` - 点赞数（默认0）
- `Integer dislikes` - 点踩数（默认0）

**嵌入类 ResourceFile**:

- `FileType fileType` - 文件类型（枚举：PDF, TEXT, OTHER）
- `BigDecimal fileSize` - 文件大小（精度3位小数2位）
- `String filePath` - 文件路径（非空，最大长度255）

注意：文件对象本身不直接保存在数据库中，而是采用 MinIO 存储，资源实体只保存资源文件的元数据

**构造方法**:

- `Resource(String name, ResourceType resourceType, ResourceFile resourceFile, Course course, User user)` - 创建资源（无描述）
- `Resource(String name, String description, ResourceType resourceType, ResourceFile resourceFile, Course course, User user)` - 创建资源（含描述）

**主要方法**:

- `Integer getId()` - 获取资源ID
- `String getName()` / `setName(String name)` - 获取/设置资源名称
- `String getDescription()` / `setDescription(String description)` - 获取/设置描述
- `ResourceType getResourceType()` / `setResourceType(ResourceType resourceType)` - 获取/设置资源类型
- `ResourceFile getResourceFile()` / `setResourceFile(ResourceFile resourceFile)` - 获取/设置文件信息
- `Course getCourse()` / `setCourse(Course course)` - 获取/设置所属课程
- `User getUser()` / `setUser(User user)` - 获取/设置上传用户
- `Integer getViews()` / `setViews(Integer views)` - 获取/设置浏览次数
- `Integer getLikes()` / `likes()` / `unlikes()` - 点赞相关操作
- `Integer getDislikes()` / `dislikes()` / `undislikes()` - 点踩相关操作
- `LocalDateTime getCreatedAt()` - 获取创建时间
- `void onCreate()` - JPA自动调用，设置创建时间

## Interaction

**描述**: 表示 OpenCourse 系统中的互动评论

**数据库表**: `Interaction`

**字段**:

- `Integer id` - 互动ID（主键，自增）
- `Course course` - 关联课程（外键关联）
- `User user` - 发表用户（外键关联）
- `String content` - 评论内容（TEXT类型）
- `Byte rating` - 用户评分（可选）
- `Integer likes` - 点赞数（默认0）
- `Integer dislikes` - 点踩数（默认0）
- `LocalDateTime createdAt` - 创建时间（不可更新）

**构造方法**:

- `Interaction(Course course, User user, Byte rating)` - 创建评分互动（无内容）
- `Interaction(Course course, User user, String content)` - 创建评论互动（无评分）
- `Interaction(Course course, User user, String content, Byte rating)` - 创建完整互动

**主要方法**:

- `Integer getId()` - 获取互动ID
- `Course getCourse()` / `setCourse(Course course)` - 获取/设置关联课程
- `User getUser()` / `setUser(User user)` - 获取/设置发表用户
- `String getContent()` / `setContent(String content)` - 获取/设置评论内容
- `Byte getRating()` / `setRating(Byte rating)` - 获取/设置评分
- `Integer getLikes()` / `likes()` / `unlikes()` - 点赞相关操作
- `Integer getDislikes()` / `dislikes()` / `undislikes()` - 点踩相关操作
- `LocalDateTime getCreatedAt()` - 获取创建时间
- `void onCreate()` - JPA自动调用，设置创建时间

## History

**描述**: 记录用户在 OpenCourse 系统中的操作历史

**数据库表**: `History`

**字段**:

- `Integer id` - 历史记录ID（主键，自增）
- `User user` - 执行操作的用户（外键关联）
- `ActionType actionType` - 操作类型（枚举）
- `ActionObject actionObject` - 操作对象（可选）
- `LocalDateTime timestamp` - 操作时间戳（不可更新）

**构造方法**:

- `History(User user, ActionType actionType)` - 创建历史记录（无操作对象）
- `History(User user, ActionType actionType, ActionObject actionObject)` - 创建历史记录（含操作对象）

**主要方法**:

- `Integer getId()` / `setId(Integer id)` - 获取/设置记录ID
- `User getUser()` / `setUser(User user)` - 获取/设置用户
- `ActionType getActionType()` / `setActionType(ActionType actionType)` - 获取/设置操作类型
- `ActionObject getActionObject()` / `setActionObject(ActionObject actionObject)` - 获取/设置操作对象
- `LocalDateTime getTimestamp()` / `setTimestamp(LocalDateTime timestamp)` - 获取/设置时间戳
- `void onCreate()` - JPA自动调用，设置时间戳

## Entity Relationship

1. **继承关系**:

   - `User`, `Course`, `Resource`, `Interaction` 都继承自 `ActionObject`，用于描述用户操作历史中的操作对象

2. **关联关系**:

   - `Course` -> `Department` (多对一)
   - `Resource` -> `Course` (多对一)
   - `Resource` -> `User` (多对一)
   - `Interaction` -> `Course` (多对一)
   - `Interaction` -> `User` (多对一)
   - `History` -> `User` (多对一)
   - `History` -> `ActionObject` (可选关联)

## Notice

1. 所有实体类都使用了 JPA 注解进行 ORM 映射
2. 时间戳字段使用 `@PrePersist` 注解自动设置
3. 部分字段使用了 Bean Validation 注解进行数据验证
4. `Resource.ResourceFile` 是嵌入式对象，不单独建表
5. 抽象基类仅用于多态查询，不单独建表
6. 所有实体都提供了完整的 getter/setter 方法和 `toString()` 方法
