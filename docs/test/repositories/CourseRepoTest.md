# OpenCourse 测试文档 - CourseRepoTest

本文档为 OpenCourse 团队测试文档之 `CourseRepoTest`

## Details

测试的主要功能：

1. 上下文加载 (`contextLoads`)
   1. 验证 CourseRepo 和 TestEntityManager 正确注入

2. 按名称模糊查找课程 (`findByNameContainingIgnoreCaseOrderByNameAsc`)
   1. 存在匹配课程时返回按名称升序排列的匹配列表
   2. 无匹配结果时返回空列表
   3. 大小写不敏感匹配成功
   4. 空字符串搜索时返回所有课程

3. 按课程代码查找 (`findByCode`)
   1. 课程存在时返回对应课程
   2. 课程不存在时返回空 Optional
   3. 大小写敏感匹配验证

4. 检查课程代码是否存在 (`existsByCode`)
   1. 课程存在时返回 true
   2. 课程不存在时返回 false
   3. 大小写敏感验证

5. 按部门ID查找课程 (`findByDepartmentId`)
   1. 存在课程时返回该部门所有课程列表
   2. 部门无课程时返回空列表
   3. 验证不同部门的课程分布

6. 按课程类型查找 (`findByCourseType`)
   1. 按通识必修课类型查找
   2. 按通识选修课类型查找
   3. 按专业必修课类型查找
   4. 按专业选修课类型查找
   5. 指定类型无课程时返回空列表

7. 按部门和课程类型联合查找 (`findByDepartmentIdAndCourseType`)
   1. 存在匹配课程时返回列表
   2. 无匹配课程时返回空列表
   3. 验证多种部门和类型组合

8. 按名称升序查找所有课程 (`findAllByOrderByNameAsc`)
   1. 返回按字母顺序排序的所有课程
   2. 无课程时返回空列表

9. 课程数据完整性验证 (`testCourseDataIntegrity`)
   1. 验证课程的所有字段正确保存和读取

10. 课程类型枚举功能验证 (`testCourseTypeEnumFunctionality`)
    1. 验证课程类型的 ID、名称、描述字段
    2. 验证不同课程类型的正确映射

11. 课程类型按ID获取 (`testCourseTypeGetById`)
    1. 正确的ID返回对应类型
    2. 不存在的ID返回 null

12. 部门关系验证 (`testDepartmentRelationship`)
    1. 验证课程与部门的正确关联

13. 学分精度验证 (`testCreditsDecimalPrecision`)
    1. 验证 BigDecimal 学分的精度保存

14. 课程toString方法验证 (`testCourseToString`)
    1. 验证课程对象的字符串表示包含关键信息

15. 边界条件测试 (`testEdgeCases`)
    1. 空字符串搜索处理
    2. 单字符搜索处理
    3. 特殊字符搜索处理

16. 课程类型分布验证 (`testCourseTypeDistribution`)
    1. 验证各种课程类型的数量分布正确

## 测试覆盖

- **总测试方法数**: 25 个
- **基础 CRUD 操作**: 3 个
- **查询方法测试**: 12 个
- **数据关系测试**: 4 个
- **边界条件测试**: 3 个
- **数据完整性测试**: 3 个

## 测试方法分类

### 查询测试 (12个)

- `testFindByNameContainingIgnoreCase_*` (4个): 测试模糊查找和排序
- `testFindByCode_*` (3个): 测试按代码精确查找
- `testExistsByCode_*` (2个): 测试代码存在性检查
- `testFindByDepartmentId_*` (2个): 测试按部门查找
- `testFindByCourseType_*` (2个): 测试按课程类型查找
- `testFindByDepartmentIdAndCourseType_*` (2个): 测试联合条件查找
- `testFindAllByOrderByNameAsc_*` (2个): 测试排序查询

### 数据关系测试 (4个)

- `testDepartmentRelationship_*` (1个): 测试部门关联
- `testCourseTypeEnumFunctionality_*` (1个): 测试枚举关系
- `testCourseTypeGetById_*` (1个): 测试枚举ID映射
- `testCourseTypeDistribution_*` (1个): 测试类型分布

### 数据完整性测试 (3个)

- `testCourseDataIntegrity_*` (1个): 测试完整字段验证
- `testCreditsDecimalPrecision_*` (1个): 测试精度保存
- `testCourseToString_*` (1个): 测试对象表示

### 边界条件测试 (3个)

- 空值和特殊字符处理 (1个)
- 不存在记录处理 (2个)
- 极值情况处理 (1个)

### CRUD 操作测试 (3个)

- 查询操作 (2个): 各种条件下的查询
- 删除操作 (1个): 测试删除功能

## 依赖组件

- Spring Data JPA
- H2 内存数据库 (测试环境)
- AssertJ 断言库
- JUnit 5 测试框架
- BigDecimal 精度计算
- CourseType 自定义枚举类
