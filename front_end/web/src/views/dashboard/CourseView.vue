  
<script setup>
import { useCourseStore } from '@/stores/course'
import { onMounted } from 'vue'
const courseStore = useCourseStore()

const handleCardClick = (course_name) => {
  window.open("http://localhost:5173/course/" + course_name) // 新标签页打开
}

onMounted(() => {
courseStore.fetchAllCourses()
})

const get_typename_by_id = (typeId) => {
  const map = {
    11: '通识必修',
    12: '通识选修',
    13: '专业必修',
    14: '专业选修'
  }
  return map[typeId] || `未知类型（ID: ${typeId})`
}

const get_name_by_id = (typeId) => {
  const map = {
    11: '通识必修',
    12: '通识选修',
    13: '专业必修',
    14: '专业选修'
  }
  return map[typeId] || `未知类型（ID: ${typeId})`
}

const handleDelete = (id, event) => {
  event.stopPropagation()  // 阻止事件冒泡，避免触发el-card的点击
  courseStore.DeleteCourse(id)
}


</script>


<template>
<div>
    <el-card
      v-for="course in courseStore.courseList"
      :key="course.id"
      class="course-card"
      shadow="hover"
      @click="handleCardClick(course.link)"
    >
    <el-row align="middle" class="course-row" style="margin-bottom: 16px">
    <el-col :span="20">
      <div>课程名称：{{ course.name }}</div>
      <div>课程代码：{{ course.code }}</div>
      <div>课程类型：{{ get_typename_by_id(course.typeId) }}</div>
      <div> 评分：{{   course.credits }}</div>
    </el-col>
    <el-col :span="4" style="text-align: right">
      <el-button type="danger" @click="(event) =>handleDelete(course.id,event)">删除</el-button>
    </el-col>
  </el-row>

    </el-card>
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
  