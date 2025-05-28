# OpenCourse 测试文档 - MinioFileStorageServiceTest

本文档为 OpenCourse 团队测试文档之 `MinioFileStorageServiceTest`

## Details

测试的主要功能：

1. 存储文件 (`storeFile`)
   1. 正常存储 PDF 文件成功
   2. 正常存储文本文件成功
   3. 文件名为 `null` 返回 `null`
   4. MinIO 抛出异常时返回 `null`
2. 获取文件 (`getFile`)
   1. 正常获取存在的文件
   2. MinIO 抛出异常时返回 `null`
3. 删除文件 (`deleteFile`)
   1. 正常删除存在的文件
   2. MinIO 抛出异常时返回 `false`
4. 计算文件大小 (`calculateFileSizeMB`)
   1. 计算各种文件大小的 MB 值
   2. 计算大文件的 MB 值
