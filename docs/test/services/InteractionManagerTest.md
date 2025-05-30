# OpenCourse 测试文档 - InteractionManagerTest

本文档为 OpenCourse 团队测试文档之 `InteractionManagerTest`

## Details

测试的主要功能：

1. 添加交互 (`addInteraction`)
   1. 用户首次评论时正常创建新交互成功
   2. 用户重复评论时更新现有交互成功
   3. 仅更新内容时不更新评分（评分为 `null`）
   4. 课程不存在时抛出 `IllegalArgumentException`
   5. 创建无评分交互时正常保存（仅内容）
   6. 处理空内容时正常创建交互
2. 更新交互 (`updateInteraction`)
   1. 交互所有者正常更新成功
   2. 仅更新内容时不更新评分（评分为 `null`）
   3. 仅更新评分时不更新内容（内容为 `null`）
   4. 交互不存在时抛出 `IllegalArgumentException`
   5. 非所有者修改时抛出 `IllegalArgumentException`
   6. 同时更新内容和评分时两者都正确更新
3. 删除交互 (`deleteInteraction`)
   1. 交互所有者正常删除成功
   2. 管理员用户删除任意交互成功
   3. 交互不存在时返回 `false`
   4. 非所有者且非管理员时返回 `false`
   5. 删除时正确扣除活跃度分数
4. 获取交互 (`getInteractions`)
   1. 按课程ID获取所有交互（按点赞数和时间排序）
   2. 课程无交互时返回空列表
5. 按用户获取交互 (`getInteractionsByUser`)
   1. 获取指定用户的所有交互
   2. 用户无交互时返回空列表
6. 点赞交互 (`likeInteraction`)
   1. 用户未点赞时点赞成功返回 `true`
   2. 用户已点赞时返回 `false`
   3. 交互不存在时返回 `false`
   4. 点赞时正确增加创作者活跃度分数
7. 取消点赞交互 (`unlikeInteraction`)
   1. 用户已点赞时取消点赞成功返回 `true`
   2. 用户未点赞时返回 `false`
   3. 交互不存在时返回 `false`
   4. 取消点赞时正确扣除创作者活跃度分数
8. 获取用户交互状态 (`getUserInteractionStatus`)
   1. 用户已点赞时返回 `true`
   2. 用户未点赞时返回 `false`
   3. 交互不存在时返回 `false`
9. 集成测试和边界条件
   1. 点赞和取消点赞操作保持一致性
   2. 多种查询调用时正确处理空列表
   3. 活跃度计算工作流程正确性验证

## 测试覆盖

- **总测试方法数**: 32 个
- **已实现功能测试**: 32 个
- **核心业务逻辑**: 100% 覆盖

## 注意事项

1. 使用 `spy()` 和 `lenient()` 处理实体对象的ID获取
2. 所有异常情况都有对应的测试覆盖
3. 活跃度计算包含完整的增减机制测试
4. 点赞/取消点赞逻辑通过 `HistoryManager` 验证状态
5. 测试覆盖了评论和评分的各种组合场景（仅评论、仅评分、两者兼有）
6. 权限验证包含所有者权限和管理员权限测试
7. 排序功能通过 Repository 层方法验证（按点赞数降序、时间降序）
8. 历史记录功能在所有相关操作中都有验证
