<script setup>
import { ref, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserModule } from "@/stores/user";
import { useCommonModule } from "@/stores/common.js";
import message from "@/utils/message";

const route = useRoute()
const router = useRouter()
const userModule = useUserModule()
const commonModule = useCommonModule()

const registerLoading = ref(false)
const userRegisterDTO = reactive({
  email: '',
  username: '',
  password: '',
  captcha: '',
})

const captchaLoading = ref(false)
const captchaDTO = reactive({
  email: '',
})

async function handleRegister() {
  try {
    registerLoading.value = true
    const res = await userModule.register(userRegisterDTO)
    if (String(res.code) === '1') {
      router.push('/')
    } else {
      message.error(res.msg || '注册失败')
      registerLoading.value = false
    }
  } catch (error) {
    message.error(error.message || '网络异常，请稍后再试')
    registerLoading.value = false
  }
}

async function handleCaptcha() {
  try {
    captchaLoading.value = true
    captchaDTO.email = userRegisterDTO.email
    const res = await commonModule.captcha(captchaDTO)
    if (String(res.code) === '1') {
      message.success('验证码已发送')
    } else {
      message.error(res.msg || '验证码发送失败，请重试')
      captchaLoading.value = false
    }
  } catch (error) {
    message.error(error.message || '网络异常，请稍后再试')
    captchaLoading.value = false
  }
}

function goLogin() {
  router.push('/login');
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
    <el-text style="color: var(--el-color-white); font-size: 24px"> 注册 OpenCourse </el-text>

    <el-card class="primary-card" style="margin-top: 25px">
      <el-form :model="userRegisterDTO" label-position="top">
        <el-form-item label="邮箱">
          <el-input class="primary-input" v-model="userRegisterDTO.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input class="primary-input" v-model="userRegisterDTO.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input class="primary-input" v-model="userRegisterDTO.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱验证码">
          <el-input class="primary-input" v-model="userRegisterDTO.captcha" placeholder="请输入邮箱验证码">
            <template #append>
              <el-button class="secondary-button" type="primary" :loading="captchaLoading" @click="handleCaptcha"> 获得验证码 </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item style="margin-top: 10px">
          <el-button class="primary-button" type="primary" :loading="registerLoading" @click="handleRegister"> 注册 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="secondary-card" style="margin-top: 16px">
      <el-text class="secondary-text"> 已有账号？ </el-text>
      <el-link class="secondary-link" type="primary" @click="goLogin"> 点此登录 </el-link>
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

.secondary-button {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
