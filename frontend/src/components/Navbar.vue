<template>
  <nav class="navbar" :class="{ 'scrolled': isScrolled }">
    <div class="container">
      <router-link to="/" class="navbar-brand">
        <div class="brand-icon">
          <el-icon><Briefcase /></el-icon>
        </div>
        <span class="brand-text">兼职平台</span>
      </router-link>
      
      <div class="navbar-menu">
        <ul class="navbar-nav">
          <li class="nav-item" v-for="item in navItems" :key="item.path">
            <router-link 
              :to="item.path" 
              class="nav-link"
              :class="{ 'active': isActive(item.path) }"
            >
              <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
              <span>{{ item.name }}</span>
              <span class="nav-dot" v-if="item.badge && unreadCount > 0"></span>
            </router-link>
          </li>
        </ul>
      </div>
      
      <div class="navbar-user">
        <template v-if="isLoggedIn">
          <router-link v-if="isCompanyRole" to="/job/publish" class="publish-btn">
            <el-icon><Plus /></el-icon>
            <span>发布岗位</span>
          </router-link>
          <div class="user-dropdown" @click="dropdownVisible = !dropdownVisible">
            <div class="user-avatar-wrapper">
              <el-avatar 
                :size="40" 
                :style="{ background: avatarGradient }"
              >
                {{ userInfo?.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="online-dot"></span>
            </div>
            <div class="user-info">
              <span class="user-name">{{ userInfo?.realName || '个人中心' }}</span>
              <span class="user-role">{{ isAdminRole ? '管理员' : (isCompanyRole ? '企业用户' : '学生用户') }}</span>
            </div>
            <el-icon class="arrow-icon" :class="{ 'rotate': dropdownVisible }">
              <ArrowDown />
            </el-icon>
            
            <transition name="dropdown-slide">
              <div class="dropdown-menu" v-if="dropdownVisible" @click.stop>
                <div class="dropdown-header">
                  <el-avatar :size="50" :style="{ background: avatarGradient }">
                    {{ userInfo?.realName?.charAt(0) || 'U' }}
                  </el-avatar>
                  <div class="header-info">
                    <span class="header-name">{{ userInfo?.realName }}</span>
                    <span class="header-phone">{{ userInfo?.phone }}</span>
                  </div>
                </div>
                <div class="dropdown-divider"></div>
                <router-link to="/profile" class="dropdown-item" @click="dropdownVisible = false" v-if="!isAdminRole">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </router-link>
                <router-link to="/chat" class="dropdown-item" @click="dropdownVisible = false">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>消息中心</span>
                  <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="msg-badge" />
                </router-link>
                <div class="dropdown-divider" v-if="isAdminRole"></div>
                <template v-if="isAdminRole">
                  <router-link to="/admin/users" class="dropdown-item" @click="dropdownVisible = false">
                    <el-icon><User /></el-icon>
                    <span>用户管理</span>
                  </router-link>
                  <router-link to="/admin/jobs" class="dropdown-item" @click="dropdownVisible = false">
                    <el-icon><List /></el-icon>
                    <span>岗位管理</span>
                  </router-link>
                  <div class="dropdown-divider"></div>
                </template>
                <button class="dropdown-item logout" @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </button>
              </div>
            </transition>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="auth-link">登录</router-link>
          <router-link to="/register" class="auth-btn">
            <span>注册</span>
          </router-link>
        </template>
      </div>
      
      <button class="mobile-menu-btn" @click="mobileMenuVisible = !mobileMenuVisible">
        <el-icon><Menu /></el-icon>
      </button>
    </div>
    
    <transition name="mobile-menu-slide">
      <div class="mobile-menu" v-if="mobileMenuVisible">
        <ul class="mobile-nav">
          <li v-for="item in navItems" :key="item.path">
            <router-link :to="item.path" @click="mobileMenuVisible = false">
              <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
              <span>{{ item.name }}</span>
            </router-link>
          </li>
        </ul>
      </div>
    </transition>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import { ChatDotRound, User, SwitchButton, Plus, ArrowDown, Menu, Briefcase, HomeFilled, List, Message } from '@element-plus/icons-vue'
import { chatAPI } from '@/api/api'
import chatWebSocket from '@/utils/websocket'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const dropdownVisible = ref(false)
const mobileMenuVisible = ref(false)
const unreadCount = ref(0)
const isScrolled = ref(false)

const isLoggedIn = computed(() => userStore.getIsLoggedIn)
const userInfo = computed(() => userStore.getUserInfo)
const isCompanyRole = computed(() => {
  if (!userInfo.value || !userInfo.value.roles) return false
  return userInfo.value.roles.some(role => role === 'COMPANY')
})

const isAdminRole = computed(() => {
  if (!userInfo.value || !userInfo.value.roles) return false
  return userInfo.value.roles.some(role => role === 'ADMIN')
})

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

const navItems = [
  { name: '首页', path: '/', icon: HomeFilled },
  { name: '兼职', path: '/jobs', icon: List },
  { name: '消息', path: '/chat', icon: Message, badge: true }
]

const isActive = (path) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

const loadUnreadCount = async () => {
  try {
    const res = await chatAPI.getChatSessions()
    if (res.data && res.code === 0) {
      unreadCount.value = res.data.reduce((sum, session) => sum + (session.unreadCount || 0), 0)
    }
  } catch (error) {
    console.error('加载未读消息数失败:', error)
  }
}

const handleWebSocketMessage = (data) => {
  if (data.type === 'new_message') {
    loadUnreadCount()
  }
}

const handleLogout = () => {
  dropdownVisible.value = false
  userStore.logout()
  router.push('/login')
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const handleClickOutside = (e) => {
  if (dropdownVisible.value && !e.target.closest('.user-dropdown')) {
    dropdownVisible.value = false
  }
}

onMounted(async () => {
  window.addEventListener('scroll', handleScroll)
  document.addEventListener('click', handleClickOutside)
  
  if (isLoggedIn.value && !userInfo.value) {
    try {
      await userStore.getCurrentUser()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  
  if (isLoggedIn.value) {
    await loadUnreadCount()
    const token = localStorage.getItem('token')
    if (token) {
      chatWebSocket.connect(token)
      chatWebSocket.onMessage(handleWebSocketMessage)
    }
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.navbar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: $shadow-md;
  padding: $spacing-4 0;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  transition: all $transition-base;
}

.navbar.scrolled {
  background: rgba(255, 255, 255, 0.98);
  box-shadow: $shadow-lg;
  padding: $spacing-2 0;
  transform: translateY(0);
}

.container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 $spacing-5;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  text-decoration: none;
}

.brand-icon {
  width: 44px;
  height: 44px;
  background: $gradient-primary;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $color-white;
  font-size: 22px;
  transition: all $transition-base;
  box-shadow: $shadow-primary;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left $transition-slow;
  }
  
  &:hover {
    transform: rotate(5deg) scale(1.05);
    box-shadow: 0 6px 20px rgba(37, 99, 235, 0.6);
    
    &::before {
      left: 100%;
    }
  }
}

.brand-text {
  font-size: $font-size-lg;
  font-weight: 700;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.navbar-menu {
  flex: 1;
  display: flex;
  justify-content: center;
}

.navbar-nav {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: $spacing-2;
}

.nav-item {
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  color: $color-gray-600;
  text-decoration: none;
  font-size: $font-size-sm;
  font-weight: 600;
  padding: $spacing-3 $spacing-5;
  border-radius: $radius-lg;
  transition: all $transition-base;
  position: relative;
  overflow: hidden;
  z-index: 1;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(79, 172, 254, 0.1), transparent);
    transition: left 0.6s ease;
    z-index: -1;
  }
  
  &:hover {
    color: #4facfe;
    background: rgba(79, 172, 254, 0.1);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.2);
    
    &::before {
      left: 100%;
    }
  }
  
  &.active {
    color: white;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
    
    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      border-radius: 0 0 12px 12px;
    }
  }
}

.nav-dot {
  position: absolute;
  top: 4px;
  right: 8px;
  width: 8px;
  height: 8px;
  background: #f56c6c;
  border-radius: 50%;
  border: 2px solid white;
}

.navbar-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.publish-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  z-index: 1;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.6s ease;
    z-index: -1;
  }
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(79, 172, 254, 0.6);
    
    &::before {
      left: 100%;
    }
  }
  
  &:active {
    transform: translateY(0);
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
  }
}

.auth-link {
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 16px;
  transition: color 0.3s ease;
}

.auth-link:hover {
  color: #4facfe;
}

.auth-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  text-decoration: none;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.auth-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
}

.user-dropdown {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  
  &:hover {
    background: rgba(79, 172, 254, 0.05);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.user-avatar-wrapper {
  position: relative;
}

.online-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  background: #67c23a;
  border: 2px solid white;
  border-radius: 50%;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.arrow-icon {
  color: #909399;
  transition: transform 0.3s ease;
  font-size: 12px;
}

.arrow-icon.rotate {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 16px);
  right: 0;
  background: white;
  border-radius: 20px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  min-width: 280px;
  z-index: 1001;
  overflow: hidden;
  transform-origin: top right;
  
  &::before {
    content: '';
    position: absolute;
    top: -8px;
    right: 24px;
    width: 16px;
    height: 16px;
    background: white;
    transform: rotate(45deg);
    border-radius: 4px;
    box-shadow: -2px -2px 4px rgba(0, 0, 0, 0.05);
  }
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.08) 0%, rgba(0, 242, 254, 0.08) 100%);
}

.header-info {
  display: flex;
  flex-direction: column;
}

.header-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-phone {
  font-size: 13px;
  color: #909399;
}

.dropdown-divider {
  height: 1px;
  background: #ebeef5;
  margin: 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 20px;
  text-align: left;
  background: none;
  border: none;
  font-size: 14px;
  color: #606266;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background: rgba(79, 172, 254, 0.06);
  color: #4facfe;
}

.dropdown-item.logout:hover {
  background: rgba(245, 108, 108, 0.08);
  color: #f56c6c;
}

.dropdown-item i {
  font-size: 18px;
}

.msg-badge {
  margin-left: auto;
}

.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  font-size: 24px;
  color: #303133;
  cursor: pointer;
}

.mobile-menu {
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  background: white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 20px;
  z-index: 999;
}

.mobile-nav {
  list-style: none;
  padding: 0;
  margin: 0;
}

.mobile-nav li a {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  color: #303133;
  text-decoration: none;
  font-size: 15px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.mobile-nav li a:hover {
  background: rgba(79, 172, 254, 0.08);
  color: #4facfe;
}

.dropdown-slide-enter-active,
.dropdown-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-slide-enter-from,
.dropdown-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
  box-shadow: 0 0 0 rgba(0, 0, 0, 0);
}

.mobile-menu-slide-enter-active,
.mobile-menu-slide-leave-active {
  transition: all 0.3s ease;
}

.mobile-menu-slide-enter-from,
.mobile-menu-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

@media (max-width: 768px) {
  .navbar-menu {
    display: none;
  }

  .navbar-user {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
  }

  .brand-text {
    font-size: 16px;
  }
}
</style>
