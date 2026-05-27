<template>
  <div class="profile">
    <Navbar />
    
    <section class="profile-section">
      <div class="container">
        <div class="profile-info">
          <div class="profile-info-wrapper">
            <el-avatar :size="100" :style="{ background: avatarGradient }" class="profile-avatar">
              {{ userInfo?.realName?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="profile-detail">
              <h1 class="profile-name">{{ userInfo?.realName }}</h1>
              <div class="profile-tags">
                <span class="tag" :class="isAdminRole ? 'admin' : (isCompanyRole ? 'company' : 'student')">
                  {{ isAdminRole ? '管理员' : (isCompanyRole ? '企业用户' : '学生用户') }}
                </span>
                <span class="tag status" :class="userInfo?.status === 1 ? 'active' : 'inactive'">
                  {{ userInfo?.status === 1 ? '正常' : '禁用' }}
                </span>
              </div>
              <div class="profile-account-info">
                <div class="account-item">
                  <el-icon><User /></el-icon>
                  <span class="account-label">账号：</span>
                  <span class="account-value">{{ userInfo?.username }}</span>
                </div>
                <div class="account-item">
                  <el-icon><Phone /></el-icon>
                  <span class="account-label">电话：</span>
                  <span class="account-value">{{ userInfo?.phone }}</span>
                </div>
              </div>
            </div>
          </div>
          <el-button type="primary" class="edit-btn" @click="handleEditProfile">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
        
        <div class="profile-content">
          <div class="content-main">
            <div v-if="loading" class="loading">
              <div class="loading-spinner"></div>
              <span>加载中...</span>
            </div>
            
            <div v-else-if="error" class="error">
              <el-icon><WarningFilled /></el-icon>
              <span>{{ error }}</span>
            </div>
            
            <div v-else class="main-content">
              <div v-if="isCompanyRole" class="company-section">
                <div class="section-card">
                  <div class="card-header">
                    <div class="header-info">
                      <h3 class="card-title">我的岗位</h3>
                      <p class="card-subtitle">管理您发布的兼职岗位</p>
                    </div>
                    <el-button type="primary" @click="handlePublishJob">
                      <el-icon><Plus /></el-icon>
                      发布岗位
                    </el-button>
                  </div>
                  <div class="card-body">
                    <div v-if="companyJobs.length > 0" class="jobs-grid">
                      <div v-for="job in companyJobs" :key="job.id" class="job-item">
                        <div class="job-header">
                          <h4 class="job-title">{{ job.jobName }}</h4>
                          <span class="job-salary">{{ job.salaryMin }}-{{ job.salaryMax }}元/时</span>
                        </div>
                        <div class="job-info">
                          <span><el-icon><Location /></el-icon> {{ job.workAddress }}</span>
                          <span><el-icon><Clock /></el-icon> {{ job.workTime }}</span>
                        </div>
                        <div class="job-actions">
                          <el-button size="small" class="view-detail-btn-company" @click="handleViewJob(job.id)">
                            <el-icon><Document /></el-icon>
                            查看详情
                          </el-button>
                          <el-button size="small" type="primary" class="view-applications-btn" @click="handleViewApplications(job.id)">
                            <el-icon><ChatDotRound /></el-icon>
                            查看申请
                          </el-button>
                          <el-button size="small" type="danger" class="delete-job-btn" @click="handleDeleteJob(job)">
                            <el-icon><Delete /></el-icon>
                            删除岗位
                          </el-button>
                        </div>
                      </div>
                    </div>
                    <div v-else class="empty-state">
                      <el-icon><Briefcase /></el-icon>
                      <p>暂无发布的岗位</p>
                      <el-button type="primary" @click="handlePublishJob">发布第一个岗位</el-button>
                    </div>
                    
                    <div class="pagination-wrapper">
                      <el-pagination
                        v-model:current-page="currentPage"
                        v-model:page-size="pageSize"
                        :page-sizes="[12, 24, 36]"
                        :total="total"
                        layout="total, sizes, prev, pager, next"
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                      />
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else class="student-section">
                <div class="section-card">
                  <div class="card-header">
                    <div class="header-info">
                      <h3 class="card-title">我的申请</h3>
                      <p class="card-subtitle">查看您的兼职申请记录</p>
                    </div>
                  </div>
                  <div class="card-body">
                    <div v-if="applicationDetails.length > 0" class="applications-list">
                      <div v-for="app in applicationDetails" :key="app.id" class="application-item">
                        <div class="app-info">
                          <h4 class="app-job-title">{{ app.job?.jobName || '兼职信息加载失败' }}</h4>
                          <div class="app-meta">
                            <span><el-icon><Money /></el-icon> {{ app.job?.salaryMin }}-{{ app.job?.salaryMax }}元/时</span>
                            <span><el-icon><Location /></el-icon> {{ app.job?.workAddress }}</span>
                            <span><el-icon><Clock /></el-icon> {{ app.job?.workTime }}</span>
                          </div>
                          <p class="app-remark" v-if="app.applyRemark">备注：{{ app.applyRemark }}</p>
                        </div>
                        <div class="app-status">
                          <el-button type="primary" size="small" class="view-detail-btn" @click="handleViewJob(app.jobId)">
                            <el-icon><Document /></el-icon>
                            查看详情
                          </el-button>
                          <el-button size="small" type="danger" class="cancel-application-btn" @click="handleCancelApplication(app)">
                            <el-icon><Close /></el-icon>
                            取消申请
                          </el-button>
                        </div>
                      </div>
                    </div>
                    <div v-else class="empty-state">
                      <el-icon><Document /></el-icon>
                      <p>暂无申请记录</p>
                      <el-button type="primary" @click="$router.push('/jobs')">浏览兼职</el-button>
                    </div>
                    
                    <div class="pagination-wrapper">
                      <el-pagination
                        v-model:current-page="currentPageStudent"
                        v-model:page-size="pageSizeStudent"
                        :page-sizes="[5, 10, 15, 20]"
                        :total="totalStudent"
                        layout="total, sizes, prev, pager, next, jumper"
                        @size-change="handleSizeChangeStudent"
                        @current-change="handleCurrentChangeStudent"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <Footer />
    
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="500px"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveEdit">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <el-dialog
      v-model="applicationsDialogVisible"
      title="申请学生信息"
      width="950px"
    >
      <el-table :data="applications" style="width: 100%">
        <el-table-column label="申请 ID" width="80">
          <template #default="scope">
            {{ scope.row.id }}
          </template>
        </el-table-column>
        <el-table-column label="学生姓名" width="100">
          <template #default="scope">
            {{ scope.row.studentRealName || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="学号/账号" width="150">
          <template #default="scope">
            {{ scope.row.studentUsername || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="150">
          <template #default="scope">
            {{ scope.row.studentPhone || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="申请备注" min-width="200">
          <template #default="scope">
            {{ scope.row.applyRemark || '无' }}
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="scope">
            {{ formatDate(new Date()) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              :loading="startingChat[scope.row.studentId]"
              :disabled="startingChat[scope.row.studentId]"
              class="start-chat-btn"
              @click="handleStartChat(scope.row)"
            >
              <el-icon><Message /></el-icon>
              {{ startingChat[scope.row.studentId] ? '连接中...' : '开启聊天' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="applications.length === 0" class="empty-state small">
        <el-icon><InfoFilled /></el-icon>
        <span>暂无申请记录</span>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applicationsDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { Loading, WarningFilled, InfoFilled, Edit, User, Phone, Postcard, HomeFilled, Search, ChatDotRound, Plus, Location, Clock, Briefcase, Money, Document, Delete, Close, Message } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
import { applicationAPI } from '../api/api'
import { jobAPI } from '../api/api'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const error = ref('')
const editDialogVisible = ref(false)
const editFormRef = ref(null)

const userInfo = computed(() => userStore.userInfo)

const avatarGradient = computed(() => {
  const gradients = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  ]
  const index = (userInfo.value?.username?.charCodeAt(0) || 0) % gradients.length
  return gradients[index]
})

const isCompanyRole = computed(() => {
  if (!userInfo.value) return false
  const roles = userInfo.value.roles
  if (roles) {
    if (Array.isArray(roles)) {
      return roles.some(role => role === 'COMPANY')
    } else if (typeof roles === 'string') {
      return roles.includes('COMPANY')
    }
  }
  const username = userInfo.value.username
  if (username && username.startsWith('company')) {
    return true
  }
  return false
})

const isAdminRole = computed(() => {
  if (!userInfo.value) return false
  const roles = userInfo.value.roles
  if (roles) {
    if (Array.isArray(roles)) {
      return roles.some(role => role === 'ADMIN')
    } else if (typeof roles === 'string') {
      return roles.includes('ADMIN')
    }
  }
  const username = userInfo.value.username
  if (username && username.startsWith('admin')) {
    return true
  }
  return false
})

const applications = ref([])
const applicationDetails = ref([])
const companyJobs = ref([])
const applicationsDialogVisible = ref(false)
const currentJobId = ref(null)
const startingChat = ref({})

const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const currentPageStudent = ref(1)
const pageSizeStudent = ref(5)
const totalStudent = ref(0)
const totalPagesStudent = computed(() => Math.ceil(totalStudent.value / pageSizeStudent.value))

const editForm = ref({
  realName: '',
  phone: ''
})

const editRules = {
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ]
}

const fetchUserInfo = async () => {
  try {
    loading.value = true
    await userStore.getCurrentUser()
    await userStore.getCurrentUser()
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

const fetchApplications = async (page = currentPageStudent.value, size = pageSizeStudent.value) => {
  try {
    loading.value = true
    const res = await applicationAPI.getUserApplications(size, page)
    const applications = res.data.items || []
    
    // 为每条申请记录获取岗位信息
    const applicationsWithJobInfo = await Promise.all(
      applications.map(async (app) => {
        try {
          if (app.jobId) {
            const jobRes = await jobAPI.getJobById(app.jobId)
            return {
              ...app,
              job: jobRes.data
            }
          }
          return app
        } catch (error) {
          console.error(`获取岗位信息失败 (ID: ${app.jobId}):`, error)
          return app
        }
      })
    )
    
    applicationDetails.value = applicationsWithJobInfo
    totalStudent.value = Number(res.data.total) || 0
  } catch (error) {
    console.error('获取申请记录失败:', error)
    ElMessage.error('获取申请记录失败')
  } finally {
    loading.value = false
  }
}

const fetchCompanyJobs = async (page = currentPage.value, size = pageSize.value) => {
  try {
    loading.value = true
    const res = await jobAPI.getMyJobs(size, page)
    companyJobs.value = res.data.items || []
    total.value = Number(res.data.total) || 0
  } catch (error) {
    console.error('获取岗位列表失败:', error)
    ElMessage.error('获取岗位列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCompanyJobs(1, val)
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCompanyJobs(val, pageSize.value)
}

const handleSizeChangeStudent = (val) => {
  pageSizeStudent.value = val
  fetchApplications(1, val)
}

const handleCurrentChangeStudent = (val) => {
  currentPageStudent.value = val
  fetchApplications(val, pageSizeStudent.value)
}

const handleEditProfile = () => {
  editForm.value = {
    realName: userInfo.value.realName,
    phone: userInfo.value.phone
  }
  editDialogVisible.value = true
}

const handleSaveEdit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        ElMessage.success('更新成功')
        editDialogVisible.value = false
        await fetchUserInfo()
      } catch (error) {
        console.error('更新用户信息失败:', error)
        ElMessage.error('更新失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleViewJob = (jobId) => {
  router.push(`/job/${jobId}`)
}

const handleViewApplications = async (jobId) => {
  try {
    loading.value = true
    currentJobId.value = jobId
    const res = await applicationAPI.getCompanyApplicationsWithStudent(jobId)
    applications.value = res.data.items
    applicationsDialogVisible.value = true
  } catch (error) {
    console.error('获取申请记录失败:', error)
    ElMessage.error('获取申请记录失败')
  } finally {
    loading.value = false
  }
}

/**
 * 开启与学生的聊天
 * @param {Object} application - 申请记录对象，包含学生信息
 */
const handleStartChat = async (application) => {
  if (!application || !application.studentId) {
    ElMessage.error('学生信息不完整，无法开启聊天')
    return
  }
  
  // 设置按钮加载状态
  startingChat.value = {
    ...startingChat.value,
    [application.studentId]: true
  }
  
  try {
    // 模拟一些连接延迟，让用户看到加载状态
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 关闭当前对话框
    applicationsDialogVisible.value = false
    
    // 跳转到聊天页面，传递学生ID和岗位ID
    router.push({
      path: '/chat',
      query: {
        userId: application.studentId,
        userName: application.studentRealName || '学生',
        jobId: currentJobId.value
      }
    })
    
    ElMessage.success('已开启聊天通道')
  } catch (error) {
    console.error('开启聊天失败:', error)
    ElMessage.error('开启聊天失败，请稍后重试')
  } finally {
    // 清除加载状态
    startingChat.value = {
      ...startingChat.value,
      [application.studentId]: false
    }
  }
}

const handlePublishJob = () => {
  router.push('/job/publish')
}

const handleDeleteJob = async (job) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除岗位"${job.jobName}"吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    try {
      await jobAPI.deleteJob(job.id)
      ElMessage.success('岗位删除成功')
      await fetchCompanyJobs(currentPage.value, pageSize.value)
    } catch (error) {
      console.error('删除岗位失败:', error)
      ElMessage.error('删除岗位失败，请稍后重试')
    }
  } catch {
  }
}

const handleCancelApplication = async (app) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消对"${app.job?.jobName || '该岗位'}"的申请吗？`,
      '取消申请确认',
      {
        confirmButtonText: '确认取消',
        cancelButtonText: '再考虑一下',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    try {
      await applicationAPI.cancelApplication(app.id)
      ElMessage.success('申请取消成功')
      await fetchApplications(currentPageStudent.value, pageSizeStudent.value)
    } catch (error) {
      console.error('取消申请失败:', error)
      ElMessage.error('取消申请失败，请稍后重试')
    }
  } catch {
  }
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  await fetchUserInfo()
  
  if (isCompanyRole.value) {
    await fetchCompanyJobs()
  } else {
    await fetchApplications()
  }
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.profile {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: $color-gray-50;
  padding-top: 80px;
}

.profile-section {
  flex: 1;
  padding-bottom: $spacing-10;
}

.profile-info {
  position: relative;
  max-width: 1280px;
  margin: $spacing-6 auto;
  padding: $spacing-8 $spacing-6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-6;
  background: white;
  border-radius: $radius-2xl;
  box-shadow: $shadow-lg;
}

.profile-info-wrapper {
  display: flex;
  align-items: center;
  gap: $spacing-6;
}

.profile-avatar {
  border: 6px solid white;
  box-shadow: 0 8px 24px rgba(14, 165, 233, 0.2);
  font-size: 42px;
  font-weight: 700;
}

.profile-detail {
  display: flex;
  flex-direction: column;
  gap: $spacing-3;
}

.profile-name {
  font-size: 32px;
  font-weight: 800;
  color: $color-gray-900;
  margin: 0;
  font-family: $font-family-display;
}

.profile-tags {
  display: flex;
  gap: $spacing-2;
}

.profile-account-info {
  display: flex;
  gap: $spacing-6;
  margin-top: $spacing-2;
}

.account-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: $font-size-sm;
  color: $color-gray-600;
  
  .el-icon {
    color: $color-primary;
    font-size: 16px;
  }
}

.account-label {
  font-weight: 500;
  color: $color-gray-500;
}

.account-value {
  font-weight: 600;
  color: $color-gray-700;
}

.tag {
  padding: $spacing-1 $spacing-3;
  border-radius: $radius-full;
  font-size: $font-size-xs;
  font-weight: 600;
}

.tag.company {
  background: rgba(14, 165, 233, 0.12);
  color: $color-primary;
}

.tag.student {
  background: rgba(249, 115, 22, 0.12);
  color: $color-accent;
}

.tag.admin {
  background: rgba(124, 58, 237, 0.12);
  color: #7c3aed;
}

.tag.status.active {
  background: rgba(16, 185, 129, 0.12);
  color: $color-success;
}

.tag.status.inactive {
  background: rgba(239, 68, 68, 0.12);
  color: $color-error;
}

.edit-btn {
  background: $gradient-accent;
  border: none;
  box-shadow: 0 4px 14px rgba(249, 115, 22, 0.3);
  padding: 14px 32px;
  font-size: $font-size-base;
  font-weight: 600;
  height: 52px;
  
  &:hover {
    box-shadow: 0 6px 20px rgba(249, 115, 22, 0.4);
  }
}

.profile-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 $spacing-6;
}

.content-main {
  width: 100%;
}

.section-card {
  background: white;
  border-radius: $radius-2xl;
  box-shadow: $shadow-md;
  overflow: hidden;
}

.section-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-6;
  border-bottom: 1px solid $color-gray-100;
}

.header-info .card-title {
  font-size: $font-size-xl;
  margin: 0 0 $spacing-1;
  padding: 0;
  border: none;
}

.card-subtitle {
  font-size: $font-size-sm;
  color: $color-gray-500;
  margin: 0;
}

.card-body {
  padding: $spacing-6;
}

.jobs-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.job-item {
  background: $color-gray-50;
  border-radius: $radius-xl;
  padding: $spacing-5;
  transition: all $transition-base;
  border: 1px solid transparent;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.job-item:hover {
  background: white;
  box-shadow: $shadow-lg;
  border-color: $color-gray-100;
  transform: translateY(-2px);
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $spacing-3;
}

.job-title {
  font-size: $font-size-lg;
  font-weight: 600;
  color: $color-gray-900;
  margin: 0;
  flex: 1;
  line-height: 1.4;
}

.job-salary {
  font-size: $font-size-lg;
  font-weight: 700;
  color: $color-accent;
  white-space: nowrap;
  margin-left: $spacing-2;
}

.job-info {
  display: flex;
  flex-direction: column;
  gap: $spacing-2;
  margin-bottom: $spacing-4;
  flex: 1;
  font-size: $font-size-sm;
  color: $color-gray-600;
}

.job-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.job-actions {
  display: flex;
  gap: $spacing-2;
  margin-top: auto;
  padding-top: $spacing-3;
  border-top: 1px dashed $color-gray-200;
}

.job-actions .el-button {
  flex: 1;
  min-width: 0;
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-4;
}

.application-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-5;
  background: $color-gray-50;
  border-radius: $radius-xl;
  transition: all $transition-base;
  border: 1px solid transparent;
}

.application-item:hover {
  background: white;
  box-shadow: $shadow-md;
  border-color: $color-gray-100;
  transform: translateX(4px);
}

.app-info {
  flex: 1;
}

.app-job-title {
  font-size: $font-size-lg;
  font-weight: 600;
  color: $color-gray-900;
  margin: 0 0 $spacing-2;
}

.app-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-4;
  font-size: $font-size-sm;
  color: $color-gray-600;
  margin-bottom: $spacing-2;
}

.app-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.app-remark {
  font-size: $font-size-sm;
  color: $color-gray-500;
  margin: 0;
}

.app-status {
  display: flex;
  flex-direction: row;
  gap: $spacing-2;
}

.view-detail-btn {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 600;
  height: 36px;
  background: $gradient-primary;
  border: none;
  box-shadow: 0 3px 12px rgba(14, 165, 233, 0.25);
  transition: all $transition-base;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 5px 16px rgba(14, 165, 233, 0.35);
  }
}

.cancel-application-btn {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 600;
  height: 36px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border: none;
  box-shadow: 0 3px 12px rgba(239, 68, 68, 0.25);
  transition: all $transition-base;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 5px 16px rgba(239, 68, 68, 0.35);
  }
}

.view-detail-btn-company, .view-applications-btn, .delete-job-btn {
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 600;
  height: 36px;
  border-radius: $radius-md;
  transition: all $transition-base;
  
  &:hover {
    transform: translateY(-1px);
  }
}

.view-detail-btn-company {
  background: $color-gray-100;
  color: $color-gray-700;
  border: 2px solid $color-gray-200;
  
  &:hover {
    background: $color-gray-200;
    color: $color-gray-900;
    border-color: $color-gray-300;
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  }
}

.view-applications-btn {
  background: $gradient-accent;
  border: none;
  box-shadow: 0 3px 12px rgba(249, 115, 22, 0.25);
  
  &:hover {
    box-shadow: 0 5px 16px rgba(249, 115, 22, 0.35);
  }
}

.delete-job-btn {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border: none;
  box-shadow: 0 3px 12px rgba(239, 68, 68, 0.25);
  
  &:hover {
    box-shadow: 0 5px 16px rgba(239, 68, 68, 0.35);
  }
}

.start-chat-btn {
  background: $gradient-accent;
  border: none;
  box-shadow: 0 2px 8px rgba(249, 115, 22, 0.2);
  transition: all $transition-base;
  
  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
  }
  
  &:disabled {
    background: $color-gray-300;
    box-shadow: none;
    cursor: not-allowed;
  }
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.empty-state {
  text-align: center;
  padding: $spacing-16 $spacing-5;
}

.empty-state .el-icon {
  font-size: 64px;
  color: $color-gray-300;
  margin-bottom: $spacing-4;
}

.empty-state p {
  font-size: $font-size-base;
  color: $color-gray-500;
  margin: 0 0 $spacing-5;
}

.empty-state.small {
  padding: $spacing-8 $spacing-5;
}

.empty-state.small .el-icon {
  font-size: 44px;
}

.loading, .error {
  text-align: center;
  padding: $spacing-20 0;
  color: $color-gray-600;
  font-size: $font-size-base;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-4;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid $color-gray-200;
  border-top-color: $color-primary;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1024px) {
  .profile-info {
    flex-direction: column;
    align-items: stretch;
  }
  
  .profile-info-wrapper {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .profile-account-info {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .profile-tags {
    justify-content: center;
  }
  
  .edit-btn {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .profile {
    padding-top: 70px;
  }
  
  .profile-info {
    padding: $spacing-6 $spacing-4;
  }
  
  .profile-name {
    font-size: 24px;
  }
  
  .profile-account-info {
    flex-direction: column;
    gap: $spacing-3;
  }
  
  .jobs-grid {
    grid-template-columns: 1fr;
  }
  
  .application-item {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-4;
  }
  
  .app-status {
    align-items: flex-start;
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
  }
}
</style>
