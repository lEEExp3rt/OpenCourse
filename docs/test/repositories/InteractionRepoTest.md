# OpenCourse 测试文档 - InteractionRepoTest

本文档为 OpenCourse 团队测试文档之 `InteractionRepoTest`

## Details

测试的主要功能：

1. 上下文加载 (`contextLoads`)
   1. 验证 InteractionRepo 和 TestEntityManager 正确注入

2. 按课程查找所有互动 (`findAllByCourse`)
   1. 有效课程返回所有相关互动记录
   2. 无互动的课程返回空列表

3. 按课程ID排序查找互动 (`findByCourseIdOrderByLikesDescCreatedAtDesc`)
   1. 按点赞数降序排列，再按创建时间降序排列
   2. 相同点赞数时按创建时间降序排列
   3. 不存在的课程ID返回空列表

4. 按用户ID查找互动 (`findByUserId`)
   1. 有效用户ID返回该用户所有互动记录
   2. 无互动的用户返回空列表
   3. 不存在的用户ID返回空列表

5. 按课程和用户查找互动 (`findByCourseAndUser`)
   1. 存在互动时返回对应的互动记录
   2. 无互动时返回空 Optional
   3. 同一用户在不同课程的互动正确区分

6. 检查课程和用户间是否存在互动 (`existsByCourseAndUser`)
   1. 存在互动时返回 true
   2. 无互动时返回 false
   3. 与 findByCourseAndUser 结果保持一致

7. 互动实体行为验证 (`interactions`)
   1. 正确处理不同类型的互动（评论+评分、仅评分、仅评论）
   2. 正确处理点赞和点踩操作
   3. 自动生成创建时间戳
   4. 维护引用完整性
   5. 正确处理边界评分值（1-10）
   6. 正确处理空内容

## 测试覆盖

- **总测试方法数**: 18 个
- **查询功能测试**: 12 个
- **实体行为测试**: 6 个
- **边界条件测试**: 4 个
- **数据完整性测试**: 3 个

## 测试方法分类

### 查询功能测试 (12个)

- `testFindAllByCourse_*` (2个): 测试按课程查找互动
- `testFindByCourseIdOrderByLikesDescCreatedAtDesc_*` (3个): 测试复杂排序查询
- `testFindByUserId_*` (3个): 测试按用户查找互动
- `testFindByCourseAndUser_*` (3个): 测试按课程和用户查找
- `testExistsByCourseAndUser_*` (3个): 测试存在性检查

### 实体行为测试 (6个)

- `testInteractions_ShouldHandleDifferentTypes_*` (1个): 测试不同互动类型
- `testInteractions_ShouldHandleLikesAndDislikes_*` (1个): 测试点赞点踩功能
- `testInteraction_ShouldAutoGenerateTimestamp_*` (1个): 测试时间戳生成
- `testInteractions_ShouldMaintainReferentialIntegrity_*` (1个): 测试引用完整性
- `testInteractions_ShouldHandleBoundaryRatings_*` (1个): 测试边界评分
- `testInteractions_ShouldHandleEmptyContent_*` (1个): 测试空内容处理

### 核心排序功能验证

**最重要的测试**: `findByCourseIdOrderByLikesDescCreatedAtDesc` 方法
- 验证双重排序逻辑：先按点赞数降序，再按时间降序
- 测试数据精心设计：3个互动分别有3、2、1个点赞
- 验证相同点赞数情况下的时间排序

## 依赖组件

- Spring Data JPA
- H2 内存数据库 (测试环境)
- AssertJ 断言库
- JUnit 5 测试框架
- BigDecimal 精度计算（课程学分）
- LocalDateTime 时间处理
- CourseType 和 UserRole 自定义枚举类
