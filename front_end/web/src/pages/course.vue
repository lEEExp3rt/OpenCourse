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

const goToProfile = () => {
  console.log("跳转到个人中心");
};
</script>

<template>
    <div class="common-layout">
        <el-container class="base" style="width: 100vw; height: 100vh;">
        <el-header>
            <!--logo和文字-->
            <div style="display: flex; align-items: center;">
            <img src="../../resources/Logo.png" style="height: 32px; margin-right: 10px;" />
            <span style="font-size: 20px; font-weight: bold;">OpenCourse</span>
            </div>
            <div class="profile-box" @click="goToProfile">
                <el-avatar :size="50" :src="public/images/default_person.jpeg" />
            </div>
        </el-header>

        <el-container class="layout-container-demo" >
            <el-aside width="200px">
                <el-scrollbar>
                <el-menu>
                <el-sub-menu index="1">
                    <template #title>
                    <el-icon><message /></el-icon>课程中心
                    </template>
                    <el-menu-item-group>
                    <template #title>Group 1</template>
                    <el-menu-item index="1-1">Option 1</el-menu-item>
                    <el-menu-item index="1-2">Option 2</el-menu-item>
                    </el-menu-item-group>
                    <el-menu-item-group title="Group 2">
                    <el-menu-item index="1-3">Option 3</el-menu-item>
                    </el-menu-item-group>
                    <el-sub-menu index="1-4">
                    <template #title>Option4</template>
                    <el-menu-item index="1-4-1">Option 4-1</el-menu-item>
                    </el-sub-menu>
                </el-sub-menu>
                <el-sub-menu index="2">
                    <template #title>
                    <el-icon><icon-menu /></el-icon>Navigator Two
                    </template>
                    <el-menu-item-group>
                    <template #title>Group 1</template>
                    <el-menu-item index="2-1">Option 1</el-menu-item>
                    <el-menu-item index="2-2">Option 2</el-menu-item>
                    </el-menu-item-group>
                    <el-menu-item-group title="Group 2">
                    <el-menu-item index="2-3">Option 3</el-menu-item>
                    </el-menu-item-group>
                    <el-sub-menu index="2-4">
                    <template #title>Option 4</template>
                    <el-menu-item index="2-4-1">Option 4-1</el-menu-item>
                    </el-sub-menu>
                </el-sub-menu>

                <!-- <el-sub-menu index="3">
                    <template #title>
                    <el-icon><setting /></el-icon>Navigator Three
                    </template>
                    <el-menu-item-group>
                    <template #title>Group 1</template>
                    <el-menu-item index="3-1">Option 1</el-menu-item>
                    <el-menu-item index="3-2">Option 2</el-menu-item>
                    </el-menu-item-group>
                    <el-menu-item-group title="Group 2">
                    <el-menu-item index="3-3">Option 3</el-menu-item>
                    </el-menu-item-group>
                    <el-sub-menu index="3-4">
                    <template #title>Option 4</template>
                    <el-menu-item index="3-4-1">Option 4-1</el-menu-item>
                    </el-sub-menu>
                </el-sub-menu> -->
                </el-menu>
            </el-scrollbar>
            </el-aside>
        </el-container>
        <el-main>Main</el-main>
        </el-container>
  </div>

  </template>
  



<style>
  body {
    margin: 0;
    font-family: 'Arial', sans-serif;
  }
.base .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
  padding: 0 20px;
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: space-between; 
}
.layout-container-demo .el-aside {
color: var(--el-text-color-primary);
background: var(--el-color-primary-light-8);
}


</style>