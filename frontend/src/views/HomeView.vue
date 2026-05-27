<template>
  <div class="home-view">
    <Navbar />
    
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-background">
        <div class="gradient-blob blob-1"></div>
        <div class="gradient-blob blob-2"></div>
        <div class="gradient-blob blob-3"></div>
      </div>
      
      <div class="container">
        <div class="hero-content">
          <div class="hero-badge animate-fadeInDown">
            <span class="badge-dot animate-pulse"></span>
            超过 10,000+ 学生找到理想兼职
          </div>
          
          <h1 class="hero-title animate-fadeInUp stagger-1">
            发现你的<span class="highlight-text">完美兼职</span>
          </h1>
          
          <p class="hero-subtitle animate-fadeInUp stagger-2">
            安全可靠的兼职平台，为大学生提供优质的兼职机会
          </p>
          
          <!-- Search Box -->
          <div class="search-container animate-fadeInUp stagger-3">
            <div class="search-box">
              <div class="search-tabs">
                <button 
                  class="tab-button" 
                  :class="{ active: searchType === 'job' }"
                  @click="searchType = 'job'"
                >
                  <el-icon><Search /></el-icon>
                  职位
                </button>
                <button 
                  class="tab-button" 
                  :class="{ active: searchType === 'company' }"
                  @click="searchType = 'company'"
                >
                  <el-icon><OfficeBuilding /></el-icon>
                  企业
                </button>
              </div>
              
              <div class="search-input-group">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><Search /></el-icon>
                  <input 
                    type="text" 
                    v-model="searchParams.keyword" 
                    :placeholder="searchType === 'job' ? '搜索兼职岗位、企业名称' : '搜索企业名称'"
                    class="search-input"
                    @keyup.enter="handleSearch"
                  />
                </div>
                <button class="search-button" @click="handleSearch">
                  搜索
                </button>
              </div>
            </div>
            
            <!-- Hot Tags -->
            <div class="hot-tags">
              <span class="tags-label">热门搜索：</span>
              <div class="tags-list">
                <button 
                  v-for="tag in hotTags" 
                  :key="tag"
                  class="tag-button"
                  @click="handleTagClick(tag)"
                >
                  {{ tag }}
                </button>
              </div>
            </div>
          </div>
          
          <!-- Stats -->
          <div class="hero-stats animate-fadeInUp stagger-4">
            <div class="stat-item">
              <span class="stat-number">10,000+</span>
              <span class="stat-label">注册学生</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-number">5,000+</span>
              <span class="stat-label">岗位数量</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-number">98%</span>
              <span class="stat-label">满意度</span>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <!-- Jobs Section -->
    <section class="jobs-section">
      <div class="container">
        <div class="section-header">
          <div class="header-content">
            <h2 class="section-title">最新兼职</h2>
            <p class="section-subtitle">精选优质兼职岗位，等你来投</p>
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
      </div>
    </section>
    
    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useJobStore } from '../store/job'
import { jobAPI } from '../api/api'
import { 
  Loading, WarningFilled, InfoFilled, Search, 
  OfficeBuilding, Clock, 
  TrendCharts, Briefcase 
} from '@element-plus/icons-vue'
import Navbar from '../components/Navbar.vue'
import JobCard from '../components/JobCard.vue'
import Footer from '../components/Footer.vue'

const router = useRouter()
const jobStore = useJobStore()

const searchType = ref('job')
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
const hotTags = ref(['家教', '实习', '兼职', '暑假工', '短期'])
const jobs = ref([])
const loading = ref(false)
const error = ref(null)

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
  } catch (err) {
    console.error('获取兼职列表失败:', err)
    error.value = err.message
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 */
const handleSearch = async () => {
  await fetchJobs(searchParams.value)
}

/**
 * 处理热门标签点击
 */
const handleTagClick = (tag) => {
  searchParams.value.keyword = tag
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

.home-view {
  min-height: 100vh;
  background: $color-gray-50;
  padding-top: 80px;
}

/* Hero Section */
.hero-section {
  position: relative;
  padding: 120px 0 100px;
  background: $gradient-primary;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.gradient-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
  animation: float 20s infinite ease-in-out;
}

.blob-1 {
  width: 600px;
  height: 600px;
  background: rgba(255, 255, 255, 0.4);
  top: -200px;
  right: -100px;
  animation-delay: 0s;
}

.blob-2 {
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.3);
  bottom: -100px;
  left: -100px;
  animation-delay: -5s;
}

.blob-3 {
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.3);
  top: 50%;
  left: 30%;
  animation-delay: -10s;
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 800px;
  margin: 0 auto;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  padding: 10px 20px;
  border-radius: 50px;
  color: white;
  font-size: $font-size-sm;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.badge-dot {
  width: 10px;
  height: 10px;
  background: $color-success;
  border-radius: 50%;
}

.hero-title {
  font-size: clamp(2.5rem, 5vw, 3.5rem);
  font-weight: 800;
  color: white;
  margin-bottom: 16px;
  line-height: 1.1;
}

.highlight-text {
  position: relative;
  display: inline-block;
  
  &::after {
    content: '';
    position: absolute;
    bottom: 4px;
    left: 0;
    right: 0;
    height: 12px;
    background: rgba(255, 255, 255, 0.3);
    border-radius: 6px;
    z-index: -1;
  }
}

.hero-subtitle {
  font-size: $font-size-lg;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 40px;
}

/* Search Container */
.search-container {
  max-width: 700px;
  margin: 0 auto 40px;
}

.search-box {
  background: white;
  border-radius: $radius-2xl;
  overflow: hidden;
  box-shadow: $shadow-2xl;
}

.search-tabs {
  display: flex;
  background: $color-gray-100;
  padding: 6px;
  gap: 4px;
}

.tab-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: transparent;
  border: none;
  border-radius: $radius-lg;
  font-size: $font-size-sm;
  font-weight: 600;
  color: $color-gray-600;
  cursor: pointer;
  transition: all $transition-base;

  &.active {
    background: white;
    color: $color-primary;
    box-shadow: $shadow-sm;
  }
}

.search-input-group {
  display: flex;
  padding: 12px;
  gap: 12px;
}

.input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background: $color-gray-100;
  border-radius: $radius-lg;
  padding: 0 16px;
  transition: all $transition-fast;

  &:focus-within {
    background: white;
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
  }
}

.input-icon {
  color: $color-gray-400;
  font-size: 18px;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 14px 12px;
  font-size: $font-size-base;
  outline: none;
  color: $color-gray-800;

  &::placeholder {
    color: $color-gray-400;
  }
}

.search-button {
  padding: 0 32px;
  background: $gradient-primary;
  color: white;
  font-size: $font-size-base;
  font-weight: 600;
  border: none;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: all $transition-base;

  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-primary;
  }
}

/* Hot Tags */
.hot-tags {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.tags-label {
  font-size: $font-size-sm;
  color: rgba(255, 255, 255, 0.85);
}

.tags-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 6px 14px;
  border-radius: $radius-full;
  font-size: $font-size-sm;
  cursor: pointer;
  transition: all $transition-base;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
  }
}

/* Hero Stats */
.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-number {
  font-size: $font-size-4xl;
  font-weight: 800;
  color: white;
}

.stat-label {
  font-size: $font-size-sm;
  color: rgba(255, 255, 255, 0.85);
}

.stat-divider {
  width: 1px;
  height: 50px;
  background: rgba(255, 255, 255, 0.3);
}

/* Jobs Section */
.jobs-section {
  padding: 40px 0 80px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.section-title {
  font-size: $font-size-3xl;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0;
}

.section-subtitle {
  font-size: $font-size-base;
  color: $color-gray-500;
  margin: 0;
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

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-20px) rotate(5deg);
  }
  50% {
    transform: translateY(0) rotate(0deg);
  }
  75% {
    transform: translateY(20px) rotate(-5deg);
  }
}

/* Footer */
.footer-section {
  background: white;
  padding: 60px 0 24px;
  border-top: 1px solid $color-gray-100;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  margin-bottom: 40px;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-icon {
  width: 56px;
  height: 56px;
  background: $gradient-primary;
  border-radius: $radius-xl;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
}

.brand-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.brand-name {
  font-size: $font-size-xl;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0;
}

.brand-description {
  font-size: $font-size-sm;
  color: $color-gray-500;
  margin: 0;
}

.footer-links {
  display: flex;
  gap: 60px;
}

.link-group h4 {
  font-size: $font-size-base;
  font-weight: 600;
  color: $color-gray-900;
  margin: 0 0 16px;
}

.link-group ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.link-group li {
  margin-bottom: 12px;
}

.link-group a {
  color: $color-gray-600;
  text-decoration: none;
  font-size: $font-size-sm;
  transition: color $transition-fast;

  &:hover {
    color: $color-primary;
  }
}

.footer-bottom {
  border-top: 1px solid $color-gray-100;
  padding-top: 24px;
  text-align: center;
}

.footer-bottom p {
  font-size: $font-size-sm;
  color: $color-gray-500;
  margin: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-section {
    padding: 80px 0 60px;
  }

  .hero-stats {
    flex-direction: column;
    gap: 24px;
  }

  .stat-divider {
    width: 50px;
    height: 1px;
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
    gap: 32px;
  }

  .footer-links {
    flex-direction: column;
    gap: 32px;
  }
}
</style>