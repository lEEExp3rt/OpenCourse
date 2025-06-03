# OpenCourse 环境配置文档

本文档为 OpenCourse 团队环境配置文档，主要记录**开发环境配置相关信息和说明**，用于开发时参考使用

如果有任何开发相关问题，请直接联系[组长](https://github.com/lEEExp3rt)或[提交 Issue](https://github.com/lEEExp3rt/OpenCourse/issues/new/choose)

## 1 Backend

### 1.1 Dependencies

环境配置如下：

- Java 版本：JDK 21
- 框架：SpringBoot 3.1.0
- 构建工具：mvn 3.6.3
- 数据库：MySQL 8.0+
- 中间件：Redis 7.2-alpine
- 文件对象存储系统： MinIO RELEASE.2025-05-24T17-08-30Z

### 1.2 Environments

本项目提供**开发容器**进行开发，容器构建脚本为 `build.sh`，如果不使用开发容器请按照 [1.1](#11-dependencies) 节中环境信息自行配置

开发容器中已经预装以下配置和工具，并提供了用户免密 sudo 权限，如果你需要其它额外工具可以直接使用 `apt-get` 进行安装

```txt
Fish Shell
Vim
maven
mysql-client
redis-cli
git
```

开发容器构建流程：

1. 确保已安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)，并保持运行
2. 初次运行容器构建脚本 `bash build.sh` ，创建[环境变量](#121-environment-variables)目录 `.envs/`，用于配置容器环境，请根据需要创建所有环境变量
3. 再次运行容器构建脚本 `bash build.sh`，等待容器构建完成（注：构建时间可能较久）
4. 构建完成后，容器会自动后台启动
5. 连接到容器，进行开发

#### 1.2.1 Environment Variables

一些容器配置信息（如用户名、密码等）作为敏感数据不便于进行公开，因此不纳入版本控制，需要通过本地环境变量配置

在本项目中，每一个 Docker 容器的创建都需要一份环境变量文件，其目录为 `.envs/`，具体可以参考 `.devcontainer/docker-compose.yml` 中的信息

本项目必需的环境变量：

```yaml
# .envs/app.env
# 没有必要的环境变量

# .envs/mysql.env
MYSQL_ROOT_PASSWORD="YOUR ROOT PASSWORD" # 管理员密码
MYSQL_DATABASE="YOUR DATABASE"           # 数据库名称
MYSQL_USER="YOUR USER NAME"              # 用户名
MYSQL_PASSWORD="YOUR PASSWORD"           # 密码

# .envs/redis.env
# 暂时没有用到

# .envs/minio.env
MINIO_ROOT_USER="YOUR ROOT USER"         # 用户名
MINIO_ROOT_PASSWORD="YOUR ROOT PASSWORD" # 密码
MINIO_BROWSER=on
MINIO_DOMAIN=localhost
```

#### 1.2.2 Devlopment

##### 1.2.2.1 Develop In VsCode

如果你使用 VsCode 进行开发，推荐使用插件 Dev Container（扩展市场搜索 `ms-vscode-remote.remote-containers`）

1. 确保容器已运行
2. 进入 VsCode，点击左下角，选择**附加到正在运行的容器**，选择 `/opencourse`，然后进入开发容器

##### 1.2.2.2 Develop In IDEA

如果你使用 IntelliJ IDEA 进行开发，推荐使用功能 Dev Container 进行开发

##### 1.2.2.3 Develop Without Dev-Container

当然，你也可以不使用上述两种方法而直接使用你最方便的方式在 Docker 容器中开发（如在终端运行 `docker exec -it opencourse bash` 即可进入容器），因为这里的开发容器挂载了你的工作目录，所有的更改都会同步反映到容器中

或者你也可以不使用容器开发，直接自行在本地配置环境

### 1.3 Build And Run

初次进入开发容器后，请在项目工作目录下 `~/OpenCourse` 运行 `mvn dependency:go-offline` 完成所有依赖下载，初次安装用时较久

项目所需依赖已在 `pom.xml` 中给出，如果有未添加的项目依赖直接在 `<dependencies><dependencies/>` 中添加

```shell
mvn clean              # 清除构建产物
mvn dependency:resolve # 解析并安装依赖
mvn compile            # 编译项目
mvn spring-boot:run    # 运行 SpringBoot 项目
mvn package            # 打包项目
...
```

### 1.4 Code Structures

后端部分的所有代码均在 `src/` 下，其结构如下：

```shell
src/
├── main/                         # 后端代码目录
│   ├── java/
│   │   └── org/
│   │       └── opencourse/
│   │           ├── dto/          # 数据传输对象层：负责接收客户端请求并整合成参数
│   │           ├── controllers/  # 控制层：负责处理客户端请求并返回响应
│   │           ├── services/     # 服务层：核心业务逻辑
│   │           ├── repositories/ # 数据层：处理服务端逻辑对象和持久化数据
│   │           ├── models/       # 模型层：业务实体定义
│   │           ├── configs/      # 配置相关
│   │           ├── utils/        # 其它工具
│   │           └── Main.java     # 主程序
│   │ 
│   └── resources/            # 资源目录
│       ├── application.yml   # 程序的配置信息，如数据库连接路径
│       └── schema.sql        # 数据库表定义和初始化脚本
│
└── test/                     # 测试目录
    ├── java/                 # 测试代码
    └── resources/            # 测试资源
```

运行 `build.sh` 初始化脚本会自动帮你创建好所有目录关系，**如果你没有运行初始化脚本，请自行创建上述目录关系，项目代码将严格遵守这个框架**
