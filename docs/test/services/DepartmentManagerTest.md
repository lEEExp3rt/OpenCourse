# OpenCourse 测试文档 - DepartmentManagerTest

本文档为 OpenCourse 团队测试文档之 `DepartmentManagerTest`

## Details

测试的主要功能：

1. 添加部门 (`addDepartment`)
   1. 正常添加成功
   2. 部门名称已存在返回 `null`
   3. 部门名称为 `null` 或空字符串抛出异常
   4. 用户不存在抛出异常
2. 更新部门 (`updateDepartment`)
   1. 正常更新成功
   2. 新名称已存在返回 `null`
   3. 部门不存在返回 `null`
   4. 名称为 `null` 抛出异常
3. 删除部门 (`deleteDepartment`)
   1. 正常删除成功
   2. 部门不存在返回 `false`
   3. 用户不存在返回 `false`
4. 获取部门 (`getDepartment`)
   1. 获取存在的部门
   2. 获取不存在的部门返回 `null`
5. 获取部门列表 (`getDepartments`)
   1. 获取所有部门（按名称排序）
   2. 按名称搜索匹配的部门
   3. 搜索名称为 `null` 或空返回所有部门
   4. 无匹配结果返回空列表
