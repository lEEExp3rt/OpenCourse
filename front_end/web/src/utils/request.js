import axios from 'axios'

const service = axios.create({
  baseURL: "/api",
  timeout: 5000
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    return res
  },
  (error) => {
    console.error('请求异常:', error)
    alert('网络错误，请检查连接')
    return Promise.reject(error)
  }
)

export default service
