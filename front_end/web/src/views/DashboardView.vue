<script setup>
import { ref } from 'vue'
import { useUserModule } from "@/stores/user";

const userModule = useUserModule()
const username = ref('')
const getUserName = () => {
  if(username.value) {
    return username.value
  }
  if(!userModule.name) {
    userModule.getName()
  }
  username.value = userModule.name || '未登录用户'
  return username.value
}
</script>

<template>
  <el-container class="base-container">
    <el-header class="base-header">
      <div style="display: flex; align-items: center;">
        <el-image src="/images/open_course.png" style="width: 50px; height: 50px; margin-right: 10px; filter: invert(100%)" />
        <el-text class="mx-1"  size="default" style="color: var(--el-color-white)"> OpenCourse </el-text>
      </div>

      <div style="display: flex; padding-right: 20px; align-items: center; justify-content: space-between;">
        <el-text style="color: var(--el-color-white); margin: 1rem;">
          {{ getUserName() }}
        </el-text>
        <el-avatar>
          <el-icon size="30"> <User /> </el-icon>
        </el-avatar>
      </div>
    </el-header>

    <el-container class="base-body">
      <el-aside class="base-aside"> 
        <el-scrollbar>
          <el-menu router>
            <el-menu-item index="/dashboard/department">
              <el-icon size="20" style="margin-right: 20px"> <Collection /> </el-icon>
              <span style="font-size: 15px"> 课程中心 </span>
            </el-menu-item>

            <el-menu-item index="/dashboard/user">
              <el-icon size="20" style="margin-right: 20px"> <User /> </el-icon>
              <span style="font-size: 15px"> 个人中心 </span>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <el-main class="base-main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.base-container {
  width: 100vw;
  height: 100vh;
}

.base-header {
  color: var(--el-color-white);
  background: var(--el-color-primary-dark-2);
  display: flex;
  align-items: center;
  justify-content: space-between;
}


.base-aside {
  width: 200px;
  color: var(--el-text-color-regular);
  background: var(--el-color-white);
}

.base-aside .el-menu-item {
  display: flex;
  align-items: center;
  justify-content: start;
}

.base-main {
  color: var(--el-text-color-primary);
  background: var(--el-color-primary-light-9);
}

</style>

