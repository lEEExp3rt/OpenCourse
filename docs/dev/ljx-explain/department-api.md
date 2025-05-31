# Department API 测试文档

本文档展示了 Department 控制器的所有 API 接口及其使用方法。

## API 接口列表

### 1. 新增部门

- **URL**: `POST /department`
- **请求体**:

```json
{
  "name": "计算机科学与技术学院"
}
```

- **成功响应**:

```json
{
  "success": true,
  "message": "部门创建成功",
  "data": {
    "id": 1,
    "name": "计算机科学与技术学院"
  }
}
```

### 2. 更新部门

- **URL**: `PUT /department`
- **请求体**:

```json
{
  "id": 1,
  "name": "计算机科学与工程学院"
}
```

- **成功响应**:

```json
{
  "success": true,
  "message": "部门更新成功",
  "data": {
    "id": 1,
    "name": "计算机科学与工程学院"
  }
}
```

### 3. 删除部门

- **URL**: `DELETE /department/{id}`
- **路径参数**: `id` - 部门 ID
- **成功响应**:

```json
{
  "success": true,
  "message": "部门删除成功",
  "data": null
}
```

### 4. 根据 ID 查询部门

- **URL**: `GET /department/{id}`
- **路径参数**: `id` - 部门 ID
- **成功响应**:

```json
{
  "success": true,
  "message": "获取部门信息成功",
  "data": {
    "id": 1,
    "name": "计算机科学与工程学院"
  }
}
```

### 5. 获取所有部门

- **URL**: `GET /department`
- **成功响应**:

```json
{
  "success": true,
  "message": "获取部门列表成功",
  "data": [
    {
      "id": 1,
      "name": "计算机科学与工程学院"
    },
    {
      "id": 2,
      "name": "电子信息工程学院"
    }
  ]
}
```

### 6. 模糊查询部门

- **URL**: `GET /department/search?name={keyword}`
- **查询参数**: `name` - 部门名称关键词（可选）
- **示例**: `GET /department/search?name=计算机`
- **成功响应**:

```json
{
  "success": true,
  "message": "搜索部门成功",
  "data": [
    {
      "id": 1,
      "name": "计算机科学与工程学院"
    }
  ]
}
```

## 错误响应示例

### 部门名称已存在

```json
{
  "success": false,
  "message": "部门名称已存在",
  "data": null
}
```

### 部门不存在

```json
{
  "success": false,
  "message": "部门不存在或名称已被使用",
  "data": null
}
```

### 验证错误

```json
{
  "success": false,
  "message": "部门名称不能为空",
  "data": null
}
```

## 注意事项

1. 所有的增删改操作都需要用户认证
2. 部门名称不能为空且不能超过 31 个字符
3. 部门名称必须唯一
4. 删除部门前请确保没有课程关联到该部门
5. 模糊查询不区分大小写
6. 如果模糊查询不提供关键词，将返回所有部门（按名称升序排列）
