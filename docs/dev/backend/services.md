# OpenCourse 后端开发文档 - 服务层

本文档为 OpenCourse 后端开发文档之服务层，主要记录服务层提供的服务和调用接口，用于开发时参考

## Introduction

服务层是业务逻辑的核心层次，所有的业务逻辑都在服务层落地实现，由服务层调用[仓库层](./repositories.md)的方法从数据库中获取[实体模型](./models.md)，然后根据业务逻辑，接收从[控制层](./controllers.md)传来的请求，然后做出响应并传送数据回给控制层

在本项目中，服务层的服务之间的互相调用采用**仓库层调用**的方式，尽量避免服务之间互相调用的情况

总体上，每一个实体类对应一个主服务类，其命名规范为**实体名称+Manager**，如用户服务类名称为 `UserManager`；而一些服务需要调用一些子服务，如资源服务需要调用文件存储子服务，子服务类命名规范为**子服务名称+Service**，并在 `services` 包下有独立的目录进行收纳

一些服务的调用参数多样且数量大，所以和控制层之间采用 **Data Transfer Object(DTO)** 进行数据传递，具体来说是：

- 控制层接收客户端响应，将请求参数封装成 DTO 对象，然后发送给服务层
- 服务层接收到 DTO 对象，对其解包，将参数提取出，然后用于服务调用
- 服务调用完成后，服务端返回数据

对于服务调用参数数量简单的情况，则直接采用原始参数传递即可，无需进一步封装成 DTO

## Structures

目前，服务层的结构如下：

```shell
src/main/java/org/opencourse/services/
├── email
│   ├── EmailService.java            # 邮件服务子类
│   └── VerificationService.java     # 验证码服务子类
├── storage
│   ├── FileStorageService.java      # 文件存储服务接口
│   └── MinioFileStorageService.java # MinIO 文件存储服务子类
├── user
│   └── UserInfoService.java         # 用户信息服务子类
│
├── CourseManager.java               # 课程服务类
├── DepartmentManager.java           # 院系服务类
├── HistoryManager.java              # 历史记录服务类
├── InteractionManager.java          # 互动服务类
├── ResourceManager.java             # 资源服务类
└── UserManager.java                 # 用户服务类
```

DTO 结构如下：

```shell
src/main/java/org/opencourse/dto/
├── request                      # 请求 DTO，用于接收服务调用请求
│   ├── CourseCreationDto.java
│   ├── PasswordResetDto.java
│   ├── ResourceUploadDto.java
│   ├── UserLoginDto.java
│   └── UserRegistrationDto.java
└── response                     # 响应 DTO，用于发送响应
    └── ApiResponse.java
```

## CourseManager

课程服务类

**基本服务**：

- 添加一门新课程，返回创建的课程实体
- 由课程名或课程代码精确查找一门课程，返回一个课程实体
- 获取所有课程
- 由关键词（名称或课程代码）查找匹配的课程列表，用于搜索时使用，支持模糊搜索和忽略大小写
- 某院系的所有课程查找
- 某类别的所有课程查找

## DepartmentManager

院系服务类

**基本服务**：

- 添加一个新院系
- 根据名称精确查找一个院系
- 根据名称模糊匹配院系列表
- 查找所有院系

## HistoryManager

用户操作历史记录服务类

**基本服务**

- 添加一条历史记录
- 获取某用户的所有历史记录

## InteractionManger

互动服务类

**基本服务**：

- 添加一条评论
- 获取某课程的所有评论
- 某用户点赞了一条评论
- 某用户点踩了一条评论
- 某用户取消点赞了一条评论
- 某用户取消点踩了一条评论

## ResourceManager

资源服务类

**基本服务**：

- 添加一个资源
- 删除一个资源
- 精确查找某一资源
- 查找某一课程的所有资源列表
- 查找某一用户创作的所有资源
- 某用户点赞了一个资源
- 某用户点踩了一个资源
- 某用户取消点赞了一个资源
- 某用户取消点菜了一个资源
- 访问资源用于在线浏览和下载

## UserManager

用户服务类

**基本服务**：

- 发送注册验证码
- 注册用户
- 用户登录
- 发送重置密码验证码
- 重置密码
- 根据邮箱查找用户
- 根据用户名查找用户
- 根据ID查找用户
- 更新用户权限
