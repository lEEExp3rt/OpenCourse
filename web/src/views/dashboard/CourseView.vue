  
<script setup>
import { useCoursesStore } from '@/stores/courses'
import { onMounted,ref } from 'vue'
import { useRoute } from 'vue-router'
const coursesStore = useCoursesStore()

const route = useRoute()
const departmentId = route.params.department_id
console.log("departmentId = ",departmentId)
const handleCardClick = (course_id) => {
  const currentUrl = window.location.href
  // 这里简单拼接 course_id，假设是直接追加在末尾
  window.open(currentUrl + '/' +course_id + '/resources')
}

const dialogVisible = ref(false)
const newCourseForm = ref({
  name: '',
  departmentId: 0,
  typeId: 0,
  credits: 0,
})

onMounted(() => {
console.log("enter course page")
coursesStore.fetchAllCourses(departmentId)
})

const get_typename_by_id = (courseType) => {
  const map = {
    11: '通识必修',
    12: '通识选修',
    13: '专业必修',
    14: '专业选修'
  }
  return map[courseType] || `未知类型（ID: ${courseType})`
}

const new_course = () => {
  dialogVisible.value = true
}


const handleDelete = (id, event) => {
  event.stopPropagation()  // 阻止事件冒泡，避免触发el-card的点击
  coursesStore.DeleteCourse(id)
}

const handleSubmitNewCourse = () => {
  newCourseForm.value.departmentId=departmentId
  coursesStore.CreateCourse({ ...newCourseForm.value })
  dialogVisible.value = false
  // 清空表单
  newCourseForm.value = {
    code: '',
    name: '',
    departmentId: departmentId,
    typeId: 0,
    credits: 0,
  }
}
</script>


<template>
<div>
    <el-card
      v-for="course in coursesStore.courseList"
      :key="course.id"
      class="course-card"
      shadow="hover"
      @click="handleCardClick(course.id)"
    >
    <el-row align="middle" class="course-row" style="margin-bottom: 16px">
      <el-col :span="20">
        <div>课程名称：{{ course.name }}</div>
        <div>课程代码：{{ course.code }}</div>
        <div>课程类型：{{ course.courseType.description }}</div>
        <div> 学分：{{   course.credits }}</div>
      </el-col>
      <el-col :span="4" style="text-align: right">
        <el-button type="danger" @click="(event) =>handleDelete(course.id,event)">删除</el-button>
      </el-col>
    </el-row>

    </el-card>

    <div style="text-align: center; margin-top: 20px;">
      <el-button type="primary" @click="new_course">新增课程</el-button>
    </div>

  <el-dialog title="新增课程" v-model="dialogVisible" width="500px">
    <el-form :model="newCourseForm" label-width="100px">
      <el-form-item label="课程名称">
        <el-input v-model="newCourseForm.name" />
      </el-form-item>

      <el-form-item label="课程代码">
        <el-input v-model="newCourseForm.code" />
      </el-form-item>
      <el-form-item label="课程类型 ID">
        <el-select v-model="newCourseForm.typeId" placeholder="请选择课程类型">
          <el-option :value="11" label="通识必修" />
          <el-option :value="12" label="通识选修" />
          <el-option :value="13" label="专业必修" />
          <el-option :value="14" label="专业选修" />
        </el-select>
      </el-form-item>
      <el-form-item label="学分">
        <el-input-number v-model="newCourseForm.credits" :min="0" :max="10" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmitNewCourse">提交</el-button>
    </template>
  </el-dialog>

  </div>
</template>

  <style scoped>
  .course-card {
    cursor: pointer;
    transition: transform 0.2s ease;
    margin-bottom: 16px;
  }
  .course-card:hover {
    transform: scale(1.02);
  }
  .course-title {
    font-size: 18px;
    font-weight: bold;
    color: #303133;
  }
  .course-teacher {
    margin-top: 8px;
    color: #606266;
  }
  </style>
  