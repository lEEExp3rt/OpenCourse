import axios from 'axios'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
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
    if (res.code !== '1') {
      alert(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || 'Error'))
    } else {
      return res
    }
  },
  (error) => {
    // 如果服务端有返回信息（如400、500等带响应体）
    if (error.response && error.response.data) {
      const msg = error.response.data.msg || '服务器错误'
      console.error('返回的错误信息:', msg)
      alert(`网络错误，请检查连接\n错误信息：${msg}`)
    } else {
      // 服务器无响应或断网等
      alert('网络错误，请检查连接')
    }
    return Promise.reject(error)
  }
)

export default service
