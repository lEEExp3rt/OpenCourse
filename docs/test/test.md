# OpenCourse 总体测试文档

本文档为 OpenCourse 总体测试文档，主要记录系统测试的相关信息

## 0 Notes

1. 建议所有测试撰写和运行使用单独的 Git 分支
2. 测试源代码目录为 `src/test/`

## 1 Brief

后端测试基于 JUnit5 框架

### Repositories

仓库层测试主要考查数据访问的正确性，包括 SQL 查询、JPA 映射、数据库交互等等，在实际场景中（开发、生成）时我们采用 MySQL 作为数据库，而在测试过程中我们采用 H2 内存型嵌入式数据库，通过使用内存型数据库，能够完全模拟实际场景中的数据库使用行为，同时又能加快数据访问速度和数据库响应速度，还能减少启动不同服务的开销

仓库层主要验证对所有实体的 CRUD 操作，涉及复杂查询和数据一致性等考量，主要依赖 SpringBoot 的数据层测试注解协同完成测试

### Services

服务层测试是基于仓库层测试的基础的，在仓库层的功能经过测试确保功能正确且符合逻辑后，在服务层进行单元测试与集成测试

- 单元测试只关注单一服务类，主要验证其业务逻辑是否符合预期、业务规则是否正确、异常处理与方法调用逻辑是否正确
- 集成测试用于测试层间协作

在单元测试中，采用 Mockito 框架进行依赖模拟和验证，通过模拟行为确保单一职责的测试运行，同时使用 AssertJ 进行断言判断

## 2 Table Of Content

- 仓库层
- 服务层
  - [CourseManager     测试文档](./services/CourseManagerTest.md)
  - [DepartmentManager 测试文档](./services/DepartmentManagerTest.md)
  - [HistoryManager    测试文档](./services/HistoryManagerTest.md)
  - [Resourcemanager   测试文档](./services/ResourceManagerTest.md)
    - [MinioFileStorageService 单元测试文档](./services/storage/MinioFileStorageServiceTest.md)
    - [MinioFileStorageService 集成测试文档](./services/storage/MinioFileStorageServiceIntegrationTest.md)
- 控制层
