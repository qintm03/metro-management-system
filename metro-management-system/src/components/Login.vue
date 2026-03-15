<template>
  <div class="login-container">

    <div class="login-box">
      <el-form :model="form" ref="formRef" :rules="form.rules">
        <!-- 用户 -->
        <el-form-item prop="username">
          <el-input placeholder="输入用户名" :prefix-icon="User" v-model="form.username" />
        </el-form-item>
        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input placeholder="输入密码" :prefix-icon="Lock" v-model="form.password" type="password" show-password />
        </el-form-item>
        <!-- 身份 -->
        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="选择登录身份">
            <el-option label="管理员" value="管理员" />
            <el-option label="普通用户" value="普通用户" />
          </el-select>
        </el-form-item>
        <!-- 按钮 -->
        <el-form-item>
          <el-button @click="login" type="primary">登录</el-button>

        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { User, Lock } from "@element-plus/icons-vue";
import request from '../utils/request.js'
import { ElMessage } from 'element-plus'
import router from '../router.js'


const form = reactive({
  username: 'admin',
  password: '123456',
  role: '管理员',
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
    ],//form验证规则
    
            /*  required: true
                作用：标记该字段为必填项
                效果：如果用户未填写该字段，验证将失败

                message: '请输入账号'
                作用：验证失败时显示的错误提示信息
                效果：当验证失败时，在输入框下方显示该提示文字

                trigger: 'blur'
                作用：指定触发验证的时机
                'blur'：输入框失去焦点时触发验证*/
  }
})

const formRef = ref()

const login = () => {
  formRef.value.validate((valid => {
    if (valid) {
      // 调用后台的接口
      request.post('/login', form).then(res => {
        if (res.code === '200') {
          ElMessage.success("登录成功")
          localStorage.setItem('system-user', JSON.stringify(res.data))//将用户信息存储到本地，用户刷新页面后仍保持登录状态
          if (res.data.role === '管理员') {
            router.push('/manager')
          } else {
            router.push('/user')
          }
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })).catch(error => {
    console.error(error)
  })
}

</script>

<style scoped>
.login-container {
  height: 120vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #2e3143;
  background-size: cover;
}

.login-box {
  width: 350px;
  padding: 50px 30px;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
  background-color: #fff;
}

.login-box button {
  width: 100%;

}
</style>