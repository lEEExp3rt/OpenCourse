# OpenCourse 测试文档 - ResourceManagerTest

本文档为 OpenCourse 团队测试文档之 `ResourceManagerTest`

## Details

测试的主要功能：

1. 添加资源 (`addResource`)
   1. 正常添加成功
   2. 课程不存在抛出 `IllegalArgumentException`
   3. 用户不存在抛出 `IllegalArgumentException`
   4. 文件存储失败抛出 `RuntimeException`
   5. 资源保存失败时文件存储回滚成功
   6. 文件回滚失败抛出 `RuntimeException`
2. 删除资源 (`deleteResource`)
   1. 正常删除成功
   2. 资源不存在抛出 `IllegalArgumentException`
   3.  用户不存在抛出 `IllegalArgumentException`
   4.  资源删除失败抛出 `RuntimeException`
   5.  文件删除失败抛出 `RuntimeException`
3. 更新资源 (`updateResource`) ( **未实现** )
   1. 仅更新元数据 - 当前返回 `null`
   2. 更新资源并上传新文件 - 当前返回 `null`
   3. 更新不存在的资源 - 当前返回 `null`（应抛出 `IllegalArgumentException`）
4. 获取资源 (`getResource`)
   1. 获取存在的资源
   2. 获取不存在的资源返回 `null`
5. 按课程获取资源 (`getResourcesByCourse`)
   1. 获取指定课程的所有资源
   2. 课程无资源时返回空列表
6. 按用户获取资源 (`getResourcesByUser`)
   1. 获取指定用户创建的所有资源
   2. 用户无资源时返回空列表
7. 点赞资源 (`likeResource`)
   1. 用户未点赞时点赞成功返回 `true`
   2. 用户已点赞时返回 `false`
   3. 资源不存在抛出 `IllegalArgumentException`
   4. 用户不存在抛出 `IllegalArgumentException`
8. 取消点赞资源 (`unlikeResource`)
   1. 用户已点赞时取消点赞成功返回 `true`
   2. 用户未点赞时返回 `false`
   3. 资源不存在抛出 `IllegalArgumentException`
   4. 用户不存在抛出 `IllegalArgumentException`
9. 查看资源 (`viewResource`)
   1. 正常查看资源返回文件流
   2. 资源不存在抛出 `IllegalArgumentException`
   3. 用户不存在抛出 `IllegalArgumentException`
   4. 文件服务返回 `null` 时返回 `null`

## 测试覆盖

- **总测试方法数**: 29 个
- **已实现功能测试**: 26 个
- **未实现功能测试**: 3 个（标记为 NOT IMPLEMENTED）

## 注意事项

1. 使用 `spy()` 和 `lenient()` 处理实体对象的ID获取
2. 未实现的 `updateResource` 方法当前返回 `null`，测试验证此行为
3. 所有异常情况都有对应的测试覆盖
4. 文件操作包含完整的回滚机制测试
5. 点赞/取消点赞逻辑通过 `HistoryManager` 验证状态
