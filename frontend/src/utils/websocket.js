class ChatWebSocket {
  constructor() {
    this.ws = null
    this.reconnectTimer = null
    this.heartbeatTimer = null
    this.reconnectCount = 0
    this.maxReconnectCount = 5
    this.reconnectInterval = 3000
    this.heartbeatInterval = 30000
    this.onMessageCallback = null
    this.onOpenCallback = null
    this.onCloseCallback = null
    this.onErrorCallback = null
  }

  connect(token) {
    const wsBaseUrl = import.meta.env.VITE_WS_BASE_URL || 'ws://localhost:9999'
    const wsUrl = `${wsBaseUrl}/ws/chat?token=${token}`
    
    try {
      this.ws = new WebSocket(wsUrl)

      this.ws.onopen = (event) => {
        console.log('WebSocket 连接成功')
        this.reconnectCount = 0
        if (this.onOpenCallback) {
          this.onOpenCallback(event)
        }
        this.startHeartbeat()
      }

      this.ws.onmessage = (event) => {
        console.log('收到消息:', event.data)
        const data = JSON.parse(event.data)
        if (this.onMessageCallback) {
          this.onMessageCallback(data)
        }
      }

      this.ws.onclose = (event) => {
        console.log('WebSocket 连接关闭')
        this.stopHeartbeat()
        if (this.onCloseCallback) {
          this.onCloseCallback(event)
        }
        this.attemptReconnect(token)
      }

      this.ws.onerror = (event) => {
        console.error('WebSocket 错误:', event)
        if (this.onErrorCallback) {
          this.onErrorCallback(event)
        }
      }
    } catch (error) {
      console.error('创建 WebSocket 连接失败:', error)
      this.attemptReconnect(token)
    }
  }

  attemptReconnect(token) {
    if (this.reconnectCount < this.maxReconnectCount) {
      // 检查token是否有效
      try {
        const base64Url = token.split('.')[1]
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        const padding = '='.repeat((4 - (base64.length % 4)) % 4)
        const jwt = JSON.parse(atob(base64 + padding))
        
        // 确保jwt.claims.id存在且为有效数字
        if (jwt.claims && jwt.claims.id && !isNaN(Number(jwt.claims.id))) {
          this.reconnectCount++
          console.log(`尝试重连 (${this.reconnectCount}/${this.maxReconnectCount})...`)
          this.reconnectTimer = setTimeout(() => {
            this.connect(token)
          }, this.reconnectInterval)
        } else {
          console.error('重连失败：token中没有有效的用户ID，停止重连')
        }
      } catch (e) {
        console.error('重连失败：token无效，停止重连', e)
      }
    } else {
      console.error('达到最大重连次数，停止重连')
    }
  }

  send(data) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data))
    } else {
      console.error('WebSocket 未连接，无法发送消息')
    }
  }

  close() {
    this.stopHeartbeat()
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  startHeartbeat() {
    this.stopHeartbeat()
    this.heartbeatTimer = setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify({ type: 'heartbeat' }))
      }
    }, this.heartbeatInterval)
  }

  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  onMessage(callback) {
    this.onMessageCallback = callback
  }

  onOpen(callback) {
    this.onOpenCallback = callback
  }

  onClose(callback) {
    this.onCloseCallback = callback
  }

  onError(callback) {
    this.onErrorCallback = callback
  }
}

export default new ChatWebSocket()
