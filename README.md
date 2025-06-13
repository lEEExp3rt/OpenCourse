# Open Course

**Open Course** 是一个开源的在线学习资源分享平台，旨在帮助大学生提高学习效率，突破信息屏障，通过无障碍的资源分享与人际互动，你可以解锁课程学习的全新方式

在这里，你可以

- 上传与下载各种课程资源
- 分享课程经验和建议
- 与校友激情互动
- ...

> 项目灵感来源：[浙江大学课程攻略共享计划](https://qsctech.github.io/zju-icicles/)

## Structures

本项目为前后端分离项目，项目结构如下：

```shell
.
├── .devcontainer/ # Docker 开发容器配置目录
├── .envs/         # 环境变量目录
├── .gitignore     # Git 忽略文件
├── docs/          # 项目文档目录
├── backend/       # 后端源代码目录
├── frontend/      # 前端源代码目录
├── scripts/       # 脚本目录
├── LICENSE        # 项目许可证
├── CONTRIBUTERS   # 开发者名单
└── README.md      # 自述文件
```

## Build And Run

运行 `bash scripts/run.sh` 以快捷启动整个应用，运行 `bash scripts/clean.sh` 以清楚所有构建产物

## Features

### v1.0.0 - 20250613

- 用户系统
  - 用户注册与登录
  - 通过邮箱验证码注册
  - 用户个人主页
- 课程系统
  - 创建院系部门
  - 创建课程
- 资源系统
  - 创建资源
  - 删除资源
  - 点赞与撤赞
  - 下载资源文件
- 互动系统
  - 发表互动评论
  - 更新互动评论
  - 删除互动评论
  - 为课程评分
  - 点赞与撤赞用户评论

## Documents

请参考 `docs/` 下的所有文档以获取更详细全面的文档

- [软件需求规划说明书](./docs/pdf/SRS.pdf)
- [总体设计报告](./docs/pdf/HLD.pdf)
- [设计模式报告](./docs/pdf/DPR.pdf)
- [开发文档](./docs/dev/main.md)
- [测试文档](./docs/test/test.md)

## License

本项目采用 [MIT 许可证](./LICENSE)
