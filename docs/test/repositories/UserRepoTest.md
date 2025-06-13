# OpenCourse 测试文档 - UserRepoTest

本文档为 OpenCourse 团队测试文档之 `UserRepoTest`

## Details

测试的主要功能：

1. 上下文加载 (`contextLoads`)
   1. 验证 UserRepo 和 TestEntityManager 正确注入

2. 按用户名查找用户 (`findByName`)
   1. 用户存在时返回对应用户（ADMIN、USER、VISITOR）
   2. 用户不存在时返回空 Optional
   3. 大小写敏感验证（严格匹配）
   4. 特殊字符用户名支持（如 user@123）

3. 按邮箱查找用户 (`findByEmail`)
   1. 用户存在时返回对应用户（三种角色验证）
   2. 用户不存在时返回空 Optional
   3. 大小写敏感性测试（依赖数据库配置）
   4. 复杂邮箱格式支持（test.user+tag@sub.domain.com）

4. 检查邮箱是否存在 (`existsByEmail`)
   1. 用户存在时返回 true
   2. 用户不存在时返回 false（包括空字符串）
   3. 与 findByEmail 结果保持一致性

5. 用户实体基础功能验证 (`userEntityBasicFunctionality`)
   1. 验证用户的所有基本字段正确保存和读取
   2. 验证默认活跃度为1
   3. 验证自动生成字段（ID、创建时间）

6. 用户角色枚举功能验证 (`userRoleEnumFunctionality`)
   1. 验证 ADMIN、USER、VISITOR 三种角色
   2. 验证角色与用户的正确映射关系

7. 创建时间自动生成验证 (`createdAtAutoGeneration`)
   1. 保存时自动设置创建时间
   2. 时间戳在合理范围内（保存前后1秒）
   3. 初始更新时间为 null

8. 更新时间自动生成验证 (`updatedAtAutoGeneration`)
   1. 更新用户时自动设置更新时间
   2. 创建时间保持不变
   3. 更新时间在合理范围内

9. 用户活跃度功能验证 (`userActivityFunctionality`)
   1. 活跃度累加功能（addActivity）
   2. 多次累加的正确性
   3. 活跃度直接设置功能

10. 用户toString方法验证 (`userToString`)
    1. 包含所有关键字段（除密码外）
    2. 密码安全性验证（不在字符串中显示）
    3. 格式正确性验证

11. 邮箱唯一性约束验证 (`emailUniquenessConstraint`)
    1. 重复邮箱处理机制验证
    2. 数据库约束正确性检查

12. 复杂查询场景验证 (`complexQueryScenarios`)
    1. 按角色统计用户数量
    2. 按用户名查找后验证邮箱一致性
    3. 跨方法查询结果一致性

13. 用户创建场景验证 (`userCreationScenarios`)
    1. 最小有效用户创建
    2. 长用户名和邮箱处理
    3. 默认值正确设置

14. 边界条件测试 (`edgeCases`)
    1. 空字符串搜索处理
    2. null值搜索处理
    3. 空格用户名处理

15. 数据一致性验证 (`dataConsistency`)
    1. 所有用户数据完整性检查
    2. 邮箱唯一性验证
    3. 必需字段非空验证
    4. 邮箱格式验证

16. 批量操作测试 (`bulkOperations`)
    1. 批量保存用户功能
    2. 批量操作后的数据验证
    3. 总数统计正确性

## 测试覆盖

- **总测试方法数**: 22 个
- **基础查询操作**: 8 个
- **实体功能测试**: 6 个
- **时间戳测试**: 2 个
- **数据完整性测试**: 3 个
- **边界条件测试**: 2 个
- **批量操作测试**: 1 个

## 测试方法分类

### 查询功能测试 (8个)

- `testFindByName_*` (4个): 测试按用户名查找
- `testFindByEmail_*` (4个): 测试按邮箱查找
- `testExistsByEmail_*` (3个): 测试邮箱存在性检查

### 实体功能测试 (6个)

- `testUserEntityBasicFunctionality_*` (1个): 测试用户实体基础功能
- `testUserRoleEnumFunctionality_*` (1个): 测试用户角色枚举
- `testUserActivityFunctionality_*` (2个): 测试活跃度功能
- `testUserToString_*` (1个): 测试字符串表示
- `testUserCreationScenarios_*` (1个): 测试创建场景

### 时间戳功能测试 (2个)

- `testCreatedAtAutoGeneration_*` (1个): 测试创建时间生成
- `testUpdatedAtAutoGeneration_*` (1个): 测试更新时间生成

### 数据完整性测试 (3个)

- `testEmailUniquenessConstraint_*` (1个): 测试邮箱唯一性
- `testDataConsistency_*` (1个): 测试数据一致性
- `testComplexQueryScenarios_*` (1个): 测试复杂查询一致性

### 边界条件测试 (2个)

- `testEdgeCases_*` (1个): 测试边界情况
- 各种特殊输入处理

### 批量操作测试 (1个)

- `testBulkOperations_*` (1个): 测试批量保存功能

## 依赖组件

- Spring Data JPA
- H2 内存数据库 (测试环境)
- AssertJ 断言库
- JUnit 5 测试框架
- LocalDateTime 时间处理
- UserRole 自定义枚举类
