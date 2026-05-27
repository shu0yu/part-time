<template>
  <div class="job-publish">
    <!-- 导航栏 -->
    <Navbar />
    
    <!-- 页面标题 -->
    <section class="page-header">
      <div class="container">
        <div class="header-content">
          <h1 class="page-title">发布兼职岗位</h1>
          <p class="page-subtitle">请填写以下信息发布您的兼职岗位</p>
        </div>
      </div>
    </section>
    
    <!-- 发布表单 -->
    <section class="form-section">
      <div class="container">
        <div class="form-card">
          <div class="form-header">
            <h2>岗位信息</h2>
            <p class="form-subtitle">请详细填写岗位信息，以便学生更好地了解</p>
          </div>
          <JobForm />
        </div>
      </div>
    </section>
    
    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script setup>
import Navbar from '../components/Navbar.vue'
import JobForm from '../components/JobForm.vue'
import Footer from '../components/Footer.vue'
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.job-publish {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: $color-gray-50;
  padding-top: 80px;
}

.page-header {
  background: $gradient-primary;
  padding: 80px 0;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.header-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.page-title {
  font-size: 42px;
  font-weight: 800;
  color: white;
  margin: 0 0 12px;
  font-family: $font-family-display;
}

.page-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.form-section {
  padding: 40px 0 80px;
  flex: 1;
}

.form-card {
  background: white;
  border-radius: $radius-2xl;
  box-shadow: $shadow-lg;
  padding: $spacing-8;
  max-width: 900px;
  margin: 0 auto;
}

.form-header {
  margin-bottom: $spacing-6;
  padding-bottom: $spacing-4;
  border-bottom: 2px solid $color-gray-100;
}

.form-header h2 {
  font-size: $font-size-2xl;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0 0 $spacing-2;
  font-family: $font-family-display;
}

.form-subtitle {
  font-size: $font-size-base;
  color: $color-gray-500;
  margin: 0;
}

@media (max-width: 768px) {
  .job-publish {
    padding-top: 70px;
  }
  
  .page-header {
    padding: 60px 0;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .form-card {
    padding: $spacing-6;
  }
}
</style>