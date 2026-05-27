<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-brand">
          <div class="brand-icon">
            <el-icon><Briefcase /></el-icon>
          </div>
          <span class="brand-name">大学生兼职平台</span>
        </div>
        
        <div class="footer-links-section">
          <h4 class="footer-section-title">快速导航</h4>
          <div class="footer-links">
            <router-link to="/">首页</router-link>
            <router-link to="/jobs">兼职列表</router-link>
            <router-link to="/profile">个人中心</router-link>
          </div>
        </div>
        
        <div class="footer-info-section">
          <h4 class="footer-section-title">平台信息</h4>
          <div class="footer-info">
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>联系电话：400-888-8888</span>
            </div>
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>邮箱：contact@parttime.com</span>
            </div>
          </div>
        </div>
        
        <div class="footer-policy-section">
          <h4 class="footer-section-title">服务条款</h4>
          <div class="footer-links">
            <a href="#" @click.prevent="handlePolicyClick('privacy')">隐私政策</a>
            <a href="#" @click.prevent="handlePolicyClick('terms')">服务条款</a>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>© 2026 大学生兼职平台 版权所有 | 备案号：京ICP备12345678号</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { Briefcase, Phone, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

/**
 * 处理政策链接点击
 * @param {string} type - 政策类型
 */
const handlePolicyClick = (type) => {
  ElMessage.info(`${type === 'privacy' ? '隐私政策' : '服务条款'}页面正在开发中`)
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.footer {
  background: $color-gray-900;
  padding: $spacing-16 0 $spacing-8;
  color: $color-gray-400;
  margin-top: auto;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 $spacing-6;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: $spacing-10;
  padding-bottom: $spacing-8;
  border-bottom: 1px solid $color-gray-700;
}

.footer-brand {
  display: flex;
  flex-direction: column;
  gap: $spacing-3;
}

.brand-icon {
  width: 48px;
  height: 48px;
  background: $gradient-primary;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.brand-name {
  font-size: $font-size-xl;
  font-weight: 700;
  color: white;
  font-family: $font-family-display;
}

.footer-section-title {
  font-size: $font-size-base;
  font-weight: 600;
  color: white;
  margin-bottom: $spacing-4;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: $spacing-2;
}

.footer-links a {
  color: $color-gray-400;
  text-decoration: none;
  font-size: $font-size-sm;
  transition: color $transition-fast;

  &:hover {
    color: $color-primary-light;
  }
}

.footer-info {
  display: flex;
  flex-direction: column;
  gap: $spacing-3;
}

.info-item {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  font-size: $font-size-sm;
  color: $color-gray-400;
  
  .el-icon {
    color: $color-primary;
  }
}

.footer-bottom {
  text-align: center;
  padding-top: $spacing-6;
}

.footer-bottom p {
  font-size: $font-size-sm;
  color: $color-gray-500;
  margin: 0;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: $spacing-8;
  }
  
  .footer-brand {
    align-items: center;
    text-align: center;
  }
  
  .footer-links-section,
  .footer-info-section,
  .footer-policy-section {
    text-align: center;
  }
  
  .footer-info {
    align-items: center;
  }
}
</style>
