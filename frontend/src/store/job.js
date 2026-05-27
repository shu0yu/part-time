import { defineStore } from 'pinia'
import { jobAPI } from '../api/api'

export const useJobStore = defineStore('job', {
  state: () => ({
    jobs: [],
    currentJob: null,
    loading: false,
    error: null
  }),
  getters: {
    getJobs: (state) => state.jobs,
    getCurrentJob: (state) => state.currentJob,
    getLoading: (state) => state.loading,
    getError: (state) => state.error
  },
  actions: {
    // 获取所有兼职列表
    async fetchJobs(pageSize = 10, currentPage = 1) {
      try {
        this.loading = true
        this.error = null
        const res = await jobAPI.getJobs(pageSize, currentPage)
        this.jobs = res.data?.items || []
        return res
      } catch (error) {
        console.error('获取兼职列表失败:', error)
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },
    // 根据ID获取兼职详情
    async fetchJobById(id) {
      try {
        this.loading = true
        this.error = null
        const res = await jobAPI.getJobById(id)
        this.currentJob = res.data
        return res
      } catch (error) {
        console.error('获取兼职详情失败:', error)
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },
    // 发布兼职
    async addJob(data) {
      try {
        this.loading = true
        this.error = null
        const res = await jobAPI.addJob(data)
        // 重新获取兼职列表
        await this.fetchJobs()
        return res
      } catch (error) {
        console.error('发布兼职失败:', error)
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },
    // 更新兼职
    async updateJob(data) {
      try {
        this.loading = true
        this.error = null
        const res = await jobAPI.updateJob(data)
        // 重新获取兼职列表
        await this.fetchJobs()
        // 如果更新的是当前兼职，也更新当前兼职详情
        if (this.currentJob && this.currentJob.id === data.id) {
          await this.fetchJobById(data.id)
        }
        return res
      } catch (error) {
        console.error('更新兼职失败:', error)
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },
    // 删除兼职
    async deleteJob(id) {
      try {
        this.loading = true
        this.error = null
        const res = await jobAPI.deleteJob(id)
        // 重新获取兼职列表
        await this.fetchJobs()
        // 如果删除的是当前兼职，清除当前兼职详情
        if (this.currentJob && this.currentJob.id === id) {
          this.currentJob = null
        }
        return res
      } catch (error) {
        console.error('删除兼职失败:', error)
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },
    // 清除当前兼职详情
    clearCurrentJob() {
      this.currentJob = null
    }
  }
})
