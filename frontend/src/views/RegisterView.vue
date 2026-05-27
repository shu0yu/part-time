<template>
  <div class="register-page">
    <div class="register-background">
      <div class="gradient-circle circle-1"></div>
      <div class="gradient-circle circle-2"></div>
      <div class="gradient-circle circle-3"></div>
    </div>
    
    <div class="register-content">
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
            加入我们
            <span class="highlight">开启职业之旅</span>
          </h1>
          
          <p class="decoration-description">
            汇聚优质兼职机会，助你快速找到心仪的工作
          </p>
          
          <div class="features-grid">
            <div class="feature-card">
              <el-icon><Lock /></el-icon>
              <span>安全保障</span>
            </div>
            <div class="feature-card">
              <el-icon><Timer /></el-icon>
              <span>快速响应</span>
            </div>
            <div class="feature-card">
              <el-icon><Coin /></el-icon>
              <span>薪资保障</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Right Side - Register Form -->
      <div class="form-section">
        <div class="form-card">
          <div class="card-header">
            <h2 class="card-title">创建账号</h2>
            <p class="card-subtitle">加入我们，开启兼职之旅</p>
          </div>
          
          <el-form 
            :model="registerForm" 
            :rules="registerRules" 
            ref="registerFormRef" 
            class="register-form"
          >
            <el-form-item prop="loginName">
              <div class="input-group">
                <div class="input-icon-wrapper">
                  <el-icon><User /></el-icon>
                </div>
                <el-input 
                  v-model="registerForm.loginName" 
                  placeholder="请输入登录账号"
                  size="large"
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <el-form-item prop="name">
              <div class="input-group">
                <div class="input-icon-wrapper">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <el-input 
                  v-model="registerForm.name" 
                  placeholder="请输入姓名"
                  size="large"
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <el-form-item prop="phone">
              <div class="input-group">
                <div class="input-icon-wrapper">
                  <el-icon><Phone /></el-icon>
                </div>
                <el-input 
                  v-model="registerForm.phone" 
                  placeholder="请输入联系电话"
                  size="large"
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <el-form-item prop="password">
              <div class="input-group">
                <div class="input-icon-wrapper">
                  <el-icon><Lock /></el-icon>
                </div>
                <el-input 
                  v-model="registerForm.password" 
                  placeholder="请输入密码"
                  type="password"
                  size="large"
                  show-password
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <el-form-item prop="confirmPassword">
              <div class="input-group">
                <div class="input-icon-wrapper">
                  <el-icon><Lock /></el-icon>
                </div>
                <el-input 
                  v-model="registerForm.confirmPassword" 
                  placeholder="请确认密码"
                  type="password"
                  size="large"
                  show-password
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <div class="form-options">
              <el-checkbox v-model="registerForm.agree" class="agree-checkbox">
                我已阅读并同意 
                <el-link type="primary" :underline="false">用户协议</el-link>
                和 
                <el-link type="primary" :underline="false">隐私政策</el-link>
              </el-checkbox>
            </div>
            
            <el-button 
              type="primary" 
              class="register-button" 
              @click="handleRegister" 
              :loading="loading"
              size="large"
            >
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
            
            <div class="form-footer">
              <span class="footer-text">已有账号？</span>
              <router-link to="/login" class="login-link">
                立即登录
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
import { User, Lock, UserFilled, Phone, Briefcase, Timer, Coin } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  loginName: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  agree: false
})

const registerRules = {
  loginName: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  agree: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请阅读并同意用户协议和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

/**
 * 处理注册
 */
const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await userStore.register(registerForm)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error('注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请填写正确的表单信息')
    }
  })
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.register-background {
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

.register-content {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 650px;
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

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.feature-card {
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

.feature-card:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.25);
}

.feature-card .el-icon {
  font-size: 32px;
  color: white;
  margin-bottom: 8px;
}

.feature-card span {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
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
  margin-bottom: 32px;
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

.register-form {
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

.custom-input {
  padding-left: 48px !important;
}

.custom-input :deep(.el-input__wrapper) {
  padding: 14px 16px;
  background: $color-gray-100;
  border-radius: 12px;
  box-shadow: none;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.custom-input :deep(.el-input__wrapper.is-focus + .input-icon-wrapper) {
  color: $color-primary;
}

.form-options {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.agree-checkbox {
  color: $color-gray-600;
  font-size: 13px;
}

.register-button {
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

.register-button:hover {
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

.login-link {
  font-size: 14px;
  font-weight: 600;
  color: $color-primary;
  text-decoration: none;
  transition: color 0.3s ease;
}

.login-link:hover {
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
  .register-content {
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
  
  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>
