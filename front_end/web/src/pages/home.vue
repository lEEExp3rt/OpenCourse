<script setup>
const departments = ['海洋学院', '计算机学院', '控制学院', '经济学院', '管理学院'];

const courses = [
  { name: '数据结构', teacher: '张老师', year: '2024-2025' },
  { name: '智能控制基础', teacher: '李老师', year: '2023-2024' },
  { name: '微观经济学', teacher: '王老师', year: '2022-2023' },
  { name: '深海探测技术', teacher: '刘老师', year: '2024-2025' },
  { name: 'Vue 3 入门', teacher: '陈老师', year: '2025-2026' },
  { name: '人工智能导论', teacher: '赵老师', year: '2024-2025' },
];    
</script>

<template>
    <div class="common-layout">
    <el-container style="width: 100vw; height: 100vh;">
      <el-header>Header</el-header>
      <el-container>
        <el-aside width="200px">Aside</el-aside>
        <el-main>Main</el-main>
      </el-container>
    </el-container>
  </div>

  </template>
  

  
  <style scoped>
  /* 你也可以在这里加入自定义样式 */
  </style>
  

  <style>
  body {
    margin: 0;
    font-family: 'Arial', sans-serif;
  }
  </style>