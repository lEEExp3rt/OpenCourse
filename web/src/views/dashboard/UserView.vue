<script setup>

import { onMounted, ref, reactive, useId } from 'vue'
import { useUserModule } from '@/stores/user'
import {useCommonModule} from '@/stores/common.js'
const UserStore = useUserModule()

const fetchUserInfo = async () => {
  try {
    await UserStore.getUserInfo()
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}
onMounted(() => {
  fetchUserInfo()
})

const handleLogout = async () => {
  try {
    await UserStore.logout()
    window.location.href = '/login'
  } catch (error) {
    console.error('注销失败:', error)
  }
}
</script>

<template>
  <div>
    <el-descriptions
      title="用户信息"
      direction="vertical"
      border
      style="margin-top: 20px"
    >
      <el-descriptions-item
        :rowspan="2"
        :width="140"
        label="Photo"
        align="center"
      >
        <el-image
          style="width: 100px; height: 100px"
          src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
        />
      </el-descriptions-item>
      <el-descriptions-item label="用户名">{{UserStore.name}}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{UserStore.email}}</el-descriptions-item>
      <el-descriptions-item label="活跃度">{{UserStore.activity}}</el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag size="small">{{UserStore.role}}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间" span="3">
        {{ UserStore.createdAt ? '创建于' + useCommonModule().formatDate(UserStore.createdAt) : '' }}
      </el-descriptions-item>
        <el-descriptions-item label="更新时间" v-if="UserStore.updatedAt" span="3">
        {{ UserStore.updatedAt ? '更新于' + useCommonModule().formatDate(UserStore.updatedAt) : '' }}
      </el-descriptions-item>
    </el-descriptions>
      <!-- 注销按钮 -->
    <div style="margin-top: 20px; text-align: center;">
      <el-button type="danger" @click="handleLogout">注销</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>
