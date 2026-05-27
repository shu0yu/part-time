<template>
  <div class="chat-window">
    <div class="chat-header">
      <div class="chat-title">
        <div class="chat-avatar-small" v-if="chatUserId">
          <el-avatar :size="32" :style="{ background: getAvatarColor(chatUserId) }">
            {{ chatUserName.charAt(0) }}
          </el-avatar>
        </div>
        <span class="chat-name">{{ chatUserName }}</span>
      </div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div v-if="chatUserId || session?.sessionId">
        <div 
          v-for="message in messages" 
          :key="message.id"
        >
          <div class="message-item message-received" v-if="Number(message.senderId) !== Number(currentUserId)">
            <div class="message-avatar">
              <el-avatar :size="40" :style="{ background: getAvatarColor(message.senderId) }">
                {{ getSenderName(message.senderId).charAt(0) }}
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-sender-name">{{ getSenderName(message.senderId) }}</div>
              <div class="message-bubble">
                {{ message.message }}
              </div>
            </div>
          </div>
          
          <div class="message-item message-sent" v-else>
            <div class="message-content">
              <div class="message-bubble">
                {{ message.message }}
              </div>
            </div>
            <div class="message-avatar">
              <el-avatar :size="40" :style="{ background: getAvatarColor(currentUserId) }">
                {{ currentUserName.charAt(0) }}
              </el-avatar>
            </div>
          </div>
        </div>
        
        <div v-if="messages.length === 0" class="empty-messages">
          <el-empty description="暂无消息" />
        </div>
      </div>
      <div v-else class="empty-messages">
        <el-empty description="请从左侧选择一个聊天会话" />
      </div>
    </div>

    <div class="chat-input-wrapper">
      <div v-if="isAdmin && isReadOnly && session && session.sessionId && showDirectChatButtons" class="direct-chat-buttons">
        <button 
          v-if="getStudentId() !== null" 
          class="direct-chat-btn student" 
          @click="startDirectChat(getStudentId(), getStudentName())"
        >
          单独沟通学生
        </button>
        <button 
          v-if="getCompanyId() !== null" 
          class="direct-chat-btn company" 
          @click="startDirectChat(getCompanyId(), getCompanyName())"
        >
          单独沟通企业
        </button>
      </div>
      
      <div class="chat-input" v-if="!isReadOnly">
        <div class="input-container">
          <el-input
            v-model="inputMessage"
            placeholder="输入消息..."
            @keyup.enter="sendMessage"
            :disabled="!currentUserId"
            class="message-input"
          />
          <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()" class="send-btn">
            发送
          </el-button>
        </div>
      </div>
      
      <div v-else class="read-only-tip">
        <el-icon><Lock /></el-icon>
        <span>只读模式 - 查看他人会话</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Lock } from '@element-plus/icons-vue'
import { chatAPI, adminAPI } from '@/api/api'
import chatWebSocket from '@/utils/websocket'
import { useUserStore } from '../store/user'

const userStore = useUserStore()

const props = defineProps({
  session: {
    type: Object,
    default: null
  },
  isAdmin: {
    type: Boolean,
    default: false
  },
  isReadOnly: {
    type: Boolean,
    default: false
  },
  showDirectChatButtons: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['startDirectChat'])

const messages = ref([])
const inputMessage = ref('')
const currentUserId = ref(null)
const currentUserName = ref('')
const messagesContainer = ref(null)

const chatUserId = computed(() => {
  if (!props.session) return null
  if (props.session.isDirect || props.session.userId) {
    return props.session.userId
  }
  return null
})

const chatUserName = computed(() => {
  if (!props.session) return '请选择一个聊天会话'
  if (props.session.isDirect || props.session.userId) {
    if (props.isAdmin && props.session.userId) {
      return `管理员 ↔ ${props.session.name}`
    }
    return props.session.name
  }
  if (props.session.sessionId) {
    return `${props.session.senderName} ↔ ${props.session.receiverName}`
  }
  return '请选择一个聊天会话'
})

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

const getSenderName = (senderId) => {
  if (!props.session) return ''
  if (Number(senderId) === Number(currentUserId.value)) {
    return currentUserName.value
  }
  if (props.session.sessionId) {
    if (Number(senderId) === Number(props.session.senderId)) {
      return props.session.senderName
    }
    if (Number(senderId) === Number(props.session.receiverId)) {
      return props.session.receiverName
    }
  }
  return props.session.name || ''
}

const getStudentId = () => {
  if (!props.session?.sessionId) return null
  if (props.session.senderRole === 'STUDENT') {
    return props.session.senderId
  }
  if (props.session.receiverRole === 'STUDENT') {
    return props.session.receiverId
  }
  return null
}

const getStudentName = () => {
  if (!props.session?.sessionId) return ''
  if (props.session.senderRole === 'STUDENT') {
    return props.session.senderName
  }
  if (props.session.receiverRole === 'STUDENT') {
    return props.session.receiverName
  }
  return ''
}

const getCompanyId = () => {
  if (!props.session?.sessionId) return null
  if (props.session.senderRole === 'COMPANY') {
    return props.session.senderId
  }
  if (props.session.receiverRole === 'COMPANY') {
    return props.session.receiverId
  }
  return null
}

const getCompanyName = () => {
  if (!props.session?.sessionId) return ''
  if (props.session.senderRole === 'COMPANY') {
    return props.session.senderName
  }
  if (props.session.receiverRole === 'COMPANY') {
    return props.session.receiverName
  }
  return ''
}

const loadChatHistory = async () => {
  if (!chatUserId.value && !props.session?.sessionId) return
  
  try {
    let res
    if (props.isAdmin && props.session?.sessionId) {
      res = await adminAPI.getChatHistoryForAdmin(
        props.session.senderId,
        props.session.receiverId,
        props.session.jobId
      )
    } else {
      let otherUserId = chatUserId.value
      let jobId = null
      
      if (props.session?.sessionId) {
        otherUserId = props.session.senderId
        jobId = props.session.jobId
      }
      
      res = await chatAPI.getChatHistory(otherUserId, jobId)
    }
    
    if (res.code === 0) {
      messages.value = res.data.sort((a, b) => a.id - b.id)
      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
    ElMessage.error('加载聊天记录失败')
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || !chatUserId.value) return

  const messageData = {
    receiverId: Number(chatUserId.value),
    message: inputMessage.value.trim()
  }

  try {
    if (chatWebSocket.ws && chatWebSocket.ws.readyState === WebSocket.OPEN) {
      const token = localStorage.getItem('token')
      if (token) {
        try {
          const base64Url = token.split('.')[1]
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
          const padding = '='.repeat((4 - (base64.length % 4)) % 4)
          const jwt = JSON.parse(atob(base64 + padding))
          
          if (jwt.claims && jwt.claims.id && !isNaN(Number(jwt.claims.id))) {
            messageData.senderId = Number(jwt.claims.id)
            currentUserId.value = Number(jwt.claims.id)
          } else {
            ElMessage.error('身份验证失败，请重新登录')
            return
          }
        } catch (e) {
          ElMessage.error('身份验证失败，请重新登录')
          return
        }
      }

      const tempMessage = {
        id: Date.now(),
        senderId: messageData.senderId,
        receiverId: messageData.receiverId,
        message: messageData.message,
        isRead: 0
      }
      messages.value.push(tempMessage)
      inputMessage.value = ''
      await nextTick()
      scrollToBottom()

      chatWebSocket.send(messageData)
    } else {
      const res = await chatAPI.sendMessage(messageData)
      if (res.code === 0) {
        messages.value.push(res.data)
        inputMessage.value = ''
        await nextTick()
        scrollToBottom()
      } else {
        ElMessage.error(res.msg || '发送失败')
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送失败，请重试')
  }
}

const startDirectChat = (targetUserId, targetUserName) => {
  emit('startDirectChat', targetUserId, targetUserName)
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const handleWebSocketMessage = (data) => {
  if (data.type === 'new_message') {
    const msg = data.data
    const shouldShow = (
      (Number(msg.senderId) === Number(chatUserId.value) && Number(msg.receiverId) === Number(currentUserId.value)) ||
      (Number(msg.receiverId) === Number(chatUserId.value) && Number(msg.senderId) === Number(currentUserId.value)) ||
      (props.session?.sessionId && 
        ((Number(msg.senderId) === Number(props.session.senderId) || Number(msg.senderId) === Number(props.session.receiverId)) &&
         (Number(msg.receiverId) === Number(props.session.senderId) || Number(msg.receiverId) === Number(props.session.receiverId))))
    )
    
    if (shouldShow) {
      messages.value.push(msg)
      nextTick(() => scrollToBottom())
    }
  }
}

watch(() => props.session, () => {
  loadChatHistory()
})

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      const base64Url = token.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const padding = '='.repeat((4 - (base64.length % 4)) % 4)
      const jwt = JSON.parse(atob(base64 + padding))
      
      if (jwt.claims && jwt.claims.id && !isNaN(Number(jwt.claims.id))) {
        currentUserId.value = Number(jwt.claims.id)
        currentUserName.value = jwt.claims.realName || '管理员'
      }
    } catch (e) {
      console.error('解析 token 失败:', e)
    }
  }

  await loadChatHistory()
  chatWebSocket.onMessage(handleWebSocketMessage)
})

onUnmounted(() => {
})
</script>

<style scoped lang="scss">
.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #fff;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chat-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.chat-avatar-small {
  margin-right: 12px;
}

.chat-name {
  font-weight: 500;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f8f9fa;
  background-image: 
    linear-gradient(rgba(24, 144, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(24, 144, 255, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
  }
}

.empty-messages {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
}

.message-item {
  display: flex;
  align-items: flex-end;
  margin-bottom: 20px;
  
  &.message-sent {
    flex-direction: row;
    justify-content: flex-end;
    
    .message-content {
      align-items: flex-end;
      margin-right: 12px;
    }
    
    .message-bubble {
      background-color: #409EFF;
      color: #fff;
      border-radius: 12px 12px 0 12px;
      box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
    }
  }
  
  &.message-received {
    flex-direction: row;
    justify-content: flex-start;
    
    .message-content {
      align-items: flex-start;
      margin-left: 12px;
    }
    
    .message-bubble {
      background-color: #F5F7FA;
      color: #333;
      border-radius: 12px 12px 12px 0;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    }
  }
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.message-sender-name {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.message-bubble {
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  position: relative;
  transition: all 0.2s ease;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
}

.chat-input-wrapper {
  border-top: 1px solid #e0e0e0;
  background-color: #fff;
}

.direct-chat-buttons {
  display: flex;
  gap: 10px;
  padding: 12px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.direct-chat-btn {
  flex: 1;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &.student {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }
  
  &.company {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

.chat-input {
  padding: 15px 20px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  border-radius: 20px;
  
  :deep(.el-input__wrapper) {
    border-radius: 20px;
    box-shadow: none;
    border: 1px solid #dcdfe6;
    
    &:hover {
      box-shadow: 0 0 0 2px rgba(79, 172, 254, 0.2);
      border-color: #c6e2ff;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 2px rgba(79, 172, 254, 0.3);
      border-color: #4facfe;
    }
  }
  
  :deep(.el-input__inner) {
    padding: 12px 16px;
    font-size: 14px;
  }
}

.send-btn {
  border-radius: 20px;
  padding: 12px 24px;
  font-weight: 500;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &:disabled {
    background: #c0c4cc;
    box-shadow: none;
    cursor: not-allowed;
  }
}

.read-only-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 15px 20px;
  color: #999;
  font-size: 14px;
  background: #f5f7fa;
}
</style>
