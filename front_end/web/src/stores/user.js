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
    set(userVO) {
      this.id = userVO.id
      this.name = userVO.name
      this.email = userVO.email
      this.role = userVO.role
      this.activity = userVO.activity
      this.createdAt = userVO.createdAt
      this.updatedAt = userVO.updatedAt
      this.token = userVO.token
      setItem('token', this.token)
    },
    reset() {
      this.id = ''
      this.name = ''
      this.email = ''
      this.role = ''
      this.activity = ''
      this.createdAt = ''
      this.updatedAt = ''
      this.token = ''
      removeItem('token')
    },

    async info() {
      if (!this.id) {
        throw new Error("登录过期，请重新登录")
      }
      try {
        const response = await UserApi.info(this.id)
        if (response.code === '1') {
          this.set(response.data)
        }
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },

    async register(userRegisterDTO) {
      let { email, password } = userRegisterDTO
      email = email.trim()
      try {
        const response = await UserApi.register({ email: email, password: password })
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },

    async login(userLoginDTO) {
      let { email, password } = userLoginDTO
      email = email.trim()
      try {
        const response = await UserApi.login({ email, password })
        if (response.code === '1') {
          this.set(response.data)
        }
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },

    async logout() {
      try {
        const response = await UserApi.logout()
        if (response.code === '1') {
          this.reset()
        }
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },
  }
})
