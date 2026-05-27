<template>
  <div class="job-detail">
    <Navbar />
    
    <div class="detail-header">
      <div class="container">
        <button class="back-button" @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </button>
      </div>
    </div>
    
    <el-dialog
      v-model="applyDialogVisible"
      title="申请兼职"
      width="500px"
    >
      <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="100px">
        <el-form-item label="申请备注" prop="applyRemark">
          <el-input
            v-model="applyForm.applyRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入申请备注（可选），如：自我介绍、可用时间等"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmApply" :loading="submitting">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
    
    <div class="container">
      <div class="job-content">
        <div v-if="loading" class="loading">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>
        
        <div v-else-if="error" class="error">
          <el-icon><WarningFilled /></el-icon>
          <span>{{ error }}</span>
        </div>
        
        <div v-else-if="job" class="job-layout">
          <div class="job-main">
            <div class="job-card">
              <div class="job-header">
                <div class="header-top">
                  <div class="job-type-tag">{{ job.jobType || '兼职' }}</div>
                  <div class="job-salary">
                    <span class="salary-value">{{ job.salaryMin }}-{{ job.salaryMax }}</span>
                    <span class="salary-unit">元/时</span>
                  </div>
                </div>
                <h1 class="job-title">{{ job.jobName }}</h1>
                <div class="job-tags">
                  <span class="tag"><el-icon><Location /></el-icon> {{ job.workAddress }}</span>
                  <span class="tag"><el-icon><Clock /></el-icon> {{ job.workTime }}</span>
                  <span class="tag"><el-icon><User /></el-icon> 招聘人数：若干</span>
                </div>
              </div>
              
              <div class="job-section">
                <h3 class="section-title">
                  <el-icon><Document /></el-icon>
                  职位描述
                </h3>
                <div class="section-content">{{ job.jobDesc }}</div>
              </div>
              
              <div class="job-section">
                <h3 class="section-title">
                  <el-icon><Reading /></el-icon>
                  工作要求
                </h3>
                <div class="section-content">{{ job.jobRequire }}</div>
              </div>
            </div>
          </div>
          
          <div class="job-sidebar">
            <div class="action-card">
              <button class="apply-btn" @click="handleApply">
                <el-icon><Position /></el-icon>
                立即申请
              </button>
              <button class="chat-btn" @click="handleChat" v-if="job.companyId">
                <el-icon><ChatDotRound /></el-icon>
                在线沟通
              </button>
              <button class="share-btn" @click="handleShare">
                <el-icon><Share /></el-icon>
                分享职位
              </button>
            </div>
            
            <div class="company-card">
              <h4 class="card-title">职位信息</h4>
              <div class="info-list">
                <div class="info-item" v-if="job.companyName">
                  <el-icon><Briefcase /></el-icon>
                  <div class="info-content">
                    <span class="info-label">公司名称</span>
                    <span class="info-value company-name">{{ job.companyName }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <div class="info-content">
                    <span class="info-label">发布时间</span>
                    <span class="info-value">{{ formatDate(new Date()) }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><Timer /></el-icon>
                  <div class="info-content">
                    <span class="info-label">工作时段</span>
                    <span class="info-value">{{ job.workTime }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <div class="info-content">
                    <span class="info-label">工作地点</span>
                    <span class="info-value">{{ job.workAddress }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><Money /></el-icon>
                  <div class="info-content">
                    <span class="info-label">薪资待遇</span>
                    <span class="info-value salary">{{ job.salaryMin }}-{{ job.salaryMax }}元/时</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else class="no-data">
          <el-icon><InfoFilled /></el-icon>
          <span>兼职信息不存在</span>
        </div>
      </div>
    </div>
    
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useJobStore } from '../store/job'
import { useUserStore } from '../store/user'
import { Loading, WarningFilled, InfoFilled, Position, Share, ChatDotRound, Document, Reading, Location, Clock, User, Calendar, Timer, Money, Briefcase, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
import { applicationAPI } from '../api/api'

const route = useRoute()
const router = useRouter()
const jobStore = useJobStore()
const userStore = useUserStore()

const jobId = computed(() => route.params.id)

const job = computed(() => jobStore.getCurrentJob)
const loading = computed(() => jobStore.getLoading)
const error = computed(() => jobStore.getError)
const isLoggedIn = computed(() => userStore.getIsLoggedIn)

const handleBack = () => {
  router.back()
}

const fetchJobDetail = async () => {
  try {
    await jobStore.fetchJobById(jobId.value)
  } catch (error) {
    console.error('获取兼职详情失败:', error)
    ElMessage.error('获取兼职详情失败')
  }
}

const applyDialogVisible = ref(false)
const applyFormRef = ref(null)
const applyingJobId = ref(null)
const submitting = ref(false)

const applyForm = ref({
  applyRemark: ''
})

const applyRules = {
  applyRemark: [
    { max: 200, message: '备注内容不能超过 200 个字符', trigger: 'blur' }
  ]
}

const handleApply = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  applyingJobId.value = job.value.id
  applyForm.value.applyRemark = ''
  applyDialogVisible.value = true
}

const confirmApply = async () => {
  if (!applyFormRef.value) return
  
  await applyFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        await applicationAPI.applyJob(applyingJobId.value, applyForm.value.applyRemark)
        ElMessage.success('申请成功')
        applyDialogVisible.value = false
      } catch (error) {
        console.error('申请兼职失败:', error)
        ElMessage.error('申请失败，请稍后重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleShare = () => {
  if (navigator.share) {
    navigator.share({
      title: job.value.title,
      text: job.value.description,
      url: window.location.href
    })
  } else {
    navigator.clipboard.writeText(window.location.href)
      .then(() => {
        ElMessage.success('链接已复制到剪贴板')
      })
      .catch(() => {
        ElMessage.error('复制失败')
      })
  }
}

const handleChat = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (job.value && job.value.companyId) {
    router.push(`/chat?userId=${job.value.companyId}&jobId=${job.value.id}`)
  }
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

onMounted(async () => {
  await fetchJobDetail()
})

onUnmounted(() => {
  jobStore.clearCurrentJob()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.job-detail {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: $color-gray-50;
  padding-top: 80px;
}

.detail-header {
  background: white;
  padding: $spacing-6 0;
  box-shadow: $shadow-sm;
  border-bottom: 1px solid $color-gray-100;
  margin-bottom: $spacing-6;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-3 $spacing-5;
  background: transparent;
  border: 1px solid $color-gray-200;
  color: $color-gray-600;
  border-radius: $radius-lg;
  font-size: $font-size-base;
  font-weight: 500;
  cursor: pointer;
  transition: all $transition-base;
  
  &:hover {
    background: $color-gray-50;
    border-color: $color-gray-300;
    color: $color-primary;
  }
}

.job-content {
  flex: 1;
  padding: $spacing-10 0;
}

.job-layout {
  display: flex;
  gap: $spacing-6;
}

.job-main {
  flex: 1;
}

.job-card {
  background: white;
  border-radius: $radius-2xl;
  box-shadow: $shadow-lg;
  overflow: hidden;
}

.job-header {
  padding: $spacing-8;
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.08) 0%, rgba(249, 115, 22, 0.05) 100%);
  border-bottom: 1px solid $color-gray-100;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-4;
}

.job-type-tag {
  background: $gradient-accent;
  color: white;
  padding: 6px 16px;
  border-radius: $radius-full;
  font-size: $font-size-sm;
  font-weight: 600;
}

.job-salary {
  text-align: right;
}

.salary-value {
  font-size: 36px;
  font-weight: 800;
  color: $color-accent;
}

.salary-unit {
  font-size: $font-size-base;
  color: $color-gray-500;
}

.job-title {
  font-size: 32px;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0 0 $spacing-4;
  line-height: 1.3;
  font-family: $font-family-display;
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-3;
}

.job-tags .tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: $font-size-sm;
  color: $color-gray-600;
  background: white;
  padding: $spacing-2 $spacing-3;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
  font-weight: 500;
}

.job-section {
  padding: $spacing-8;
  border-bottom: 1px solid $color-gray-100;
}

.job-section:last-child {
  border-bottom: none;
}

.section-title {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  font-size: $font-size-xl;
  font-weight: 600;
  color: $color-gray-900;
  margin: 0 0 $spacing-4;
}

.section-title .el-icon {
  color: $color-primary;
}

.section-content {
  font-size: $font-size-base;
  color: $color-gray-700;
  line-height: 1.8;
  white-space: pre-wrap;
}

.job-sidebar {
  width: 380px;
  flex-shrink: 0;
}

.action-card {
  background: white;
  border-radius: $radius-2xl;
  padding: $spacing-6;
  box-shadow: $shadow-lg;
  margin-bottom: $spacing-5;
  display: flex;
  flex-direction: column;
  gap: $spacing-3;
}

.apply-btn, .chat-btn, .share-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-2;
  width: 100%;
  padding: $spacing-4;
  border: none;
  border-radius: $radius-xl;
  font-size: $font-size-base;
  font-weight: 600;
  cursor: pointer;
  transition: all $transition-base;
}

.apply-btn {
  background: $gradient-success;
  color: white;
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.3);
}

.apply-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.4);
}

.chat-btn {
  background: $gradient-accent;
  color: white;
  box-shadow: 0 4px 14px rgba(249, 115, 22, 0.3);
}

.chat-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
}

.share-btn {
  background: $color-gray-100;
  color: $color-gray-700;
}

.share-btn:hover {
  background: $color-gray-200;
  color: $color-gray-800;
}

.company-card {
  background: white;
  border-radius: $radius-2xl;
  padding: $spacing-6;
  box-shadow: $shadow-lg;
}

.company-card .card-title {
  font-size: $font-size-lg;
  font-weight: 600;
  color: $color-gray-900;
  margin: 0 0 $spacing-5;
  padding-bottom: $spacing-3;
  border-bottom: 1px solid $color-gray-100;
}

.company-info {
  margin-bottom: $spacing-5;
  padding-bottom: $spacing-5;
  border-bottom: 1px solid $color-gray-100;
}

.company-name {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  font-size: $font-size-lg;
  font-weight: 700;
  color: $color-gray-900;
}

.company-name .el-icon {
  color: $color-primary;
  font-size: 24px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-4;
}

.info-item {
  display: flex;
  align-items: center;
  gap: $spacing-3;
}

.info-item .el-icon {
  width: 40px;
  height: 40px;
  background: $color-gray-100;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $color-primary;
  font-size: 18px;
  flex-shrink: 0;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-label {
  font-size: $font-size-xs;
  color: $color-gray-500;
}

.info-value {
  font-size: $font-size-base;
  color: $color-gray-800;
  font-weight: 500;
}

.info-value.salary {
  color: $color-accent;
  font-weight: 700;
}

.info-value.company-name {
  color: $color-primary;
  font-weight: 700;
  font-size: $font-size-lg;
}

.loading, .error, .no-data {
  text-align: center;
  padding: $spacing-20 0;
  color: $color-gray-600;
  font-size: $font-size-lg;
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
  .job-layout {
    flex-direction: column;
  }
  
  .job-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .job-detail {
    padding-top: 70px;
  }
  
  .job-header {
    padding: $spacing-6;
  }
  
  .job-section {
    padding: $spacing-6;
  }
  
  .job-title {
    font-size: 24px;
  }
  
  .salary-value {
    font-size: 28px;
  }
  
  .job-tags {
    gap: $spacing-2;
  }
  
  .job-tags .tag {
    font-size: $font-size-xs;
  }
}
</style>
