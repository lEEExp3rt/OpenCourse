# OpenCourse 测试文档 - UserManagerTest

本文档为 OpenCourse 团队测试文档之 `UserManagerTest`

## Details

测试的主要功能：

1. 发送注册验证码 (`sendRegistrationVerificationCode`)
   1. 邮箱未注册时发送成功返回 `true`
   2. 邮箱已注册时发送失败返回 `false`
2. 用户注册 (`registerUser`)
   1. 验证码正确时注册成功并保存用户
   2. 验证码错误时注册失败返回 `null`
3. 用户登录 (`login`)
   1. 有效凭据时认证成功返回JWT令牌
   2. 无效凭据时认证失败返回 `null`
   3. 用户不存在时返回 `null`
4. 发送密码重置验证码 (`sendPasswordResetVerificationCode`)
   1. 邮箱存在时发送成功返回 `true`
   2. 邮箱不存在时发送失败返回 `false`
5. 重置密码 (`resetPassword`)
   1. 验证码正确且用户存在时重置成功返回 `true`
   2. 验证码错误时重置失败返回 `false`
   3. 用户不存在时重置失败返回 `false`
6. 通过邮箱获取用户 (`getUserByEmail`)
   1. 用户存在时返回用户对象
   2. 用户不存在时返回 `null`
7. 通过用户名获取用户 (`getUserByName`)
   1. 用户存在时返回用户对象
   2. 用户不存在时返回 `null`
8. 通过ID获取用户 (`getUser`)
   1. 用户存在时返回用户对象
   2. 用户不存在时返回 `null`
9. 更新用户角色 (`updateUserRole`)
   1. 用户存在时更新角色成功返回 `true`
   2. 用户不存在时更新失败返回 `false`
10. 增加用户活跃度 (`addUserActivity`)
    1. 用户存在时增加活跃度并返回新值
    2. 用户不存在时返回 `0`
11. 获取用户活跃度 (`getUserActivity`)
    1. 用户存在时返回当前活跃度值
    2. 用户不存在时返回 `0`
12. 减少用户活跃度 (`reduceUserActivity`)
    1. 用户存在时减少活跃度并返回新值
    2. 用户不存在时返回 `0`

## 测试覆盖

- **总测试方法数**: 22 个
- **已实现功能测试**: 22 个
- **注释掉的未实现功能**: 4 个（`disableUser` 和 `enableUser` 相关）

## 注意事项

1. 使用手动依赖注入而非 `@InjectMocks`，提供更好的控制
2. 完整的方法调用验证，确保所有依赖正确交互
3. 边界条件测试覆盖（用户不存在、验证码错误等）
4. 数据状态验证（用户对象字段、活跃度变化等）
5. 异常场景完整覆盖，确保系统健壮性
6. 邮件服务调用参数精确验证
7. JWT令牌生成流程验证
