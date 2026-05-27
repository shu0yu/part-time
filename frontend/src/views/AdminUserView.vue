<template>
  <div class="admin-user-view">
    <div class="container">
      <h2>用户管理</h2>
      
      <div class="filters">
        <select v-model="filters.roleName" @change="loadUsers">
          <option value="">全部角色</option>
          <option value="ADMIN">管理员</option>
          <option value="COMPANY">企业</option>
          <option value="STUDENT">学生</option>
        </select>
        <select v-model="filters.status" @change="loadUsers">
          <option value="">全部状态</option>
          <option :value="1">正常</option>
          <option :value="0">禁用</option>
        </select>
      </div>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>账号</th>
              <th>手机号</th>
              <th>真实姓名</th>
              <th>角色</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.phone }}</td>
              <td>{{ user.realName }}</td>
              <td>
                <span class="badge" :class="getRoleClass(user.roleName)">
                  {{ getRoleText(user.roleName) }}
                </span>
              </td>
              <td>
                <span class="badge" :class="user.status === 1 ? 'badge-success' : 'badge-danger'">
                  {{ user.status === 1 ? '正常' : '禁用' }}
                </span>
              </td>
              <td>
                <button 
                  v-if="user.status === 1"
                  class="btn btn-danger" 
                  @click="toggleUserStatus(user.id, 0)"
                >
                  禁用
                </button>
                <button 
                  v-else
                  class="btn btn-success" 
                  @click="toggleUserStatus(user.id, 1)"
                >
                  启用
                </button>
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

const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const filters = ref({
  roleName: '',
  status: ''
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

/**
 * 加载用户列表
 */
const loadUsers = async () => {
  try {
    const res = await adminAPI.getAllUsers(
      pageSize.value,
      currentPage.value,
      filters.value.roleName || undefined,
      filters.value.status !== '' ? Number(filters.value.status) : undefined
    )
    if (res.code === 0) {
      users.value = res.data.items
      total.value = res.data.total
    }
  } catch (error) {
    console.error('加载用户列表失败', error)
    alert('加载用户列表失败')
  }
}

/**
 * 获取角色文本
 */
const getRoleText = (roleName) => {
  const roleMap = {
    'ADMIN': '管理员',
    'COMPANY': '企业',
    'STUDENT': '学生'
  }
  return roleMap[roleName] || roleName
}

/**
 * 获取角色样式类
 */
const getRoleClass = (roleName) => {
  const classMap = {
    'ADMIN': 'badge-warning',
    'COMPANY': 'badge-info',
    'STUDENT': 'badge-primary'
  }
  return classMap[roleName] || ''
}

/**
 * 切换用户状态
 */
const toggleUserStatus = async (userId, status) => {
  const confirmText = status === 0 ? '确定要禁用该用户吗？' : '确定要启用该用户吗？'
  if (!confirm(confirmText)) {
    return
  }
  
  try {
    const res = await adminAPI.updateUserStatus(userId, status)
    if (res.code === 0) {
      alert(res.data)
      loadUsers()
    } else {
      alert(res.message)
    }
  } catch (error) {
    console.error('操作失败', error)
    alert('操作失败')
  }
}

/**
 * 切换页码
 */
const changePage = (page) => {
  currentPage.value = page
  loadUsers()
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-user-view {
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
}

.filters select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
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

.badge-primary {
  background: #e3f2fd;
  color: #1976d2;
}

.badge-success {
  background: #e8f5e9;
  color: #388e3c;
}

.badge-danger {
  background: #ffebee;
  color: #d32f2f;
}

.badge-warning {
  background: #fff3e0;
  color: #f57c00;
}

.badge-info {
  background: #e0f7fa;
  color: #0097a7;
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

.btn-success {
  background: #4caf50;
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
