# OpenCourse 测试文档 - DepartmentRepoTest

本文档为 OpenCourse 团队测试文档之 `DepartmentRepoTest`

## Details

测试的主要功能：

1. 上下文加载 (`contextLoads`)
   1. 验证 DepartmentRepo 和 TestEntityManager 正确注入

2. 按名称查找部门 (`findByName`)
   1. 部门存在时返回对应部门
   2. 部门不存在时返回空 Optional
   3. 名称为 null 时返回空 Optional

3. 按名称模糊查找部门 (`findByNameContainingIgnoreCase`)
   1. 存在匹配部门时返回匹配列表
   2. 大小写不敏感匹配成功
   3. 无匹配结果时返回空列表
   4. 空关键词时返回所有部门

4. 检查部门名称是否存在 (`existsByName`)
   1. 部门存在时返回 true
   2. 部门不存在时返回 false
   3. 名称为 null 时返回 false

5. 按名称升序查找所有部门 (`findAllByOrderByNameAsc`)
   1. 返回按字母顺序排序的部门列表
   2. 无部门时返回空列表
   3. 单个部门时返回单项列表

6. 保存部门 (`save`)
   1. 保存新部门并自动生成 ID

7. 删除部门 (`deleteById`)
   1. 成功从数据库中删除部门

8. 按 ID 查找部门 (`findById`)
   1. 部门存在时返回对应部门
   2. 部门不存在时返回空 Optional

9.  更新部门 (`save` - 更新操作)
   1. 修改现有部门记录成功

10. 部门名称唯一性验证
    1. 验证部门名称的唯一性约束

## 测试覆盖

- **总测试方法数**: 20 个
- **基础 CRUD 操作**: 5 个
- **查询方法测试**: 10 个
- **边界条件测试**: 4 个
- **数据完整性测试**: 1 个

## 测试方法分类

### 查询测试 (9个)

- `testFindByName_*` (3个): 测试按名称精确查找
- `testFindByNameContainingIgnoreCase_*` (4个): 测试模糊查找
- `testExistsByName_*` (3个): 测试名称存在性检查
- `testFindAllByOrderByNameAsc_*` (3个): 测试排序查询
- `testFindById_*` (2个): 测试按ID查找

### CRUD 操作测试 (4个)

- `testSaveDepartment_*` (1个): 测试创建操作
- `testDeleteDepartment_*` (1个): 测试删除操作
- `testUpdateDepartment_*` (1个): 测试更新操作
- `testFindById_*` (2个): 测试读取操作

### 边界条件测试 (6个)

- null 值处理 (3个)
- 空结果处理 (2个)
- 不存在记录处理 (3个)

### 数据完整性测试 (1个)

- 部门名称唯一性验证

## 依赖组件

- Spring Data JPA
- H2 内存数据库 (测试环境)
- AssertJ 断言库
- JUnit 5 测试框架
