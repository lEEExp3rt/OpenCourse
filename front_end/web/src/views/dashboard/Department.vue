<script setup>
import { onMounted } from 'vue'
import { useDepartmentsStore } from '@/stores/departments'
import { useRouter } from 'vue-router'  // 导入路由
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const departmentStore = useDepartmentsStore()
const router = useRouter()

onMounted(() => {
  departmentStore.fetchAllDepartments()
})

function chooseDepartment(department) {
    console.log("网页一开始 department = ",department.name)
  departmentStore.chooseDepartment(department)
  // 跳转到课程页面，传入部门id
  router.push({path: `/dashboard/department/${department.id}/course`})

}

async function createNewDepartment() {
  try {
    const { value } = await ElMessageBox.prompt('请输入学院名称', '新增学院', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /^.{1,50}$/,
      inputErrorMessage: '学院名称不能为空且长度不超过50个字符',
      inputPlaceholder: '请输入学院名称'
    })

    if (value && value.trim()) {
      const newDepartment = {
        name: value.trim()
      }
      
      await departmentStore.createDepartment(newDepartment)
      ElMessage.success('学院创建成功')
    }
  } catch (error) {
    // 用户取消操作或其他错误
    if (error !== 'cancel') {
      console.error('创建学院时出错：', error)
      ElMessage.error('创建学院失败，请重试')
    }
  }
}
</script>


<template>
    <div class="p-4">
      <h1 class="text-large font-600 mr-3"> 开课学院 </h1>

  
      <el-row :gutter="20">
        <el-col
          v-for="department in departmentStore.departmentList"
          :key="department.id"
          :span="3"
        >
          <el-card
            class="department-card"
            shadow="hover"
            @click="chooseDepartment(department)"
          >
            <div class="card-content">
              <div class="card-title">{{ department.name }}</div>
            </div>
          </el-card>
        </el-col>
  
        <!-- ➕ 新增学院卡片 -->
        <el-col :span="3">
          <el-card
            class="department-card add-card"
            shadow="hover"
            @click="createNewDepartment"
          >
            <div class="card-content">
              <el-icon :size="28">
                <Plus />
              </el-icon>
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <el-divider />
    </div>
  </template>
  

  <style scoped>
  .department-card {
    width: 100%;
    aspect-ratio: 1 / 1;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
  }
  
  .department-card:hover {
    transform: scale(1.03);
  }
  
  .card-content {
    text-align: center;
  }
  
  .card-title {
    font-size: 1.1rem;
    font-weight: bold;
  }
  
  /* 加号卡片样式 */
  .add-card .el-icon {
    font-size: 2rem;
    color: #409EFF;
  }
  </style>
  