# OpenCourse 开发文档

本文档为 OpenCourse 团队总体开发文档

## 0 Basic

一些注意事项：

1. 本项目采用 Git 版本控制，**开发必须拉取一个独立的分支进行开发，禁止直接在主分支开发！！！**
2. 开发者需要编写测试并通过所有测试
3. 确保模块实现了完整且经过测试的功能后，向主分支发起合并
4. 注意定义不同开发服务之间的清晰接口定义以提供其它开发者使用

如果有任何开发相关问题，请直接联系[组长](https://github.com/lEEExp3rt)或[提交 Issue](https://github.com/lEEExp3rt/OpenCourse/issues/new/choose)

## 1 Backend

### 1.1 Dependencies

环境配置如下：

- Java 版本：JDK 21
- 框架：SpringBoot 3.1.0
- 构建工具：mvn 3.6.3
- 数据库：MySQL 8.0+
- 中间件：Redis 7.2-alpine
- ...（后续需要会继续补充）

### 1.2 Environments

本项目采用**开发容器**进行开发，容器构建脚本为 `build.sh`，如果不使用开发容器请按照 [1.1](#11-dependencies) 节中环境信息自行配置

开发容器中已经预装的配置和工具有

```txt
Fish Shell
Vim
maven
mysql-client
redis-cli
git
```

> 如果你需要其它工具，使用 apt-get 进行安装即可

开发容器构建流程：

1. 确保已安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)，并保持运行
2. 初次运行容器构建脚本 `bash build.sh` 会创建环境变量文件，用于配置容器环境，在 `.envs` 中定义了所有环境变量，你可以按照自己的喜好创建对应的环境变量
3. 创建完环境变量后，再次运行容器构建脚本 `bash build.sh`，等待容器构建完成（注：构建时间可能较久）
4. 构建完成后，容器会自动后台启动
5. 连接到容器，进行开发

关于 `.envs/` 下的环境变量：每个环境变量文件对应每个容器内的环境变量（如 `mysql.env` 对应数据库容器），通常是数据库等工具的连接用户名和密码、JDBC 配置等，这些环境变量不纳入版本控制系统，所以需本地自行创建，你可以设置自己的喜好用户名和密码等，以及其它必要的信息，可以参考的环境变量信息配置如下：

```yaml
# Environment variables for app container.
APP_DATABASE_URL="jdbc:mysql://mysql:3306/opencourse"
APP_DATABASE_USERNAME="opencourse_admin"
APP_DATABASE_PASSWORD="opencourse_pwd"
APP_REDIS_HOST="redis"
APP_REDIS_PORT="6379"
APP_REDIS_PASSWORD="redispwd"
```

#### 1.2.1 Develop In VsCode

如果你使用 VsCode 进行开发，推荐使用插件 Dev Container（扩展市场搜索 `ms-vscode-remote.remote-containers`）

1. 确保容器已运行
2. 进入 VsCode，点击左下角，选择**附加到正在运行的容器**，选择 `/opencourse`，然后进入开发容器

#### 1.2.2 Develop In IDEA

如果你使用 IntelliJ IDEA 进行开发，推荐使用功能 Dev Container 进行开发

#### 1.2.3 Develop Without Dev-Container

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

项目的总体框架如下：

```Shell
.
├── build.sh        # 开发容器构建脚本
├── docs/           # 项目文档，dev/下文档为开发文档
├── pom.xml         # 后端依赖及配置
├── README.md       # 项目自述文件
├── src/            # 源代码
└── target/         # 构建产物
```

后端部分的所有代码均在 `src/` 下，其结构如下：

```shell
src/
├── main/                         # 后端代码目录
│   ├── java/
│   │   └── org/
│   │       └── opencourse/
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
│       └── db                # 数据库表定义和初始化脚本
│           ├── Course.sql
│           ├── Department.sql
│           ├── History.sql
│           ├── Interaction.sql
│           ├── Resource.sql
│           ├── Type.sql
│           └── User.sql
│
└── test/                     # 测试目录
    ├── java/                 # 测试代码
    └── resources/            # 测试资源
```

运行 `build.sh` 初始化脚本会自动帮你创建好所有目录关系，**如果你没有运行初始化脚本，请自行创建上述目录关系，项目代码将严格遵守这个框架**

### 1.5 Tests

使用 SpringBoot 提供的测试套件进行测试，每个文件都应该进行测试，测试代码应存放在 `src/test/java/` 下，测试文件命名应该为 `被测试类名 + Test.java`

> 例如，需要测试 `src/main/java/folder/MyClass.java` 下的类 `MyClass`，其测试文件名应为 `src/test/java/folder/MyClassTest.java`

### 1.6 Utilitis

#### 1.6.1 Connect To Database Through Container

如果你使用多容器开发，主容器内并没有数据库服务器，而只有一个客户端，因此请你采用以下方式连接到数据库容器：

```shell
# 容器内使用数据库的端口号为 3306.
$ mysql -h mysql -P 3306 -u opencourse_user -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is xxxx
Server version: 8.0.42 MySQL Community Server - GPL

Copyright (c) 2000, 2025, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its affiliates. Other names may be trademarks of their respective owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> 
```

同理，你的 JDBC 路径也需要作对应修改

### 1.7 Database And Tables

数据库表定义和说明，详见 [database.md](./database.md)
