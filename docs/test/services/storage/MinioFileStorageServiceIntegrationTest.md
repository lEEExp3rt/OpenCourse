# OpenCourse 测试文档 - MinioFileStorageServiceIntegrationTest

本文档为 OpenCourse 团队测试文档之 `MinioFileStorageServiceIntegrationTest`

## Details

测试的主要功能：

1. 存储和获取文件 (`storeFile` + `getFile`)
   1. 正常存储和获取 PDF 文件成功
   2. 正常存储和获取文本文件成功
   3. 正常存储和获取空文件
   4. 文件名为 `null` 返回 `null`
2. 完整工作流测试 (`storeFile` + `getFile` + `deleteFile`)
   1. 正常存储、获取、删除文件的完整流程
   2. 验证删除后文件不可访问
3. 删除文件 (`deleteFile`)
   1. 删除不存在的文件返回 `false`
4. 多种文件类型处理
   1. 支持 PDF、TEXT、OTHER 等不同文件类型
   2. 正确设置文件路径和类型信息
5. 集成测试环境
   1. 使用真实 MinIO 服务器进行测试
   2. 自动清理测试文件
   3. 支持配置文件和环境变量配置
