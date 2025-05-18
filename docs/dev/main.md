# OpenCourse 总体开发文档

本文档为 OpenCourse 团队总体开发文档，用于辅助整体开发流程

如果有任何开发相关问题，请直接联系[组长](https://github.com/lEEExp3rt)或[提交 Issue](https://github.com/lEEExp3rt/OpenCourse/issues/new/choose)

## 0 Notes

1. 本项目采用 Git 版本控制，**开发必须拉取一个独立的分支进行开发，禁止直接在主分支开发！！！**
2. 确保模块实现了完整且经过测试的功能后，向主分支发起合并
3. 提交信息撰写推荐参考 [Conventional Commits](https://www.conventionalcommits.org/) 规范

## 1 Code Structures

项目的总体框架如下：

```Shell
.
├── build.sh  # 开发容器构建脚本
├── docs/     # 项目文档，dev/下文档为开发文档
├── pom.xml   # 后端依赖及配置
├── README.md # 项目自述文件
├── src/      # 源代码
└── target/   # 构建产物
```

各部分的项目结构见各部分说明文档

## 2 Module Development

1. [环境配置](./env.md)
2. [数据库表设计](./database.md)
