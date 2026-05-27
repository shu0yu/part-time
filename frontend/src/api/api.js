import request from './request'

// 用户相关API
export const userAPI = {
  // 用户登录
  login: (data) => {
    return request.post('/user/login', data)
  },
  // 获取当前登录用户信息
  getCurrentUser: () => {
    return request.get('/user/currentUser')
  },
  // 获取用户角色
  getUserRoles: () => {
    return request.get('/user/roles')
  },
  // 用户注册
  register: (data) => {
    return request.post('/user/register', data)
  }
}

// 兼职相关API
export const jobAPI = {
  // 获取所有已发布的兼职信息
  getJobs: (pageSize = 10, currentPage = 1) => {
    return request.get('/job/list', { params: { pageSize, currentPage } })
  },
  // 根据ID获取兼职详情
  getJobById: (id) => {
    return request.get(`/job/get/${id}`)
  },
  // 发布兼职信息
  addJob: (data) => {
    return request.post('/job/add', data)
  },
  // 更新兼职信息
  updateJob: (data) => {
    return request.put('/job/update', data)
  },
  // 删除兼职信息
  deleteJob: (id) => {
    return request.delete(`/job/delete/${id}`)
  },
  // 获取当前企业发布的兼职信息
  getMyJobs: (pageSize = 10, currentPage = 1) => {
    return request.get('/job/myJobs', { params: { pageSize, currentPage } })
  },
  // 多条件搜索兼职信息
  searchJobs: (params) => {
    return request.get('/job/search', { params })
  }
}

// 企业相关API
export const companyAPI = {
  // 获取企业信息
  getCompanyInfo: (id) => {
    return request.get(`/company/get/${id}`)
  },
  // 更新企业信息
  updateCompanyInfo: (data) => {
    return request.put('/company/update', data)
  }
}

// 申请相关 API
export const applicationAPI = {
  // 申请兼职
  applyJob: (jobId, applyRemark = '') => {
    return request.post(`/application/apply/${jobId}`, null, {
      params: { applyRemark }
    })
  },
  // 获取用户的申请记录
  getUserApplications: (pageSize = 10, currentPage = 1) => {
    return request.get('/application/myApplications', { params: { pageSize, currentPage } })
  },
  // 获取企业收到的申请
  getCompanyApplications: (jobId, pageSize = 10, currentPage = 1) => {
    return request.get(`/application/jobApplications/${jobId}`, { params: { pageSize, currentPage } })
  },
  // 获取企业收到的申请（包含学生信息）
  getCompanyApplicationsWithStudent: (jobId, pageSize = 10, currentPage = 1) => {
    return request.get(`/application/jobApplicationsWithStudent/${jobId}`, { params: { pageSize, currentPage } })
  },
  // 更新申请状态
  updateApplicationStatus: (id, status) => {
    return request.put(`/application/updateStatus/${id}/${status}`)
  },
  // 取消申请
  cancelApplication: (applicationId) => {
    return request.delete(`/application/cancel/${applicationId}`)
  }
}

// 聊天相关 API
export const chatAPI = {
  // 发送消息
  sendMessage: (data) => {
    return request.post('/chat/send', data)
  },
  // 获取聊天历史
  getChatHistory: (otherUserId, jobId = null) => {
    return request.get('/chat/history', { params: { otherUserId, jobId } })
  },
  // 获取聊天会话列表
  getChatSessions: () => {
    return request.get('/chat/sessions')
  },
  // 标记消息已读
  markAsRead: (otherUserId) => {
    return request.post('/chat/markAsRead', null, { params: { otherUserId } })
  }
}

// 管理员相关 API
export const adminAPI = {
  // 获取所有用户列表
  getAllUsers: (pageSize, currentPage, roleName, status) => {
    return request.get('/admin/users', { params: { pageSize, currentPage, roleName, status } })
  },
  // 更新用户状态（禁用/启用）
  updateUserStatus: (userId, status) => {
    return request.put(`/admin/user/${userId}/status`, null, { params: { status } })
  },
  // 获取所有岗位列表
  getAllJobs: (pageSize, currentPage, workAddress, companyId) => {
    return request.get('/admin/jobs', { params: { pageSize, currentPage, workAddress, companyId } })
  },
  // 下架岗位
  takeDownJob: (jobId) => {
    return request.put(`/admin/job/${jobId}/takeDown`)
  },
  // 获取纠纷信息
  getDisputeInfo: (studentId, companyId, jobId) => {
    return request.get('/admin/dispute', { params: { studentId, companyId, jobId } })
  },
  // 获取所有聊天会话
  getAllChatSessions: () => {
    return request.get('/admin/chat/sessions')
  },
  // 获取聊天历史（管理员专用）
  getChatHistoryForAdmin: (userId1, userId2, jobId) => {
    return request.get('/admin/chat/history', { params: { userId1, userId2, jobId } })
  }
}
