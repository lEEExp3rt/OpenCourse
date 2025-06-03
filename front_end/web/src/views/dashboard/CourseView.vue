<template>
    <el-card
      class="course-card"
      shadow="hover"
      @click="handleCardClick"
    >
      <el-row align="middle">
        <!-- 左侧课程图片 -->
        <el-col :span="2">
          <el-image
            src="public/images/courses/bs.png"
            fit="cover"
            style="width: 100px; height: 100px; border-radius: 8px"
          />
        </el-col>
  
        <!-- 右侧课程信息 -->
        <el-col :span="18" style="padding-left: 16px">
          <div class="course-title">{{ course.name }}</div>
          <div class="course-teacher">任课教师：{{ course.teacher }}</div>
        </el-col>
      </el-row>
    </el-card>
  </template>
  
  <script setup>
  const course = {
    name: '软件工程',
    teacher: '张三教授',
    image: "public/images/courses/bs.png",
    link: 'http://localhost:5173/software_engineering'
  }
  
  const handleCardClick = () => {
    window.open(course.link, '_blank') // 新标签页打开
  }
  </script>
  
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
  