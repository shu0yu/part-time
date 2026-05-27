<template>
  <div class="job-card" @click="handleClick">
    <div class="job-card-header">
      <span class="job-type">{{ job.jobType || '兼职' }}</span>
      <div class="job-salary">
        <span class="salary-number">{{ job.salaryMin }}-{{ job.salaryMax }}</span>
        <span class="salary-unit">元/时</span>
      </div>
    </div>
    
    <h3 class="job-title">{{ job.jobName }}</h3>
    
    <div class="job-meta">
      <div class="meta-item">
        <el-icon><Location /></el-icon>
        <span>{{ job.workAddress }}</span>
      </div>
      <div class="meta-item">
        <el-icon><Clock /></el-icon>
        <span>{{ job.workTime }}</span>
      </div>
    </div>
    
    <p class="job-description">{{ job.jobDesc }}</p>
    
    <div class="job-card-footer">
      <span class="publish-date">{{ formatDate(job.publishTime) }}</span>
      <button class="apply-button" @click.stop="handleApply">
        立即申请
      </button>
    </div>
  </div>
</template>

<script setup>
import { Location, Clock } from '@element-plus/icons-vue'

const props = defineProps({
  job: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['click', 'apply'])

/**
 * 处理卡片点击事件
 */
const handleClick = () => {
  emit('click', props.job)
}

/**
 * 处理申请按钮点击事件
 */
const handleApply = () => {
  emit('apply', props.job)
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.job-card {
  background: $color-white;
  border-radius: $radius-2xl;
  padding: $spacing-6;
  border: 1px solid $color-gray-100;
  transition: all $transition-base;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.6s ease-out forwards;
  animation-delay: calc(var(--index, 0) * 0.08s);
  opacity: 0;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: $gradient-primary;
    opacity: 0;
    transition: opacity $transition-base;
  }

  &:hover {
    transform: translateY(-8px);
    box-shadow: $shadow-2xl;
    border-color: $color-primary;

    &::before {
      opacity: 1;
    }
  }
}

.job-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $spacing-4;
}

.job-type {
  display: inline-block;
  padding: $spacing-2 $spacing-3;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.1) 0%, rgba(124, 58, 237, 0.1) 100%);
  color: $color-primary;
  font-size: $font-size-sm;
  font-weight: 600;
  border-radius: $radius-full;
}

.job-salary {
  text-align: right;
}

.salary-number {
  font-size: $font-size-2xl;
  font-weight: 800;
  background: $gradient-accent;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.salary-unit {
  display: block;
  font-size: $font-size-xs;
  color: $color-gray-400;
}

.job-title {
  font-size: $font-size-xl;
  font-weight: 700;
  color: $color-gray-900;
  margin-bottom: $spacing-4;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.job-meta {
  display: flex;
  flex-direction: column;
  gap: $spacing-2;
  margin-bottom: $spacing-4;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  font-size: $font-size-sm;
  color: $color-gray-500;

  .el-icon {
    color: $color-gray-400;
    font-size: 16px;
  }
}

.job-description {
  font-size: $font-size-sm;
  color: $color-gray-500;
  line-height: 1.6;
  margin-bottom: $spacing-5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.job-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: $spacing-4;
  border-top: 1px solid $color-gray-100;
}

.publish-date {
  font-size: $font-size-xs;
  color: $color-gray-400;
}

.apply-button {
  padding: $spacing-2 $spacing-5;
  background: $gradient-primary;
  color: $color-white;
  font-size: $font-size-sm;
  font-weight: 600;
  border-radius: $radius-full;
  transition: all $transition-base;

  &:hover {
    transform: scale(1.05);
    box-shadow: $shadow-primary;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
