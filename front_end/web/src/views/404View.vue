<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

function goHome() {
  router.push('/');
}
</script>

<template>
  <el-container class="base">
    <el-main class="base-main">
      <el-result icon="error" title="404 Not Found" sub-title="您访问的页面不存在或已被移除">
        <template #extra>
          <el-button type="primary" @click="goHome"> 回到首页 </el-button>
        </template>
      </el-result>
    </el-main>
  </el-container>
</template>

<style scoped>
.base {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--el-color-primary-light-9);
}

.base-main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
