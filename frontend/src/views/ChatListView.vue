<template>
  <div class="chat-list-container">
    <Navbar />

    <div class="chat-main">
      <div class="chat-list">
        <div class="chat-tabs" v-if="isAdmin">
          <div 
            v-for="tab in tabs" 
            :key="tab.value"
            class="chat-tab"
            :class="{ active: activeTab === tab.value }"
            @click="switchTab(tab.value)"
          >
            <el-icon><component :is="tab.icon" /></el-icon>
            <span>{{ tab.label }}</span>
          </div>
        </div>
        
        <div class="chat-list-search">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索会话"
            prefix-icon="Search"
            clearable
          />
        </div>
        <div 
          v-for="session in filteredSessions" 
          :key="getSessionKey(session)"
          class="chat-item"
          :class="{ active: isSessionActive(session) }"
          @click="openChat(session)"
        >
          <div class="chat-avatar">
            <el-avatar 
              :size="56" 
              :style="{ background: getAvatarColor(getSessionId(session)) }"
            >
              {{ getSessionAvatarText(session) }}
            </el-avatar>
          </div>
          <div class="chat-info">
            <div class="chat-header">
              <div class="chat-name-wrapper">
                <span class="chat-name">{{ getSessionName(session) }}</span>
              </div>
            </div>
            <div class="chat-footer">
              <span class="last-message">{{ session.lastMessage || '暂无消息' }}</span>
            </div>
          </div>
        </div>

        <el-empty v-if="filteredSessions.length === 0" description="暂无聊天会话" />
      </div>

      <div class="chat-window-wrapper">
        <div v-if="!currentSession" class="empty-chat">
          <el-icon class="empty-icon"><Message /></el-icon>
          <p class="empty-text">{{ emptyText }}</p>
        </div>
        <ChatWindow 
          v-else
          :session="currentSession"
          :isAdmin="isAdmin"
          :isReadOnly="isReadOnlyMode"
          :showDirectChatButtons="activeTab === 'view'"
          @startDirectChat="handleStartDirectChat"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { chatAPI, adminAPI } from '@/api/api'
import chatWebSocket from '@/utils/websocket'
import ChatWindow from '@/components/ChatWindow.vue'
import Navbar from '../components/Navbar.vue'
import { useUserStore } from '../store/user'
import { Message, Search, ChatDotRound, View } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const sessions = ref([])
const currentSession = ref(null)
const searchKeyword = ref('')
const isDirectChat = ref(false)
const activeTab = ref('chat')

const tabs = [
  { label: '聊天', value: 'chat', icon: ChatDotRound },
  { label: '聊天记录查看', value: 'view', icon: View }
]

const isAdmin = computed(() => {
  const userInfo = userStore.getUserInfo
  return userInfo?.roles?.includes('ADMIN')
})

const isReadOnlyMode = computed(() => {
  return isAdmin.value && activeTab.value === 'view' && !isDirectChat.value
})

const emptyText = computed(() => {
  if (activeTab.value === 'chat') {
    return '请选择一个会话开始聊天'
  } else {
    return '请选择一个会话查看聊天记录'
  }
})

const switchTab = (tabValue) => {
  activeTab.value = tabValue
  currentSession.value = null
  isDirectChat.value = false
  loadSessions()
}

const loadSessions = async () => {
  try {
    if (isAdmin.value) {
      const res = await adminAPI.getAllChatSessions()
      console.log('后端返回的所有会话:', res.data)
      if (res.code === 0) {
        const currentAdminId = getCurrentUserId()
        console.log('当前管理员ID:', currentAdminId)
        if (activeTab.value === 'chat') {
          const adminSessions = res.data.filter(session => {
            const hasAdmin = 
              Number(session.senderId) === Number(currentAdminId) || 
              Number(session.receiverId) === Number(currentAdminId)
            console.log('检查会话是否包含管理员:', session, hasAdmin)
            return hasAdmin
          })
          console.log('聊天栏会话:', adminSessions)
          sessions.value = adminSessions
        } else {
          const viewSessions = res.data.filter(session => {
            const senderRole = session.senderRole
            const receiverRole = session.receiverRole
            const isStudentCompanyChat = 
              (senderRole === 'STUDENT' && receiverRole === 'COMPANY') ||
              (senderRole === 'COMPANY' && receiverRole === 'STUDENT')
            const hasAdmin = 
              Number(session.senderId) === Number(currentAdminId) || 
              Number(session.receiverId) === Number(currentAdminId)
            console.log('检查会话:', session, '学生企业:', isStudentCompanyChat, '有管理员:', hasAdmin)
            return isStudentCompanyChat && !hasAdmin
          })
          console.log('查看聊天记录栏会话:', viewSessions)
          sessions.value = viewSessions
        }
      }
    } else {
      const res = await chatAPI.getChatSessions()
      if (res.code === 0) {
        sessions.value = res.data
      }
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
  }
}

const getCurrentUserId = () => {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      const base64Url = token.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const padding = '='.repeat((4 - (base64.length % 4)) % 4)
      const jwt = JSON.parse(atob(base64 + padding))
      if (jwt.claims && jwt.claims.id) {
        return Number(jwt.claims.id)
      }
    } catch (e) {
      console.error('解析token失败:', e)
    }
  }
  return null
}

const filteredSessions = computed(() => {
  if (!searchKeyword.value) return sessions.value
  return sessions.value.filter(session => {
    const name = getSessionName(session)
    return name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  })
})

const getSessionKey = (session) => {
  return session.sessionId || session.userId
}

const getSessionId = (session) => {
  return session.sessionId ? session.senderId : session.userId
}

const getSessionName = (session) => {
  if (session.sessionId) {
    const currentAdminId = getCurrentUserId()
    if (Number(session.senderId) === Number(currentAdminId)) {
      return session.receiverName
    } else if (Number(session.receiverId) === Number(currentAdminId)) {
      return session.senderName
    } else {
      return `${session.senderName} ↔ ${session.receiverName}`
    }
  }
  return session.name
}

const getSessionAvatarText = (session) => {
  if (session.sessionId) {
    return session.senderName.charAt(0)
  }
  return session.name.charAt(0)
}

const getAvatarColor = (userId) => {
  const colors = [
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  ]
  const index = Number(userId) % colors.length
  return colors[index]
}

const isSessionActive = (session) => {
  if (!currentSession.value) return false
  
  // 处理chat标签页的情况：原始会话有sessionId，currentSession是directSession（只有userId）
  if (activeTab.value === 'chat' && session.sessionId && currentSession.value.userId) {
    const currentAdminId = getCurrentUserId()
    const sessionOtherUserId = Number(session.senderId) === Number(currentAdminId) 
      ? session.receiverId 
      : session.senderId
    return Number(sessionOtherUserId) === Number(currentSession.value.userId)
  }
  
  // 处理其他情况
  if (session.sessionId && currentSession.value.sessionId) {
    return session.sessionId === currentSession.value.sessionId
  }
  if (!session.sessionId && !currentSession.value.sessionId) {
    return session.userId === currentSession.value.userId
  }
  return false
}

const openChat = async (session) => {
  if (activeTab.value === 'chat' && session.sessionId) {
    const currentAdminId = getCurrentUserId()
    const otherUserId = Number(session.senderId) === Number(currentAdminId) 
      ? session.receiverId 
      : session.senderId
    const otherUserName = Number(session.senderId) === Number(currentAdminId) 
      ? session.receiverName 
      : session.senderName
    
    const directSession = {
      userId: otherUserId,
      name: otherUserName,
      isDirect: true
    }
    currentSession.value = directSession
    isDirectChat.value = true
  } else {
    currentSession.value = session
    isDirectChat.value = false
  }
  
  if (!isAdmin.value) {
    try {
      await chatAPI.markAsRead(Number(session.userId))
      await loadSessions()
    } catch (error) {
      console.error('标记消息已读失败:', error)
    }
  }
}

/**
 * 从 URL 参数中获取用户信息并直接打开聊天
 */
const handleUrlParams = async () => {
  const userId = route.query.userId
  const userName = route.query.userName
  
  if (userId) {
    // 等待会话列表加载完成
    await loadSessions()
    
    // 创建或使用现有会话
    const directSession = {
      userId: Number(userId),
      name: userName || '用户',
      isDirect: true
    }
    
    currentSession.value = directSession
    isDirectChat.value = true
    
    // 清除 URL 参数，避免刷新后重复打开
    router.replace('/chat')
  }
}

const handleStartDirectChat = (targetUserId, targetUserName) => {
  const directSession = {
    userId: targetUserId,
    name: targetUserName,
    isDirect: true
  }
  currentSession.value = directSession
  isDirectChat.value = true
}

const handleWebSocketMessage = (data) => {
  if (data.type === 'new_message') {
    loadSessions()
  }
}

onMounted(async () => {
  if (!userStore.getUserInfo && userStore.getIsLoggedIn) {
    try {
      await userStore.getCurrentUser()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  
  await loadSessions()
  
  const token = localStorage.getItem('token')
  if (token) {
    try {
      const base64Url = token.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const padding = '='.repeat((4 - (base64.length % 4)) % 4)
      const jwt = JSON.parse(atob(base64 + padding))
      
      if (jwt.claims && jwt.claims.id && !isNaN(Number(jwt.claims.id))) {
        chatWebSocket.connect(token)
        chatWebSocket.onMessage(handleWebSocketMessage)
      }
    } catch (e) {
      console.error('WebSocket连接失败：token无效', e)
    }
  }
  
  // 处理 URL 参数，打开指定的聊天
  await handleUrlParams()
})

onUnmounted(() => {
  chatWebSocket.close()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.chat-list-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, rgba(14, 165, 233, 0.05) 0%, $color-gray-50 100%);
  padding-top: 80px;
}

.chat-main {
  display: flex;
  flex: 1;
  overflow: hidden;
  max-width: 1280px;
  margin: $spacing-5 auto $spacing-5;
  width: 100%;
  background: white;
  border-radius: $radius-2xl;
  box-shadow: 0 10px 40px rgba(14, 165, 233, 0.1);
  border: 1px solid $color-gray-100;
}

.chat-list {
  width: 380px;
  background-color: white;
  overflow-y: auto;
  border-right: 1px solid $color-gray-200;
  flex-shrink: 0;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: $color-gray-100;
  }
  
  &::-webkit-scrollbar-thumb {
    background: $color-gray-300;
    border-radius: 3px;
    transition: background $transition-fast;
  }
  
  &::-webkit-scrollbar-thumb:hover {
    background: $color-gray-400;
  }
}

.chat-tabs {
  display: flex;
  padding: $spacing-3;
  background: $color-gray-50;
  border-bottom: 1px solid $color-gray-200;
  gap: $spacing-2;
}

.chat-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-2;
  padding: $spacing-3 $spacing-4;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: all $transition-base;
  font-size: $font-size-sm;
  font-weight: 500;
  color: $color-gray-600;
  
  &:hover {
    background: rgba(14, 165, 233, 0.08);
    color: $color-primary;
  }
  
  &.active {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
  }
}

.chat-list-search {
  padding: $spacing-5 $spacing-5;
  border-bottom: 1px solid $color-gray-100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  
  .el-input {
    --el-input-bg-color: white;
    --el-input-border-color: $color-gray-200;
    --el-input-border-radius: $radius-xl;
    --el-input-height: 48px;
  }
}

.chat-item {
  display: flex;
  padding: $spacing-4 $spacing-5;
  cursor: pointer;
  transition: all $transition-base;
  border-bottom: 1px solid $color-gray-100;
  position: relative;

  &:hover {
    background-color: $color-gray-50;
  }

  &.active {
    background-color: rgba(14, 165, 233, 0.08);
    border-left: 4px solid $color-primary;
  }
}

.chat-avatar {
  margin-right: $spacing-4;
  flex-shrink: 0;
  position: relative;
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-2;
}

.chat-name-wrapper {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  flex-wrap: wrap;
}

.chat-name {
  font-size: $font-size-base;
  font-weight: 600;
  color: $color-gray-800;
}

.dispute-tag {
  font-size: $font-size-xs;
  padding: 2px 8px;
  border-radius: $radius-full;
  background: rgba(244, 67, 54, 0.1);
  color: #f44336;
  font-weight: 500;
}

.chat-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.last-message {
  flex: 1;
  font-size: $font-size-sm;
  color: $color-gray-500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: $spacing-2;
  line-height: 1.5;
}

.chat-window-wrapper {
  flex: 1;
  overflow: hidden;
  min-width: 0;
  background-color: $color-gray-50;
}

.empty-chat {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $spacing-4;
  background-color: $color-gray-50;
}

.empty-icon {
  font-size: 72px;
  color: $color-gray-300;
}

.empty-text {
  font-size: $font-size-base;
  color: $color-gray-500;
  margin: 0;
}

@media (max-width: 1024px) {
  .chat-list {
    width: 320px;
  }
}

@media (max-width: 768px) {
  .chat-list-container {
    padding-top: 70px;
  }
  
  .chat-main {
    flex-direction: column;
    border-radius: $radius-lg;
    margin: 10px auto 10px;
  }
  
  .chat-list {
    width: 100%;
    max-height: 40vh;
    border-right: none;
    border-bottom: 1px solid $color-gray-200;
  }
  
  .chat-window-wrapper {
    flex: 1;
    min-height: 0;
  }
}
</style>
