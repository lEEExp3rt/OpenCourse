<script setup>
import { onMounted, ref, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useCourseStore } from '@/stores/course'

const route = useRoute()
const courseStore = useCourseStore()
const courseId = Number(route.params.id)

const dialogVisible = ref(false)
const uploadFile = ref(null)
const form = reactive({
  description: '',
  name: '',
  typeId: 1,
  fileTypeId: 1,
})

const fetchCourseDetail = async () => {
  await courseStore.fetchCourseResources(courseId)
}

// 删除资源方法
const handleDelete = async (id) => {
  await courseStore.deleteCourseResource(id, courseId)
}

// 添加资源方法（简化示例，真实场景中可能需要弹窗上传等）

onMounted(() => {
  fetchCourseDetail()
})


const handleAdd = () => {
  dialogVisible.value = true
}

const submitUpload = async () => {
  if (!uploadFile.value) {
    alert('请先选择文件')
    return
  }
  if (!form.description) {
    alert('请填写描述')
    return
  }
  // 这里实际上传文件和获取路径逻辑要结合后台API
  // 假设上传成功返回路径 path
  // 这里模拟路径写死或者后续替换成实际接口调用

  // 模拟文件大小
  const fileSize = uploadFile.value.size

  // 模拟文件路径为文件名，实际根据后台返回调整
  const filePath = '/uploads/' + uploadFile.value.name

  const newResource = {
    name: form.name || uploadFile.value.name,
    description: form.description,
    typeId: form.typeId,
    fileTypeId: form.fileTypeId,
    fileSize: fileSize,
    filePath: filePath,
    course: courseId,
    user: 'admin',
  }

  await courseStore.addCourseResource(newResource, courseId)

  // 重置状态，关闭弹窗
  dialogVisible.value = false
  uploadFile.value = null
  form.description = ''
  form.name = ''
}

// 监听文件选择，存储文件
const handleFileChange = (file) => {
  uploadFile.value = file.raw
  return false // 阻止自动上传，改为手动处理
}

</script>

<template>
  <div>
    <h1>课程资源</h1>
    <div v-if="courseStore.resourceList.length > 0">
      <ul>
        <li v-for="resource in courseStore.resourceList"  :key="resource.id"  class="resource-item">
        <div class="resource-row">
          <div class="resource-info">
            <p><strong>{{ resource.name }}</strong>（{{ resource.fileSize }} bytes）</p>
            <p>{{ resource.description }}</p>
            <p>类型: {{ resource.typeId }} | 上传者: {{ resource.user }}</p>
            <p>👍 {{ resource.likes }} 👎 {{ resource.dislikes }} 👁️ {{ resource.views }}</p>
          </div>

          <div class="resource-actions">
            <a
              :href="resource.filePath"
              target="_blank"
              rel="noopener noreferrer"
              class="action-link"
            >
              查看
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
        :before-upload="handleFileChange"
        :show-file-list="false"
        accept="*/*"
      >
        <el-button>选择文件</el-button>
        <span v-if="uploadFile">{{ uploadFile.name }}</span>
      </el-upload>

      <el-form label-position="top" style="margin-top: 1rem;">
        <el-form-item label="资源名称">
          <el-input v-model="form.name" placeholder="默认文件名"></el-input>
        </el-form-item>

        <el-form-item label="资源描述" required>
          <el-input
            type="textarea"
            v-model="form.description"
            placeholder="请输入资源描述"
            rows="3"
          ></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">确认上传</el-button>
      </template>
    </el-dialog>
</div>


</template>

<style scoped>
.resource-item {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #ccc;
  padding-bottom: 1rem;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.icon {
  cursor: pointer;
  color: #555;
  transition: color 0.2s;
}
.icon:hover {
  color: red;
}

.add-button {
  margin-top: 2rem;
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #007bff;
  font-weight: bold;
}
.add-button .icon {
  margin-right: 0.5rem;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-button-container {
  margin-top: 2rem;
  text-align: center;
}
</style>
