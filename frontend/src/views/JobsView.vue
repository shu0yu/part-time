<template>
  <div class="jobs-page">
    <Navbar />
    
    <!-- Page Header -->
    <section class="page-header">
      <div class="container">
        <div class="header-content">
          <h1 class="page-title">兼职岗位</h1>
          <p class="page-subtitle">发现适合你的理想兼职</p>
        </div>
      </div>
    </section>
    
    <!-- Filter Section -->
    <section class="filter-section">
      <div class="container">
        <div class="filter-card">
          <div class="filter-grid">
            <div class="filter-item">
              <label class="filter-label">
                <el-icon><Search /></el-icon>
                关键词
              </label>
              <el-input 
                v-model="searchParams.keyword" 
                placeholder="搜索兼职岗位"
                class="filter-input"
                @input="handleInput"
              />
            </div>
            
            <div class="filter-item">
              <label class="filter-label">
                <el-icon><List /></el-icon>
                岗位类型
              </label>
              <el-select v-model="searchParams.jobType" placeholder="全部" class="filter-select">
                <el-option label="全部" value="" />
                <el-option label="技术开发" value="技术开发" />
                <el-option label="产品运营" value="产品运营" />
                <el-option label="设计创意" value="设计创意" />
                <el-option label="市场销售" value="市场销售" />
                <el-option label="行政人事" value="行政人事" />
                <el-option label="财务金融" value="财务金融" />
                <el-option label="教育培训" value="教育培训" />
                <el-option label="实习兼职" value="实习兼职" />
              </el-select>
            </div>
            
            <div class="filter-item">
              <label class="filter-label">
                <el-icon><Location /></el-icon>
                工作地点
              </label>
              <el-input 
                v-model="searchParams.workAddress" 
                placeholder="输入工作地点" 
                class="filter-input"
              />
            </div>
            
            <div class="filter-item">
              <label class="filter-label">
                <el-icon><Money /></el-icon>
                薪资范围
              </label>
              <div class="salary-range">
                <el-input 
                  v-model.number="searchParams.salaryMin" 
                  type="number" 
                  min="0" 
                  step="5" 
                  placeholder="最低"
                  class="salary-input"
                />
                <span class="salary-separator">-</span>
                <el-input 
                  v-model.number="searchParams.salaryMax" 
                  type="number" 
                  min="0" 
                  step="5" 
                  placeholder="最高"
                  class="salary-input"
                />
              </div>
            </div>
          </div>
          
          <div class="filter-actions">
            <el-button type="primary" @click="handleSearch" class="filter-button">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetFilter" class="reset-button">
              重置
            </el-button>
          </div>
        </div>
      </div>
    </section>
    
    <!-- Jobs Section -->
    <section class="jobs-section">
      <div class="container">
        <div class="section-header">
          <div class="header-info">
            <span class="jobs-count">共 {{ total }} 个岗位</span>
          </div>
          
          <div class="sort-buttons">
            <button 
              class="sort-button" 
              :class="{ active: sortBy === 'publishTime' }"
              @click="sortBy = 'publishTime'"
            >
              <el-icon><Clock /></el-icon>
              最新
            </button>
            <button 
              class="sort-button" 
              :class="{ active: sortBy === 'salary' }"
              @click="sortBy = 'salary'"
            >
              <el-icon><TrendCharts /></el-icon>
              高薪
            </button>
          </div>
        </div>
        
        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>
        
        <!-- Error State -->
        <div v-else-if="error" class="error-state">
          <el-icon><WarningFilled /></el-icon>
          <span>{{ error }}</span>
        </div>
        
        <!-- Jobs Grid -->
        <div v-else class="jobs-grid">
          <JobCard 
            v-for="(job, index) in sortedJobs" 
            :key="job.id" 
            :job="job"
            :index="index"
            @click="navigateToJobDetail(job.id)"
            @apply="handleApply"
          />
        </div>
        
        <!-- Empty State -->
        <div v-if="!loading && !error && sortedJobs.length === 0" class="empty-state">
          <el-icon><InfoFilled /></el-icon>
          <span>暂无兼职信息</span>
        </div>
        
        <!-- Pagination -->
        <div v-if="!loading && !error && sortedJobs.length > 0" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="searchParams.currentPage"
            v-model:page-size="searchParams.pageSize"
            :page-sizes="[9, 18, 27]"
            layout="total, sizes, prev, pager, next"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </section>
    
    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { jobAPI } from '../api/api'
import { 
  Loading, WarningFilled, InfoFilled, Search, 
  List, Location, Money, Clock, TrendCharts, Briefcase 
} from '@element-plus/icons-vue'
import Navbar from '../components/Navbar.vue'
import JobCard from '../components/JobCard.vue'
import Footer from '../components/Footer.vue'

const router = useRouter()

const searchParams = ref({
  keyword: '',
  jobType: '',
  workAddress: '',
  salaryMin: null,
  salaryMax: null,
  pageSize: 9,
  currentPage: 1
})

const sortBy = ref('publishTime')
const jobs = ref([])
const total = ref(0)
const loading = ref(false)
const error = ref(null)
let debounceTimer = null

/**
 * 排序后的工作列表
 */
const sortedJobs = computed(() => {
  if (!jobs.value || jobs.value.length === 0) return []
  
  return [...jobs.value].sort((a, b) => {
    if (sortBy.value === 'salary') {
      const salaryA = (a.salaryMin + a.salaryMax) / 2
      const salaryB = (b.salaryMin + b.salaryMax) / 2
      return salaryB - salaryA
    }
    return 0
  })
})

/**
 * 获取工作列表
 */
const fetchJobs = async (params) => {
  try {
    loading.value = true
    error.value = null
    const res = await jobAPI.searchJobs(params)
    jobs.value = res.data.items
    total.value = Number(res.data.total) || 0
  } catch (err) {
    console.error('获取兼职列表失败:', err)
    error.value = err.message
  } finally {
    loading.value = false
  }
}

/**
 * 处理每页条数变化
 */
const handleSizeChange = (size) => {
  searchParams.value.pageSize = size
  handleSearch()
}

/**
 * 处理页码变化
 */
const handleCurrentChange = (current) => {
  searchParams.value.currentPage = current
  handleSearch()
}

/**
 * 处理搜索
 */
const handleSearch = async () => {
  await fetchJobs(searchParams.value)
}

/**
 * 输入防抖
 */
const handleInput = () => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    handleSearch()
  }, 500)
}

/**
 * 重置筛选条件
 */
const resetFilter = () => {
  searchParams.value = {
    keyword: '',
    jobType: '',
    workAddress: '',
    salaryMin: null,
    salaryMax: null,
    pageSize: 9,
    currentPage: 1
  }
  handleSearch()
}

/**
 * 跳转到工作详情页
 */
const navigateToJobDetail = (id) => {
  router.push(`/job/${id}`)
}

/**
 * 处理申请
 */
const handleApply = (job) => {
  console.log('申请职位:', job)
}

onMounted(async () => {
  await fetchJobs(searchParams.value)
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.jobs-page {
  min-height: 100vh;
  background: $color-gray-50;
  padding-top: 80px;
  display: flex;
  flex-direction: column;
}

/* Page Header */
.page-header {
  background: $gradient-primary;
  padding: 80px 0;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.header-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.page-title {
  font-size: 42px;
  font-weight: 800;
  color: white;
  margin: 0 0 12px;
}

.page-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

/* Filter Section */
.filter-section {
  padding: 20px 0;
  position: relative;
  z-index: 10;
  margin-top: -40px;
}

.filter-card {
  background: white;
  border-radius: $radius-2xl;
  padding: 28px;
  box-shadow: $shadow-lg;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: $font-size-sm;
  color: $color-gray-600;
  font-weight: 500;

  .el-icon {
    color: $color-primary;
  }
}

.salary-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.salary-input {
  flex: 1;
  text-align: center;
}

.salary-separator {
  font-size: $font-size-base;
  color: $color-gray-400;
}

.filter-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.filter-button {
  background: $gradient-primary;
  border: none;
  padding: 14px 32px;
  font-size: $font-size-base;
  font-weight: 600;
  height: 52px;
}

.reset-button {
  background: $color-gray-100;
  border: none;
  color: $color-gray-600;
  padding: 14px 32px;
  font-size: $font-size-base;
  font-weight: 600;
  height: 52px;

  &:hover {
    background: $color-gray-200;
  }
}

/* Jobs Section */
.jobs-section {
  padding: 40px 0 80px;
  flex: 1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.jobs-count {
  font-size: $font-size-base;
  color: $color-gray-500;
}

.sort-buttons {
  display: flex;
  gap: 10px;
}

.sort-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: white;
  border: 2px solid $color-gray-200;
  border-radius: $radius-lg;
  padding: 10px 18px;
  font-size: $font-size-sm;
  color: $color-gray-600;
  cursor: pointer;
  transition: all $transition-base;
  font-weight: 500;

  &:hover {
    border-color: $color-primary;
    color: $color-primary;
  }

  &.active {
    background: $gradient-primary;
    color: white;
    border-color: transparent;
  }
}

.jobs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

/* States */
.loading-state,
.error-state,
.empty-state {
  text-align: center;
  padding: 80px 0;
  color: $color-gray-500;
  font-size: $font-size-lg;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;

  .el-icon {
    font-size: 48px;
    color: $color-gray-400;
  }
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

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* Footer */
.footer-section {
  background: white;
  padding: 40px 0 24px;
  border-top: 1px solid $color-gray-100;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-icon {
  width: 40px;
  height: 40px;
  background: $gradient-primary;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.brand-name {
  font-size: $font-size-lg;
  font-weight: 600;
  color: $color-gray-900;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  color: $color-gray-600;
  text-decoration: none;
  font-size: $font-size-sm;
  transition: color $transition-fast;

  &:hover {
    color: $color-primary;
  }
}

.footer-bottom {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid $color-gray-100;
}

.footer-bottom p {
  font-size: $font-size-sm;
  color: $color-gray-500;
  margin: 0;
}

/* Responsive */
@media (max-width: 1024px) {
  .filter-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .jobs-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 60px 0;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .filter-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .jobs-grid {
    grid-template-columns: 1fr;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 24px;
  }
}
</style>
