<script setup>
import { ref, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserModule } from "@/stores/user";
import message from "@/utils/message";

const route = useRoute()
const router = useRouter()
const userModule = useUserModule()

const loginLoading = ref(false)
const userLoginDTO = reactive({
  email: '',
  password: '',
})

async function handleLogin() {
  try {
    loginLoading.value = true
    const res = await userModule.login(userLoginDTO)
    if (res.success === true) {
      message.success('登录成功')
      console.log("登录成功，",res.token)
      router.push('/')
    } else {
      message.error(res.msg || '登录失败')
      loginLoading.value = false
    }
  } catch (error) {
    message.error(error.message || '网络异常，请稍后再试')
    loginLoading.value = false
  }
}

function goRegister() {
  router.push('/register');
}

watch(
  () => route,
  newRoute => {},
  { immediate: true, deep: true },
)
</script>

<template>
  <el-container class="base-container">
    <el-image src="/images/open_course.png" style="width: 100px; height: 100px; margin-right: 10px; filter: invert(100%)"/>
    <el-text style="color: var(--el-color-white); font-size: 24px"> 登录 OpenCourse </el-text>

    <el-card class="primary-card" style="margin-top: 25px">
      <el-form :model="userLoginDTO" label-position="top">
        <el-form-item label="账号">
          <el-input class="primary-input" v-model="userLoginDTO.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input class="primary-input" v-model="userLoginDTO.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item style="margin-top: 10px">
          <el-button class="primary-button" type="primary" :loading="loginLoading" @click="handleLogin"> 登录 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="secondary-card" style="margin-top: 16px">
      <el-text class="secondary-text"> 还没有账号？ </el-text>
      <el-link class="secondary-link" type="primary" @click="goRegister"> 点此注册 </el-link>
    </el-card>
  </el-container>
</template>

<style scoped>
.base-container {
  width: 100vw;
  height: 100vh;
  padding-bottom: 50px;
  background: var(--el-color-primary-dark-2);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.primary-card {
  width: 288px;
  background: var(--el-fill-color-extra-light);
}

.primary-input {
  width: 100%;
}

.primary-button {
  width: 100%;
}

.secondary-card {
  width: 288px;
  background: var(--el-fill-color-blank);
  display: flex;
  justify-content: center;
  align-items: center;
}

.secondary-text {
  height: 22px;
  font-size: 14px;
  display: inline-block;
}

.secondary-link {
  height: 22px;
  font-size: 14px;
  display: inline-block;
}
</style>
