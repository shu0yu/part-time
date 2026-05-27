<template>
  <div class="admin-dispute-view">
    <div class="container">
      <h2>纠纷处理</h2>
      
      <div class="query-form">
        <div class="form-group">
          <label>学生用户ID</label>
          <input type="number" v-model="query.studentId" placeholder="请输入学生用户ID" />
        </div>
        <div class="form-group">
          <label>企业用户ID</label>
          <input type="number" v-model="query.companyId" placeholder="请输入企业用户ID" />
        </div>
        <div class="form-group">
          <label>岗位ID</label>
          <input type="number" v-model="query.jobId" placeholder="请输入岗位ID" />
        </div>
        <button class="btn btn-primary" @click="loadDisputeInfo">查询</button>
      </div>

      <div v-if="disputeInfo" class="dispute-content">
        <div class="info-section">
          <h3>基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">岗位ID：</span>
              <span class="value">{{ disputeInfo.jobId }}</span>
            </div>
            <div class="info-item">
              <span class="label">岗位名称：</span>
              <span class="value">{{ disputeInfo.jobName }}</span>
            </div>
            <div class="info-item">
              <span class="label">学生ID：</span>
              <span class="value">{{ disputeInfo.studentId }}</span>
            </div>
            <div class="info-item">
              <span class="label">学生姓名：</span>
              <span class="value">{{ disputeInfo.studentName }}</span>
            </div>
            <div class="info-item">
              <span class="label">企业ID：</span>
              <span class="value">{{ disputeInfo.companyId }}</span>
            </div>
            <div class="info-item">
              <span class="label">企业名称：</span>
              <span class="value">{{ disputeInfo.companyName }}</span>
            </div>
          </div>
        </div>

        <div v-if="disputeInfo.application" class="info-section">
          <h3>申请记录</h3>
          <div class="application-info">
            <p><strong>申请ID：</strong>{{ disputeInfo.application.id }}</p>
            <p><strong>申请备注：</strong>{{ disputeInfo.application.applyRemark || '无' }}</p>
          </div>
        </div>

        <div class="info-section">
          <h3>聊天记录</h3>
          <div v-if="disputeInfo.chatRecords && disputeInfo.chatRecords.length > 0" class="chat-records">
            <div 
              v-for="record in disputeInfo.chatRecords" 
              :key="record.id" 
              class="chat-message"
              :class="{ 'student-message': record.senderId === disputeInfo.studentId, 'company-message': record.senderId === disputeInfo.companyId }"
            >
              <div class="message-header">
                <span class="sender">
                  {{ record.senderId === disputeInfo.studentId ? disputeInfo.studentName : disputeInfo.companyName }}
                </span>
              </div>
              <div class="message-content">{{ record.message }}</div>
            </div>
          </div>
          <div v-else class="no-records">暂无聊天记录</div>
        </div>
      </div>

      <div v-else-if="hasSearched" class="no-data">
        未找到相关纠纷信息
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { adminAPI } from '../api/api'

const query = ref({
  studentId: '',
  companyId: '',
  jobId: ''
})
const disputeInfo = ref(null)
const hasSearched = ref(false)

/**
 * 加载纠纷信息
 */
const loadDisputeInfo = async () => {
  if (!query.value.studentId || !query.value.companyId || !query.value.jobId) {
    alert('请填写完整的查询条件')
    return
  }
  
  try {
    const res = await adminAPI.getDisputeInfo(
      Number(query.value.studentId),
      Number(query.value.companyId),
      Number(query.value.jobId)
    )
    if (res.code === 0) {
      disputeInfo.value = res.data
    } else {
      alert(res.message)
      disputeInfo.value = null
    }
  } catch (error) {
    console.error('查询纠纷信息失败', error)
    alert('查询纠纷信息失败')
    disputeInfo.value = null
  }
  hasSearched.value = true
}
</script>

<style scoped>
.admin-dispute-view {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px 0;
}

.container {
  max-width: 1000px;
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

.query-form {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  align-items: flex-end;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
  flex: 1;
  min-width: 180px;
}

.form-group label {
  font-size: 14px;
  color: #666;
}

.form-group input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.btn {
  padding: 10px 24px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn:hover {
  opacity: 0.8;
}

.btn-primary {
  background: #2196f3;
  color: white;
}

.dispute-content {
  margin-top: 20px;
}

.info-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.info-section:last-child {
  border-bottom: none;
}

.info-section h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.info-item {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}

.info-item .label {
  color: #666;
  font-size: 14px;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

.application-info {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
}

.application-info p {
  margin: 8px 0;
}

.chat-records {
  max-height: 500px;
  overflow-y: auto;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
}

.chat-message {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.student-message {
  align-items: flex-start;
}

.company-message {
  align-items: flex-end;
}

.message-header {
  margin-bottom: 5px;
}

.sender {
  font-size: 12px;
  color: #666;
  padding: 2px 8px;
  background: #e0e0e0;
  border-radius: 10px;
}

.message-content {
  padding: 10px 14px;
  border-radius: 12px;
  max-width: 70%;
  word-wrap: break-word;
}

.student-message .message-content {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
}

.company-message .message-content {
  background: #2196f3;
  color: white;
  border-bottom-right-radius: 4px;
}

.no-records,
.no-data {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 14px;
}
</style>
