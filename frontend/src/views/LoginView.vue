<template>
  <div class="login-page">
    <div class="login-background">
      <div class="gradient-circle circle-1"></div>
      <div class="gradient-circle circle-2"></div>
      <div class="gradient-circle circle-3"></div>
    </div>
    
    <div class="login-content">
      <!-- Left Side - Decoration -->
      <div class="decoration-section">
        <div class="decoration-content">
          <router-link to="/" class="brand-logo">
            <div class="logo-icon">
              <el-icon><Briefcase /></el-icon>
            </div>
            <span class="logo-text">兼职平台</span>
          </router-link>
          
          <h1 class="decoration-title">
            开启你的
            <span class="highlight">职业之旅</span>
          </h1>
          
          <p class="decoration-description">
            汇聚优质兼职机会，助你快速找到心仪的工作
          </p>
          
          <div class="stats-grid">
            <div class="stat-card">
              <span class="stat-number">10K+</span>
              <span class="stat-label">注册用户</span>
            </div>
            <div class="stat-card">
              <span class="stat-number">5K+</span>
              <span class="stat-label">岗位数量</span>
            </div>
            <div class="stat-card">
              <span class="stat-number">98%</span>
              <span class="stat-label">满意度</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Right Side - Login Form -->
      <div class="form-section">
        <div class="form-card">
          <div class="card-header">
            <h2 class="card-title">欢迎回来</h2>
            <p class="card-subtitle">登录你的账户继续</p>
          </div>
          
          <el-form 
            :model="loginForm" 
            :rules="loginRules" 
            ref="loginFormRef" 
            class="login-form"
            @keyup.enter="handleLogin"
          >
            <el-form-item prop="loginName" class="custom-form-item">
              <div class="input-group">
                <div class="input-icon-wrapper">
                  <el-icon><User /></el-icon>
                </div>
                <el-input 
                  v-model="loginForm.loginName" 
                  placeholder="请输入登录账号"
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <el-form-item prop="password" class="custom-form-item">
              <div class="input-group">
                <div class="input-icon-wrapper">
                  <el-icon><Lock /></el-icon>
                </div>
                <el-input 
                  v-model="loginForm.password" 
                  placeholder="请输入密码"
                  type="password"
                  show-password
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <div class="form-options">
              <el-checkbox v-model="loginForm.remember" class="remember-checkbox">
                记住我
              </el-checkbox>
              <el-link type="primary" :underline="false" class="forgot-link" @click="handleForgotPassword">
                忘记密码？
              </el-link>
            </div>
            
            <el-button 
              type="primary" 
              class="login-button" 
              @click="handleLogin" 
              :loading="loading"
              size="large"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
            
            <div class="form-footer">
              <span class="footer-text">还没有账号？</span>
              <router-link to="/register" class="register-link">
                立即注册
              </router-link>
            </div>
          </el-form>
        </div>
        
        <div class="copyright">
          © 2026 大学生兼职平台 版权所有
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Briefcase } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  loginName: '',
  password: '',
  remember: false
})

const loginRules = {
  loginName: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

/**
 * 处理登录
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败，请检查账号和密码')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请填写正确的表单信息')
    }
  })
}

/**
 * 处理忘记密码
 */
const handleForgotPassword = () => {
  ElMessage.info('忘记密码功能暂未开放')
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.gradient-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.4;
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 600px;
  height: 600px;
  background: rgba(255, 255, 255, 0.5);
  top: -200px;
  right: -150px;
  animation-delay: 0s;
}

.circle-2 {
  width: 500px;
  height: 500px;
  background: rgba(255, 255, 255, 0.4);
  bottom: -150px;
  left: -100px;
  animation-delay: -7s;
}

.circle-3 {
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.3);
  top: 40%;
  left: 40%;
  animation-delay: -14s;
}

.login-content {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 32px;
  box-shadow: 0 50px 100px -20px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  margin: 20px;
}

/* Decoration Section */
.decoration-section {
  flex: 1;
  background: $gradient-primary;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.decoration-section::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.brand-logo {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  margin-bottom: 40px;
  transition: transform 0.3s ease;
}

.brand-logo:hover {
  transform: scale(1.05);
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: white;
}

.decoration-title {
  font-size: 42px;
  font-weight: 800;
  color: white;
  line-height: 1.1;
  margin-bottom: 16px;
}

.highlight {
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

.decoration-description {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.6;
  margin-bottom: 48px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.25);
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: white;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

/* Form Section */
.form-section {
  flex: 1;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
}

.form-card {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.card-header {
  text-align: left;
  margin-bottom: 40px;
}

.card-title {
  font-size: 32px;
  font-weight: 800;
  color: $color-gray-900;
  margin: 0 0 8px;
}

.card-subtitle {
  font-size: 15px;
  color: $color-gray-500;
  margin: 0;
}

.login-form {
  margin-bottom: 0;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon-wrapper {
  position: absolute;
  left: 16px;
  z-index: 1;
  color: $color-gray-400;
  display: flex;
  align-items: center;
  font-size: 18px;
  transition: color 0.3s ease;
}

.custom-form-item {
  margin-bottom: 20px !important;
}

.custom-input {
  padding-left: 48px !important;
}

.custom-input :deep(.el-input__wrapper) {
  padding: 16px 18px !important;
  background: $color-gray-100 !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
  min-height: 56px !important;
  height: 56px !important;
}

.custom-input :deep(.el-input__inner) {
  height: 56px !important;
  line-height: 56px !important;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background: white !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

.custom-input :deep(.el-input__wrapper.is-focus + .input-icon-wrapper) {
  color: $color-primary !important;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.remember-checkbox {
  color: $color-gray-600;
}

.forgot-link {
  font-size: 14px;
  font-weight: 500;
}

.login-button {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  background: $gradient-primary;
  border: none;
  border-radius: 12px;
  margin-top: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.5);
}

.form-footer {
  text-align: center;
  margin-top: 32px;
  display: flex;
  justify-content: center;
  gap: 8px;
  align-items: center;
}

.footer-text {
  font-size: 14px;
  color: $color-gray-600;
}

.register-link {
  font-size: 14px;
  font-weight: 600;
  color: $color-primary;
  text-decoration: none;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: $color-primary-dark;
}

.copyright {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 13px;
  color: $color-gray-400;
}

/* Animations */
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-30px) rotate(5deg);
  }
  50% {
    transform: translateY(0) rotate(0deg);
  }
  75% {
    transform: translateY(30px) rotate(-5deg);
  }
}

/* Responsive */
@media (max-width: 1024px) {
  .decoration-section {
    display: none;
  }
  
  .form-section {
    padding: 40px;
  }
}

@media (max-width: 640px) {
  .login-content {
    margin: 0;
    border-radius: 0;
    min-height: 100vh;
  }
  
  .form-section {
    padding: 32px 24px;
  }
  
  .card-title {
    font-size: 28px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
