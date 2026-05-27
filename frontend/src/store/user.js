import { defineStore } from 'pinia'
import { userAPI } from '../api/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    token: localStorage.getItem('token') || '',
    isLoggedIn: !!localStorage.getItem('token')
  }),
  getters: {
    getUserInfo: (state) => state.userInfo,
    getToken: (state) => state.token,
    getIsLoggedIn: (state) => state.isLoggedIn
  },
  actions: {
    // 登录
    async login(data) {
      try {
        // 映射字段：将loginName转换为username
        const loginData = {
          username: data.loginName,
          password: data.password
        }
        const res = await userAPI.login(loginData)
        this.token = res.data
        localStorage.setItem('token', res.data)
        this.isLoggedIn = true
        // 获取用户信息
        await this.getCurrentUser()
        return res
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },
    // 获取当前用户信息
    async getCurrentUser() {
      try {
        const res = await userAPI.getCurrentUser()
        this.userInfo = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        return res
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    },
    // 注册
    async register(data) {
      try {
        const res = await userAPI.register(data)
        return res
      } catch (error) {
        console.error('注册失败:', error)
        throw error
      }
    },
    // 登出
    logout() {
      this.userInfo = null
      this.token = ''
      this.isLoggedIn = false
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
