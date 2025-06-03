<!-- CourseDetail.vue -->
<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const courseId = route.params.id

const courseData = ref(null)

const fetchCourseDetail = async () => {
  // 假设你有一个封装好的 request
  const res = await request.get(`/course/${courseId}`)
  courseData.value = res.data
}

onMounted(() => {
  fetchCourseDetail()
})
</script>

<template>
  <div v-if="courseData">
    <h1>{{ courseData.name }}</h1>
    <p>任课教师：{{ courseData.teacher }}</p>
    <img :src="courseData.image" alt="课程图片" />
    <div class="course-content">
      {{ courseData.description }}
    </div>
  </div>
  <div v-else>加载中...</div>
</template>
