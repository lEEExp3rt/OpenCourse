<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useCourseStore } from '@/stores/course'
import { genFileId, type UploadInstance, type UploadProps, type UploadRawFile } from 'element-plus'

const route = useRoute()
const courseStore = useCourseStore()
const courseId = Number(route.params.id)

const dialogVisible = ref(false)
const uploadFile = ref<UploadFile | null>(null)

const upload = ref<UploadInstance>()  // ✅ 新增：用于控制 el-upload
const fileList = ref<UploadFile[]>([])  // 控制 UI 列表
const maxFileSize = 50 * 1024 * 1024  
const form = reactive({
  description: '',
  name: '',
  resourceTypeId: null as number | null,
  fileType: ''
})

// 下拉选项数组，对应传入枚举类型
const fileTypeOptions = [
  { label: '历年卷', value: 51 },
  { label: '作业', value: 52 },
  { label: '笔记', value: 53 },
  { label: '教材', value: 54 },
  { label: '课件', value: 55 },
  { label: '其它', value: 56 },
]

const fetchCourseDetail = async () => {
  await courseStore.fetchCourseResources(courseId)
}

const handleDelete = async (id: number) => {
  await courseStore.deleteResource(id)
  await fetchCourseDetail()
}

onMounted(() => {
  fetchCourseDetail()
})

const handleAdd = () => {
  dialogVisible.value = true
}

// 自动替换上传文件
const handleFileChange: UploadProps['onChange'] = (file, files) => {
  if (file.raw!.size > maxFileSize) {
    // 超大则清空列表
    fileList.value = []
    alert('文件大小不能超过50MB')
    return
  } else {
    // 保持只保留最后一个文件
    fileList.value = files.slice(-1)
  }
  uploadFile.value = file
  const filename = file.name
  const ext = filename.split('.').pop()?.toLowerCase() || ''

  if(ext === 'pdf' || ext == 'txt'){
    form.fileType = ext  
  }
  else{
    form.fileType = 'other'  // 默认类型
  }
}
const submitUpload = async () => {
  if (!uploadFile.value) {
    alert('请先选择文件')
    return
  }
  if (!form.name) {
    alert('请填写资源名称')
    return
  }
  if (!form.resourceTypeId) {
    alert('请选择资源类型')
    return
  }
  // 读取二进制数据
  const buffer = await new Promise<ArrayBuffer>((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as ArrayBuffer)
    reader.onerror = (e) => reject(e)
    reader.readAsArrayBuffer(uploadFile.value!.raw!)
  })
  console.log('buffer length:', buffer.byteLength)
  // 构造新资源对象，包含 body 二进制内容

  const formData = new FormData()
  formData.append('name', form.name)
  formData.append('description', form.description)
  formData.append('resourceTypeId', String(form.resourceTypeId!))
  formData.append('courseId', String(courseId))
  formData.append('fileType', form.fileType)
  formData.append('file', uploadFile.value.raw!)  
  // 添加资源
  await courseStore.addResource(formData)
  await fetchCourseDetail()

  // 重置状态
  dialogVisible.value = false
  form.description = ''
  form.name = ''
  form.resourceTypeId = null
  uploadFile.value = null
  fileList.value = []
}

const downloadResource = (resource: any) => {
  courseStore.downloadResource(resource)
}

function getFileTypeLabel(fileTypeId) {
  const item = fileTypeOptions.find(opt => opt.value === fileTypeId);
  return item ? item.label : '未知类型';
}

const handleLike = async (resourceId: number) => {
  await courseStore.likeResource(resourceId)
  await fetchCourseDetail()
}
</script>


<template>
  <div>
    <h1>课程资源</h1>

    <div v-if="courseStore.resourceList.length > 0">
      <ul>
        <li
          v-for="resource in courseStore.resourceList"
          :key="resource.id"
          class="resource-item"
        >
          <div class="resource-row">
            <div class="resource-info">
              <p><strong>{{ resource.name }}</strong>（{{ resource.fileSize }} bytes）</p>
              <p>{{ resource.description }}</p>
              <p>类型: {{ getFileTypeLabel(resource.resourceType) }} | {{ resource.user }} 上传于 {{ resource.createdAt }}</p>
              <div class="resource-meta">
                <button
                  class="vote-button"
                  :aria-label="`赞同 ${count}`"
                  aria-live="polite"
                  type="button"
                  @click="handleLike(resource.id)"
                >
                  <span style="display: inline-flex; align-items: center;">
                    <svg
                      width="10"
                      height="10"
                      viewBox="0 0 24 24"
                      class="icon-triangle"
                      fill="currentColor"
                    >
                      <path
                        fill-rule="evenodd"
                        clip-rule="evenodd"
                        d="M13.792 3.681c-.781-1.406-2.803-1.406-3.584 0l-7.79 14.023c-.76 1.367.228 3.046 1.791 3.046h15.582c1.563 0 2.55-1.68 1.791-3.046l-7.79-14.023Z"
                      />
                    </svg>
                  </span>
                  赞同 {{ resource.likes }}
                </button>

                <div class="comment-count" :aria-label="`${count} 条评论`">
                  {{ resource.views }} 次下载
                </div>
              </div>
            </div>

            <div class="resource-actions">
              <a href="javascript:void(0);"
                @click="downloadResource(resource)"
                class="action-link download-link"
              >
                下载
              </a>

              <el-button type="danger" size="small" @click="handleDelete(resource.id)">
                删除
              </el-button>
            </div>
          </div>
        </li>
      </ul>
    </div>

    <div v-else>
      <p>暂无资源，或正在加载中...</p>
    </div>

    <div class="upload-button-container">
      <el-button type="primary" @click="handleAdd">上传资源</el-button>
    </div>

    <el-dialog v-model="dialogVisible" title="上传新资源">
      <el-upload
        ref="upload"
        class="upload-demo"
        :auto-upload="false"
        accept="*/*"
        :file-list="fileList"
        :on-change="handleFileChange"
      >
      <el-button>选择文件</el-button>
      <span v-if="uploadFile">{{ uploadFile.name }}</span>
    </el-upload>
    <el-form label-position="top" style="margin-top: 1rem;">
      <!-- 资源名称 -->
      <el-form-item label="资源名称"  required>
        <el-input v-model="form.name" placeholder="默认文件名"></el-input>
      </el-form-item>

      <!-- 资源描述 -->
      <el-form-item label="资源描述">
        <el-input
          type="textarea"
          v-model="form.description"
          placeholder="请输入资源描述"
          :rows="3"
        ></el-input>
      </el-form-item>

      <!-- 资源类型下拉选择框 -->
      <el-form-item label="资源类型" required>
        <el-select v-model="form.resourceTypeId" placeholder="请选择资源类型">
          <el-option
            v-for="item in fileTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>




      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">确认上传</el-button>
      </template>
    </el-dialog>
    <span v-if="uploadFile">{{ uploadFile.name }}</span>
  </div>
</template>

<style scoped>
.resource-item {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #ccc;
  padding-bottom: 1rem;
}

.resource-row {
  display: flex;
  justify-content: space-between;
}

.resource-info {
  flex: 1;
}

.resource-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-left: 2rem;
}

.upload-button-container {
  margin-top: 2rem;
  text-align: center;
}
.download-link {
  color: purple;
  text-decoration: underline;
  cursor: pointer;
}

.vote-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  font-size: 14px;
  background-color: white;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  color: #1e80ff;
  transition: all 0.2s ease;
}

.vote-button:hover {
  background-color: #f0f8ff;
  border-color: #409eff;
}

.icon-triangle {
  fill: currentColor;
}


.comment-count {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #8590a6;
  cursor: pointer;
  font-size: 14px;
  user-select: none;
  transition: color 0.2s ease;
}

.resource-meta {
  display: flex;
  align-items: center;
  gap: 12px; /* 控制按钮与下载数之间的间距 */
  margin-top: 8px;
}
</style>
