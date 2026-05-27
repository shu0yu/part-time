<template>
  <div class="job-form">
    <el-form
      :model="jobForm"
      :rules="rules"
      ref="jobFormRef"
      label-width="100px"
      class="job-publish-form"
    >
      <div class="form-section">
        <div class="section-title">
          <el-icon><Document /></el-icon>
          <span>基本信息</span>
        </div>
        <div class="form-row">
          <el-form-item label="岗位名称" prop="jobName" class="form-item-full custom-form-item">
            <el-input 
              v-model="jobForm.jobName" 
              placeholder="请输入岗位名称"
              class="custom-input"
            />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="岗位类型" prop="jobType" class="form-item-full custom-form-item">
            <el-select 
              v-model="jobForm.jobType" 
              placeholder="请选择岗位类型"
              class="custom-select"
            >
              <el-option label="技术开发" value="技术开发" />
              <el-option label="产品运营" value="产品运营" />
              <el-option label="设计创意" value="设计创意" />
              <el-option label="市场销售" value="市场销售" />
              <el-option label="行政人事" value="行政人事" />
              <el-option label="财务金融" value="财务金融" />
              <el-option label="教育培训" value="教育培训" />
              <el-option label="实习兼职" value="实习兼职" />
            </el-select>
          </el-form-item>
        </div>
      </div>
      
      <div class="form-section">
        <div class="section-title">
          <el-icon><Money /></el-icon>
          <span>薪资信息</span>
        </div>
        <el-form-item label="薪资范围" class="form-item-full custom-form-item">
          <div class="salary-range">
            <div class="salary-input-wrapper">
              <el-input-number 
                v-model="jobForm.salaryMin" 
                :min="0" 
                :step="5" 
                placeholder="最低薪资"
                class="salary-input custom-input-number"
                prop="salaryMin"
              />
              <span class="salary-label">最低</span>
            </div>
            <span class="salary-separator">-</span>
            <div class="salary-input-wrapper">
              <el-input-number 
                v-model="jobForm.salaryMax" 
                :min="0" 
                :step="5" 
                placeholder="最高薪资"
                class="salary-input custom-input-number"
                prop="salaryMax"
              />
              <span class="salary-label">最高</span>
            </div>
            <span class="salary-unit">元/小时</span>
          </div>
        </el-form-item>
      </div>
      
      <div class="form-section">
        <div class="section-title">
          <el-icon><Location /></el-icon>
          <span>工作信息</span>
        </div>
        <div class="form-row">
          <el-form-item label="工作地点" prop="workAddress" class="form-item-full custom-form-item">
            <el-input 
              v-model="jobForm.workAddress" 
              placeholder="请输入工作地点"
              class="custom-input"
            />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="工作时间" prop="workTime" class="form-item-full custom-form-item">
            <el-input 
              v-model="jobForm.workTime" 
              placeholder="如：每周一至周五18:00-20:00" 
              type="textarea"
              :rows="2"
              class="custom-textarea"
            />
          </el-form-item>
        </div>
      </div>
      
      <div class="form-section">
        <div class="section-title">
          <el-icon><Edit /></el-icon>
          <span>岗位详情</span>
        </div>
        <div class="form-row">
          <el-form-item label="岗位描述" prop="jobDesc" class="form-item-full custom-form-item">
            <el-input 
              v-model="jobForm.jobDesc" 
              placeholder="请描述岗位内容，包括工作内容、职责范围等" 
              type="textarea"
              :rows="4"
              class="custom-textarea"
            />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="岗位要求" prop="jobRequire" class="form-item-full custom-form-item">
            <el-input 
              v-model="jobForm.jobRequire" 
              placeholder="请描述岗位要求，包括技能要求、经验要求等" 
              type="textarea"
              :rows="4"
              class="custom-textarea"
            />
          </el-form-item>
        </div>
      </div>
      
      <el-form-item class="form-actions">
        <el-button type="primary" @click="handleSubmit" class="submit-btn">
          <el-icon><CircleCheck /></el-icon>
          发布岗位
        </el-button>
        <el-button @click="handleReset" class="reset-btn">
          <el-icon><RefreshLeft /></el-icon>
          重置表单
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useJobStore } from '../store/job'
import { ElMessage } from 'element-plus'
import { Document, Money, Location, Edit, CircleCheck, RefreshLeft } from '@element-plus/icons-vue'

const jobFormRef = ref(null)
const jobStore = useJobStore()

/**
 * 表单数据
 */
const jobForm = reactive({
  jobName: '',
  jobType: '',
  salaryMin: null,
  salaryMax: null,
  workAddress: '',
  workTime: '',
  jobDesc: '',
  jobRequire: ''
})

/**
 * 表单验证规则
 */
const rules = {
  jobName: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  jobType: [
    { required: true, message: '请选择岗位类型', trigger: 'change' }
  ],
  salaryMin: [
    { required: true, message: '请输入最低薪资', trigger: 'blur' },
    { type: 'number', min: 0, message: '最低薪资必须大于等于0', trigger: 'blur' }
  ],
  salaryMax: [
    { required: true, message: '请输入最高薪资', trigger: 'blur' },
    { type: 'number', min: 0, message: '最高薪资必须大于等于0', trigger: 'blur' }
  ],
  workAddress: [
    { required: true, message: '请输入工作地点', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  workTime: [
    { required: true, message: '请输入工作时间', trigger: 'blur' }
  ],
  jobDesc: [
    { required: true, message: '请输入岗位描述', trigger: 'blur' }
  ],
  jobRequire: [
    { required: true, message: '请输入岗位要求', trigger: 'blur' }
  ]
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!jobFormRef.value) return
  
  await jobFormRef.value.validate(async (valid) => {
    if (valid) {
      if (jobForm.salaryMin > jobForm.salaryMax) {
        ElMessage.error('最低薪资不能大于最高薪资')
        return
      }
      
      try {
        await jobStore.addJob(jobForm)
        ElMessage.success('岗位发布成功')
        handleReset()
      } catch (error) {
        ElMessage.error('岗位发布失败：' + error.message)
      }
    } else {
      ElMessage.error('请检查表单填写是否正确')
      return false
    }
  })
}

/**
 * 重置表单
 */
const handleReset = () => {
  if (jobFormRef.value) {
    jobFormRef.value.resetFields()
  }
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.job-form {
  width: 100%;
}

.job-publish-form {
  .form-section {
    margin-bottom: $spacing-8;
    padding-bottom: $spacing-6;
    border-bottom: 1px dashed $color-gray-200;
    
    &:last-of-type {
      border-bottom: none;
      margin-bottom: 0;
      padding-bottom: 0;
    }
  }
  
  .section-title {
    display: flex;
    align-items: center;
    gap: $spacing-2;
    font-size: $font-size-lg;
    font-weight: 600;
    color: $color-gray-800;
    margin-bottom: $spacing-5;
    padding-left: $spacing-3;
    border-left: 4px solid $color-primary;
    
    .el-icon {
      color: $color-primary;
      font-size: 20px;
    }
  }
  
  .form-row {
    display: flex;
    gap: $spacing-4;
    flex-wrap: wrap;
  }
  
  .form-item-full {
    flex: 1;
    min-width: 100%;
  }
  
  .form-item-half {
    flex: 1;
    min-width: calc(50% - $spacing-2);
  }
  
  .form-item-third {
    flex: 1;
    min-width: calc(33.333% - $spacing-3);
  }
}

.salary-range {
  display: flex;
  align-items: center;
  gap: $spacing-6;
  flex-wrap: wrap;
}

.salary-input-wrapper {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  flex: 1;
  min-width: 180px;
}

.salary-input {
  flex: 1;
  min-width: 100px;
}

.salary-label {
  font-size: $font-size-sm;
  color: $color-gray-500;
  font-weight: 500;
  white-space: nowrap;
}

.salary-separator {
  font-size: $font-size-lg;
  color: $color-gray-400;
  font-weight: 600;
}

.salary-unit {
  font-size: $font-size-base;
  color: $color-gray-600;
  white-space: nowrap;
  font-weight: 500;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: $spacing-4;
  margin-top: $spacing-8;
  padding-top: $spacing-6;
  border-top: 2px solid $color-gray-100;
}

.submit-btn {
  background: $gradient-primary;
  border: none;
  padding: 14px 40px;
  font-size: $font-size-base;
  font-weight: 600;
  height: 52px;
  border-radius: $radius-lg;
  box-shadow: $shadow-primary;
  transition: all $transition-base;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 30px -10px rgba(14, 165, 233, 0.6);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.reset-btn {
  background: $color-gray-100;
  border: none;
  color: $color-gray-700;
  padding: 14px 40px;
  font-size: $font-size-base;
  font-weight: 600;
  height: 52px;
  border-radius: $radius-lg;
  transition: all $transition-base;
  
  &:hover {
    background: $color-gray-200;
    color: $color-gray-900;
    transform: translateY(-1px);
  }
  
  &:active {
    transform: translateY(0);
  }
}

// 关键修复：按照登录页面的方式
.custom-form-item {
  margin-bottom: 20px !important;
}

:deep(.custom-input .el-input__wrapper),
:deep(.custom-select .el-input__wrapper) {
  padding: 16px 18px !important;
  background: $color-gray-100 !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
  min-height: 56px !important;
  height: 56px !important;
}

:deep(.custom-input .el-input__inner),
:deep(.custom-select .el-input__inner) {
  height: 56px !important;
  line-height: 56px !important;
}

:deep(.custom-input .el-input__wrapper.is-focus),
:deep(.custom-select .el-input__wrapper.is-focus) {
  background: white !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

:deep(.custom-textarea .el-textarea__inner) {
  padding: 16px 18px !important;
  background: $color-gray-100 !important;
  border-radius: 12px !important;
  border: none !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
  
  &:hover {
    background: $color-gray-100;
  }
  
  &:focus {
    background: white !important;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
  }
}

:deep(.custom-input-number .el-input__wrapper) {
  padding: 16px 18px !important;
  background: $color-gray-100 !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
  min-height: 56px !important;
  height: 56px !important;
}

:deep(.custom-input-number .el-input__inner) {
  height: 56px !important;
  line-height: 56px !important;
}

:deep(.custom-input-number .el-input__wrapper.is-focus) {
  background: white !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

@media (max-width: 768px) {
  .job-publish-form {
    .form-row {
      flex-direction: column;
    }
    
    .form-item-half,
    .form-item-third {
      min-width: 100%;
    }
  }
  
  .salary-range {
    flex-direction: column;
    align-items: stretch;
  }
  
  .salary-input-wrapper {
    width: 100%;
  }
  
  .salary-separator {
    text-align: center;
    width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>

<!-- 全局样式：解决下拉选项问题 -->
<style>
/* 下拉选项样式 - 全局应用 */
.el-select-dropdown__item {
  padding: 10px 20px !important;
  font-size: 15px !important;
  line-height: 1.4 !important;
  margin: 4px 12px !important;
  border-radius: 6px !important;
  height: 44px !important;
  display: flex !important;
  align-items: center !important;
}

.el-select-dropdown__wrap {
  padding: 12px 0 !important;
}

.el-select-dropdown__item:hover {
  background-color: rgba(14, 165, 233, 0.08) !important;
}

.el-select-dropdown__item.selected {
  background-color: rgba(14, 165, 233, 0.15) !important;
  color: #0ea5e9 !important;
  font-weight: 600 !important;
}
</style>