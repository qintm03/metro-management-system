<template>
  <div class="login-page">
    <!-- 背景层 -->
    <div class="login-bg">
      <div class="bg-gradient"></div>
      <div class="bg-pattern"></div>
      <div class="bg-glow"></div>
    </div>

    <!-- 登录容器 -->
    <div class="login-wrapper">
      <div class="login-card">
        <!-- Logo区域 -->
        <div class="brand-section">
          <div class="brand-logo">
            <el-icon :size="48"><Van /></el-icon>
          </div>
          <h1 class="brand-title">地铁管理系统</h1>
          <p class="brand-subtitle">Metro Management System</p>
        </div>

        <!-- 登录表单 -->
        <el-form 
          ref="formRef" 
          :model="form" 
          :rules="rules" 
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <div class="input-group">
              <el-icon class="input-icon"><User /></el-icon>
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名"
                size="large"
                clearable
              />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="input-group">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                size="large"
                show-password
                clearable
              />
            </div>
          </el-form-item>

          <el-form-item prop="role">
            <div class="input-group">
              
              <el-select 
                v-model="form.role" 
                placeholder="请选择登录身份"
                size="large"
                class="role-select"
              >
                <el-option value="管理员" class="role-option">
                  
                  
                </el-option>
                <el-option value="普通用户" class="role-option">
                 
                  
                </el-option>
              </el-select>
            </div>
          </el-form-item>

          <el-form-item class="submit-item">
            <el-button 
              type="primary" 
              size="large" 
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              <template #icon>
                <el-icon v-if="!loading"><Right /></el-icon>
              </template>
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 底部信息 -->
        <div class="login-footer">
          <p>©  地铁管理系统 | 智慧城市交通</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Van, Right } from '@element-plus/icons-vue'
import request from '../utils/request.js'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: 'qtm',
  password: '123456',
  role: '普通用户'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择登录身份', trigger: 'change' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const res = await request.post('/login', form)
    
    if (res.code === '200') {
      ElMessage.success('登录成功')
      localStorage.setItem('system-user', JSON.stringify(res.data))
      
      if (res.data.role === '管理员') {
        router.push('/manager')
      } else {
        router.push('/user')
      }
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('登录失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 页面全屏容器 */
.login-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  min-height: 100vh;
  min-width: 100vw;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 背景层 */
.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, 
    #0f172a 0%, 
    #1e3a5f 25%, 
    #2563eb 50%, 
    #1e40af 75%, 
    #0f172a 100%
  );
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(59, 130, 246, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(37, 99, 235, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(30, 58, 95, 0.4) 0%, transparent 40%);
  animation: pulse 8s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
}

.bg-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 800px;
  height: 800px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

/* 登录容器 */
.login-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 登录卡片 */
.login-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  animation: slideUp 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 品牌区域 */
.brand-section {
  text-align: center;
  margin-bottom: 40px;
}

.brand-logo {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 10px 30px rgba(59, 130, 246, 0.4);
  animation: logoPulse 2s ease-in-out infinite;
}

@keyframes logoPulse {
  0%, 100% { box-shadow: 0 10px 30px rgba(59, 130, 246, 0.4); }
  50% { box-shadow: 0 10px 40px rgba(59, 130, 246, 0.6); }
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 13px;
  color: #64748b;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 3px;
}

/* 登录表单 */
.login-form {
  width: 100%;
}

/* 输入组 */
.input-group {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 16px;
  font-size: 18px;
  color: #94a3b8;
  z-index: 10;
  transition: color 0.3s ease;
}

/* 表单项样式 */
:deep(.el-form-item) {
  margin-bottom: 20px;
  width: 100%;
}

:deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

:deep(.el-input__wrapper) {
  width: 100%;
  padding-left: 48px;
  padding-right: 16px;
  height: 52px;
  border-radius: 12px;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  box-shadow: none !important;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #cbd5e1;
  background: #f1f5f9;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1) !important;
}

:deep(.el-input__wrapper.is-focus) + .input-icon,
.input-group:focus-within .input-icon {
  color: #3b82f6;
}

:deep(.el-input__inner) {
  font-size: 15px;
  color: #1e293b;
}

:deep(.el-input__inner::placeholder) {
  color: #94a3b8;
}

/* 选择器样式 */
:deep(.role-select) {
  width: 100%;
}

:deep(.role-select .el-input__wrapper) {
  padding-left: 48px;
}

:deep(.role-select .el-input__inner) {
  text-align: left;
}

/* 角色选项样式 - 覆盖 Element Plus 默认样式 */
:deep(.role-option) {
  display: flex !important;
  align-items: center !important;
  padding: 8px 16px !important;
  height: auto !important;
  line-height: normal !important;
}

:deep(.role-option .el-icon) {
  position: static !important;
  margin-right: 12px;
  font-size: 18px;
  flex-shrink: 0;
}

:deep(.role-option span) {
  position: static !important;
  transform: none !important;
}

/* 提交按钮 */
.submit-item {
  margin-top: 28px;
}

.login-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.4);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.5);
}

.login-btn:active {
  transform: translateY(0);
}

/* 底部信息 */
.login-footer {
  margin-top: 32px;
  text-align: center;
}

.login-footer p {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

/* 错误提示 */
:deep(.el-form-item__error) {
  padding-top: 6px;
  font-size: 12px;
  color: #ef4444;
  padding-left: 16px;
}

/* 响应式适配 */
@media (max-width: 480px) {
  .login-wrapper {
    padding: 16px;
  }
  
  .login-card {
    padding: 32px 24px;
    border-radius: 20px;
  }
  
  .brand-title {
    font-size: 24px;
  }
  
  .brand-logo {
    width: 64px;
    height: 64px;
  }
  
  :deep(.el-input__wrapper) {
    height: 48px;
  }
  
  .login-btn {
    height: 48px;
  }
}

/* 超小屏幕适配 */
@media (max-width: 360px) {
  .login-card {
    padding: 24px 20px;
  }
  
  .brand-title {
    font-size: 22px;
  }
  
  .brand-subtitle {
    font-size: 11px;
  }
}
</style>
