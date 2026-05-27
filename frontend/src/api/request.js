import axios from 'axios'
import { addCsrfToken, escapeHtml } from '@/utils/security'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: false
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage中获取token
    const token = localStorage.getItem('token')
    // 如果token存在，添加到请求头
    if (token) {
      config.headers['Authorization'] = token
    }
    // 添加CSRF token
    config = addCsrfToken(config)
    return config
  },
  error => {
    // 处理请求错误
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 处理响应数据
    const res = response.data
    // 检查业务状态码
    if (res.code !== 0) {
      // 处理业务错误
      console.error('业务错误:', res.message)
      // 可以根据实际情况添加错误处理逻辑，例如跳转到登录页
      if (res.message && (res.message.includes('token') || res.message.includes('未登录'))) {
        // 清除token并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    // 处理响应错误
    console.error('响应错误:', error)
    // 处理网络错误、超时等
    if (error.message.includes('timeout')) {
      console.error('请求超时，请检查网络连接')
    } else if (error.response) {
      // 处理HTTP错误状态码
      const status = error.response.status
      switch (status) {
        case 401:
          console.error('未授权，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          window.location.href = '/login'
          break
        case 403:
          console.error('拒绝访问')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error(`请求失败，状态码: ${status}`)
      }
    }
    return Promise.reject(error)
  }
)

export default request
