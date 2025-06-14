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
    const disposition = response.headers['content-disposition']
    if (disposition) {
      const match = disposition.match(/filename="?([^"]+)"?/)
      if (match) {
        const filename = decodeURIComponent(match[1])  // 防止中文乱码
        localStorage.setItem('download-filename', filename)
      }
    }
    return res
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
