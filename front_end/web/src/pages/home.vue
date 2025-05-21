<script setup>
import { useCounterStore } from "../stores/counter"
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
    <div class="flex h-screen bg-softgreen">
      <!-- 左侧边栏 -->
      <aside class="w-64 bg-white shadow p-4">
        <h2 class="text-lg font-bold mb-4">学院分类</h2>
        <ul class="space-y-2">
          <li v-for="dept in departments" :key="dept" class="hover:text-primary cursor-pointer">
            {{ dept }}
          </li>
        </ul>
      </aside>
  
      <!-- 主体区域 -->
      <main class="flex-1 p-6 overflow-y-auto">
        <div class="flex justify-end mb-4">
          <button class="bg-primary text-white px-4 py-2 rounded-full shadow hover:bg-green-600">
            个人中心
          </button>
        </div>
  
        <!-- 课程展示 -->
        <div class="grid grid-cols-3 gap-4">
          <div
            v-for="course in courses"
            :key="course.name"
            class="bg-white p-4 rounded-lg shadow hover:shadow-md transition relative group"
          >
            <h3 class="text-lg font-semibold text-primary">{{ course.name }}</h3>
            <div
              class="absolute inset-0 bg-white bg-opacity-90 opacity-0 group-hover:opacity-100 transition flex flex-col items-center justify-center p-4 text-center text-sm"
            >
              <p>授课老师：{{ course.teacher }}</p>
              <p>开课学年：{{ course.year }}</p>
            </div>
          </div>
        </div>
      </main>
    </div>
  </template>
  

  <style>
  body {
    margin: 0;
    font-family: 'Arial', sans-serif;
  }
  </style>
  