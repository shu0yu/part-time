<template>
  <div class="admin-job-view">
    <div class="container">
      <h2>岗位管理</h2>
      
      <div class="filters">
        <input 
          type="text" 
          v-model="filters.workAddress" 
          placeholder="输入地区筛选"
          @keyup.enter="loadJobs"
        />
        <button class="btn btn-primary" @click="loadJobs">搜索</button>
      </div>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>岗位名称</th>
              <th>薪资</th>
              <th>工作地点</th>
              <th>发布企业</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="job in jobs" :key="job.id">
              <td>{{ job.id }}</td>
              <td>{{ job.jobName }}</td>
              <td>
                {{ job.salaryMin }} - {{ job.salaryMax }}
              </td>
              <td>{{ job.workAddress }}</td>
              <td>{{ job.companyName || '-' }}</td>
              <td>
                <span class="badge" :class="job.isDelete === 0 ? 'badge-success' : 'badge-danger'">
                  {{ job.isDelete === 0 ? '上架' : '已下架' }}
                </span>
              </td>
              <td>
                <button 
                  v-if="job.isDelete === 0"
                  class="btn btn-danger" 
                  @click="takeDownJob(job.id)"
                >
                  下架
                </button>
                <span v-else style="color: #999;">已下架</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="pagination" v-if="total > 0">
        <button 
          class="btn" 
          :disabled="currentPage === 1" 
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button 
          class="btn" 
          :disabled="currentPage === totalPages" 
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminAPI } from '../api/api'

const jobs = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const filters = ref({
  workAddress: ''
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

/**
 * 加载岗位列表
 */
const loadJobs = async () => {
  try {
    const res = await adminAPI.getAllJobs(
      pageSize.value,
      currentPage.value,
      filters.value.workAddress || undefined,
      undefined
    )
    if (res.code === 0) {
      jobs.value = res.data.items
      total.value = res.data.total
    }
  } catch (error) {
    console.error('加载岗位列表失败', error)
    alert('加载岗位列表失败')
  }
}

/**
 * 下架岗位
 */
const takeDownJob = async (jobId) => {
  if (!confirm('确定要下架该岗位吗？')) {
    return
  }
  
  try {
    const res = await adminAPI.takeDownJob(jobId)
    if (res.code === 0) {
      alert(res.data)
      loadJobs()
    } else {
      alert(res.message)
    }
  } catch (error) {
    console.error('下架失败', error)
    alert('下架失败')
  }
}

/**
 * 切换页码
 */
const changePage = (page) => {
  currentPage.value = page
  loadJobs()
}

onMounted(() => {
  loadJobs()
})
</script>

<style scoped>
.admin-job-view {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.filters {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
}

.filters input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  flex: 1;
  max-width: 300px;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.badge-success {
  background: #e8f5e9;
  color: #388e3c;
}

.badge-danger {
  background: #ffebee;
  color: #d32f2f;
}

.btn {
  padding: 6px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn:hover {
  opacity: 0.8;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: #2196f3;
  color: white;
}

.btn-danger {
  background: #f44336;
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.pagination span {
  color: #666;
}
</style>
