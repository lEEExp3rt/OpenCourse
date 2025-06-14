import { defineStore } from 'pinia'
import UserApi from '@/api/user'
import { getItem, setItem, removeItem } from '@/utils/storage'

export const useUserModule = defineStore('user', {
  state: () => ({
    id: '',
    name: '',
    email: '',
    role: '',
    activity: '',
    createdAt: '',
    updatedAt: '',
    token: getItem('token') || '',
  }),

  actions: {
    setInfo(userVO) {
      this.id = userVO.id
      this.name = userVO.name
      this.email = userVO.email
      this.role = userVO.role
      this.activity = userVO.activity
      this.createdAt = userVO.createdAt
      this.updatedAt = userVO.updatedAt
      setItem('opencourse_username', userVO.name)  // 存储用户名
    },
    resetInfo() {
      this.id = ''
      this.name = ''
      this.email = ''
      this.role = ''
      this.activity = ''
      this.createdAt = ''
      this.updatedAt = ''
    },
    setToken(tokenVO) {
      let str = String(tokenVO.token)
      str = str.replace(/"/g, '');
      this.token = str
      setItem('token', this.token)
    },
    resetToken() {
      this.token = ''
      removeItem('token')
    },

    async info() {
      if (!this.id) {
        throw new Error("登录过期，请重新登录")
      }
      try {
        const response = await UserApi.info(this.id)
        if (response.success === true) {
          this.setInfo(response.data)
        }
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },

    async register(userRegisterDTO) {
      userRegisterDTO.email = userRegisterDTO.email.trim()
      try {
        const response = await UserApi.register({ email: userRegisterDTO.email, password: userRegisterDTO.password, name: userRegisterDTO.name , verificationCode: userRegisterDTO.captcha })
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },

    async login(userLoginDTO) {
      let { email, password } = userLoginDTO
      email = email.trim()
      try {
        const response = await UserApi.login({ email: email, password: password })
        if (response.success === true) {
          this.setInfo(response.data)
          this.setToken(response.data)
        }
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },



    async logout() {
      try {
        const response = await UserApi.logout()
        if (response.success === true) {
          this.resetInfo()
          this.resetToken()
        }
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },
    getName() {
      const storedName = getItem('opencourse_username')
      if (storedName) {
        this.name = storedName
      }
    },

    getUserInfo: async function () {
      try {
        const response = await UserApi.get_me_info()
        if (response.success === true) {
          this.setInfo(response.data)
          return response
        } else {
          throw new Error(response.message || '获取用户信息失败')
        }
      } catch (error) {
        throw new Error(error.message)
      }
    }
    
    
  }
})
