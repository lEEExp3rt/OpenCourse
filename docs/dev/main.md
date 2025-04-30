# OpenCourse 开发文档

本文档为 OpenCourse 团队总体开发文档

## 0 Basic

一些注意事项：

1. 本项目采用 Git 版本控制，开发必须拉取一个独立的分支进行开发，禁止直接在主分支开发！！！
2. 开发者需要编写测试并通过所有测试
3. 确保模块实现了完整且经过测试的功能后，向主分支发起合并
4. 注意定义不同开发服务之间的清晰接口定义以提供其它开发者使用

## 1 Backend

### 1.1 Dependencies

环境配置如下：

- Java 版本：JDK 21
- 框架：SpringBoot 3.1.0
- 构建工具：mvn 3.6.3
- 数据库：MySQL 8.0+
- 中间件：Redis 7.2-alpine
- ...

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
2. 运行容器构建脚本 `bash build.sh`，等待容器构建完成（注：构建时间可能较久）
3. 构建完成后，容器会自动后台启动

#### 1.2.1 Develop In VsCode

如果你使用 VsCode 进行开发，推荐使用插件 Dev Container（扩展市场搜索 `ms-vscode-remote.remote-containers`）

1. 确保容器已运行
2. 进入 VsCode，点击左下角，选择**附加到正在运行的容器**，选择 `/opencourse`，然后进入开发容器

#### 1.2.2 Develop In IDEA

如果你使用 IntelliJ IDEA 进行开发，推荐使用功能 Dev Container 进行开发

> 经过实践，这并不好用

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
├── main/
│   ├── java/               # 后端代码目录
│   │   ├── controller/     # 控制层：负责处理客户端请求并返回响应
│   │   ├── services/       # 服务层：核心业务逻辑
│   │   ├── repository/     # 数据层：处理服务端逻辑对象和持久化数据
│   │   ├── models/         # 模型层：业务实体定义
│   │   ├── configs/        # 配置相关文件
│   │   └── Main.java       # 主程序
│   │ 
│   └── resources/          # 资源目录
│       └── application.yml # 程序的配置信息，如数据库连接路径
│
└── test/                   # 测试目录
    ├── java/               # 测试代码
    └── resources/          # 测试资源
```

### 1.5 Tests

使用 SpringBoot 提供的测试套件进行测试，每个文件都应该进行测试，测试代码应存放在 `src/test/java/` 下，测试文件命名应该为 `被测试类名 + Test.java`

> 如需要测试 `MyClass`，其文件名为 `src/main/java/folder/MyClass.java`，那么测试文件名为 `src/test/java/folder/MyClassTest.java`
